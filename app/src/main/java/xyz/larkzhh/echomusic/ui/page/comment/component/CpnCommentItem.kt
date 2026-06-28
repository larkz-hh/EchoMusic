package xyz.larkzhh.echomusic.ui.page.comment.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.core.CommentLiker
import xyz.larkzhh.echomusic.model.CommentBean
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.TimeUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.utils.showToast


@Composable
fun CpnCommentItem(comment: CommentBean,
                   isFloorComment: Boolean = false,
                   resourceId: String? = null,
                   commentType: Int = 0,
                   onFloorCommentClick: ((comment: CommentBean) -> Unit)? = null) {
    var liked by remember(comment.commentId) { mutableStateOf(comment.liked) }
    var likedCount by remember(comment.commentId) { mutableIntStateOf(comment.likedCount) }
    val scope = rememberCoroutineScope()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.cdp, end = 32.cdp, top = 24.cdp)
    ) {
        val (ivAvatar, tvName, tvTime, tvFabulousCount, icFabulous, tvContent, tvCommentFloor, divider) = createRefs()

        CommonNetworkImage(comment.user.avatarUrl,
            placeholder = R.drawable.ic_default_avator,
            error = R.drawable.ic_default_avator,
            modifier = Modifier
                .padding(end = 24.cdp)
                .size(80.cdp)
                .clip(CircleShape)
                .constrainAs(ivAvatar) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                })

        Text(
            text = comment.user.nickname,
            fontSize = 28.csp,
            color = AppColorsProvider.current.firstText,
            modifier = Modifier.constrainAs(tvName) {
                start.linkTo(ivAvatar.end)
                top.linkTo(ivAvatar.top)
                bottom.linkTo(tvTime.top)
            }
        )

        Text(
            text = TimeUtil.parse(comment.time),
            fontSize = 24.csp,
            color = AppColorsProvider.current.secondText,
            modifier = Modifier.constrainAs(tvTime) {
                start.linkTo(ivAvatar.end)
                top.linkTo(tvName.bottom)
                bottom.linkTo(ivAvatar.bottom)
            }
        )

        CommonIcon(resId = R.drawable.ic_like_no,
            tint = if (liked) Color(0xFFFF4D4F) else AppColorsProvider.current.thirdIcon,
            modifier = Modifier.size(32.cdp)
                .clickable(enabled = resourceId != null) {
                    val target = !liked
                    liked = target
                    likedCount = (likedCount + if (target) 1 else -1).coerceAtLeast(0)
                    comment.liked = liked
                    comment.likedCount = likedCount
                    scope.launch {
                        val success = CommentLiker.like(resourceId!!, commentType, comment.commentId, target)
                        if (!success) {
                            liked = !target
                            likedCount = (likedCount + if (target) -1 else 1).coerceAtLeast(0)
                            comment.liked = liked
                            comment.likedCount = likedCount
                            showToast("操作失败")
                        }
                    }
                }
                .constrainAs(icFabulous) {
                    end.linkTo(parent.end)
                    top.linkTo(ivAvatar.top)
                    bottom.linkTo(ivAvatar.bottom)
                })

        Text(
            text = likedCount.toString(),
            fontSize = 28.csp,
            color = AppColorsProvider.current.thirdText,
            modifier = Modifier
                .padding(end = 12.cdp)
                .constrainAs(tvFabulousCount) {
                    end.linkTo(icFabulous.start)
                    top.linkTo(ivAvatar.top)
                    bottom.linkTo(ivAvatar.bottom)
                }
        )

        Text(
            text = comment.content,
            fontSize = 32.csp,
            color = AppColorsProvider.current.firstText,
            modifier = Modifier
                .padding(top = 20.cdp, bottom = if (isFloorComment && (comment.showFloorComment?.replyCount
                        ?: 0) > 0
                ) 0.cdp else 24.cdp)
                .constrainAs(tvContent) {
                    start.linkTo(ivAvatar.end)
                    top.linkTo(ivAvatar.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        if (!isFloorComment && (comment.showFloorComment?.replyCount ?: 0) > 0) {
            comment.showFloorComment?.let {
                Text(
                    text = "${it.replyCount}条回复 >",
                    fontSize = 24.csp,
                    color = AppColorsProvider.current.secondText,
                    modifier = Modifier
                        .constrainAs(tvCommentFloor) {
                            start.linkTo(ivAvatar.end)
                            top.linkTo(tvContent.bottom)
                        }
                        .padding(vertical = 14.cdp)
                        .clickable {
                            onFloorCommentClick?.invoke(comment)
                        }
                        .padding(10.cdp)
                )
            }
        }

        Divider(
            modifier = Modifier
                .constrainAs(divider) {
                    start.linkTo(ivAvatar.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            color = AppColorsProvider.current.divider,
            thickness = 1.5.cdp
        )
    }
}