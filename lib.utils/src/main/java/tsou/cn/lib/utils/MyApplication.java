package tsou.cn.lib.utils;

import android.app.Application;

/**
 * Created by Administrator on 2017/11/27 0027.
 */

public class MyApplication extends Application {
    /**
     * 上下文
     */
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
