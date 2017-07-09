package com.psylabs.psychotests.model.quizprocessors;

import com.psylabs.psychotests.App;
import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizProcessor;
import com.psylabs.psychotests.model.rx.ImageToolbarEvent;
import com.psylabs.psychotests.model.rx.QuizFinishedEvent;
import com.psylabs.psychotests.service.RxBus;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Andrew on 07.04.2017.
 */

public abstract class BaseQuiz implements QuizProcessor {
    String name;
    String description;
    List<Integer> answers;
    String [] questions;
    String[] variants;
    int currentQuestionIndex = -1;
    int resultImage = R.drawable.psy_logo_grey_bg;
    int quizImage = R.drawable.psy_logo_grey_bg;
    @Inject
    RxBus rxBus;

    public BaseQuiz() {
        App.getComponent().inject(this);

    }

    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    @Override
    public void answer(int i) {
        answers.add(i);
    }

    @Override
    public int getQuestionsCount() {
        return questions.length;
    }

    @Override
    public int setQuestionsCount() {
        return questions.length;
    }

    @Override
    public String getNextQuestion() {
        if(questions.length> currentQuestionIndex+1)
                return questions[++currentQuestionIndex];
        else {
            rxBus.send(new QuizFinishedEvent(calculate(), getResultImage()));
            rxBus.send(new ImageToolbarEvent(getResultImage()));
            return "";
        }
    }

    @Override
    public String getPreviousQuestion() {
//        if(answers.size()>currentQuestionIndex)
        currentQuestionIndex-=2;
        answers.remove(currentQuestionIndex+1);
        return questions[currentQuestionIndex+1];
    }

    @Override
    public int getCurrentIndex() {
        return currentQuestionIndex;
    }

    @Override
    public void reset() {
        currentQuestionIndex = -1;
        answers.clear();
    }

    @Override
    public int getQuizImage() {
        return quizImage;
    }

    @Override
    public int getResultImage() {
        return resultImage;
    }
}
