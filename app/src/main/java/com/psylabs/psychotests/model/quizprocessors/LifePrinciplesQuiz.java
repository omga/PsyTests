package com.psylabs.psychotests.model.quizprocessors;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.psylabs.psychotests.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 15.04.2017.
 */

public class LifePrinciplesQuiz extends BaseQuiz {
    private boolean isDifferentVariants = true;
    private String res1,res2,res3,res4;
    private List<String[]> variantsList;

    public LifePrinciplesQuiz(Context context) {
        Resources resources = context.getResources();
        name = resources.getString(R.string.life_quiz_name);
        description = resources.getString(R.string.life_quiz_intro);
        questions = resources.getStringArray(R.array.life_questions);
        answers = new ArrayList<>(questions.length);
        res1 = resources.getString(R.string.life_res_1);
        res2 = resources.getString(R.string.life_res_2);
        res3 = resources.getString(R.string.life_res_3);
        res4 = resources.getString(R.string.life_res_4);
        variantsList = new ArrayList<>(questions.length);
        final String prefix = "life_answers_";
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
        int [] gg = {0,2,2,2,2,1,2,0,0,2,2,0};
        int [] gb = {1,2,1,0,1,0,1,0,1,1,1,1}; //1б 2в 3б 4а 5б 6а 7б 8а 9б 10б 11б 12б
        int [] bg = {2,1,2,1,1,1,0,1,2,0,1,2}; //1в 2б 3в 4б 5б 6б 7а 8б 9в 10а 11б 12в
        int [] bb = {1,0,0,1,0,2,2,2,1,2,0,2}; //1б 2а 3а 4б 5а 6в 7в 8в 9б 10в 11а 12в
        int ggsum = 0, gbsum = 0, bgsum = 0, bbsum = 0;
        for (int i = 0; i < answers.size(); i++) {
            int answer = answers.get(i);
            if(gg[i] == answer)
                ggsum++;
            if(gb[i] == answer)
                gbsum++;
            if(bg[i] == answer)
                bgsum++;
            if(bb[i] == answer)
                bbsum++;
        }
        int max = Math.max(Math.max(ggsum, gbsum),Math.max(bgsum,bbsum));
        return max == ggsum ? res1 :
                max == gbsum? res2:
                        max == bgsum? res3:
                                res4;
    }

    @Override
    public int getQuizImage() {
        return R.drawable.life_penguin;
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

