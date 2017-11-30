package tsou.cn.mysmalltest;

import android.app.Application;

import net.wequick.small.Small;

/**
 * Created by Administrator on 2017/11/27 0027.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Small.preSetUp(this);
    }
}
