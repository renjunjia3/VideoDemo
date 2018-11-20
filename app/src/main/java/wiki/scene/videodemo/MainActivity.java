package wiki.scene.videodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.danikula.videocache.HttpProxyCacheServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.play_video).setOnClickListener(view -> {
            HttpProxyCacheServer proxy = App.getProxy(MainActivity.this);
            String proxyUrl = proxy.getProxyUrl("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");
            VideoPlayerActivity.openActivity(MainActivity.this, proxyUrl, "测试");
        });
    }

}
