package com.ccenglish.ccplayerdemo

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.ccenglish.ccplayerlib.CCPlayer
import kotlinx.android.synthetic.main.activity_diy_player.*

/**
 * Created by yangqc on 2019-09-29.
 * describe: 获取到CCVideo返回的MediaPlayer，自定义播放界面
 */
class DiyPlayerActivity2 : AppCompatActivity(),TextureView.SurfaceTextureListener{
    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {



        val videoId = intent.getStringExtra("videoId")
        CCPlayer.instance.diyPlayer(videoId,object :CCPlayer.CallBack{
            override fun getPlayer(player: MediaPlayer) {
            }

            override fun onInfo(mp: MediaPlayer, what: Int, extra: Int) {
            }

            override fun onPlayError(
                errorCode: String,
                errorMessage: String,
                detailMessage: String
            ) {
            }

            override fun onError(mp: MediaPlayer, what: Int, extra: Int) {
            }

            override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
            }

            override fun onPrepared(mp: MediaPlayer) {
                mp.setSurface(Surface(p0))
                mp.start()
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diy_player)

        tv_paly_video.surfaceTextureListener = this
    }

}
