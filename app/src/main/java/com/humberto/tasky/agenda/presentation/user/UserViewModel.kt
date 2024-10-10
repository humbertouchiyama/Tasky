    package com.humberto.tasky.agenda.presentation.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.agenda.presentation.AgendaAction
import com.humberto.tasky.auth.domain.AuthRepository
import com.humberto.tasky.core.domain.repository.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
): ViewModel() {

    var state by mutableStateOf(UserState())
        private set

    init {
        buildUserInitials()
    }

    private fun buildUserInitials() {
        val fullName = sessionManager.get()?.fullName
        fullName?.let {
            state = state.copy(
                initials = fullName.toInitials()
            )
        }
    }

    fun onAction(action: AgendaAction) {

    }

    fun logout() {

    }
}