package com.trunksku.asmobile.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.adapter.TemplateAdapter;
import com.trunksku.asmobile.async.CreateProjectTask;
import com.trunksku.asmobile.model.Category;
import com.trunksku.asmobile.utils.FileUtil;
import com.trunksku.asmobile.utils.ProjectFileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateProject {

    public static void showCreateDialog(final Activity activity, final CreateProjectTask.Listener listener) {
        final ExpandableListView customExpandableListView = new ExpandableListView(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder = builder.setTitle(R.string.create_new_project).setView(customExpandableListView);

        final AlertDialog create = builder/*setPositiveButton(charSequence, null)
             .setNegativeButton(17039360, null)*/
                /*.setNeutralButton("Git", new OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                 //new Pf().run();
                 }
                 })*/.create();
        create.show();
        try {
            List<Category> list = new ArrayList<>();
            String data = FileUtil.readFileAssetString(activity, "data/template.txt");
            JSONArray jarr = new JSONObject(data).getJSONArray("category");
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);

                Category catg = new Category();
                //catg.setIcon(jobj.getString("icon"));
                catg.setTitle(jobj.getString("title"));
                //catg.setDescribes("");
                List<Category.Templates> template = new ArrayList<>();
                JSONArray jarr2 = jobj.getJSONArray("templates");
                for (int i2 = 0; i2 < jarr2.length(); i2++) {
                    JSONObject jobj2 = jarr2.getJSONObject(i2);
                    //Toast.makeText(activity, jobj2.getString("icon"), 1).show();
                    Category.Templates temp = new Category.Templates();
                    temp.setTitle(jobj2.getString("title"));
                    temp.setIcon(jobj2.getString("icon"));
                    temp.setDescribes(jobj2.getString("describes"));
                    temp.setProject(jobj2.getString("template"));
                    template.add(temp);
                }

                catg.setTemplates(template);
                list.add(catg);
            }
            final TemplateAdapter templateAdapter = new TemplateAdapter(activity);

            templateAdapter.setList(list);
            customExpandableListView.setAdapter(templateAdapter);
            customExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView p1, View p2, int p3, int p4, long p5) {
                    String template = templateAdapter.getChild(p3, p4).getProject();
                    createProject(activity, template, listener);
                    create.dismiss();
                    return true;
                }
            });
            //customExpandableListView.setOnChildClickListener(new AnonymousClass100000002(templateAdapter, activity, create));
            /*if (templateAdapter.getGroupCount() > 0) {
                customExpandableListView.expandGroup(0, false);
            }*/
        } catch (Throwable e) {
            Toast.makeText(activity, e.getMessage(), 1).show();
            //throw new NoClassDefFoundError(e.getMessage());
        }
        customExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int p1) {
                ExpandableListAdapter expandableListAdapter = customExpandableListView.getExpandableListAdapter();
                int i2 = 0;
                while (i2 < expandableListAdapter.getGroupCount()) {
                    if (i2 != p1 && customExpandableListView.isGroupExpanded(i2)) {
                        customExpandableListView.collapseGroup(i2);
                    }
                    i2++;
                }
            }
        });
    }

    public static void createProject(final Context c, final String template, final CreateProjectTask.Listener listener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(c);
        CreateProjectView createProjectView = new CreateProjectView(c);
        adb.setCancelable(true).setTitle(R.string.create_project).setView(createProjectView);
        adb.setPositiveButton(R.string.create, null);
        adb.setNegativeButton(android.R.string.cancel, null);
        //Bitmap icon = PUtils.getIcon(activity, templates.getIcon());
        /*if (icon != null) {
		 negativeButton.setIcon(new BitmapDrawable(icon));
		 }*/
        final AlertDialog create = adb.create();
        create.show();
        final EditText editText = createProjectView.input_projectName.getEditText();
        final EditText editText2 = createProjectView.input_packageName.getEditText();
        //final CheckBox androidx = createProjectView.ck_androidx;
		/* if (project.isNoPkg()) {
		 editText2.setEnabled(false);
		 createProjectView.input_packageName.setVisibility(8);
		 }*/
        Button button = create.getButton(-1);
        // AnonymousClass100000004 anonymousClass100000004 = new AnonymousClass100000004(editText, file, button, editText2);
        // editText.addTextChangedListener(anonymousClass100000004);
        //editText2.addTextChangedListener(anonymousClass100000004);
        editText.setText(getCreateLocation(ProjectFileUtil.getProjectHome(), "Application", 1).getName());
        editText2.setText("com.mycompany.myapp");
        editText.selectAll();

        // editText.post(new AnonymousClass100000005(editText));
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View p1) {
                create.dismiss();
                CreateProjectTask createPT = new CreateProjectTask(c, new File(ProjectFileUtil.getProjectHome(), editText.getText().toString()), listener);
                createPT.setTemplate(template);
                createPT.setProjectName(editText.getText().toString());
                createPT.setPackageName(editText2.getText().toString());
                createPT.execute();
            }
        });
        create.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 4 || keyEvent.getAction() != 0) {
                    return false;
                }
                dialogInterface.dismiss();
                return true;
            }
        });

    }

    private static File getCreateLocation(File file, String str, int i) {
        File file2 = new File(file, i > 1 ? str + i : str);
        return !file2.exists() ? file2 : getCreateLocation(file, str, i + 1);
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    private boolean canWrite(File file) {
        File file2 = new File(file, UUID.randomUUID().toString().replace("/", "_"));
        //LogUtils.iTag("testCanWrite", new Object[]{file2.getAbsolutePath()});
        if (file2.getParentFile() != null) {
            file2.getParentFile().mkdirs();
        }
        try {
            if (file2.createNewFile()) {
                //FileUtils.delete(file2);
                return true;
            }
        } catch (Throwable th) {
            //FileUtils.delete(file2);
        }
        //FileUtils.delete(file2);
        return false;
    }

    private static class CreateProjectView extends LinearLayout {
        private final TitleEditText input_packageName;
        private final TitleEditText input_projectName;

        //private CheckBox ck_androidx;

        public CreateProjectView(Context context) {
            super(context);
            this.input_projectName = new TitleEditText(context, context.getString(R.string.project_name));
            this.input_packageName = new TitleEditText(context, context.getString(R.string.package_name));
            //ck_androidx = new CheckBox(context);
            //ck_androidx.setText("Use AndroidX");
            setOrientation(LinearLayout.VERTICAL);
            addView(this.input_projectName);
            addView(this.input_packageName);
            //addView(ck_androidx);
            setPadding(dp2px((float) 24), dp2px((float) 10), dp2px((float) 24), 0);
        }
    }



	/*public static Bitmap getIcon(Context context, String str) {
	 try {
	 return BitmapFactory.decodeStream(context.getAssets().open(getAssetsFile(new StringBuffer().append("icons/").append(str).toString())));
	 } catch (IOException e) {
	 return (Bitmap) null;
	 }
	 }*/

    private static class TitleEditText extends LinearLayout {
        private final EditText editText;

        public TitleEditText(Context context, CharSequence charSequence) {
            super(context);
            this.editText = new EditText(context);
            setOrientation(1);
            TextView textView = new TextView(context);
            textView.setText(charSequence);
            textView.setTextSize((float) 17);
            addView(textView);
            addView(this.editText, -1, -2);
        }

        public EditText getEditText() {
            return this.editText;
        }
    }

	/* public static String getAssetsFile(String str) {
	 return Utils.getAssetsDataFile(new StringBuffer().append("templates/").append(str).toString());
	 }*/


}
