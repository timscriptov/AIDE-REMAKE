package com.trunksku.asmobile.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.trunksku.asmobile.R;

public class Utils {

    public static void setAlertDialogMovementMethod(AlertDialog alertDialog) {
        TextView textView = (TextView) alertDialog.findViewById(16908299);
        if (textView != null) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void setMessageIsSelectable(AlertDialog alertDialog) {
        setMessageIsSelectable(alertDialog, true);
    }

    public static void setMessageIsSelectable(AlertDialog alertDialog, boolean z) {
        TextView textView = (TextView) alertDialog.findViewById(16908299);
        if (textView != null) {
            textView.setTextIsSelectable(z);
        }
    }

    public static void GetTelegram(Context context, String id_user) {
        try {
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            telegramIntent.setData(Uri.parse("http://telegram.me/" + id_user));
            context.startActivity(telegramIntent);
        } catch (Exception e) {
            // show error message
        }
    }

    public static boolean copyToClipboard(Context context, String text) {
        try {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
                        .getSystemService(context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText(
                                context.getResources().getString(
                                        R.string.app_name), text);
                clipboard.setPrimaryClip(clip);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
