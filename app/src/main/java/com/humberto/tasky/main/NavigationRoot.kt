package com.humberto.tasky.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsScreenRoot
import com.humberto.tasky.agenda.presentation.agenda_list.AgendaScreenRoot
import com.humberto.tasky.auth.presentation.login.LoginScreenRoot
import com.humberto.tasky.auth.presentation.registration.RegisterScreenRoot
import com.humberto.tasky.main.navigation.AgendaDetails
import com.humberto.tasky.main.navigation.AgendaList
import com.humberto.tasky.main.navigation.Login
import com.humberto.tasky.main.navigation.Register

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) AgendaList else Login
    ) {
        authGraph(navController)
        agendaGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable<Login> {
        LoginScreenRoot(
            onLoginSuccess = {
                navController.navigate(AgendaList) {
                    popUpTo(Login)
                }
            },
            onSignUpClick = {
                navController.navigate(Register) {
                    popUpTo(Login) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        )
    }
    composable<Register> {
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

fun fromRegisterToLogin(navController: NavHostController) {
    navController.navigate(Login) {
        popUpTo(Register) {
            inclusive = true
            saveState = true
        }
        restoreState = true
    }
}

private fun NavGraphBuilder.agendaGraph(navController: NavHostController) {
    composable<AgendaList> {
        AgendaScreenRoot(
            onLogoutSuccess = {
                navController.navigate(Login) {
                    popUpTo(AgendaList) {
                        inclusive = true
                    }
                }
            },
            onNewAgendaItemClick = { agendaDetails ->
                navController.navigate(
                    route = agendaDetails,
                ) {
                    popUpTo(AgendaList)
                }
            }
        )
    }
    composable<AgendaDetails> { backStackEntry ->
        val agendaDetails: AgendaDetails = backStackEntry.toRoute<AgendaDetails>()
        AgendaDetailsScreenRoot(agendaDetails)
    }
}