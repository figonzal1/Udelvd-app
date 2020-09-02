package cl.udelvd.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.List;

import cl.udelvd.R;
import cl.udelvd.adapters.IntroPageAdapter;
import cl.udelvd.models.IntroItem;

public class IntroActivity extends AppCompatActivity {

    private MaterialButton btnGetStarted;

    private TextView skipButton;
    private ImageButton ibNext;
    private List<IntroItem> introItemList;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int position;
    private View separator;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private Animation btnGetStarAnimation;
    private Animation itemAnimation;
    private boolean isShowBtnGetStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //FULLSCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro);

        sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF_MASTER_KEY), Context.MODE_PRIVATE);

        checkFirstLoad();

        initResourcesSlicesAndViewPager();
    }

    private void initResourcesSlicesAndViewPager() {

        editor = sharedPreferences.edit();

        skipButton = findViewById(R.id.tv_skip);
        ibNext = findViewById(R.id.ib_next);
        separator = findViewById(R.id.separator);
        btnGetStarted = findViewById(R.id.btn_start);
        btnGetStarAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        itemAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_item_animation);

        //SET SLIDES
        introItemList = new ArrayList<>();
        introItemList.add(new IntroItem(getString(R.string.app_name) + "\n APP", getString(R.string.SLIDE_1_DESCRIPTION), R.drawable.ic_logo_rounded));
        introItemList.add(new IntroItem(getString(R.string.SLIDE_2_TITLE), getString(R.string.SLIDE_2_DESCRIPTION), R.drawable.ic_goal_logo_rounded));
        introItemList.add(new IntroItem(getString(R.string.SLIDE_3_TITLE), getString(R.string.SLIDE_3_DESCRIPTION), R.drawable.ic_interview_logo_rounded));
        introItemList.add(new IntroItem(getString(R.string.SLIDE_4_TITLE), getString(R.string.SLIDE_4_DESCRIPTION), R.drawable.ic_calendar_logo_rounded));
        introItemList.add(new IntroItem(getString(R.string.SLIDE_5_TITLE), getString(R.string.SLIDE_5_DESCRIPTION), R.drawable.ic_stats_logo_rounded));

        //SETUP VIEW PAGER
        viewPager = findViewById(R.id.view_pager_intro);
        IntroPageAdapter introPageAdapter = new IntroPageAdapter(getApplicationContext(), introItemList);
        viewPager.setAdapter(introPageAdapter);

        //SETUP TABS
        tabLayout = findViewById(R.id.tab_intro);
        tabLayout.setupWithViewPager(viewPager);

        //TAB LAYOUT CHANGE LISTENER
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == introItemList.size() - 1) {
                    startAnimationBtnGetStarted();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //NEXT BUTTON
        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = viewPager.getCurrentItem();

                if (position < introItemList.size()) {

                    position++;
                    viewPager.setCurrentItem(position);
                }

                if (position == introItemList.size() - 1) {

                    startAnimationBtnGetStarted();
                }
            }
        });

        //GET STARTED BUTTON
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean(getString(R.string.SHARED_PREF_FIRST_LOAD), false);
                editor.apply();

                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //SKIP BUTTON
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean(getString(R.string.SHARED_PREF_FIRST_LOAD), false);
                editor.apply();

                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //CONFIG ANIMATIONS INSIDE VIEW PAGER
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {

                    View v = viewPager.findViewWithTag("view" + position);
                    ImageView imgSlide = v.findViewById(R.id.iv_intro);
                    TextView title = v.findViewById(R.id.tv_intro_title);
                    TextView description = v.findViewById(R.id.tv_intro_description);

                    imgSlide.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (position > 0) {

                    View v = viewPager.findViewWithTag("view" + position);
                    ImageView imgSlide = v.findViewById(R.id.iv_intro);
                    TextView title = v.findViewById(R.id.tv_intro_title);
                    TextView description = v.findViewById(R.id.tv_intro_description);

                    imgSlide.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);

                    itemAnimation.setStartOffset(500);
                    imgSlide.startAnimation(itemAnimation);
                    title.startAnimation(itemAnimation);
                    description.startAnimation(itemAnimation);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkFirstLoad() {

        boolean isFirstLoad = sharedPreferences.getBoolean(getString(R.string.SHARED_PREF_FIRST_LOAD), true);

        Log.d(getString(R.string.SHARED_PREF_FIRST_LOAD), String.valueOf(isFirstLoad));
        FirebaseCrashlytics.getInstance().setCustomKey(getString(R.string.SHARED_PREF_FIRST_LOAD), isFirstLoad);

        if (!isFirstLoad) {

            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startAnimationBtnGetStarted() {

        if (!isShowBtnGetStarted) {

            tabLayout.setVisibility(View.INVISIBLE);
            skipButton.setVisibility(View.INVISIBLE);
            ibNext.setVisibility(View.INVISIBLE);
            separator.setVisibility(View.INVISIBLE);

            btnGetStarted.setVisibility(View.VISIBLE);
            btnGetStarted.startAnimation(btnGetStarAnimation);

            isShowBtnGetStarted = true;
        }

    }
}
