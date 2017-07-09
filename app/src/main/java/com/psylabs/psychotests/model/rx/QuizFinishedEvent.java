package com.psylabs.psychotests.model.rx;

/**
 * Created by Andrew on 08.04.2017.
 */

public class QuizFinishedEvent {
    public String result;
    public int resultImage;

    public QuizFinishedEvent(String result, int resultImage) {
        this.result = result;
        this.resultImage = resultImage;
    }
}
