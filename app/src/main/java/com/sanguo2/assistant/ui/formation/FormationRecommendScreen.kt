package com.sanguo2.assistant.ui.formation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sanguo2.assistant.data.model.FormationRecommendation
import com.sanguo2.assistant.data.model.FormationRecommendResult
import com.sanguo2.assistant.ui.theme.*

private val FORMATION_MATRICES: Map<String, List<List<Int>>> = mapOf(
    "方" to listOf(
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "圆" to listOf(
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0),
        listOf(0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0),
        listOf(0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0),
        listOf(0, 0, 2, 0, 0, 9, 0, 0, 2, 0, 0),
        listOf(0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0),
        listOf(0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0),
        listOf(0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "锥" to listOf(
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "雁" to listOf(
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0),
        listOf(0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0),
        listOf(0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "玄" to listOf(
        listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1),
        listOf(1, 1, 1, 0, 2, 9, 2, 0, 1, 1, 1),
        listOf(1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2),
        listOf(2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2),
        listOf(2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2),
    ),
    "鱼" to listOf(
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "钩" to listOf(
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0),
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0),
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0),
        listOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "冲" to listOf(
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0),
        listOf(0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0),
        listOf(0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
    "箭" to listOf(
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0),
        listOf(0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
    ),
)

private val FORMATION_FULL_NAMES = mapOf(
    "方" to "方形之阵",
    "圆" to "圆形之阵",
    "锥" to "锥形之阵",
    "雁" to "雁型之阵",
    "玄" to "玄襄之阵",
    "鱼" to "鱼丽之阵",
    "钩" to "钩型之阵",
    "冲" to "冲锋之阵",
    "箭" to "箭矢之阵",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FormationRecommendScreen(
    viewModel: FormationRecommendViewModel = hiltViewModel(),
) {
    val queryResult by viewModel.queryResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedFormation by viewModel.selectedFormation.collectAsState()
    val formationNames by viewModel.formationNames.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "阵型推荐",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "选择敌方阵型",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FormationSelector(
            formationNames = formationNames,
            selectedFormation = selectedFormation
        ) { name ->
            viewModel.onFormationSelected(name)
            viewModel.queryFormation(name)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
            AnimatedVisibility(visible = true) {
                FormationResultContent(result)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FormationSelector(
    formationNames: List<String>,
    selectedFormation: String,
    onFormationSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            formationNames.forEach { name ->
                val isSelected = name == selectedFormation
                FilterChip(
                    selected = isSelected,
                    onClick = { onFormationSelected(name) },
                    label = {
                        Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun FormationResultContent(result: FormationRecommendResult) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EnemyFormationCard(result)
        }

        item {
            DescriptionCard(result.description)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold700,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "推荐阵型",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(result.recommendedFormations) { recommendation ->
            FormationRecommendationCard(recommendation, result.recommendedFormations.indexOf(recommendation))
        }

        if (result.recommendedFormations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Gray100)
                ) {
                    Text(
                        "没有特别克制${result.enemyFormation.name}阵的阵型",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray600
                    )
                }
            }
        }

        item {
            FormationLayoutCard(result.enemyFormation.name)
        }
    }
}

@Composable
private fun EnemyFormationCard(result: FormationRecommendResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "敌方阵型",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${result.enemyFormation.name}阵",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun DescriptionCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun FormationRecommendationCard(recommendation: FormationRecommendation, index: Int) {
    val progressFraction = (recommendation.restraintValue.toFloat() / 30f).coerceIn(0f, 1f)
    val isTop = index == 0
    val barColor = when {
        recommendation.restraintValue >= 25 -> Green700
        recommendation.restraintValue >= 15 -> Gold700
        else -> Blue700
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTop) 4.dp else 2.dp),
        colors = if (isTop) CardDefaults.cardColors(containerColor = Green100) else CardDefaults.elevatedCardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isTop) {
                        Icon(Icons.Default.Star, contentDescription = "最佳", tint = Gold700, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = "${recommendation.formation.name}阵",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isTop) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("最佳", style = MaterialTheme.typography.labelSmall, color = Green700, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "+${recommendation.restraintValue}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progressFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = Gray200
            )

            Spacer(modifier = Modifier.height(8.dp))
            FormationMiniLayout(recommendation.formation.name, compact = true)
        }
    }
}

@Composable
private fun FormationLayoutCard(enemyName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${FORMATION_FULL_NAMES[enemyName] ?: enemyName} 布局示意",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            FormationMiniLayout(enemyName, compact = false)
            Spacer(modifier = Modifier.height(12.dp))
            FormationLayoutLegend()
        }
    }
}

@Composable
private fun FormationLayoutLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("前排", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Blue700)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("后排", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Gold700)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("将领", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FormationMiniLayout(formationName: String, compact: Boolean = true) {
    val positions = FORMATION_MATRICES[formationName]
        ?: List(11) { List(11) { 1 } }

    val cellSize = if (compact) 10.dp else 20.dp
    val gap = if (compact) 2.dp else 3.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        positions.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                row.forEach { cell ->
                    val color = when (cell) {
                        1 -> MaterialTheme.colorScheme.primary
                        2 -> Blue700
                        9 -> Gold700
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            Spacer(modifier = Modifier.height(gap))
        }
    }
}
