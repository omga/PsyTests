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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizItem;
import com.psylabs.psychotests.model.rx.StartQuizEvent;
import com.psylabs.psychotests.service.ResourceManager;
import com.psylabs.psychotests.service.RxBus;
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
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fabBottom)
    FloatingActionButton fabBottom;
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
        appBarListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d(TAG, state.name());
                if(state.equals(State.COLLAPSED)) {
                    fabBottom.show();
                } else {
                    fabBottom.hide();

                }
            }
        };
        setupToolbar();
        addFabListener();
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

    @OnClick({R.id.fab, R.id.fabBottom})
    public void startQuiz() {
        Log.d(TAG,"startQuiz");
        rxBus.send(new StartQuizEvent());
        removeFabListener();
    }

    private void addFabListener() {
        fab.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams p =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        fab.setLayoutParams(p);
        if(appBarLayout.getHeight() - appBarLayout.getBottom() == 0)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.nav_all) {
            fragment = QuizListFragment.newInstance(1,0);
        } else if (id == R.id.nav_personal) {
            fragment = QuizListFragment.newInstance(1,1);

        } else if (id == R.id.nav_professional) {
            fragment = QuizListFragment.newInstance(1,2);

        } else if (id == R.id.nav_gender) {
            fragment = QuizListFragment.newInstance(1,3);

        } else if (id == R.id.nav_various) {
            fragment = QuizListFragment.newInstance(1,4);

        } else if (id == R.id.nav_child) {
            fragment = QuizListFragment.newInstance(1,5);

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
//        addFabListener();
//        mFragmentTransaction = mFragmentManager.beginTransaction();
//        mFragmentTransaction.replace(R.id.container, QuizFragment.newInstance(quiz)).commit();
    }

    private void goToQuizActivity(QuizItem quiz) {
        Intent i = new Intent(this, QuizActivity.class);
        i.putExtra(QuizActivity.QUIZ_EXTRA, Parcels.wrap(quiz));
        startActivity(i);
    }


}
