package xyz.larkzhh.echomusic.core.player


sealed class PlayerStatus {
    object IDLE: PlayerStatus()
    object PREPARED: PlayerStatus()
    object STARTED: PlayerStatus()
    object PAUSED: PlayerStatus()
    object STOPPED: PlayerStatus()
    object COMPLETED: PlayerStatus()
    object ERROR: PlayerStatus()

}