package com.sanguo2.assistant.ui.garrison

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanguo2.assistant.data.local.entity.SoldierConfig
import com.sanguo2.assistant.data.repository.ForceNoteWithConfigs
import com.sanguo2.assistant.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForceNoteListScreen(
    onNavigateToAdd: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    viewModel: ForceNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDeleteDialog by remember { mutableStateOf<ForceNoteWithConfigs?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    showDeleteDialog?.let { item ->
        val label = getForceLabel(item)
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除「$label」的备注吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(item)
                    showDeleteDialog = null
                }) { Text("删除", color = Red700) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("取消") }
            }
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("清空所有备注") },
            text = { Text("确定要清空所有备注记录吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllNotes()
                    showClearAllDialog = false
                }) { Text("清空", color = Red700) }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) { Text("取消") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "部队兵种备注",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    if (uiState.notes.isNotEmpty()) {
                        TextButton(onClick = { showClearAllDialog = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("清空", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    FloatingActionButton(
                        onClick = onNavigateToAdd,
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "添加", tint = Color.White)
                    }
                }
            }

            FilterBar(
                uiState = uiState,
                onCityFilterChanged = { viewModel.setFilterCityName(it) },
                onLabelFilterChanged = { viewModel.setFilterLabel(it) },
                onClearFilters = { viewModel.clearFilters() },
                focusManager = focusManager
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }

            uiState.errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = { TextButton(onClick = { viewModel.clearMessages() }) { Text("关闭") } }
                ) { Text(msg) }
            }

            uiState.successMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = { TextButton(onClick = { viewModel.clearMessages() }) { Text("关闭") } },
                    containerColor = Green700
                ) { Text(msg, color = Color.White) }
            }

            if (uiState.notes.isEmpty() && !uiState.isLoading) {
                EmptyNotesView(onNavigateToAdd)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notes, key = { it.note.id }) { item ->
                        ForceNoteCard(
                            item = item,
                            onClick = { onNavigateToDetail(item.note.id) },
                            onDelete = { showDeleteDialog = item }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterBar(
    uiState: ForceNoteUiState,
    onCityFilterChanged: (String) -> Unit,
    onLabelFilterChanged: (String) -> Unit,
    onClearFilters: () -> Unit,
    focusManager: FocusManager
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("筛选", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.weight(1f))
                if (uiState.filterCityName.isNotBlank() || uiState.filterLabel.isNotBlank()) {
                    TextButton(onClick = onClearFilters) { Text("清除", style = MaterialTheme.typography.labelSmall) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.filterCityName,
                    onValueChange = onCityFilterChanged,
                    label = { Text("城池名称") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = uiState.filterLabel,
                    onValueChange = onLabelFilterChanged,
                    label = { Text("备注/主帅") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ForceNoteCard(
    item: ForceNoteWithConfigs,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val note = item.note
    val label = getForceLabel(item)
    val totalCount = item.configs.sumOf { it.count }
    val isCity = note.forceType == "city"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isCity) MaterialTheme.colorScheme.primaryContainer else Purple700.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isCity) Icons.Default.LocationCity else Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isCity) MaterialTheme.colorScheme.onPrimaryContainer else Purple700
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "${totalCount}将",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    val configText = item.configs.joinToString("  ") { "${it.soldierType}×${it.count}" }
                    Text(configText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "删除", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun EmptyNotesView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.NoteAdd, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))
        Text("暂无备注信息", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))
        Text("点击右上角 + 添加部队兵种备注", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onAddClick, shape = RoundedCornerShape(8.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("添加备注")
        }
    }
}

private fun getForceLabel(item: ForceNoteWithConfigs): String {
    val note = item.note
    return when {
        note.forceType == "city" && note.cityName.isNotBlank() -> note.cityName
        note.forceType == "field" && note.customLabel.isNotBlank() -> note.customLabel
        else -> "未命名部队"
    }
}
