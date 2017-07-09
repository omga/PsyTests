package com.psylabs.psychotests.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.psylabs.psychotests.R;
import com.psylabs.psychotests.model.QuizItem;
import com.psylabs.psychotests.model.QuizProcessor;
import com.psylabs.psychotests.model.quizprocessors.ArtisticQuiz;
import com.psylabs.psychotests.model.quizprocessors.FriendsQuiz;
import com.psylabs.psychotests.model.quizprocessors.IsItLoveQuiz;
import com.psylabs.psychotests.model.quizprocessors.JealousyQuiz;
import com.psylabs.psychotests.model.quizprocessors.KindnessQuiz;
import com.psylabs.psychotests.model.quizprocessors.LifePrinciplesQuiz;
import com.psylabs.psychotests.model.quizprocessors.ShynessQuiz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 30.03.2017.
 */

public class ResourceManagerImpl implements ResourceManager {
    public static final int ALL = 0;
    public static final int PERSONALITY = 1;
    public static final int PROFESSIONAL = 2;
    public static final int FAMILY_AND_GENDER = 3;
    public static final int VARIOUS = 4;

    private Context context;
    private SharedPreferences sharedPrefs;

    public ResourceManagerImpl(Context context, SharedPreferences prefs) {
        this.context = context;
        this.sharedPrefs = prefs;
    }

    @Override
    public List<QuizItem> getQuizListByCategory(int cat_id) {
        int id;
        switch (cat_id) {
            case ALL: id = R.array.all_tests;
                break;
            case PERSONALITY: id = R.array.personality_tests;
                break;
            case PROFESSIONAL: id = R.array.professional_tests;
                break;
            case FAMILY_AND_GENDER: id = R.array.family_and_gender_tests;
                break;
            case VARIOUS: id = R.array.various_tests;
                break;
            default: id = R.array.kids_tests;
        }
        return createQuizList(context.getResources().getStringArray(id), cat_id);
    }

    private List<QuizItem> createQuizList(String[] names, int cat_id) {
        List<QuizItem> quizItems = new ArrayList<>(10);
        for (int i = 0; i < names.length; i++) {
            QuizItem quizItem = new QuizItem();
            quizItem.setName(names[i]);
            quizItem.setPassed(sharedPrefs.getBoolean(names[i], false));
            quizItems.add(quizItem);
            quizItem.setId(i + cat_id*1000);
        }
        return quizItems;
    }
//LOL WTF IS THIS SHIT?????
    public QuizProcessor getQuizProcessor(int id) {
        switch (id) {
            case 0: return new KindnessQuiz(context);
            case 1000: return new KindnessQuiz(context);
            case 1: return new FriendsQuiz(context);
            case 1001: return new FriendsQuiz(context);
            case 2: return new ShynessQuiz(context);
            case 1002: return new ShynessQuiz(context);
            case 3: return new LifePrinciplesQuiz(context);
            case 1003: return new LifePrinciplesQuiz(context);
            case 4: return new ArtisticQuiz(context);
            case 2004: return new ArtisticQuiz(context);
            case 5: return new IsItLoveQuiz(context);
            case 3005: return new IsItLoveQuiz(context);
            case 6: return new JealousyQuiz(context);
            case 3006: return new JealousyQuiz(context);

        }
        return new KindnessQuiz(context);
    }

}
