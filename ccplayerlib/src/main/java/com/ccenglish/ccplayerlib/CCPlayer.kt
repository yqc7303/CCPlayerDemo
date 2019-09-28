package com.ccenglish.ccplayerlib

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.widget.Toast
import com.bokecc.sdk.mobile.drm.DRMServer
import com.bokecc.sdk.mobile.play.DWMediaPlayer
import com.ccenglish.ccplayerlib.util.ConfigUtil

/**
 * Created by yangqc on 2019-09-27.
 * describe:
 */
class CCPlayer:MediaPlayer.OnPreparedListener{
    override fun onPrepared(p0: MediaPlayer?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var drmServer: DRMServer? = null

    private var drmServerPort: Int = 0

    private var context:Application? = null

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: CCPlayer? = null

        val instance: CCPlayer
            get() {
                if (INSTANCE == null) {
                    synchronized(CCPlayer::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = CCPlayer()
                        }
                    }
                }
                return INSTANCE!!
            }

    }

    /**
     * 使用默认的apikey、userId
     */
    fun register(context: Application){
        this.context = context
        startDRMServer()
    }

    fun register(context: Application, apiKey:String, userId:String){
        this.context = context
        startDRMServer()
    }

    fun unRegister(){
        if (drmServer != null) {
            drmServer!!.stop()
        }
    }


    // 启动DRMServer
    fun startDRMServer() {
        if (drmServer == null) {
            drmServer = DRMServer()
            drmServer!!.setRequestRetryCount(20)
        }

        try {
            drmServer!!.start()
            setDrmServerPort(drmServer!!.port)
        } catch (e: Exception) {
            Toast.makeText(context, "启动解密服务失败，请检查网络限制情况", Toast.LENGTH_LONG).show()
        }

    }


    fun getDrmServerPort(): Int {
        return drmServerPort
    }

    fun setDrmServerPort(drmServerPort: Int) {
        this.drmServerPort = drmServerPort
    }

    fun getDRMServer(): DRMServer? {
        return drmServer
    }


    fun getDwPlayer(dwPlayer: DwPlayerITF){
        val player = DWMediaPlayer()
        player.setVideoPlayInfo("", ConfigUtil.USERID, ConfigUtil.API_KEY, context)

        player.reset()
        player.prepareAsync()
        player.setOnPreparedListener {
            dwPlayer.getPlayer(it)
        }
    }

    interface DwPlayerITF{
        fun getPlayer(player:MediaPlayer)
    }

}

