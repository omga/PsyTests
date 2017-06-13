package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 16.04.2017.
 */

public class IsItLoveQuiz extends BaseQuiz {
    private boolean isDifferentVariants = true;
    private String res1,res2,res3;
    private List<String[]> variantsList;

    public IsItLoveQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.love_quiz_name);
        description = resources.getString(R.string.love_quiz_intro);
        questions = resources.getStringArray(R.array.love_questions);
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.love_res_1);
        res2 = resources.getString(R.string.love_res_2);
        res3 = resources.getString(R.string.love_res_3);
        variantsList = new ArrayList<>(questions.length);
        resultImage = R.drawable.love_quiz;
        quizImage =  R.drawable.love_quiz;
        final String prefix = "love_answers_";
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
        int[][] answerValues = new int[][]{
                {2, 3, 1},
                {2, 3, 1},
                {1, 2, 3},
                {2, 1, 3},
                {2, 3, 1},
                {2, 1, 3},
                {3, 2, 1},
                {2, 3, 1},
                {1, 3, 2},
                {2, 3, 1}
        };
        int sum = 0;
        for (int i = 0; i < answers.size(); i++) {
            sum += answerValues[i][answers.get(i)];
        }
        Log.d("LOVE","count: " + answers.size());
        Log.d("LOVE","count questions: " + questions.length);
        Log.d("LOVE","sum: " + sum);
        if(sum<17) {
            resultImage = R.drawable.love_1;
            return res1;
        }
        if(sum<=26) {
            return res2;
        }
        resultImage = R.drawable.love_3;
        return res3;
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
