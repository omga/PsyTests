package com.psylabs.psychotests.ui;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.service.RxBus;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    private static final String QUIZ_RESULT_STRING = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int DELAY = 100;

    private String resultText;
    private String mParam2;
    @Inject
    RxBus rxBus;
    private TextView resultTextView;
    private ViewGroup rootView;


    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param resultText Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String resultText, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(QUIZ_RESULT_STRING, resultText);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent().inject(this);
        if (getArguments() != null) {
            resultText = getArguments().getString(QUIZ_RESULT_STRING);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_result, container, false);
        resultTextView = (TextView) rootView.findViewById(R.id.textResult);
        resultTextView.setText(resultText);
        resultTextView.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(this::revealGreen,100);
    }

    private void revealGreen() {
        animateRevealColor(rootView, R.color.colorPrimaryLight);
        resultTextView.setText(resultText);
        resultTextView.setVisibility(View.VISIBLE);
    }

    private void animateRevealColor(ViewGroup viewRoot, @ColorRes int color) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        animateRevealColorFromCoordinates(viewRoot, color, cx, cy);
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
        Animator anim = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius):
                ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f);
        viewRoot.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_very_long));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }
}