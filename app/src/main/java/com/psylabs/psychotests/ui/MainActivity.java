package com.psylabs.psychotests.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizItem;
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
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        QuizListFragment.OnListFragmentInteractionListener {

    private static final String TAG = "MainActivity";
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
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private AppBarStateChangeListener appBarListener;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Disposable subscribtion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        App.getComponent().inject(this);
        ButterKnife.bind(this);
        navigationView.setNavigationItemSelectedListener(this);
        setupToolbar();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.container, QuizListFragment.newInstance(1,0)).commit();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupToolbar() {
        collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent));
        collapsingToolbar.setContentScrimColor(
                ContextCompat.getColor(this, R.color.colorIndigo));
        collapsingToolbar.setStatusBarScrimColor(
                ContextCompat.getColor(this, R.color.colorDarkIndigo));
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscribe();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unsubscribe();
    }

    private void subscribe() {
        subscribtion = rxBus.toObservable().subscribe(o -> {
            Log.d(TAG,"rxbus accept");

//            else if( o instanceof QuizFinishedEvent) {
//                mFragmentManager = getSupportFragmentManager();
//                mFragmentTransaction = mFragmentManager.beginTransaction();
//                mFragmentTransaction.replace(R.id.container, ResultFragment.newInstance(
//                        ((QuizFinishedEvent) o).result, "hi")).commit();
//                addFabListener();
//            }
        });
    }

    private void unsubscribe() {
        if(!subscribtion.isDisposed())
            subscribtion.dispose();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_send) {
            if(Util.checkPermission(this))
                Util.rateApp(this);
            return true;
        }
        if(id == R.id.nav_share) {
            if(Util.checkPermission(this))
                Util.shareSocial(this, getString(R.string.sharing_text),
                    R.drawable.psy_logo_grey_bg);
            return true;
        }

        Fragment fragment;
        if (id == R.id.nav_all) {
            fragment = QuizListFragment.newInstance(1,0);
        } else if (id == R.id.nav_personal) {
            fragment = QuizListFragment.newInstance(1,1);

        } else if (id == R.id.nav_professional) {
            fragment = QuizListFragment.newInstance(1,2);

        } else if (id == R.id.nav_gender) {
            fragment = QuizListFragment.newInstance(1,3);

        } else { //if (id == R.id.nav_send) {
            fragment =  new MainActivityFragment();
        }

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mFragmentTransaction.replace(R.id.container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(QuizItem quiz) {
        goToQuizActivity(quiz);
//        mFragmentTransaction = mFragmentManager.beginTransaction();
//        mFragmentTransaction.replace(R.id.container, QuizFragment.newInstance(quiz)).commit();
    }

    private void goToQuizActivity(QuizItem quiz) {
        Intent i = new Intent(this, QuizActivity.class);
        i.putExtra(QuizActivity.QUIZ_EXTRA, Parcels.wrap(quiz));
        startActivity(i);
    }


}
