package ru.reaperoq.pf2ecl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.reaperoq.pf2ecl.data.CharacterBuilderViewModel
import ru.reaperoq.pf2ecl.screens.*
import ru.reaperoq.pf2ecl.theme.AppTheme

@Composable
fun App(
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {}
) = AppTheme(onThemeChanged) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        val characterViewModel: CharacterBuilderViewModel = viewModel { CharacterBuilderViewModel() }
        val characterState by characterViewModel.characterState.collectAsState()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        val showCreationNav = currentRoute != null && currentRoute != "dashboard"

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth > 720.dp

            if (showCreationNav && isWideScreen) {
                val navItems = listOf(
                    Triple("ancestry", "Родословная", Icons.Rounded.Groups),
                    Triple("background", "Предыстория", Icons.Rounded.History),
                    Triple("classSelection", "Класс", Icons.Rounded.Shield),
                    Triple("matrix", "Атрибуты", Icons.Rounded.AutoAwesome),
                    Triple("printSheet", "Сводка", Icons.Rounded.Print)
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Pathfinder Forge",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = characterState.name.ifBlank { "Новый герой" },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val sub = listOfNotNull(
                                        characterState.ancestry?.name,
                                        characterState.classData?.name
                                    ).joinToString(" / ")
                                    Text(
                                        text = sub.ifBlank { "Уровень 1 Искатель" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            navItems.forEach { (route, label, icon) ->
                                val selected = currentRoute == route
                                NavigationDrawerItem(
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label) },
                                    selected = selected,
                                    onClick = { navController.navigate(route) },
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    characterViewModel.saveCurrentCharacter()
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = false }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Rounded.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Сохранить героя")
                            }
                            OutlinedButton(
                                onClick = {
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = false }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("На главную")
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        AppNavHost(navController, characterViewModel)
                    }
                }
            } else if (showCreationNav && !isWideScreen) {
                val navItems = listOf(
                    Triple("ancestry", "Родословная", Icons.Rounded.Groups),
                    Triple("background", "Предыстория", Icons.Rounded.History),
                    Triple("classSelection", "Класс", Icons.Rounded.Shield),
                    Triple("matrix", "Атрибуты", Icons.Rounded.AutoAwesome),
                    Triple("printSheet", "Сводка", Icons.Rounded.Print)
                )
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            navItems.forEach { (route, label, icon) ->
                                val selected = currentRoute == route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navController.navigate(route) },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavHost(navController, characterViewModel)
                    }
                }
            } else {
                AppNavHost(navController, characterViewModel)
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    characterViewModel: CharacterBuilderViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = characterViewModel,
                onForgeClick = {
                    characterViewModel.reset()
                    navController.navigate("ancestry")
                },
                onCharacterClick = { charState ->
                    characterViewModel.loadCharacter(charState)
                    navController.navigate("printSheet")
                }
            )
        }
        composable("ancestry") {
            AncestryScreen(
                viewModel = characterViewModel,
                onBack = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = false }
                    }
                },
                onContinue = { navController.navigate("background") }
            )
        }
        composable("background") {
            BackgroundScreen(
                viewModel = characterViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("classSelection") }
            )
        }
        composable("classSelection") {
            ClassScreen(
                viewModel = characterViewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate("matrix") }
            )
        }
        composable("matrix") {
            AttributeMatrixScreen(
                viewModel = characterViewModel,
                onBack = { navController.popBackStack() },
                onGenerate = { navController.navigate("printSheet") }
            )
        }
        composable("printSheet") {
            PrintSheetScreen(
                viewModel = characterViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
