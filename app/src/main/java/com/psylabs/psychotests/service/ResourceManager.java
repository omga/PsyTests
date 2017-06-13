package com.psylabs.psychotests.service;

import com.psylabs.psychotests.model.QuizItem;
import com.psylabs.psychotests.model.QuizProcessor;

import java.util.List;

/**
 * Created by Andrew on 30.03.2017.
 */

public interface ResourceManager {

    List<QuizItem> getQuizListByCategory(int cat_id);
    QuizProcessor getQuizProcessor(int id);
}
