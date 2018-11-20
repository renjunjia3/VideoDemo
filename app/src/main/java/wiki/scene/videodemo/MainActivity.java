package wiki.scene.videodemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.danikula.videocache.HttpProxyCacheServer;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MainActivity extends AppCompatActivity {
    String proxyUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpProxyCacheServer proxy = App.getProxy(MainActivity.this);
        proxyUrl = proxy.getProxyUrl("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");

        findViewById(R.id.play_video).setOnClickListener(view -> {
            VideoPlayerActivity.openActivity(MainActivity.this, proxyUrl, "测试");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                JzvdStd.SAVE_PROGRESS = false;
                JzvdStd.WIFI_TIP_DIALOG_SHOWED = true;
                JzvdStd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                JzvdStd.startFullscreen(MainActivity.this, JzvdStd.class, proxyUrl, "测试");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
