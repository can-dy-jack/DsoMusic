package com.dirror.music.ui.dialog

import android.app.Activity
import android.content.Context
import com.dirror.music.MyApplication
import com.dirror.music.data.PLAYLIST_TAG_MY_FAVORITE
import com.dirror.music.data.PLAYLIST_TAG_NORMAL
import com.dirror.music.databinding.DialogSongMenuBinding
import com.dirror.music.music.local.MyFavorite
import com.dirror.music.music.standard.data.SOURCE_NETEASE
import com.dirror.music.music.standard.data.SOURCE_QQ
import com.dirror.music.music.standard.data.StandardSongData
import com.dirror.music.ui.base.BaseBottomSheetDialog
import com.dirror.music.util.BroadcastUtil
import com.dirror.music.util.toast

/**
 * 每个歌曲右边三个点点击后显示
 */
class SongMenuDialog
@JvmOverloads
constructor(
    context: Context,
    private val activity: Activity,
    private val songData: StandardSongData,
    private val tag: Int = PLAYLIST_TAG_NORMAL
) : BaseBottomSheetDialog(context) {

    companion object {
        const val BROADCAST_UPDATE_PLAYLIST = "com.dirror.music.UPDATE_PLAYLIST"
    }

    private var binding = DialogSongMenuBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)
    }

    override fun initListener() {
        super.initListener()
        binding.apply {
            // 下一首播放
            itemNextPlay.setOnClickListener {
                MyApplication.musicController.value?.addToNextPlay(songData)
                toast("成功添加到下一首播放")
                dismiss()
            }
            // 添加到本地我喜欢
            itemAddLocalMyFavorite.setOnClickListener {
                MyFavorite.addSong(songData)
                dismiss()
            }
            // 添加到网易云我喜欢
            itemAddNeteaseFavorite.setOnClickListener {
                if (MyApplication.userManager.getCloudMusicCookie().isEmpty()) {
                    toast("离线模式无法收藏到在线我喜欢~")
                } else {
                    when (songData.source) {
                        SOURCE_NETEASE -> {
                            MyApplication.cloudMusicManager.likeSong(songData.id, {
                                toast("添加到我喜欢成功")
                            }, {
                                toast("添加到我喜欢失败")
                            })
                        }
                        SOURCE_QQ -> {
                            toast("暂不支持此音源")
                        }
                    }
                }
            }
            // 歌曲信息
            itemSongInfo.setOnClickListener {
                // toast("歌曲信息 ${ songData.id }")
                SongInfoDialog(context, songData).show()
                // 自己消失
                dismiss()
            }
            // 歌曲评论
            itemSongComment.setOnClickListener {
                MyApplication.activityManager.startCommentActivity(activity, songData.source, songData.id)
                dismiss()
            }
            // 歌曲删除
            itemDeleteSong.setOnClickListener {
                if (tag == PLAYLIST_TAG_MY_FAVORITE) {
                    MyFavorite.deleteById(songData.id)
                    toast("删除成功")
                    BroadcastUtil.send(this@SongMenuDialog.context, BROADCAST_UPDATE_PLAYLIST)
                    dismiss()
                } else {
                    toast("暂不支持删除")
                }
            }
        }
    }

}