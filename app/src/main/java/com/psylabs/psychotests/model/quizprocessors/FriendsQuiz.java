package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 11.04.2017.
 */

public class FriendsQuiz extends BaseQuiz {
    private boolean isDifferentVariants = true;
    private String res1,res2,res3,res4;
    private List<String[]> variantsList;
    private int[][] answerValues = new int[][]{
            {10, 5, 15, 0, 20},
            {20, 0, 5, 15, 10},
            {15, 10, 10, 5, 0},
            {0, 5, 10, 15, 20},
            {15, 20, 10, 5, 0},
            {20, 10, 10, 0, 15},
            {5, 0, 10, 15, 0},
            {15, 10, 0, 50, 0},
            {10, 5, 15, 0, 5},
            {15, 20, 10, 0, 15},
            {5, 0, 10, 20, 15},
            {0, 5, 10, 0, 0},
            {0, 10, 20, 15, 5},
            {10, 15, 20, 0, 5},
            {15, 50, 10, 20, 0}
    };

    public FriendsQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.friends_quiz_name);
        description = resources.getString(R.string.friends_quiz_intro);
        questions = resources.getStringArray(R.array.friends_questions);
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.friends_res_1);
        res2 = resources.getString(R.string.friends_res_2);
        res3 = resources.getString(R.string.friends_res_3);
        res4 = resources.getString(R.string.friends_res_4);
        variantsList = new ArrayList<>(questions.length);
        final String prefix = "friends_answers_";
        for (int i = 1; i <= questions.length; i++) {
            variantsList.add(getArrayByName(context, resources, prefix + i));
        }
        variants = variantsList.get(0);

    }


    private String[] getArrayByName(Context context, Resources resources, String name) {
        int arryid = resources.getIdentifier(name, "array",
                context.getPackageName());
        return resources.getStringArray(arryid);
    }

    @Override
    public String calculate() {
        int sum = 0;
        for (int i = 0; i < answers.size(); i++) {
            sum += answerValues[i][answers.get(i)];
        }
        Log.d("FRIENDS","count: " + answers.size());
        Log.d("FRIENDS","count questions: " + questions.length);
        Log.d("FRIENDS","sum: " + sum);
        if(sum<75)
            return res1;
        if(sum<145)
            return res2;
        if(sum<220)
            return res3;
        else
            return res4;
    }

    @Override
    public int getQuizImage() {
        return R.drawable.lemur_toolbar;
    }

    @Override
    public String[] getCurrentVariants() {
        return variantsList.get(currentQuestionIndex);
    }

    @Override
    public boolean isDifferentVariants() {
        return isDifferentVariants;
    }
}
