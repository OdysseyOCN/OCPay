package com.snow.commonlibrary.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by y on 2018/5/1.
 */

public class ShareUtils {

    /**
     * @param activity
     * @param content
     * @param title
     */
    public static void toShare(Activity activity, String content, String title) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, content);
        activity.startActivity(Intent.createChooser(i, title));

    }


    /**
     * @param context
     * @param label
     * @param data
     */
    public static void toClipboardData(Context context, String label, String data, String tip) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, data);
        clipboard.setPrimaryClip(clip);
        if (tip != null && !tip.isEmpty()) {
            Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();

        }
    }
}
