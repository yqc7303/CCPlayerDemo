package com.ccenglish.ccplayerdemo

import android.media.MediaPlayer
import com.ccenglish.ccplayerlib.activity.DefaultPlayerActivity

/**
 * Created by yangqc on 2019-09-30.
 * describe:
 */
class ReusePlayerActivity : DefaultPlayerActivity() {

    //播放进度，一秒钟调用一次
    override fun onPlayProgress(position: Int, duration: Int) {
        super.onPlayProgress(position, duration)
        println("----> position:$position ,duration:$duration")
    }

    //播放完成
    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        super.onCompletion(mediaPlayer)
        println("----> play complete")
    }

    //准备就绪
    override fun onPrepared(mp: MediaPlayer?) {
        super.onPrepared(mp)
        println("----> play prepared")
    }

    //其他相关方法

}