package com.ccenglish.ccplayerlib.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.ccenglish.ccplayerlib.CCPlayer;
import com.ccenglish.ccplayerlib.R;
import com.ccenglish.ccplayerlib.util.ConfigUtil;
import com.ccenglish.ccplayerlib.util.MediaUtil;
import com.ccenglish.ccplayerlib.util.ParamsUtil;
import com.ccenglish.ccplayerlib.widget.VerticalSeekBar;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by yangqc on 2019-09-29.
 * 默认视频播放界面
 */
public class DefaultPlayerActivity extends Activity implements
        DWMediaPlayer.OnBufferingUpdateListener,
        DWMediaPlayer.OnInfoListener,
        DWMediaPlayer.OnPreparedListener, DWMediaPlayer.OnErrorListener,
        SurfaceHolder.Callback, MediaPlayer.OnCompletionListener {

    private DWMediaPlayer player;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ProgressBar bufferProgressBar;
    private SeekBar skbProgress;
    private ImageView playOp, backPlayList;
    private TextView videoIdText, playDuration, videoDuration;
    private LinearLayout playerTopLayout, volumeLayout;
    private RelativeLayout playerBottomLayout;
    private AudioManager audioManager;
    private VerticalSeekBar volumeSeekBar;
    private int currentVolume;
    private int maxVolume;

    private boolean isLocalPlay;
    private boolean isPrepared;
    private Map<String, Integer> definitionMap;

    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    //标志经过onPause生命周期时，视频当前的播放状态
    private Boolean isPlaying;
    //当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = false;
    int currentPosition;
    String path;

    private CCPlayer ccplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ccplayer = CCPlayer.Companion.getInstance();
        // 隐藏标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_default_player);

        initView();

        initPlayHander();

        initPlayInfo();

        super.onCreate(savedInstanceState);
    }

    public static void startAction(Context context, String videoId) {
        Intent intent = new Intent(context, DefaultPlayerActivity.class);
        intent.putExtra("videoId", videoId);
        context.startActivity(intent);
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.playerSurfaceView);
        playOp = (ImageView) findViewById(R.id.btnPlay);
        backPlayList = (ImageView) findViewById(R.id.backPlayList);
        bufferProgressBar = (ProgressBar) findViewById(R.id.bufferProgressBar);

        videoIdText = (TextView) findViewById(R.id.videoIdText);
        playDuration = (TextView) findViewById(R.id.playDuration);
        videoDuration = (TextView) findViewById(R.id.videoDuration);
        playDuration.setText(ParamsUtil.millsecondsToStr(0));
        videoDuration.setText(ParamsUtil.millsecondsToStr(0));

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar = (VerticalSeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setThumbOffset(2);

        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        volumeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);

        playerTopLayout = (LinearLayout) findViewById(R.id.playerTopLayout);
        volumeLayout = (LinearLayout) findViewById(R.id.volumeLayout);
        playerBottomLayout = (RelativeLayout) findViewById(R.id.playerBottomLayout);

        playOp.setOnClickListener(onClickListener);
        backPlayList.setOnClickListener(onClickListener);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(this);
        surfaceView.setOnTouchListener(touchListener);

    }

    @SuppressLint("HandlerLeak")
    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }

                if (!isPrepared) {
                    return;
                }

                // 更新播放进度
                int position = player.getCurrentPosition();
                int duration = player.getDuration();
                //更新播放进度
                currentPosition = position;

                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    playDuration.setText(ParamsUtil.millsecondsToStr(player.getCurrentPosition()));
                    skbProgress.setProgress((int) pos);
                }
                onPlayProgress(position, duration);
            }

            ;
        };

        // 通过定时器和Handler来更新进度
        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!isPrepared) {
                    return;
                }

                playerHandler.sendEmptyMessage(0);
            }
        };

    }

    private void initPlayInfo() {
        timer.schedule(timerTask, 0, 1000);
        isPrepared = false;
        player = new DWMediaPlayer();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        player.reset();

        Intent intent = getIntent();
        String videoId = intent.getStringExtra("videoId");
        String verificationCode = getIntent().getStringExtra("verifyCode");
        videoIdText.setText(videoId);

        isLocalPlay = intent.getBooleanExtra("isLocalPlay", false);

        // DRM加密播放
        player.setDRMServerPort(ccplayer.getDrmServerPort());

        try {

            if (!isLocalPlay) {// 播放线上视频

                player.setVideoPlayInfo(videoId, ccplayer.getUserId(), ccplayer.getApiKey(), verificationCode, this);

            } else {// 播放本地已下载视频
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                    path = Environment.getExternalStorageDirectory() + "/".
                            concat(ConfigUtil.DOWNLOAD_DIR).concat("/").concat(videoId).concat(MediaUtil.PCM_FILE_SUFFIX);

                    if (!new File(path).exists()) {
                        finish();
                    }
                }
            }

        } catch (IllegalArgumentException e) {
            Log.e("player error", e.getMessage());
        } catch (SecurityException e) {
            Log.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("player error", "illegal", e);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setDisplay(surfaceHolder);

            player.setOnInfoListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);

            if (isLocalPlay) {
                player.setOfflineVideoPath(path, this);
            }
            ccplayer.getDRMServer().reset();
            player.prepareAsync();

        } catch (Exception e) {
            Log.e("videoPlayer", "error", e);
        }
        Log.i("videoPlayer", "surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//		holder.setFixedSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player == null) {
            return;
        }
        if (isPrepared) {
            currentPosition = (int) player.getCurrentPosition();
        }

        isPrepared = false;

        player.stop();
        player.reset();

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;

        if (!isFreeze) {
            player.start();
            player.setScreenOnWhilePlaying(true);
        }

        if (isPlaying != null && !isPlaying.booleanValue()) {
            player.pause();
        }

        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }

        definitionMap = player.getDefinitions();

        bufferProgressBar.setVisibility(View.GONE);

        surfaceHolder.setFixedSize(player.getVideoWidth(), player.getVideoHeight());
        videoDuration.setText(ParamsUtil.millsecondsToStr((int) player.getDuration()));
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        skbProgress.setSecondaryProgress(percent);
    }

    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btnPlay) {
                if (!isPrepared) {
                    return;
                }

                if (player.isPlaying()) {
                    player.pause();
                    playOp.setImageResource(R.drawable.smallbegin_ic);

                } else {
                    player.start();
                    playOp.setImageResource(R.drawable.smallstop_ic);
                }
            } else if (id == R.id.backPlayList) {
                finish();
            }
        }
    };

    OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.seekTo(progress);
            currentPosition = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress * (int) player.getDuration() / skbProgress.getMax();
        }
    };

    VerticalSeekBar.OnSeekBarChangeListener seekBarChangeListener = new VerticalSeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            currentVolume = progress;
            volumeSeekBar.setProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    //控制播放器面板显示
    private boolean isDisplay = false;
    private OnTouchListener touchListener = new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            if (!isPrepared) {
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isDisplay) {
                    setLayoutVisibility(View.GONE, false);
                } else {
                    setLayoutVisibility(View.VISIBLE, true);
                }
            }
            return false;
        }
    };


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 监测音量变化
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {


            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume != volume) {
                currentVolume = volume;
                volumeSeekBar.setProgress(currentVolume);
            }

            if (isPrepared) {
                setLayoutVisibility(View.VISIBLE, true);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void setLayoutVisibility(int visibility, boolean isDisplay) {
        if (player == null) {
            return;
        }

        if (player.getDuration() <= 0) {
            return;
        }

        this.isDisplay = isDisplay;
        playerTopLayout.setVisibility(visibility);
        playerBottomLayout.setVisibility(visibility);
        volumeLayout.setVisibility(visibility);
    }

    @Override
    public void onResume() {
        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                player.start();
            }
        } else {
            if (isPlaying != null && isPlaying.booleanValue() && isPrepared) {
                player.start();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (isPrepared) {
            //如果播放器prepare完成，则对播放器进行暂停操作，并记录状态
            if (player.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
            player.pause();
        } else {
            //如果播放器没有prepare完成，则设置isFreeze为true
            isFreeze = true;
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        timerTask.cancel();
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
        ccplayer.getDRMServer().disconnectCurrentStream();
        super.onDestroy();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //这两行代码实现出错时续播功能
        player.seekTo(currentPosition);
        player.start();

        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (player.isPlaying()) {
                    bufferProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_END:
                bufferProgressBar.setVisibility(View.GONE);
                break;

        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
    }

    /**
     * 供子类复写调用
     */
    public void onPlayProgress(int position, int duration) {

    }

    /**
     * isPrepared、isPlaying暴露给子类
     */
    public boolean isPrepared() {
        return isPrepared;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }


}
