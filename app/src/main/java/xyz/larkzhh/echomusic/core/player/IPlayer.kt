package xyz.larkzhh.echomusic.core.player

import xyz.larkzhh.echomusic.model.SongBean

interface IPlayer {
    fun setDataSource(songBean: SongBean)
    fun start()
    fun pause()
    fun resume()
    fun stop()
    fun seekTo(position: Int)
}