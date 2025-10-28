package com.eguerra.ciudadanodigital.helpers

object InternetStatusManager {
    private val listeners = mutableListOf<InternetStatusListener>()
    private var lastConnectionState: Boolean? = null

    fun addListener(listener: InternetStatusListener) {
        listeners.add(listener)
        lastConnectionState?.let { listener.onInternetStatusChanged(it) }
    }

    fun removeListener(listener: InternetStatusListener) {
        listeners.remove(listener)
    }

    fun notifyStatusChange(isConnected: Boolean) {
        if (lastConnectionState != isConnected) {
            lastConnectionState = isConnected
            listeners.forEach { it.onInternetStatusChanged(isConnected) }
        }
    }

    fun getLastConnectionState(): Boolean = lastConnectionState ?: false
}