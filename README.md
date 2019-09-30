# CCPlayer
基于CC云视频最新drmSDK封装，用于以DRM方式播放视频

##### 初始化
1. 在application 的oncreat方法中添加
```
        CCPlayer.instance.register(this)
```
2. 在application 的onTerminate方法中添加
```
        CCPlayer.instance.unRegister()
```
##### 播放使用
1. 默认的简单播放

适用：于只需要播放视频即可没有其他的业务要求

使用：直接跳转DefaultPlayerActivity,并传递要播放视频的videoId
```
        DefaultPlayerActivity.startAction(this,"83CFD26BAB4E1FE49C33DC5901307461")
```

2. 复用播放

适用：对播放样式无要求，但有其他的相关业务要求。播放完成、播放一定进度下的操作等。如：播放80%点亮能量块。

使用：创建一个新的类，继承DefaultPlayerActivity。重写父类方法，获取相关状态。
```
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
```


3. 自定义播放界面播放

适用：默认播放界面无法满足要求需自定义的

使用：1. 自定义的播放的Activity实现CCPlayer.CallBack接口，在合适的位置
(如surface的surfaceCreated方法)调用CCPlayer.diyPlayer，
在onPrepared方法中获得到Android传统的MediaPlayer进行后续自定义工作
页面销毁是调用CCPlayer.clearPlayer方法释放对象

```
        CCPlayer.instance.diyPlayer(videoId,this)
        CCPlayer.instance.clearPlayer()//退出时清除
```
CCPlayer.CallBack接口中的方法
```
    /**
     * 获取DWMediaPlayer的相关回调
     */
    interface CallBack{

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
```


##### 待补充
1. 列表播放
2. 本地播放（V6用户要实现DWSdkStorage接口）
3. 上传（巡课）
4. 其他业务补充
5. 私有云相关


##### 关于CC云视频

> https://github.com/CCVideo/VOD_Android_DRM_SDK/wiki






