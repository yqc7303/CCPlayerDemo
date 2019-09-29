package com.ccenglish.ccplayerlib

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.widget.Toast
import com.bokecc.sdk.mobile.drm.DRMServer
import com.bokecc.sdk.mobile.play.DWMediaPlayer
import com.ccenglish.ccplayerlib.util.ConfigUtil

/**
 * Created by yangqc on 2019-09-28.
 * describe:
 */
class CCPlayer{

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


    /**
     * 获得MediaPlayer
     */
    fun diyPlayer(userId: String,callBack: CallBack){
        val player = DWMediaPlayer()
        player.reset()
        //设置MediaPlayer的DRM服务端口号
        player.setDRMServerPort(getDrmServerPort())

        player.setVideoPlayInfo(userId, ConfigUtil.USERID, ConfigUtil.API_KEY, "",context)


        //prepareAsync()前，调用DRMServer的reset()方法重置drmserver的设置。
        drmServer?.reset()
        player.prepareAsync()


        //设置各种监听回调
        player.setOnErrorListener{mp,what,extra->
            callBack.onError(mp,what,extra)
            true
        }

        player.setOnDreamWinErrorListener{
            val error = when(it.errorCode.Value()){
                -10 -> "NETWORK_ERROR"
                -11 -> "PROCESS_FAIL"
                -12 -> "INVALID_REQUEST"
                -13 -> "VERIFY_FAIL"
                else -> "UNKNOW"
            }

            callBack.onPlayError(error,it.message?:"",it.detailMessage)
        }

        player.setOnInfoListener{mp,what,extra->
            callBack.onInfo(mp,what,extra)
            false
        }

        player.setOnBufferingUpdateListener{mp,precent->
            callBack.onBufferingUpdate(mp,precent)
        }

        player.setOnPreparedListener {
            callBack.onPrepared(it)
        }
    }

    interface CallBack{
        fun getPlayer(player:MediaPlayer)

        /**
         * @what 可以表示player加载状态等，DWMediaPlayer.MEDIA_INFO_BUFFERING_START、END。可用于加载时一些业务处理，如进度条
         * 获取额外参数信息
         */
        fun onInfo(mp :MediaPlayer , what:Int, extra:Int)


        /**
         * 业务相关错误，具体错误码参看文档
         */
        fun onPlayError(errorCode: String,errorMessage:String,detailMessage:String)

        /**
         * 错误信息
         */
        fun onError(mp:MediaPlayer,what:Int,extra:Int)

        /**
         * 缓冲相关回调，可用于第二进度设置
         */
        fun onBufferingUpdate(mp:MediaPlayer,percent:Int)

        /**
         * 重要！
         * 已就绪状态，可以拿到返回的MediaPlayer用户自定义操作
         */
        fun onPrepared(mp:MediaPlayer)

    }

}

