package ru.siamoil.siamservice.pvtcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    InputFragment inputFragment;
    PlotFragment plotFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        tabLayout = findViewById(R.id.tab_pane);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        inputFragment = new InputFragment();
        plotFragment = new PlotFragment();
        inputFragment.setPlotFragment(plotFragment);
        adapter.addFragment(inputFragment, getString(R.string.input_fragment));
        adapter.addFragment(plotFragment, getString(R.string.plot_fragment));
        viewPager.setAdapter(adapter);
    }

}
