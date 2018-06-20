package tsou.cn.app.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.wequick.small.Small;

import org.json.JSONArray;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tsou.cn.lib.data.EvenBusTag;
import tsou.cn.lib.utils.ThreadUtils;
import tsou.cn.lib.utils.UIUtils;


public class MainFragment extends Fragment implements View.OnClickListener {
    /**
     * 跳转到Chat模块主页
     */
    private Button mBtnGoChat;
    /**
     * 不带参数跳转到Chat模块指定Activity
     */
    private Button mBtnGoChatActivity;
    /**
     * 带参数跳转到Chat模块指定Activity
     */
    private Button mBtnHavestGoChatActivity;
    private View view;
    /**
     * small支持本地网页组件
     */
    private Button mBtnOpenUrl;
    /**
     * 使用eventBus数据传输
     */
    private Button mBtnEventBus;
    /**
     * 更新插件
     */
    private Button mBtnUpdataSmall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_home, null);
        initView(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        ThreadUtils.cancelLongBackThread();
        super.onDestroyView();
    }

    private void initView(View view) {
        mBtnGoChat = (Button) view.findViewById(R.id.btn_go_chat);
        mBtnGoChat.setOnClickListener(this);
        mBtnGoChatActivity = (Button) view.findViewById(R.id.btn_go_chat_activity);
        mBtnGoChatActivity.setOnClickListener(this);
        mBtnHavestGoChatActivity = (Button) view.findViewById(R.id.btn_havest_go_chat_activity);
        mBtnHavestGoChatActivity.setOnClickListener(this);
        mBtnOpenUrl = (Button) view.findViewById(R.id.btn_open_url);
        mBtnOpenUrl.setOnClickListener(this);
        mBtnEventBus = (Button) view.findViewById(R.id.btn_event_bus);
        mBtnEventBus.setOnClickListener(this);
        mBtnUpdataSmall = (Button) view.findViewById(R.id.btn_updata_small);
        mBtnUpdataSmall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_go_chat://跳转到Chat模块主页
                Small.setUp(getContext(), new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("chat", getContext());
                    }
                });
                break;
            case R.id.btn_go_chat_activity://不带参数跳转到Chat模块指定Activity
                Small.setUp(getContext(), new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        /**
                         * Small.openUri("chat", getContext());
                         * 直接跳转chat模块的主页。
                         *
                         *   Small.openUri("chat/FromHomeActivity", getContext());
                         *   跳转指定页面
                         */
                        Small.openUri("chat/FromHomeActivity", getContext());
                    }
                });
                break;
            case R.id.btn_havest_go_chat_activity://带参数跳转到Chat模块指定Activity
                Small.setUp(getContext(), new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("chat/FromHomeActivity?name=huangxiaoguo&age=25", getContext());
                    }
                });
                break;
            case R.id.btn_open_url://small支持本地网页组件
