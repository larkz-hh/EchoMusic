package xyz.larkzhh.echomusic.core

import xyz.larkzhh.echomusic.hilt.entrypoint.EntryPointFinder

/** 评论点赞 */
object CommentLiker {

    suspend fun like(resourceId: String, type: Int, commentId: Long, like: Boolean): Boolean {
        val result = runCatching {
            EntryPointFinder.getEchoApi().likeComment(
                id = resourceId,
                commentId = commentId,
                like = if (like) 1 else 0,
                type = type,
            )
        }
        return result.getOrNull()?.resultOk() == true
    }
}
