package com.rohitneel.todomaster.presentation.navigation

import org.jetbrains.compose.resources.DrawableResource

data class NavDrawerItem(
    val title: String,
    val route: String? = null,
    val icon: DrawableResource,
    val action: (() -> Unit)? = null,
    val badgeCount: Int? = null,
    val isExpandable: Boolean = false,
    val children: List<NavDrawerItem> = emptyList()
)

data class BottomNavItem(
    val selectedIcon: DrawableResource,
    val route: NavDestinations,
    val label: String
)
