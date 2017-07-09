package com.psylabs.psychotests.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * Created by Andrew on 11.04.2017.
 */

public class Util {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void disableViewPreventDoubleClick(final View view, int duration) {
        view.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(view!=null)
                    view.setEnabled(true);
            }
        },duration);
    }

    public static void shareSocial(Context ctx, String text, int id) {
        try {
            Uri imageUri = null;
            try {
                imageUri = Uri.parse(MediaStore.Images.Media.insertImage(ctx.getContentResolver(),
                        BitmapFactory.decodeResource(ctx.getResources(), id), null, null));
            } catch (NullPointerException e) {
                Log.e("SHARE","NullPointerException " + e.getMessage());
            }
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Психологические тесты");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            ctx.startActivity(Intent.createChooser(sharingIntent, "Выберите с кем поделиться"));

        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("SHARE","ActivityNotFoundException");
        }
    }
}
