package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 13.04.2017.
 */

public class ShynessQuiz extends BaseQuiz {
    private String res1,res2,res3,res4,res5;
    private int resultImage = R.drawable.shyness;

    public ShynessQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.shyness_quiz_name);
        description = resources.getString(R.string.shyness_quiz_intro);
        questions = resources.getStringArray(R.array.shyness_questions);
        variants = new String[]{"да","нет"};
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.shyness_res_1);
        res2 = resources.getString(R.string.shyness_res_2);
        res3 = resources.getString(R.string.shyness_res_3);
        res4 = resources.getString(R.string.shyness_res_4);
        res5 = resources.getString(R.string.shyness_res_5);
    }

    @Override
    public String calculate() {
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            if(answers.get(i) == 0)
                sum++;
        }
        for (int i = 8; i < 16; i++) {
            if(answers.get(i) == 1)
                sum++;
        }
        Log.d("SHY","count: " + answers.size());
        Log.d("SHY","count: " + answers);
        Log.d("SHY","count questions: " + questions.length);
        Log.d("SHY","sum: " + sum);
        if(sum<=2) {
            resultImage = R.drawable.shyness_sociable;
            return res5;
        }
        if(sum<=6) {
            resultImage = R.drawable.shyness_sociable;
            return res4;
        }
        if(sum<=9) {
            resultImage = R.drawable.shyness_medium;
            return res3;
        }
        resultImage = R.drawable.shyness;
        if(sum<=13) return res2;
        return res1;
    }

    @Override
    public String[] getCurrentVariants() {
        return variants;
    }

    @Override
    public boolean isDifferentVariants() {
        return false;
    }

    @Override
    public int getQuizImage() {
        return R.drawable.shyness;
    }

    @Override
    public int getResultImage() {
        return resultImage;
    }
}
