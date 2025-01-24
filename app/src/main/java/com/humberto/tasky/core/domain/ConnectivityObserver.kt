package com.humberto.tasky.core.domain

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun startObserving(): Flow<ConnectivityStatus>

    enum class ConnectivityStatus {
        Available, Unavailable, Lost;

        fun isConnected() = this == Available
    }
}