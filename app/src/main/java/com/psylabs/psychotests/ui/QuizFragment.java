package com.psylabs.psychotests.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizItem;
import com.psylabs.psychotests.model.QuizProcessor;
import com.psylabs.psychotests.model.rx.ImageToolbarEvent;
import com.psylabs.psychotests.model.rx.StartQuizEvent;
import com.psylabs.psychotests.service.ResourceManager;
import com.psylabs.psychotests.service.RxBus;
import com.psylabs.psychotests.service.Util;
import com.psylabs.psychotests.ui.adapter.AnswerRecyclerViewAdapter;
import com.transitionseverywhere.ChangeText;
import com.transitionseverywhere.TransitionManager;

import org.parceler.Parcels;

import java.lang.reflect.Field;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class QuizFragment extends Fragment implements AnswerRecyclerViewAdapter.OnAnswerListener {

    private static final String ARG_QUIZ = "quiz_arg";
    private static final int DELAY = 100;
    private static final String TAG = "QuizFragment";
    private TextView description, questionIndex;
    private RecyclerView recyclerView;
    private QuizItem quiz;
    private QuizProcessor quizProcessor;
    @Inject
    ResourceManager resourceManager;
    @Inject
    RxBus rxBus;
    ViewGroup rootView;
    NestedScrollView scrollViewDescription;
    Interpolator interpolator;
    Animation inAnim, outAnim;
    String question;
    AnswerRecyclerViewAdapter adapter;
    boolean quizStarted = false;
    private Disposable subscribtion;
    private FloatingActionButton fab, fabBottom;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuizFragment() {
    }

    @SuppressWarnings("unused")
    public static QuizFragment newInstance(QuizItem quiz) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QUIZ, Parcels.wrap(quiz));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        App.getComponent().inject(this);
        if (getArguments() != null) {
            quiz = Parcels.unwrap(getArguments().getParcelable(ARG_QUIZ));
            quizProcessor = resourceManager.getQuizProcessor(quiz.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        rootView = (ViewGroup) view;
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        scrollViewDescription = (NestedScrollView) view.findViewById(R.id.scrollViewDescription);
        description = (TextView) view.findViewById(R.id.description);
        questionIndex = (TextView) view.findViewById(R.id.questionIndex);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fabBottom = (FloatingActionButton) getActivity().findViewById(R.id.fabBottom);
        initAnimations();
        initUI();
        return view;
    }

    private void initAnimations() {
        interpolator = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear_out_slow_in) :
                AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear);
        inAnim = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_in_left);
        outAnim = AnimationUtils.loadAnimation(getContext(),
                android.R.anim.slide_out_right);
    }

    private void initUI() {
        if(quizProcessor!=null) {
            description.setText(quizProcessor.getDescription());
            questionIndex.setText("Колличесво Вопросов: " + quizProcessor.getQuestionsCount());
        }
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(getContext(), R.color.colorPrimaryLight)));
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG,"onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(TAG,"onDetach");
        super.onDetach();
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(
                ContextCompat.getColor(getContext(), R.color.colorBackgroundBlueGrey)));
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onStart() {
        Log.d(TAG,"onStart");
        super.onStart();
        subscribe();
        rxBus.send(new ImageToolbarEvent(quizProcessor.getQuizImage()));
    }

    @Override
    public void onStop() {
        Log.d(TAG,"onStop");
        super.onStop();
        unsubscribe();
    }

    private void subscribe() {
        subscribtion = rxBus.toObservable().subscribe(o -> {
            if (o instanceof StartQuizEvent)
                if (!quizStarted)
                    startQuiz();
                else
                    previousQuestion();
        });
    }

    private void unsubscribe() {
        if(!subscribtion.isDisposed())
            subscribtion.dispose();
    }

    private void startQuiz() {
        quizStarted = true;
        scrollViewDescription.getLayoutParams().height =
                Util.dpToPx(getContext(),160);
        initRecycler(quizProcessor.getVariants());
        updateUI();
        revealBlue();
        setFabBackIcon();
    }

    private void previousQuestion() {
        if(quizProcessor.getCurrentIndex()<=0) {
            adapter.setData(new String[0]);
            initUI();
            quizStarted = false;
            quizProcessor.reset();
            setFabForwardIcon();
        } else {
            quizProcessor.getPreviousQuestion();
            updateUI();
        }
    }

    private void initRecycler(String[] answers) {
        if (recyclerView !=null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new AnswerRecyclerViewAdapter(answers, this);
            recyclerView.setAdapter(adapter);
        }
    }

    private void updateUI() {
        if(quizProcessor!=null) {
            question = quizProcessor.getNextQuestion();
            TransitionManager.beginDelayedTransition(scrollViewDescription,
                    new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN));
            if(quizProcessor.isDifferentVariants()) {
                TransitionManager.beginDelayedTransition(rootView);
                adapter.setData(quizProcessor.getCurrentVariants());
            }
            description.setText(question);
            questionIndex.setText(quizProcessor.getCurrentIndex() + "/" + quizProcessor.getQuestionsCount());
        }
    }

    @Override
    public void onListFragmentInteraction(int index) {
        quizProcessor.answer(index);
        updateUI();
//        revealBlue();
    }

    private void revealBlue() {
        animateButtonsOut();
        Animator anim = animateRevealColorFromCoordinates(
                rootView, R.color.colorBackgroundBlueGrey, rootView.getWidth() / 2, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateButtonsIn();
            }
        });
        description.setText(question);
        description.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
    }

    private void animateButtonsOut() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            child.animate()
                    .setStartDelay(i)
                    .setInterpolator(interpolator)
                    .alpha(0)
                    .scaleX(0f)
                    .scaleY(0f);
        }
    }

    private void animateButtonsIn() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View child = rootView.getChildAt(i);
            child.animate()
                    .setStartDelay(100 + i * DELAY)
                    .setInterpolator(interpolator)
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1);
        }
    }

    private void setFabBackIcon() {
        fab.animate()
                .rotation(540)
                .setDuration(800)
                .setInterpolator(interpolator);
        fabBottom.animate()
                .rotation(540)
                .setDuration(800)
                .setInterpolator(interpolator);
    }

    private void setFabForwardIcon() {
        fab.animate()
                .rotation(0)
                .setDuration(500)
                .setInterpolator(interpolator);
        fabBottom.animate()
                .rotation(0)
                .setDuration(500)
                .setInterpolator(interpolator);
    }

    private Animator animateRevealColorFromCoordinates(ViewGroup viewRoot, @ColorRes int color, int x, int y) {
        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius):
                ObjectAnimator.ofFloat(rootView, "alpha", 0f, 1f);

        viewRoot.setBackgroundColor(ContextCompat.getColor(getContext(),color));
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        return anim;
    }

}
