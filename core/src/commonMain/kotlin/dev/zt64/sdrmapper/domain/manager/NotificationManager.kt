package dev.zt64.sdrmapper.domain.manager

expect class NotificationManager() {
    fun notifyInApp(message: String)

    fun notifySystem(message: String)
}