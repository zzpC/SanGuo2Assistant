package com.sanguo2.assistant.data.local

import com.sanguo2.assistant.data.model.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class ExcelParser {
    private val rangeMap = mapOf(
        "朴刀" to "近战", "蛮族" to "近战", "黄巾" to "近战",
        "弓箭" to "远程", "弩兵" to "远程", "白马" to "远程",
    )
    private val rangeValueMap = mapOf("近战" to 1, "半远程" to 2, "远程" to 3)

    fun parse(inputStream: InputStream): GameData? {
        return try {
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            val soldierNames = mutableListOf<String>()
            for (i in 1..26) {
                val row = sheet.getRow(i) ?: continue
                val nameCell = row.getCell(1)
                val name = nameCell?.stringCellValue?.trim() ?: continue
                soldierNames.add(name)
            }

            val soldiers = mutableListOf<Soldier>()
            for (i in 1..26) {
                val row = sheet.getRow(i) ?: continue
                val name = soldierNames.getOrNull(i - 1) ?: continue
                val desc = row.getCell(29)?.stringCellValue?.trim() ?: ""
                val restraint = mutableMapOf<String, Int>()
                for (j in 0 until minOf(26, soldierNames.size)) {
                    val cell = row.getCell(3 + j)
                    val value = cell?.numericCellValue?.toInt() ?: 0
                    restraint[soldierNames[j]] = value
                }
                val rangeType = rangeMap[name] ?: "半远程"
                val rangeValue = rangeValueMap[rangeType] ?: 2
                val counters = restraint.filter { it.value > 0 }
                    .map { CounterInfo(it.key, it.value) }
                    .sortedByDescending { it.value }
                val counteredBy = restraint.filter { it.value < 0 }
                    .map { CounterInfo(it.key, -it.value) }
                    .sortedByDescending { it.value }
                soldiers.add(Soldier(
                    id = i - 1, name = name, description = desc,
                    rangeType = rangeType, rangeValue = rangeValue,
                    infantryRange = row.getCell(30)?.numericCellValue?.toInt(),
                    cavalryRange = row.getCell(31)?.numericCellValue?.toInt(),
                    infantryRangedSpeed = row.getCell(32)?.numericCellValue?.toInt(),
                    infantryMeleeSpeed = row.getCell(33)?.numericCellValue?.toInt(),
                    cavalryRangedSpeed = row.getCell(34)?.numericCellValue?.toInt(),
                    cavalryMeleeSpeed = row.getCell(35)?.numericCellValue?.toInt(),
                    totalRestraint = row.getCell(36)?.numericCellValue?.toInt() ?: 0,
                    restraint = restraint, counters = counters, counteredBy = counteredBy
                ))
            }

            val formationNames = mutableListOf<String>()
            val formationRow = sheet.getRow(28)
            for (j in 3..11) {
                val cell = formationRow?.getCell(j)
                val name = cell?.stringCellValue?.trim() ?: continue
                formationNames.add(name)
            }

            val formations = mutableListOf<Formation>()
            for (i in 0 until formationNames.size) {
                val row = sheet.getRow(29 + i) ?: continue
                val restraint = mutableMapOf<String, Int>()
                for (j in 0 until formationNames.size) {
                    val cell = row.getCell(3 + j)
                    val value = cell?.numericCellValue?.toInt() ?: 0
                    restraint[formationNames[j]] = value
                }
                val counters = restraint.filter { it.value > 0 }
                    .map { CounterInfo(it.key, it.value) }
                    .sortedByDescending { it.value }
                val counteredBy = restraint.filter { it.value < 0 }
                    .map { CounterInfo(it.key, -it.value) }
                    .sortedByDescending { it.value }
                formations.add(Formation(
                    id = i, name = formationNames[i],
                    restraint = restraint, counters = counters, counteredBy = counteredBy
                ))
            }

            workbook.close()
            GameData(
                version = "1.4+",
                rushRule = "26个兵种分为三档射程，朴刀、蛮族、黄巾纯近战（1），弓箭、弩兵、白马真远程（3），其他都是半远程（2），1见到23都会全突，2见到3会全突。",
                speedNote = "攻速数字越大，攻速越慢。骑兵即高级兵的意思。",
                readingNote = "此表请横向查阅，举例：朴刀对长枪-43，说明长枪克朴刀43点。",
                soldiers = soldiers, formations = formations
            )
        } catch (e: Exception) {
            null
        }
    }

    fun validateFormat(inputStream: InputStream): ValidationResult {
        return try {
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            if (sheet.physicalNumberOfRows < 28) {
                workbook.close()
                return ValidationResult(false, "数据行数不足，至少需要28行数据")
            }
            val headerRow = sheet.getRow(0)
            if (headerRow == null) {
                workbook.close()
                return ValidationResult(false, "缺少表头行")
            }
            val firstSoldierRow = sheet.getRow(1)
            if (firstSoldierRow == null || firstSoldierRow.getCell(1)?.stringCellValue.isNullOrBlank()) {
                workbook.close()
                return ValidationResult(false, "兵种数据格式异常：第2行B列应为兵种名称")
            }
            workbook.close()
            ValidationResult(true, "格式验证通过")
        } catch (e: org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException) {
            ValidationResult(false, "文件不是有效的Excel(.xlsx)格式，请检查文件类型")
        } catch (e: Exception) {
            ValidationResult(false, "文件解析失败：${e.message}")
        }
    }
}

data class ValidationResult(val isValid: Boolean, val message: String)
