package com.psylabs.psychotests.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

    public static void rateApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void requestMultiplePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                12121);
    }

    public static boolean checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions(activity);
            return false;
        }
        return true;
    }

}
