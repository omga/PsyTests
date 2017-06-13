package com.psylabs.psychotests.model;

/**
 * Created by Andrew on 31.03.2017.
 */

public interface QuizProcessor {

    void answer(int i);
    String calculate();
    int getQuestionsCount();
    int setQuestionsCount();
    String getNextQuestion();
    String getPreviousQuestion();
    String getDescription();
    String[] getVariants();
    String[] getCurrentVariants();
    int getCurrentIndex();
    void reset();
    boolean isDifferentVariants();
    int getQuizImage();
    int getResultImage();

}
