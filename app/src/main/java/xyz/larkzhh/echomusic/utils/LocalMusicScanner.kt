package xyz.larkzhh.echomusic.utils

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import xyz.larkzhh.echomusic.model.Al
import xyz.larkzhh.echomusic.model.Ar
import xyz.larkzhh.echomusic.model.SongBean

/** 扫描本地音频 */
object LocalMusicScanner {

    private const val MIN_DURATION = 30_000L

    fun scan(context: Context): List<SongBean> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " +
            "${MediaStore.Audio.Media.DURATION} >= $MIN_DURATION"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"

        val songs = mutableListOf<SongBean>()
        runCatching {
            context.contentResolver.query(collection, projection, selection, null, sortOrder)
                ?.use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idIndex)
                        val albumId = cursor.getLong(albumIdIndex)
                        val uri = ContentUris.withAppendedId(collection, id)
                        songs.add(
                            SongBean(
                                id = id,
                                name = cursor.getString(titleIndex).orEmpty(),
                                al = Al(
                                    id = albumId,
                                    name = cursor.getString(albumIndex).orEmpty(),
                                    picUrl = "content://media/external/audio/albumart/$albumId",
                                ),
                                ar = listOf(Ar(id = 0, name = cursor.getString(artistIndex).orEmpty())),
                                localUri = uri.toString(),
                            )
                        )
                    }
                }
        }
        return songs
    }
}
