package com.trunksku.asmobile.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class CreateClassDialog implements OnClickListener, DialogInterface.OnClickListener, OnItemSelectedListener {
    private final Activity mActivity;
    private final String mCreateInDir;
    private ArrayAdapter<Item> mAdapter;
    private Configuration mConfiguration = new Configuration(this);
    private AlertDialog mDialog;
    private DialogView mDialogView;

    private CreateDialog.Listener listen;

    public CreateClassDialog(Activity activity, String str, CreateDialog.Listener listen) {
        this.listen = listen;
        this.mActivity = activity;
        this.mCreateInDir = str;

    }

    public static void showDialog(Activity activity, String str, CreateDialog.Listener listen) {

        new CreateClassDialog(activity, str, listen).showDialog();
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void showDialog() {
        initDialogView();
        ScrollView scrollView = new ScrollView(this.mActivity);
        this.mDialogView.setPadding(dp2px((float) 24), 0, dp2px((float) 24), 0);
        scrollView.addView(this.mDialogView, -1, -2);
        this.mDialog = new Builder(this.mActivity).setTitle(R.string.add_new_class).setView(scrollView).setPositiveButton(R.string.create, this).setNegativeButton(17039360, this).create();
        this.mDialog.show();
       /* this.mDialogView.input.post(new Runnable() {

            @Override
            public void run() {
                KeyboardUtils.showSoftInput(this.this$0.mDialogView.input);
            }
        });*/
       /* this.mDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                InputMethodManager inputMethodManager = (InputMethodManager) Utils.getApp().getSystemService("input_method");
                if (KeyboardUtils.isSoftInputVisible(this.this$0.mActivity)) {
                    inputMethodManager.toggleSoftInput(1, 0);
                }
            }
        });*/
        Button button = this.mDialog.getButton(-1);
        button.setOnClickListener(this);
        this.mDialogView.input.addTextChangedListener(new DialogView3(this, button));
        this.mDialogView.input.setText("");
        this.mDialogView.input.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 6) {
                    onClick(mDialog.getButton(-1));
                }
                return mDialogView.input.getError() != null;
            }
        });
    }

    private void initDialogView() {
        this.mDialogView = new DialogView(this.mActivity);
        this.mAdapter = new ArrayAdapter(this.mActivity, 17367043);
        addAdapterData(this.mAdapter);
        this.mDialogView.spinner.setAdapter(this.mAdapter);
        this.mDialogView.spinner.setOnItemSelectedListener(this);
    }

    private void addAdapterData(ArrayAdapter<Item> arrayAdapter) {
        Collection<Item> arrayList = new ArrayList();
        arrayList.add(new Item(this, "Class - \u7c7b", new CreateBasicClass()));
        arrayList.add(new Item(this, "Activity - \u6d3b\u52a8", new CreateActivity()));
        arrayList.add(new Item(this, "Interface - \u63a5\u53e3", new CreateInterface()));
        arrayList.add(new Item(this, "Enum - \u679a\u4e3e", new CreateEnum()));
        arrayList.add(new Item(this, "Service - \u670d\u52a1", new CreateService()));
        arrayList.add(new Item(this, "BroadcastReceiver - \u5e7f\u64ad\u63a5\u6536\u5668", new CreateBroadcastReceiver()));
        for (Item item : arrayList) {
            int lastIndexOf = item.title.lastIndexOf(" - ");
            if (lastIndexOf > 0) {
                item.title = item.title.substring(0, lastIndexOf).trim();
            }
        }

        arrayAdapter.addAll(arrayList);
    }

    private String getSimpleName(String str) {
        String str2 = ".java";
        if (str.toLowerCase(Locale.ENGLISH).endsWith(str2)) {
            return str.substring(0, str.length() - str2.length());
        }
        return str;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == this.mDialog.getButton(-1).getId()) {
            String inputContent = this.mDialogView.getInputContent();
            if (inputContent.trim().length() == 0 || Character.isDigit(inputContent.charAt(0))) {
                mDialogView.input.setError(mActivity.getString(R.string.invalid_class_name));
                return;
            }
            String simpleName = getSimpleName(inputContent);
            if (simpleName.trim().length() == 0) {
                this.mDialogView.input.setError(mActivity.getString(R.string.invalid_class_name));
                return;
            }
            Item item = (Item) this.mAdapter.getItem(this.mDialogView.spinner.getSelectedItemPosition());
            upConfiguration();
            inputContent = item.createClass.makeCode(this.mConfiguration);
            File file = new File(this.mCreateInDir, new StringBuffer().append(simpleName).append(".java").toString());
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                this.mDialogView.input.setError(mActivity.getString(R.string.file_already_exist));
                return;
            }
            try {
                /*if (file.createNewFile()) {
                    file.delete();
                }*/
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append(inputContent);
                FileUtils.writeFile(file, inputContent);
                /*Vl.j6(file.getAbsolutePath(), stringBuffer.toString());
                this.mAbVar.j6(file.getAbsolutePath())

                if (AIDEUtils.getCurrentEditorFile() != null) {
                    inputContent = AIDEUtils.getCurrentEditorFile().getParentFile().getAbsolutePath();
                    if (!inputContent.equals(AIDEUtils.getFileBrowserCurrentDir())) {
                        AIDEUtils.setFileBrowserCurrentDir(inputContent);
                    }
                }*/
                listen.success();
                this.mDialog.dismiss();
                //LogUtils.i(new Object[]{new StringBuffer().append("mAbVar = ").append(this.mAbVar).toString(), new StringBuffer().append("\nfile = ").append(file).toString(), new StringBuffer().append("\nsb = ").append(stringBuffer).toString()});
            } catch (Exception e) {
                this.mDialogView.input.setError(mActivity.getString(R.string.unable_to_write));
            }
        }
    }

    private void upConfiguration() {
        boolean z = true;
        String inputContent = this.mDialogView.getInputContent();
        /*if (inputContent.contains("/")) {
            inputContent = gj.DW(U.vy().J8(), U.vy().tp(), new File(this.mCreateInDir, new File(inputContent).getParentFile().getAbsolutePath()).getAbsolutePath());
        } else {
            inputContent = gj.DW(U.vy().J8(), U.vy().tp(), this.mCreateInDir);
        }*/
        inputContent =/* findPackage(Project.dirJava, new File(mCreateInDir))*/ mCreateInDir.split("main/java/")[1].replace("/", ".");
        this.mConfiguration.packageName = inputContent;
        inputContent = getSimpleName(this.mDialogView.getInputContent());
        int lastIndexOf = inputContent.lastIndexOf("/");
        if (lastIndexOf >= 0) {
            inputContent = inputContent.substring(lastIndexOf + 1, inputContent.length());
        }
        this.mConfiguration.className = inputContent;
        Configuration configuration = this.mConfiguration;
        boolean z2 = this.mDialogView.cb_more.isChecked() && this.mDialogView.cb_addTag.isChecked();
        configuration.isAddTag = z2;
        Configuration configuration2 = this.mConfiguration;
        if (!(this.mDialogView.cb_more.isChecked() && this.mDialogView.cb_abstract.isChecked())) {
            z = false;
        }
        configuration2.abstractModifier = z;
    }

    public String findPackage(File javaDir, File currentFolder) {
        try {
            String path = currentFolder.getPath();
            if (path.startsWith(javaDir.getPath())) {
                String pkg = path.substring(javaDir.getPath().length() + 1);
                pkg = pkg.replace(File.separator, ".");
                return pkg;
            } else {
                return "";
            }
        } catch (Exception e) {
            return javaDir.getAbsolutePath() + " - " + currentFolder + e.getMessage();
        }
    }

    public static abstract class CreateClass {
        public abstract String makeCode(Configuration configuration);
    }

    public static class DialogView extends LinearLayout {
        private final EditText input;
        private final Spinner spinner;
        private final CheckBox cb_abstract;
        private final CheckBox cb_addTag;
        private final CheckBox cb_more;

        public DialogView(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            this.input = new EditText(context);
            this.input.setHint(R.string.class_name);
            this.input.setSingleLine(true);
            this.input.setInputType(1);
            this.input.setImeOptions(6);
            this.spinner = new Spinner(context);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            this.cb_abstract = new CheckBox(context);
            this.cb_abstract.setText(R.string.abstract_modification);
            this.cb_addTag = new CheckBox(context);
            this.cb_addTag.setText(R.string.add_tag);
            linearLayout.addView(this.cb_addTag);
            linearLayout.addView(this.cb_abstract);
            linearLayout.setVisibility(View.GONE);
            this.cb_more = new CheckBox(context);
            this.cb_more.setText(R.string.more_options);
            this.cb_more.setOnCheckedChangeListener(new DialogView2(this, linearLayout));
            addView(this.input);
            addView(this.spinner);
            addView(this.cb_more);
            addView(linearLayout);
        }

        public String getInputContent() {
            return this.input.getText().toString();
        }

        public static class DialogView2 implements OnCheckedChangeListener {
            private final DialogView dialogView;
            private final LinearLayout linearLayout;

            DialogView2(DialogView dialogView, LinearLayout linearLayout) {
                this.dialogView = dialogView;
                this.linearLayout = linearLayout;
            }

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                this.linearLayout.setVisibility(z ? View.VISIBLE : View.GONE);
            }
        }
    }

    public static class DialogView3 implements TextWatcher {
        private final CreateClassDialog createClassDialog;
        private final Button button;

        DialogView3(CreateClassDialog createClassDialog, Button button) {
            this.createClassDialog = createClassDialog;
            this.button = button;
        }


        @Override
        public void afterTextChanged(Editable editable) {
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            this.button.setEnabled(charSequence.length() != 0);
        }
    }

    public static class Configuration {
        private final CreateClassDialog createClassDialog;
        public boolean abstractModifier;
        public String className = "clazz";
        public boolean isAddTag;
        public String packageName = "pkg";

        public Configuration(CreateClassDialog createClassDialog) {
            this.createClassDialog = createClassDialog;
        }
    }

    class CreateActivity extends CreateClass {


        @Override
        public String makeCode(Configuration configuration) {
            String str;
            StringBuilder stringBuffer = new StringBuilder();
            StringBuilder stringBuffer2 = new StringBuilder();
            StringBuilder stringBuffer3 = new StringBuilder();
            StringBuilder stringBuffer4 = new StringBuilder();
            StringBuilder stringBuffer5 = new StringBuilder();
            StringBuilder stringBuffer6 = new StringBuilder();
            StringBuilder stringBuffer7 = new StringBuilder();
            StringBuffer stringBuffer8 = new StringBuffer();
            StringBuilder stringBuffer9 = new StringBuilder();
            StringBuilder stringBuffer10 = new StringBuilder();
            StringBuilder stringBuffer11 = new StringBuilder();
            StringBuilder stringBuffer12 = new StringBuilder();
            StringBuffer stringBuffer13 = new StringBuffer();
            StringBuilder stringBuffer14 = new StringBuilder();
            StringBuilder stringBuffer15 = new StringBuilder();
            StringBuilder stringBuffer16 = new StringBuilder();
            StringBuilder stringBuffer17 = new StringBuilder();
            if (TextUtils.isEmpty(configuration.packageName)) {
                str = "";
            } else {
                str = "package " + configuration.packageName + ";" + "\n\n";
            }
            stringBuffer13 = stringBuffer13.append(stringBuffer14.append(stringBuffer15.append(stringBuffer16.append(stringBuffer17.append(str).append("import android.app.Activity;").toString()).append("\nimport android.os.Bundle;").toString()).append("\n").toString()).append("\npublic ").toString());
            if (configuration.abstractModifier) {
                str = "abstract ";
            } else {
                str = "";
            }
            stringBuffer8 = stringBuffer8.append(stringBuffer9.append(stringBuffer10.append(stringBuffer11.append(stringBuffer12.append(stringBuffer13.append(str).toString()).append("class ").toString()).append(configuration.className).toString()).append(" extends Activity {").toString()).append("\n    ").toString());
            if (configuration.isAddTag) {
                str = "\n    public static final String TAG = \"" + configuration.className + "\";\n    ";
            } else {
                str = "";
            }
            return stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(stringBuffer7.append(stringBuffer8.append(str).toString()).append("\n    @Override").toString()).append("\n    protected void onCreate(Bundle savedInstanceState) {").toString()).append("\n        super.onCreate(savedInstanceState);").toString()).append("\n        ").toString()).append("\n    }").toString()).append("\n    ").toString()).append("\n}").toString();
        }
    }

    class CreateBasicClass extends CreateClass {
        @Override
        public String makeCode(Configuration configuration) {
            StringBuilder stringBuffer = new StringBuilder();
            if (!TextUtils.isEmpty(configuration.packageName)) {
                stringBuffer.append(String.format("package %s;", new Object[]{configuration.packageName}));
                stringBuffer.append("\n\n");
            }
            stringBuffer.append("public ");
            if (configuration.abstractModifier) {
                stringBuffer.append("abstract ");
            }
            stringBuffer.append("class ");
            stringBuffer.append(configuration.className);
            stringBuffer.append(" ");
            stringBuffer.append("{\n    \n    ");
            if (configuration.isAddTag) {
                stringBuffer.append(String.format("public static final String TAG = \"%s\";\n    \n    ", new Object[]{configuration.className}));
            }
            stringBuffer.append("\n    \n}");
            return stringBuffer.toString();
        }
    }

    class CreateBroadcastReceiver extends CreateClass {


        @Override
        public String makeCode(Configuration configuration) {
            String str;
            StringBuilder stringBuffer = new StringBuilder();
            StringBuilder stringBuffer2 = new StringBuilder();
            StringBuilder stringBuffer3 = new StringBuilder();
            StringBuilder stringBuffer4 = new StringBuilder();
            StringBuilder stringBuffer5 = new StringBuilder();
            StringBuilder stringBuffer6 = new StringBuilder();
            StringBuffer stringBuffer7 = new StringBuffer();
            StringBuilder stringBuffer8 = new StringBuilder();
            StringBuilder stringBuffer9 = new StringBuilder();
            StringBuilder stringBuffer10 = new StringBuilder();
            StringBuilder stringBuffer11 = new StringBuilder();
            StringBuffer stringBuffer12 = new StringBuffer();
            StringBuilder stringBuffer13 = new StringBuilder();
            StringBuilder stringBuffer14 = new StringBuilder();
            StringBuilder stringBuffer15 = new StringBuilder();
            StringBuilder stringBuffer16 = new StringBuilder();
            StringBuilder stringBuffer17 = new StringBuilder();
            if (TextUtils.isEmpty(configuration.packageName)) {
                str = "";
            } else {
                str = "package " + configuration.packageName + ";" + "\n\n";
            }
            stringBuffer12 = stringBuffer12.append(stringBuffer13.append(stringBuffer14.append(stringBuffer15.append(stringBuffer16.append(stringBuffer17.append(str).append("import android.content.BroadcastReceiver;").toString()).append("\nimport android.content.Context;").toString()).append("\nimport android.content.Intent;").toString()).append("\n").toString()).append("\npublic ").toString());
            if (configuration.abstractModifier) {
                str = "abstract ";
            } else {
                str = "";
            }
            stringBuffer7 = stringBuffer7.append(stringBuffer8.append(stringBuffer9.append(stringBuffer10.append(stringBuffer11.append(stringBuffer12.append(str).toString()).append("class ").toString()).append(configuration.className).toString()).append(" extends BroadcastReceiver {").toString()).append("\n    ").toString());
            if (configuration.isAddTag) {
                str = "\n    public static final String TAG = \"" + configuration.className + "\";\n    ";
            } else {
                str = "";
            }
            return stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(stringBuffer7.append(str).toString()).append("\n    @Override").toString()).append("\n    public void onReceive(Context context, Intent intent) {").toString()).append("\n        ").toString()).append("\n    }").toString()).append("\n    ").toString()).append("\n}").toString();
        }
    }

    class CreateEnum extends CreateClass {

        @Override
        public String makeCode(Configuration configuration) {
            return (!TextUtils.isEmpty(configuration.packageName) ? "package " + configuration.packageName + ";" + "\n\n" : "") + "public " + (configuration.abstractModifier ? "abstract " : "") + "enum " + configuration.className + " {" + "\n    " + "\n    \n    \n    ;\n    " + (configuration.isAddTag ? "\n    public static final String TAG = \"" + configuration.className + "\";\n    " : "") + "\n    \n    " + "\n}";
        }
    }

    class CreateInterface extends CreateClass {

        @Override
        public String makeCode(Configuration configuration) {
            return (!TextUtils.isEmpty(configuration.packageName) ? "package " + configuration.packageName + ";" + "\n\n" : "") + "public " + (configuration.abstractModifier ? "abstract " : "") + "interface " + configuration.className + " {" + "\n    " + "\n    " + (configuration.isAddTag ? "public static final String TAG = \"" + configuration.className + "\";\n    \n    " : "") + "\n    " + "\n}";
        }
    }

    class CreateService extends CreateClass {

        @Override
        public String makeCode(Configuration configuration) {
            String str;
            StringBuilder stringBuffer = new StringBuilder();
            StringBuilder stringBuffer2 = new StringBuilder();
            StringBuilder stringBuffer3 = new StringBuilder();
            StringBuilder stringBuffer4 = new StringBuilder();
            StringBuilder stringBuffer5 = new StringBuilder();
            StringBuilder stringBuffer6 = new StringBuilder();
            StringBuilder stringBuffer7 = new StringBuilder();
            StringBuffer stringBuffer8 = new StringBuffer();
            StringBuilder stringBuffer9 = new StringBuilder();
            StringBuilder stringBuffer10 = new StringBuilder();
            StringBuilder stringBuffer11 = new StringBuilder();
            StringBuilder stringBuffer12 = new StringBuilder();
            StringBuffer stringBuffer13 = new StringBuffer();
            StringBuilder stringBuffer14 = new StringBuilder();
            StringBuffer stringBuffer15 = new StringBuffer();
            StringBuffer stringBuffer16 = new StringBuffer();
            StringBuilder stringBuffer17 = new StringBuilder();
            StringBuilder stringBuffer18 = new StringBuilder();
            if (TextUtils.isEmpty(configuration.packageName)) {
                str = "";
            } else {
                str = "package " + configuration.packageName + ";" + "\n\n";
            }
            StringBuffer stringBuffer19 = stringBuffer16;
            stringBuffer19 = stringBuffer15;
            stringBuffer13 = stringBuffer13.append(stringBuffer14.append(stringBuffer19.append(stringBuffer19.append(stringBuffer17.append(stringBuffer18.append(str).append("import android.app.Service;").toString()).append("\nimport android.content.Intent;").toString()).append("\nimport android.os.IBinder;").toString()).append("\n").toString()).append("\npublic ").toString());
            if (configuration.abstractModifier) {
                str = "abstract ";
            } else {
                str = "";
            }
            stringBuffer8 = stringBuffer8.append(stringBuffer9.append(stringBuffer10.append(stringBuffer11.append(stringBuffer12.append(stringBuffer13.append(str).toString()).append("class ").toString()).append(configuration.className).toString()).append(" extends Service {").toString()).append("\n    ").toString());
            if (configuration.isAddTag) {
                str = "\n    public static final String TAG = \"" + configuration.className + "\";\n    ";
            } else {
                str = "";
            }
            return stringBuffer.append(stringBuffer2.append(stringBuffer3.append(stringBuffer4.append(stringBuffer5.append(stringBuffer6.append(stringBuffer7.append(stringBuffer8.append(str).toString()).append("\n    @Override").toString()).append("\n    public IBinder onBind(Intent intent) {").toString()).append("\n        ").toString()).append("\n        return null;").toString()).append("\n    }").toString()).append("\n    ").toString()).append("\n}").toString();
        }


    }

    class Item {
        public final CreateClass createClass;
        private final CreateClassDialog createClassDialog;
        public String title;


        public Item(CreateClassDialog createClassDialog, String str, CreateClass createClass) {
            this.createClassDialog = createClassDialog;
            this.title = str;
            this.createClass = createClass;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}

