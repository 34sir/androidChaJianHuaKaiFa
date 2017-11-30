package tsou.cn.mysmalltest;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wequick.small.Small;

/**
 * 使用style等lib的时候切记在bundle.json上继续配置，不然会找不到
 * <p>
 * 例如：提示AppTheme找不到让你进行配置问题
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mMViewPager;
    private TabLayout mToolbarTab;
    /**
     * 图标
     */
    private int[] tabIcons = {
            R.drawable.tab_home,
            R.drawable.tab_weichat,
            R.drawable.tab_recommend,
            R.drawable.tab_user
    };
    private static String[] fragments = new String[]{"home", "chat", "recom", "me"};
    private String[] tab_array;
    private DemandAdapter mDemandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        // 给viewpager设置适配器
        setViewPagerAdapter();
        setTabBindViewPager();
        setItem();
    }

    private void initData() {
        tab_array = getResources().getStringArray(R.array.tab_main);
    }

    private void initView() {
        mMViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mToolbarTab = (TabLayout) findViewById(R.id.toolbar_tab);

    }

    private void setViewPagerAdapter() {
        mDemandAdapter = new DemandAdapter(getSupportFragmentManager());
        mMViewPager.setAdapter(mDemandAdapter);
    }

    private void setTabBindViewPager() {
        mToolbarTab.setupWithViewPager(mMViewPager);
    }

    private void setItem() {
        /**
         * 一定要在设置适配器之后设置Icon
         */
        for (int i = 0; i < mToolbarTab.getTabCount(); i++) {
            mToolbarTab.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        ImageView tab_image = view.findViewById(R.id.tab_image);
        TextView tab_text = view.findViewById(R.id.tab_text);
        tab_image.setImageResource(tabIcons[position]);
        tab_text.setText(tab_array[position]);
        return view;
    }

    /**
     * 适配器
     */
    public class DemandAdapter extends FragmentStatePagerAdapter {


        public DemandAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /**
             * 在使用createObject前，需要再此页面之前已经存在activity，初始化Small，
             * 否则获取不到Fragment，都是null
             */
            Fragment fragment = Small.createObject("fragment-v4", fragments[position],
                    MainActivity.this);
            return fragment;
        }

        @Override
        public int getCount() {
            return tabIcons.length;
        }

    }
}
