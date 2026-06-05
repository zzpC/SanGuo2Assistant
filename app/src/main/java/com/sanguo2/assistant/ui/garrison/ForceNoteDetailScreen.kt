package com.sanguo2.assistant.ui.garrison

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanguo2.assistant.data.local.entity.SoldierConfig
import com.sanguo2.assistant.data.repository.ForceNoteWithConfigs
import com.sanguo2.assistant.ui.components.QuickCounterBottomSheet
import com.sanguo2.assistant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ForceNoteDetailScreen(
    noteId: Long,
    onNavigateToEdit: (Long) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: ForceNoteViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    var quickQueryConfigId by remember { mutableStateOf<Long>(-1L) }
    var quickQuerySoldierName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(noteId) {
        viewModel.loadNoteDetail(noteId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "备注详情",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onNavigateToEdit(noteId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑", tint = MaterialTheme.colorScheme.primary)
                }
            }

            if (detailState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            detailState.noteWithConfigs?.let { item ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { ForceInfoCard(item) }

                    item { 
                        SoldierConfigCard(
                            configs = item.configs,
                            onQuickQuery = { config, name -> 
                                quickQueryConfigId = config
                                quickQuerySoldierName = name
                            },
                            onClearPlanned = { viewModel.updatePlannedCounterType(it, noteId, "") }
                        ) 
                    }

                    if (item.note.remark.isNotBlank()) {
                        item { RemarkCard(item.note.remark) }
                    }

                    item { TimeInfoCard(item) }
                }
            }
        }

        quickQuerySoldierName?.let { name ->
            QuickCounterBottomSheet(
                soldierName = name,
                onDismissRequest = { 
                    quickQuerySoldierName = null
                    quickQueryConfigId = -1L
                },
                onSelectCounter = { 
                    if (quickQueryConfigId != -1L) {
                        viewModel.updatePlannedCounterType(quickQueryConfigId, noteId, it)
                    }
                    quickQuerySoldierName = null
                    quickQueryConfigId = -1L
                }
            )
        }
    }
}

@Composable
private fun ForceInfoCard(item: ForceNoteWithConfigs) {
    val note = item.note
    val isCity = note.forceType == "city"
    val label = when {
        isCity && note.cityName.isNotBlank() -> note.cityName
        !isCity && note.customLabel.isNotBlank() -> note.customLabel
        else -> "未命名部队"
    }
    val totalUnits = item.configs.sumOf { it.count }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCity) Icons.Default.LocationCity else Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(label, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                if (isCity) "城池部队" else "野外部队",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "部队总数: $totalUnits",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SoldierConfigCard(
    configs: List<SoldierConfig>,
    onQuickQuery: (Long, String) -> Unit,
    onClearPlanned: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("兵种配置", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            
            // Note: In Detail screen, configs are already loaded from DB with IDs.
            // If they are saved with count > 1, we might need to expand them if we want to show distinct counters.
            // But user said "associated with each enemy soldier entry".
            // If they have same type but different counters, they were saved as count=1 separate rows in my new logic.
            
            configs.forEachIndexed { index, config ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onQuickQuery(config.id, config.soldierType) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("部队 ${index + 1}: ${config.soldierType}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        Icon(
                            Icons.Default.MilitaryTech, 
                            contentDescription = "查询克制", 
                            tint = Gold700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (config.plannedCounterType?.isNotBlank() == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 22.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "计划上阵: ${config.plannedCounterType}", 
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { onClearPlanned(config.id) }, modifier = Modifier.size(16.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "清除", tint = Gray400, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    if (index < configs.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun RemarkCard(remark: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Gold50)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(Icons.Default.Notes, contentDescription = null, tint = Gold800, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(remark, style = MaterialTheme.typography.bodyMedium, color = Gold800)
        }
    }
}

@Composable
private fun TimeInfoCard(item: ForceNoteWithConfigs) {
    val note = item.note
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = Gray600)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("创建时间", style = MaterialTheme.typography.labelMedium, color = Gray600)
                }
                Text(formatTimestamp(note.createdAt), style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(16.dp), tint = Gray600)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("更新时间", style = MaterialTheme.typography.labelMedium, color = Gray600)
                }
                Text(formatTimestamp(note.updatedAt), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
