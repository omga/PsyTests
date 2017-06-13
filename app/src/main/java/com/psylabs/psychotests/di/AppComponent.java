package com.psylabs.psychotests.di;

import com.psylabs.psychotests.di.module.AppModule;
import com.psylabs.psychotests.di.module.QuizModule;
import com.psylabs.psychotests.di.module.RxModule;
import com.psylabs.psychotests.model.quizprocessors.BaseQuiz;
import com.psylabs.psychotests.ui.QuizActivity;
import com.psylabs.psychotests.ui.QuizFragment;
import com.psylabs.psychotests.ui.QuizListFragment;
import com.psylabs.psychotests.ui.MainActivity;
import com.psylabs.psychotests.ui.ResultFragment;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by Andrew on 30.03.2017.
 */

@Component (modules = {AppModule.class, QuizModule.class, RxModule.class})
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);
    void inject(QuizActivity quizActivity);
    void inject(QuizListFragment quizFragment);
    void inject(QuizFragment quizFragment);
    void inject(ResultFragment resultFragment);
    void inject(BaseQuiz baseQuiz);
}
