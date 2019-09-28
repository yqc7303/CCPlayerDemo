package com.ccenglish.ccplayerdemo

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.ccenglish.ccplayerlib.CCPlayer

/**
 * Created by yangqc on 2019-09-29.
 * describe: 获取到CCVideo返回的MediaPlayer，自定义播放界面
 */
class DiyPlayerActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {
    private var mTexture: SurfaceTexture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diy_player)
    }


    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {


        CCPlayer.instance.getDwPlayer(object :CCPlayer.DwPlayerITF{
            override fun getPlayer(player: MediaPlayer) {
                player.setSurface(Surface(p0))
            }
        })
    }


}
