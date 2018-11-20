package wiki.scene.videodemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayerActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    @BindView(R.id.textureView)
    TextureView textureView;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.btn_open_cdn)
    Button btnOpenCdn;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.current)
    TextView current;
    @BindView(R.id.bottom_seek_progress)
    SeekBar bottomSeekProgress;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.fullscreen)
    ImageView fullscreen;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.iv_start)
    ImageView ivStart;
    @BindView(R.id.iv_pause)
    ImageView ivPause;
    @BindView(R.id.retry_btn)
    TextView retryBtn;
    @BindView(R.id.retry_layout)
    LinearLayout retryLayout;

    private MediaPlayer mediaPlayer;
    private Surface surface;

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_URL = "arg_url";

    private String title;
    private String url;

    public static void openActivity(Activity activity, String url, String title) {
        Intent intent = new Intent(activity, VideoPlayerActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_URL, url);
        activity.startActivityForResult(intent, 1001);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra(ARG_TITLE);
        url = getIntent().getStringExtra(ARG_URL);

        tvTitle.setText(title);

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnOpenCdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        initVideo();
        textureView.setOnClickListener(view -> {
            if (layoutTop.getVisibility() == View.VISIBLE) {
                layoutTop.setVisibility(View.GONE);
            } else {
                layoutTop.setVisibility(View.VISIBLE);
            }
            if (layoutBottom.getVisibility() == View.VISIBLE) {
                layoutBottom.setVisibility(View.GONE);
            } else {
                layoutBottom.setVisibility(View.VISIBLE);
            }

            if (ivPause.getVisibility() == View.VISIBLE) {

            } else {

            }
        });
        bottomSeekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
                        current.setText(mathTime((int) (i / 100f * mediaPlayer.getDuration())));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    loading.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mediaPlayer.seekTo((int) (seekBar.getProgress() / 100f * mediaPlayer.getDuration()), MediaPlayer.SEEK_CLOSEST);
                    } else {
                        mediaPlayer.seekTo((int) (seekBar.getProgress() / 100f * mediaPlayer.getDuration()));
                    }
                }
            }
        });

        RxTimerUtil.interval(1000, number -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                current.setText(mathTime(mediaPlayer.getCurrentPosition()));
                bottomSeekProgress.setProgress(mathProgress(mediaPlayer.getCurrentPosition()));
                if (mediaPlayer.getCurrentPosition() > 5000) {
                    mediaPlayer.pause();
                    loading.setVisibility(View.VISIBLE);
                    btnOpenCdn.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * 初始化播放器面板
     */
    private void initVideo() {
        showLoading();
        textureView.setSurfaceTextureListener(this);
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
        layoutBottom.setVisibility(View.INVISIBLE);
        layoutTop.setVisibility(View.INVISIBLE);
    }


    @SuppressLint("SetTextI18n")
    private void initMediaPlayer(SurfaceTexture surfaceTexture) {
        try {
            surface = new Surface(surfaceTexture);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnPreparedListener(mp -> {
                        mediaPlayer.start();
                        loading.setVisibility(View.GONE);
                        total.setText(mathTime(mediaPlayer.getDuration()));
                        current.setText("00:00");
                    }
            );
            mediaPlayer.setOnCompletionListener(mp -> {
                onBackPressed();
            });
            mediaPlayer.setOnBufferingUpdateListener((mp, i) -> {
                current.setText(mathTime(mp.getCurrentPosition()));
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });

            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mediaPlayer) {
                    loading.setVisibility(View.INVISIBLE);
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }
                }
            });

            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        initMediaPlayer(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surfaceTexture = null;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

    }


    private String mathTime(int mills) {
        mills /= 1000;
        int minutes = mills / 60;
        int seconds = mills % 60;
        String result;
        if (minutes >= 10) {
            result = String.valueOf(minutes) + ":";
        } else {
            result = "0" + minutes + ":";
        }

        if (seconds >= 10) {
            return result + seconds;
        } else {
            return result + "0" + seconds;
        }
    }

    private int mathProgress(int mills) {
        if (mediaPlayer != null && mediaPlayer.getDuration() > 0) {
            return (int) (mills * 100f / mediaPlayer.getDuration());
        } else {
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        RxTimerUtil.cancel();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
        super.onBackPressed();
    }
}