//                Small.openUri("http://www.baidu.com", getContext());
                Small.openUri("https://github.com/wequick/Small/issues", getContext());
                break;
            case R.id.btn_event_bus://使用eventBus数据传输
                Small.setUp(getContext(), new Small.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Small.openUri("chat/FromHomeActivity", getContext());
                    }
                });
                break;
            case R.id.btn_updata_small://更新插件
                checkUpgrade();
                break;
        }
    }

    @Subscriber(tag = EvenBusTag.EVENT_GET_DATA)
    public void onEvent(String s) {
        UIUtils.showToast(s);
    }

    /**
     * 插件化更新
     */
    private void checkUpgrade() {
        new UpgradeManager(getContext()).checkUpgrade();
    }

    private static class UpgradeManager {

        private static class UpdateInfo {
            public String packageName;
            public String downloadUrl;
        }

        private static class UpgradeInfo {
            public JSONObject manifest;
            public List<UpdateInfo> updates;
        }

        private interface OnResponseListener {
            void onResponse(UpgradeInfo info);
        }

        private interface OnUpgradeListener {
            void onUpgrade(boolean succeed);
        }

        private static class ResponseHandler extends Handler {
            private OnResponseListener mListener;

            public ResponseHandler(OnResponseListener listener) {
                mListener = listener;
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mListener.onResponse((UpgradeInfo) msg.obj);
                        break;
                }
            }
        }

        private ResponseHandler mResponseHandler;

        private Context mContext;
        private ProgressDialog mProgressDlg;

        public UpgradeManager(Context context) {
            mContext = context;
        }

        public void checkUpgrade() {
            mProgressDlg = ProgressDialog.show(mContext, "Small", "检查更新...", false, true);
            requestUpgradeInfo(Small.getBundleVersions(), new OnResponseListener() {
                @Override
                public void onResponse(UpgradeInfo info) {
                    mProgressDlg.setMessage("升级中...");
                    upgradeBundles(info,
                            new OnUpgradeListener() {
                                @Override
                                public void onUpgrade(boolean succeed) {
                                    mProgressDlg.dismiss();
                                    mProgressDlg = null;
                                    String text = succeed ?
                                            "升级成功!切换到后台并返回到前台来查看更改"
                                            : "升级失败!";
                                    UIUtils.showToast(text);
                                }
                            });
                }
            });
        }

        /**
         * @param versions
         * @param listener
         */
        private void requestUpgradeInfo(Map versions, OnResponseListener listener) {
            System.out.println(versions); // this should be passed as HTTP parameters
            mResponseHandler = new ResponseHandler(listener);
            ThreadUtils.runOnLongBackThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Example HTTP request to get the upgrade bundles information.
                        // Json format see http://wequick.github.io/small/upgrade/bundles.json
                        URL url = new URL("http://192.168.19.125:8080/json/bundle.json");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        StringBuilder sb = new StringBuilder();
                        InputStream is = conn.getInputStream();
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) != -1) {
                            sb.append(new String(buffer, 0, length));
                        }

                        // Parse json
                        JSONObject jo = new JSONObject(sb.toString());
                        JSONObject mf = jo.has("manifest") ? jo.getJSONObject("manifest") : null;
                        JSONArray updates = jo.getJSONArray("updates");
                        int N = updates.length();
                        List<UpdateInfo> infos = new ArrayList<UpdateInfo>(N);
                        for (int i = 0; i < N; i++) {
                            JSONObject o = updates.getJSONObject(i);
                            UpdateInfo info = new UpdateInfo();
                            info.packageName = o.getString("pkg");
                            info.downloadUrl = o.getString("url");
                            infos.add(info);
                        }

                        // Post message
                        UpgradeInfo ui = new UpgradeInfo();
                        ui.manifest = mf;
                        ui.updates = infos;
                        Message.obtain(mResponseHandler, 1, ui).sendToTarget();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDlg.dismiss();
                                mProgressDlg = null;
                                UIUtils.showToast("更新失败");
                            }
                        });
                    }
                }
            });
        }

        private static class DownloadHandler extends Handler {
            private OnUpgradeListener mListener;

            public DownloadHandler(OnUpgradeListener listener) {
                mListener = listener;
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mListener.onUpgrade((Boolean) msg.obj);
                        break;
                }
            }
        }

        private DownloadHandler mHandler;

        /**
         * 更新插件
         * @param info
         * @param listener
         */
        private void upgradeBundles(final UpgradeInfo info,
                                    final OnUpgradeListener listener) {
            // Just for example, you can do this by OkHttp or something.
            mHandler = new DownloadHandler(listener);
            ThreadUtils.runOnLongBackThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Update manifest
                        if (info.manifest != null) {
                            if (!Small.updateManifest(info.manifest, false)) {

                                Message.obtain(mHandler, 1, false).sendToTarget();
                                return;
                            }
                        }
                        // Download bundles
                        List<UpdateInfo> updates = info.updates;
                        for (UpdateInfo u : updates) {
                            // Get the patch file for downloading
                            net.wequick.small.Bundle bundle = Small.getBundle(u.packageName);
                            // bundle.getPatchFile()获取到插件.so存储的路径 使用的内部存储
                            File file = bundle.getPatchFile();

                            // Download
                            URL url = new URL(u.downloadUrl);
                            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                            InputStream is = urlConn.getInputStream();
                            OutputStream os = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) != -1) {
                                os.write(buffer, 0, length);
                            }
                            os.flush();
                            os.close();
                            is.close();

                            // Upgrade
                            bundle.upgrade();
                        }
                        Message.obtain(mHandler, 1, true).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message.obtain(mHandler, 1, false).sendToTarget();
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDlg.dismiss();
                                mProgressDlg = null;
                                UIUtils.showToast("更新失败");
                            }
                        });
                    }
                }
            });
        }
    }
}