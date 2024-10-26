package com.humberto.tasky.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.humberto.tasky.agenda.presentation.agenda_list.AgendaScreenRoot
import com.humberto.tasky.auth.presentation.login.LoginScreenRoot
import com.humberto.tasky.auth.presentation.registration.RegisterScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "agenda" else "auth"
    ) {
        authGraph(navController)
        agendaGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "login",
        route = "auth"
    ) {
        composable("login") {
            LoginScreenRoot(
                onLoginSuccess = {
                    navController.navigate("agenda") {
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
        composable("register") {
            RegisterScreenRoot(
                onRegisterSuccess = {
                    fromRegisterToLogin(navController)
                },
                onBackClick = {
                    fromRegisterToLogin(navController)
                }
            )
        }
    }
}

fun fromRegisterToLogin(navController: NavHostController) {
    navController.navigate("login") {
        popUpTo("register") {
            inclusive = true
            saveState = true
        }
        restoreState = true
    }
}

private fun NavGraphBuilder.agendaGraph(navController: NavHostController) {
    navigation(
        startDestination = "agenda_list",
        route = "agenda"
    ) {
        composable("agenda_list") {
            AgendaScreenRoot(
                onLogoutSuccess = {
                    navController.navigate("auth") {
                        popUpTo("agenda") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}