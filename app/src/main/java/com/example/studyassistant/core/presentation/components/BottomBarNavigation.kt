package com.example.studyassistant.core.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studyassistant.core.navigation.Route


data class BottomNavigationItem(
    val title: String,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

val navItems = listOf(
    BottomNavigationItem(
        title = "Dashboard",
        route = Route.DashboardScreen,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false,
    ),
    BottomNavigationItem(
        title = "Flashcard",
        route = Route.FlashcardScreen,
        selectedIcon = Icons.Filled.AccountBox,
        unselectedIcon = Icons.Outlined.AccountBox,
        hasNews = false,
        badgeCount = 45
    ),
    BottomNavigationItem(
        title = "Utility",
        route = Route.UtilityScreen,
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        hasNews = false,
    ),
    BottomNavigationItem(
        title = "Settings",
        route = Route.MainSetting,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        hasNews = true,
    ),
)

@Composable
fun BottomBarNavigation(
    selectedItemIndex: Int,
    onSelectedItemIndexChange: (Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.extractRouteName()

    // Update selectedItemIndex when the route changes
    LaunchedEffect(currentRoute) {
        val newIndex = navItems.indexOfFirst {
            it.route.toString() == currentRoute
        }
        if (newIndex >= 0 && newIndex != selectedItemIndex) {
            onSelectedItemIndexChange(newIndex)
        }
    }

    val isInBottomNavItem = currentRoute in listOf(
        "DashboardScreen", "FlashcardScreen", "UtilityScreen", "MainSetting"
    )
    if(isInBottomNavItem){
        NavigationBar {
            navItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = {
                        if (selectedItemIndex != index) {
                            onSelectedItemIndexChange(index)
                            navController.navigate(item.route)
                        }
                    },
                    label = { Text(text = item.title) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount != null) {
                                    Badge { Text(text = item.badgeCount.toString()) }
                                } else if (item.hasNews) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun String.extractRouteName(): String? {
    val prefix = "com.example.studyassistant.core.navigation.Route."
    return if (this.contains(prefix)) {
        this.substringAfter(prefix)
    } else {
        null
    }
}