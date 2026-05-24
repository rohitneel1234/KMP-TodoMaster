package com.rohitneel.todomaster.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants.ALL
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@Composable
fun NavDrawerHeader(taskViewModel: TaskViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(color = Color(taskViewModel.themeColor.value.toArgb())),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(Res.drawable.header_logo),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.app_name),
                color = Color.White,
                fontSize = 24.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White, blurRadius = 10f,
                        offset = Offset(2f, 2f)
                    ),
                    fontFamily = FontFamily.Serif,
                    fontSize = 35.sp
                ),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun NavDrawerBody(
    drawerItems: List<NavDrawerItem>,
    currentRoute: String?,
    taskViewModel: TaskViewModel,
    onClick: (NavDrawerItem) -> Unit,
) {
    var expandedCategory by remember { mutableStateOf(false) }
    val taskCounts by taskViewModel.taskCounts.collectAsState()
    val totalTasksCount = taskCounts.values.sum()

    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(drawerItems) { navigationItem ->
            if (navigationItem.isExpandable) {
                val isExpanded = expandedCategory
                NavigationDrawerItem(
                    item = navigationItem,
                    selected = false,
                    onClick = { expandedCategory = !isExpanded },
                    isExpanded = isExpanded,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                // Display child items if the category is expanded
                if (isExpanded) {
                    navigationItem.children.forEachIndexed { index, childItem ->
                        val count = if (childItem.title == ALL) totalTasksCount else taskCounts[childItem.title]
                        NavigationDrawerItem(
                            item = childItem.copy(badgeCount = count),
                            selected = currentRoute == childItem.route,
                            onClick = {
                                taskViewModel.selectCategory(index)
                                taskViewModel.getCategory(childItem.title)
                                onClick(childItem)
                            },
                            startPadding = 24.dp
                        )
                    }
                }
            } else {
                NavigationDrawerItem(
                    item = navigationItem,
                    selected = currentRoute == navigationItem.route,
                    onClick = { onClick(navigationItem) }
                )
            }
        }
    }
}

@Composable
fun NavigationDrawerItem(
    item: NavDrawerItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    startPadding: Dp = 20.dp
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .padding(PaddingValues(horizontal = 12.dp))
            .semantics { role = Role.Tab }
            .height(56.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.title,
            )
            Spacer(Modifier.width(startPadding))
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            if (item.isExpandable) {
                Icon(
                    painter = if (isExpanded) {
                        painterResource(Res.drawable.baseline_arrow_drop_up_24)
                    } else {
                        painterResource(Res.drawable.baseline_arrow_drop_down_24)
                    },
                    contentDescription = "Expand/Collapse",
                )
            }
            item.badgeCount?.let {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = it.toString(),
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }
        }
    }
}
