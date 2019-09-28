package com.ccenglish.ccplayerlib.activity;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.ccenglish.ccplayerlib.R;
import com.ccenglish.ccplayerlib.util.ConfigUtil;

/**
 * Created by yangqc on 2019-09-28.
 * describe:
 */
public class SimpleMediaPlayActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, DWMediaPlayer.OnPreparedListener {
    private TextureView tv_paly_video;
    private SurfaceTexture mTexture;
    private Surface surface;
    private DWMediaPlayer player;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.simple_media_play);

        tv_paly_video = findViewById(R.id.tv_paly_video);
        tv_paly_video.setSurfaceTextureListener(this);


        player = new DWMediaPlayer();
        Context context = getApplicationContext();


        String videoId = getIntent().getStringExtra("videoId");


        // 设置视频播放信息
        player.setVideoPlayInfo(videoId, ConfigUtil.USERID, ConfigUtil.API_KEY, context);
        mTexture = tv_paly_video.getSurfaceTexture();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        this.surface = new Surface(surfaceTexture);
        player.reset();
        player.prepareAsync();
        player.setOnPreparedListener(this);
        player.setSurface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
