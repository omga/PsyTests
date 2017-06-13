package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 15.04.2017.
 */

public class ArtisticQuiz extends BaseQuiz {
    private String res1, res2, res3;
    private boolean isDifferentVariants = false;
    private int resultImage = R.drawable.artistic_logo;


    public ArtisticQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.artist_quiz_name);
        description = resources.getString(R.string.artist_quiz_intro);
        questions = resources.getStringArray(R.array.artist_questions);
        variants = new String[]{"да","нет"};
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.artist_res_1);
        res2 = resources.getString(R.string.artist_res_2);
        res3 = resources.getString(R.string.artist_res_3);
    }

    @Override
    public String calculate() {

        int result = 0;
        for (int i = 0; i < answers.size(); i++) {
            if(i==2||i==6||i==10||i==14||i==15||i==18) {
                if (answers.get(i) == 1)
                    result++;
            } else
            if(answers.get(i) == 0)
                result++;
        }
        Log.d("ArtisticQuiz","count: " + answers.size());
        Log.d("ArtisticQuiz","count: " + answers);
        Log.d("ArtisticQuiz","count questions: " + questions.length);
        Log.d("ArtisticQuiz","sum: " + result);
        String resText = res1;
        if(result > 16)
            resultImage = R.drawable.artistic_monroe;
        else if (result < 4) {
            resText = res3;
            resultImage = R.drawable.artistic_sad;
        } else
            resText = res2;
        return resText;
    }

    @Override
    public int getQuizImage() {
        return R.drawable.artistic_logo;
    }

    @Override
    public int getResultImage() {
        return resultImage;
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
