package com.psylabs.psychotests.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizItem;
import com.psylabs.psychotests.model.rx.ImageToolbarEvent;
import com.psylabs.psychotests.model.rx.QuizFinishedEvent;
import com.psylabs.psychotests.model.rx.StartQuizEvent;
import com.psylabs.psychotests.service.ResourceManager;
import com.psylabs.psychotests.service.RxBus;
import com.psylabs.psychotests.service.Util;
import com.psylabs.psychotests.ui.adapter.AppBarStateChangeListener;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class QuizActivity extends AppCompatActivity implements RewardedVideoAdListener {

    public static final String QUIZ_EXTRA = "QUIZ_XTR";
    private static final String TAG = "QuizActivity";
    private static final String EXTRA_IMAGE = "com.psylabs.psychotests.ui.extraImage";
    @Inject
    ResourceManager resourceManager;
    @Inject
    RxBus rxBus;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.image_toolbar)
    ImageView imageViewToolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fabBottom)
    FloatingActionButton fabBottom;
    int mStackLevel = 0;
    QuizItem quiz;
    private AppBarStateChangeListener appBarListener;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Interpolator interpolator;
    private Disposable subscription;
    private int imgResId;

    //AdMob stuff
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_quiz);
        App.getComponent().inject(this);
        ButterKnife.bind(this);
//        ViewCompat.setTransitionName(appBarLayout, EXTRA_IMAGE);
        setSupportActionBar(toolbar);
        Parcelable parcelable = getIntent().getParcelableExtra(QUIZ_EXTRA);
        if (parcelable != null) {
            quiz = Parcels.unwrap(parcelable);
            init();
            if (savedInstanceState == null)
                addFragment();
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

    }

    private void init() {
        interpolator = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in) :
                AnimationUtils.loadInterpolator(this, android.R.interpolator.linear);
        appBarListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d(TAG, state.name());
                if (state.equals(State.COLLAPSED)) {
                    fabBottom.show();
                } else {
                    fabBottom.hide();
                }
            }
        };
        setupToolbar();
        addFabListener();
        subscribe();
    }

    private void addFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, QuizFragment.newInstance(quiz)).commit();
    }

    private void setupToolbar() {
        collapsingToolbar.setTitle(quiz.getName());
        collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbar.setContentScrimColor(
                ContextCompat.getColor(this, R.color.colorPrimary));
        collapsingToolbar.setStatusBarScrimColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setImageToolbar(int res) {
        imgResId = res;
        imageViewToolbar.setImageResource(res);
        appBarLayout.setExpanded(true);

        //set collapsed toolbar and status bar color using muted color from image
        Bitmap bm = BitmapFactory.decodeResource(getResources(), res);
        Palette.from(bm).generate(palette -> {
            collapsingToolbar.setContentScrimColor(palette.getMutedColor(
                    ContextCompat.getColor(QuizActivity.this, R.color.colorPrimary)));
            collapsingToolbar.setStatusBarScrimColor(palette.getMutedColor(
                    ContextCompat.getColor(QuizActivity.this, R.color.colorPrimary)));
        });
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
//        subscribe();
        super.onStart();

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
//        unsubscribe();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mRewardedVideoAd.resume(this);
        unsubscribe();
        super.onDestroy();
    }

    @OnClick({R.id.fab, R.id.fabBottom})
    public void fabClick() {
        Log.d(TAG, "startQuiz");
        rxBus.send(new StartQuizEvent());
//        appBarLayout.setExpanded(false);

    }

    private void addFabListener() {
        fab.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams p =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        fab.setLayoutParams(p);
        if (appBarLayout.getHeight() - appBarLayout.getBottom() == 0)
            fab.show();
        else
            fabBottom.show();
        appBarLayout.addOnOffsetChangedListener(appBarListener);
    }

    private void removeFabListener() {
        fab.hide();
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        fabBottom.hide();
        appBarLayout.removeOnOffsetChangedListener(appBarListener);
    }

    private void setFabShare(String sharingText) {
        View.OnClickListener listener = v -> {
            if (Util.checkPermission(QuizActivity.this))
                Util.shareSocial(this, sharingText, imgResId);
        };
        fab.setImageResource(R.drawable.ic_action_share);
        fabBottom.setImageResource(R.drawable.ic_action_share);
        fab.setOnClickListener(listener);
        fabBottom.setOnClickListener(listener);

    }

    private void subscribe() {
        subscription = rxBus.toObservable().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> {
                    Log.d(TAG, "rxbus accept");
                    if (o instanceof ImageToolbarEvent) {
                        Log.d(TAG, "rxbus setFabForwardIcon");
                        setImageToolbar(((ImageToolbarEvent) o).imageResId);
                    } else if (o instanceof QuizFinishedEvent) {
                        String resultText = ((QuizFinishedEvent) o).result;
                        showResult(resultText, ((QuizFinishedEvent) o).resultImage);
                        if (mRewardedVideoAd.isLoaded()) {
                            mRewardedVideoAd.show();
                        }
                    }
                });
    }

    private void showResult(String resultText, int resultImage) {
        setFabShare(resultText);
        Fragment newFragment = ResultFragment.newInstance(
                resultText, resultImage);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.addToBackStack(null);
        ft.commit();
    }

    private void unsubscribe() {
        if (!subscription.isDisposed())
            subscription.dispose();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
}
