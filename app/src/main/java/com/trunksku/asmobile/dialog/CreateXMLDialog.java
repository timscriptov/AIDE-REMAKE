package com.trunksku.asmobile.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.utils.FileUtils;

import java.io.File;

public class CreateXMLDialog {

    private static String getBasicLayoutCode() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<LinearLayout\n\txmlns:android=\"http://schemas.android.com/apk/res/android\"\n\tandroid:orientation=\"vertical\"\n\tandroid:layout_width=\"match_parent\"\n\tandroid:layout_height=\"match_parent\">\n    \n</LinearLayout>";
    }

    private static String getBasicMenuCode() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<menu xmlns:android=\"http://schemas.android.com/apk/res/android\">\n    \n    <item\n        android:id=\"@+id/item\"\n        android:title=\"Item\"/>\n    \n</menu>";
    }

    private static String getEmptyResCode() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n    \n</resources>";
    }

    public void showDialog(Activity activity, String str, final CreateDialog.Listener listen) {
        CharSequence charSequence;
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setPadding(dp2px((float) 24), 0, dp2px((float) 24), 0);

        final EditText editText = new EditText(activity);
        linearLayout.addView(editText, -1, -1);
        final File file = new File(str);
        Builder view = new Builder(activity).setTitle("Create XML").setView(linearLayout);

        final AlertDialog create = view.setPositiveButton(R.string.create, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        create.show();
        create.getButton(-1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                createXml(create, editText, file);
                listen.success();
            }
        });
        editText.setHint("");
        editText.setSingleLine(true);
        editText.setInputType(1);
        editText.setImeOptions(6);
    }

    public int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    private void createXml(AlertDialog alertDialog, EditText editText, File file) {
        String editable = editText.getText().toString();
        if (TextUtils.isEmpty(editable.trim())) {
            CharSequence charSequence;
            editText.setError("invalid input");
            return;
        }
        if (!editable.endsWith(".xml")) {
            editable = new StringBuffer().append(editable).append(".xml").toString();
        }
        File file2 = new File(file, editable);
        if (file2.exists()) {
            editText.setError("file already exist");
            return;
        }
        String content;
        try {
            /*if (file2.createNewFile()) {
                file2.delete();
            }*/
            if (file.getName().startsWith("layout")) {
                content = getBasicLayoutCode();
            } else if (file.getName().startsWith("menu")) {
                content = getBasicMenuCode();
            } else if (file.getName().startsWith("values")) {
                content = getEmptyResCode();
            } else {
                content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
            }
            FileUtils.writeFile(file2, content);
            //Vl.j6(file2.getAbsolutePath(), editable);
            //abVar.j6(file2.getAbsolutePath());
            alertDialog.dismiss();
        } catch (Exception e) {
            editText.setError("Unable to write" + e.getMessage());
        }
    }
}

