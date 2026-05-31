package com.sanguo2.assistant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanguo2.assistant.data.model.CounterInfo
import com.sanguo2.assistant.data.model.Soldier
import com.sanguo2.assistant.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoldierDetailBottomSheet(
    soldier: Soldier,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SoldierDetailHeader(soldier)

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            SoldierTypeSection(soldier)

            SoldierRangeSection(soldier)

            SoldierSpeedSection(soldier)

            if (soldier.description.isNotBlank()) {
                SoldierDescriptionSection(soldier)
            }

            SoldierRestraintOverviewSection(soldier)

            if (soldier.counters.isNotEmpty()) {
                SoldierCounterListSection(
                    title = "克制兵种",
                    icon = Icons.Default.ArrowUpward,
                    items = soldier.counters,
                    color = Green700
                )
            }

            if (soldier.counteredBy.isNotEmpty()) {
                SoldierCounterListSection(
                    title = "被克制兵种",
                    icon = Icons.Default.ArrowDownward,
                    items = soldier.counteredBy,
                    color = Red700
                )
            }
        }
    }
}

@Composable
private fun SoldierDetailHeader(soldier: Soldier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = soldier.name.first().toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = soldier.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RangeTypeChip(soldier.rangeType)
                Spacer(modifier = Modifier.width(8.dp))
                val tierLabel = when (soldier.id) {
                    in 0..12 -> "基础兵种"
                    else -> "高级兵种（骑兵）"
                }
                Text(
                    text = tierLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SoldierTypeSection(soldier: Soldier) {
    SectionCard(title = "兵种类型", icon = Icons.Default.Shield) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeInfoChip(
                label = "射程类型",
                value = soldier.rangeType,
                modifier = Modifier.weight(1f),
                color = when (soldier.rangeType) {
                    "近战" -> Red700
                    "远程" -> Blue700
                    else -> Gold700
                }
            )
            TypeInfoChip(
                label = "射程等级",
                value = "${soldier.rangeValue}",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary
            )
            TypeInfoChip(
                label = "兵种层级",
                value = if (soldier.id < 13) "步兵" else "骑兵",
                modifier = Modifier.weight(1f),
                color = Purple700
            )
        }
    }
}

@Composable
private fun SoldierRangeSection(soldier: Soldier) {
    SectionCard(title = "攻击射程", icon = Icons.Default.Straighten) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RangeParamCard(
                label = "步兵射程",
                value = soldier.infantryRange,
                modifier = Modifier.weight(1f)
            )
            RangeParamCard(
                label = "骑兵射程",
                value = soldier.cavalryRange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SoldierSpeedSection(soldier: Soldier) {
    SectionCard(title = "攻击速度", icon = Icons.Default.Speed) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "注：攻速数字越大，攻速越慢",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpeedParamCard(
                    label = "步兵远程攻速",
                    value = soldier.infantryRangedSpeed,
                    modifier = Modifier.weight(1f)
                )
                SpeedParamCard(
                    label = "步兵近战攻速",
                    value = soldier.infantryMeleeSpeed,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpeedParamCard(
                    label = "骑兵远程攻速",
                    value = soldier.cavalryRangedSpeed,
                    modifier = Modifier.weight(1f)
                )
                SpeedParamCard(
                    label = "骑兵近战攻速",
                    value = soldier.cavalryMeleeSpeed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SoldierDescriptionSection(soldier: Soldier) {
    SectionCard(title = "定位描述", icon = Icons.Default.Info) {
        Text(
            text = soldier.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun SoldierRestraintOverviewSection(soldier: Soldier) {
    SectionCard(title = "克制总览", icon = Icons.Default.Analytics) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RestraintSummaryCard(
                label = "克度总和",
                value = soldier.totalRestraint,
                modifier = Modifier.weight(1f)
            )
            RestraintSummaryCard(
                label = "克制兵种数",
                value = soldier.counters.size,
                modifier = Modifier.weight(1f)
            )
            RestraintSummaryCard(
                label = "被克制数",
                value = soldier.counteredBy.size,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SoldierCounterListSection(
    title: String,
    icon: ImageVector,
    items: List<CounterInfo>,
    color: Color
) {
    SectionCard(title = title, icon = icon) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items.take(8).forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = if (color == Green700) "+${item.value}" else "-${item.value}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
            if (items.size > 8) {
                Text(
                    text = "还有${items.size - 8}个...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun TypeInfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun RangeParamCard(
    label: String,
    value: Int?,
    modifier: Modifier = Modifier
) {
    val displayValue = value?.toString() ?: "-"
    val rangeLabel = when (value) {
        1 -> "近战"
        2 -> "半远程"
        3 -> "远程"
        else -> ""
    }
    val rangeColor = when (value) {
        1 -> Red700
        2 -> Gold700
        3 -> Blue700
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = displayValue,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = rangeColor
        )
        if (rangeLabel.isNotEmpty()) {
            Text(
                text = rangeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = rangeColor
            )
        }
    }
}

@Composable
private fun SpeedParamCard(
    label: String,
    value: Int?,
    modifier: Modifier = Modifier
) {
    val displayValue = value?.toString() ?: "-"
    val speedColor = when {
        value != null && value <= 1 -> Green700
        value != null && value <= 5 -> Gold700
        value != null -> Red700
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = displayValue,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = speedColor
        )
    }
}

@Composable
private fun RestraintSummaryCard(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    val valueColor = when {
        value > 0 -> Green700
        value < 0 -> Red700
        else -> MaterialTheme.colorScheme.onSurface
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun RangeTypeChip(rangeType: String) {
    val (bgColor, textColor) = when (rangeType) {
        "近战" -> Pair(Red100, Red900)
        "远程" -> Pair(Blue100, Blue700)
        else -> Pair(Gold100, Gold800)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = rangeType,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
