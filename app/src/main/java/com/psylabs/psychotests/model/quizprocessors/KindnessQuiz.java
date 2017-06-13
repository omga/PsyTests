package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 06.04.2017.
 */

public class KindnessQuiz extends BaseQuiz {
    private String res1, res2, res3;
    private boolean isDifferentVariants = false;

    public KindnessQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.dobrota_quiz_name);
        description = resources.getString(R.string.dobrota_quiz_intro);
        questions = resources.getStringArray(R.array.dobrota_questions);
        variants = new String[]{"да","нет"};
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.dobrota_res_1);
        res2 = resources.getString(R.string.dobrota_res_2);
        res3 = resources.getString(R.string.dobrota_res_3);
    }

    @Override
    public String calculate() {

        int result = 0;
        for (int i = 0; i < answers.size(); i++) {
            if(i==0||i==2||i==3||i==6||i==10) {
                if (answers.get(i) == 0)
                    result++;
            } else
                if(answers.get(i) == 1)
                    result++;
        }
        Log.d("KindnessQuiz","count: " + answers.size());
        Log.d("KindnessQuiz","count: " + answers);
        Log.d("KindnessQuiz","count questions: " + questions.length);
        Log.d("KindnessQuiz","sum: " + result);
        return  result > 8 ? res1 :
                    result < 4 ? res3 : res2;
    }

    @Override
    public String[] getCurrentVariants() {
        return variants;
    }

    @Override
    public boolean isDifferentVariants() {
        return isDifferentVariants;
    }
}
