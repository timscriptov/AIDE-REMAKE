package com.trunksku.asmobile.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.trunksku.asmobile.R;

import java.io.File;

public class CreateFolderDialog {

    public static void showDialog(Activity activity, String str, final CreateDialog.Listener listen) {
        CharSequence charSequence;
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setPadding(dp2px((float) 24), 0, dp2px((float) 24), 0);

        final EditText editText = new EditText(activity);
        linearLayout.addView(editText, -1, -1);
        final File file = new File(str);
        AlertDialog.Builder view = new AlertDialog.Builder(activity).setTitle(R.string.create_folder).setView(linearLayout);

        final AlertDialog create = view.setPositiveButton(R.string.create, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        create.show();
        create.getButton(-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                new File(file, editText.getText().toString()).mkdirs();
                listen.success();
                create.dismiss();
            }
        });
        editText.setHint("");
        editText.setSingleLine(true);
        editText.setInputType(1);
        editText.setImeOptions(6);
        //editText.setOnEditorActionListener(new AnonymousClass100000001(create, editText, file, abVar));
        //editText.post(new AnonymousClass100000002(editText));
        // create.setOnDismissListener(new AnonymousClass100000003(activity));
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }
}
