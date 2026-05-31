package com.sanguo2.assistant.ui.garrison

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanguo2.assistant.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForceNoteEditScreen(
    noteId: Long = 0,
    isEdit: Boolean = false,
    onNavigateBack: () -> Unit = {},
    viewModel: ForceNoteViewModel = hiltViewModel()
) {
    val editState by viewModel.editState.collectAsState()
    val soldierNames by remember { derivedStateOf { viewModel.uiState.value.soldierNames } }
    val focusManager = LocalFocusManager.current
    var soldierDropdownIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(noteId) {
        if (isEdit && noteId > 0) {
            val noteWithConfigs = viewModel.getNoteById(noteId)
            if (noteWithConfigs != null) {
                viewModel.initEditState(noteWithConfigs)
            }
        } else {
            viewModel.initEditState()
        }
    }

    LaunchedEffect(editState.saveSuccess) {
        if (editState.saveSuccess) {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editState.isEdit) "编辑备注" else "添加备注",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("部队类型", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = editState.forceType == "city",
                    onClick = { viewModel.onForceTypeChanged("city") },
                    label = { Text("城池部队") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                FilterChip(
                    selected = editState.forceType == "field",
                    onClick = { viewModel.onForceTypeChanged("field") },
                    label = { Text("野外部队") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (editState.forceType == "city") {
                OutlinedTextField(
                    value = editState.cityName,
                    onValueChange = { viewModel.onCityNameChanged(it) },
                    label = { Text("城池名称（可选）") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                OutlinedTextField(
                    value = editState.customLabel,
                    onValueChange = { viewModel.onCustomLabelChanged(it) },
                    label = { Text("备注（如主帅名称）") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editState.remark,
                onValueChange = { viewModel.onRemarkChanged(it) },
                label = { Text("附加备注（可选）") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("兵种配置", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                val totalCount = editState.soldierConfigs.sumOf { it.count.toIntOrNull() ?: 0 }
                Text(
                    "${totalCount}/15 将",
                    style = MaterialTheme.typography.labelLarge,
                    color = when {
                        totalCount > 15 -> Red700
                        totalCount > 0 -> Green700
                        else -> Gray600
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            editState.generalCountError?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(error, style = MaterialTheme.typography.labelSmall, color = Red700)
            }

            Spacer(modifier = Modifier.height(8.dp))

            editState.soldierConfigs.forEachIndexed { index, configItem ->
                SoldierConfigRow(
                    index = index,
                    configItem = configItem,
                    soldierNames = soldierNames,
                    canRemove = editState.soldierConfigs.size > 1,
                    onTypeChanged = { viewModel.onSoldierTypeChanged(index, it) },
                    onCountChanged = { viewModel.onSoldierCountChanged(index, it) },
                    onRemove = { viewModel.removeSoldierConfig(index) },
                    dropdownExpanded = soldierDropdownIndex == index,
                    onDropdownExpandedChange = { soldierDropdownIndex = if (it) index else -1 },
                    focusManager = focusManager
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { viewModel.addSoldierConfig() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加兵种")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveNote() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !editState.isSaving
            ) {
                if (editState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (editState.isEdit) "保存修改" else "添加备注", fontWeight = FontWeight.Bold)
            }

            if (!editState.isEdit) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.onCityNameChanged("")
                        viewModel.onCustomLabelChanged("")
                        viewModel.onRemarkChanged("")
                        viewModel.initEditState()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("重置表单")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoldierConfigRow(
    index: Int,
    configItem: SoldierConfigUiItem,
    soldierNames: List<String>,
    canRemove: Boolean,
    onTypeChanged: (String) -> Unit,
    onCountChanged: (String) -> Unit,
    onRemove: () -> Unit,
    dropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = onDropdownExpandedChange,
                modifier = Modifier.weight(1.6f)
            ) {
                OutlinedTextField(
                    value = configItem.soldierType,
                    onValueChange = {
                        onTypeChanged(it)
                        onDropdownExpandedChange(it.isNotBlank())
                    },
                    placeholder = { Text("兵种", style = MaterialTheme.typography.bodySmall) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
                ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { onDropdownExpandedChange(false) }) {
                    val filtered = if (configItem.soldierType.isBlank()) soldierNames else soldierNames.filter { it.contains(configItem.soldierType) }
                    filtered.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name, style = MaterialTheme.typography.bodySmall) },
                            onClick = {
                                onTypeChanged(name)
                                onDropdownExpandedChange(false)
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = configItem.count,
                onValueChange = onCountChanged,
                placeholder = { Text("数量", style = MaterialTheme.typography.bodySmall) },
                isError = configItem.countError != null,
                modifier = Modifier.weight(0.8f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "移除", tint = Red700.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
