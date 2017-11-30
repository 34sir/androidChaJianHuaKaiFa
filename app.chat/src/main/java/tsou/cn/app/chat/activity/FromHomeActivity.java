package tsou.cn.app.chat.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.wequick.small.Small;

import org.simple.eventbus.EventBus;

import tsou.cn.app.chat.R;
import tsou.cn.lib.data.EvenBusTag;
import tsou.cn.lib.utils.UIUtils;

public class FromHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextview;
    private String name;
    private String age;
    /**
     * eventBus返回数据
     */
    private Button mBtnFackEventbus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_home);
        initData();
        initView();
    }

    private void initData() {
        Uri uri = Small.getUri(this);
        if (uri != null) {
            name = uri.getQueryParameter("name");
            age = uri.getQueryParameter("age");
        }
    }

    private void initView() {
        mTextview = (TextView) findViewById(R.id.textview);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)) {
            mTextview.setText("name=" + name + ",age=" + age);
//            UIUtils.showToast("name=" + name + ",age=" + age);
        }


        mBtnFackEventbus = (Button) findViewById(R.id.btn_fack_eventbus);
        mBtnFackEventbus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_fack_eventbus:
                EventBus.getDefault().post("eventBus从FromHomeActivity中返回数据了", EvenBusTag.EVENT_GET_DATA);
                finish();
                break;
        }
    }
}
