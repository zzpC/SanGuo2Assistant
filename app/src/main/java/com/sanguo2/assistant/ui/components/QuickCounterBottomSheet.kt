package com.sanguo2.assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanguo2.assistant.data.model.CounterSoldier
import com.sanguo2.assistant.data.model.Soldier
import com.sanguo2.assistant.ui.soldier.SoldierQueryViewModel
import com.sanguo2.assistant.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCounterBottomSheet(
    soldierName: String,
    onDismissRequest: () -> Unit,
    viewModel: SoldierQueryViewModel = hiltViewModel()
) {
    val queryResult by viewModel.queryResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedSoldierDetail by remember { mutableStateOf<Soldier?>(null) }

    LaunchedEffect(soldierName) {
        if (soldierName.isNotBlank()) {
            viewModel.querySoldier(soldierName)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxHeight(0.8f).padding(horizontal = 16.dp)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "${soldierName} 克制关系",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                }

                errorMessage?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Red100)
                    ) {
                        Text(msg, modifier = Modifier.padding(16.dp), color = Red900)
                    }
                }

                queryResult?.let { result ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            RushInfoCard(result.rushInfo)
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = Gold700,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "克制${result.enemySoldier.name}的兵种",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(result.counterSoldiers) { counter ->
                            SimpleCounterSoldierCard(
                                counter = counter,
                                onClick = { selectedSoldierDetail = counter.soldier }
                            )
                        }

                        if (result.counterSoldiers.isEmpty() && !isLoading) {
                            item {
                                Text(
                                    "没有特别克制${result.enemySoldier.name}的兵种",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Gray600
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedSoldierDetail?.let { soldier ->
        SoldierDetailBottomSheet(
            soldier = soldier,
            onDismiss = { selectedSoldierDetail = null }
        )
    }
}

@Composable
private fun RushInfoCard(rushInfo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Gold50)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Gold800, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(rushInfo, style = MaterialTheme.typography.bodySmall, color = Gold800)
        }
    }
}

@Composable
private fun SimpleCounterSoldierCard(
    counter: CounterSoldier,
    onClick: () -> Unit
) {
    val barColor = when {
        counter.restraintValue >= 50 -> Green700
        counter.restraintValue >= 30 -> Gold700
        else -> Blue700
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = counter.soldier.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                RangeTypeChip(counter.rangeType)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${counter.restraintValue}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Info,
                    contentDescription = "详情",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
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
