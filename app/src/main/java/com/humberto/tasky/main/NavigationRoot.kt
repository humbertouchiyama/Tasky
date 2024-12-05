package com.humberto.tasky.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsScreenRoot
import com.humberto.tasky.agenda.presentation.agenda_list.AgendaScreenRoot
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenRoot
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import com.humberto.tasky.auth.presentation.login.LoginScreenRoot
import com.humberto.tasky.auth.presentation.registration.RegisterScreenRoot
import com.humberto.tasky.main.navigation.AgendaDetails
import com.humberto.tasky.main.navigation.AgendaList
import com.humberto.tasky.main.navigation.EditTextArgs
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
                    popUpTo(Login) {
                        inclusive = true
                    }
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
        val selectedDateEpochDay: Long? = it.savedStateHandle["selectedDateEpochDay"]
        AgendaScreenRoot(
            onLogoutSuccess = {
                navController.navigate(Login) {
                    popUpTo(AgendaList) {
                        inclusive = true
                    }
                }
            },
            onGoToAgendaDetailsClick = { agendaDetails ->
                navController.navigate(
                    route = agendaDetails,
                ) {
                    popUpTo(AgendaList)
                }
            },
            selectedDateEpochDay = selectedDateEpochDay
        )
    }
    composable<AgendaDetails>(
        deepLinks = listOf(
            navDeepLink<AgendaDetails>(
                basePath = "tasky://agenda_item",
            )
        )
    ) { backStackEntry ->
        val agendaDetails: AgendaDetails = backStackEntry.toRoute<AgendaDetails>()
        val editTextScreenType: EditTextScreenType? = backStackEntry.savedStateHandle["editTextScreenType"]
        val textToBeUpdated: String? = backStackEntry.savedStateHandle["textToBeUpdated"]
        val editTextArgs = if (editTextScreenType != null && textToBeUpdated != null) {
            EditTextArgs(editTextScreenType, textToBeUpdated)
        } else null
        AgendaDetailsScreenRoot(
            onBackClick = { selectedDateEpochDay ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selectedDateEpochDay", selectedDateEpochDay ?: agendaDetails.selectedDateEpochDay )
                navController.popBackStack()
            },
            onEditTextClick = { onClickEditTextArgs ->
                navController.navigate(
                    route = onClickEditTextArgs,
                ) {
                    popUpTo(agendaDetails)
                }
            },
            editTextArgs = editTextArgs
        )
    }
    composable<EditTextArgs> {
        EditTextScreenRoot(
            onGoBack = {
                navController.popBackStack()
            },
            onSaveClick = { editTextArgs ->
                navController.previousBackStackEntry
                    ?.savedStateHandle?.apply {
                        set("editTextScreenType", editTextArgs.editTextScreenType)
                        set("textToBeUpdated", editTextArgs.textToBeUpdated)
                    }
                navController.popBackStack()
            }
        )
    }
}