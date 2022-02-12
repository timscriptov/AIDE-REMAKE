package com.trunksku.asmobile.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.trunksku.asmobile.adapter.TemplateAdapter;
import com.trunksku.asmobile.model.Category;
import com.trunksku.asmobile.utils.FileUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectDialog {

    public static void showCreateDialog(Activity activity) {
        CharSequence charSequence;
        final ExpandableListView customExpandableListView = new ExpandableListView(activity);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        charSequence = "Create New Project";

        builder = builder.setTitle(charSequence).setView(customExpandableListView);

        charSequence = "More";

        AlertDialog create = builder/*setPositiveButton(charSequence, null)
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
                    template.add(temp);
                }

                catg.setTemplates(template);
                list.add(catg);
            }
            TemplateAdapter templateAdapter = new TemplateAdapter(activity);
           /* Category catg = new Category();
            catg.setIcon("test");
            catg.setTitle("test");
            templateAdapter.add(catg);*/
            templateAdapter.setList(list);
            customExpandableListView.setAdapter(templateAdapter);
            //customExpandableListView.setOnChildClickListener(new AnonymousClass100000002(templateAdapter, activity, create));
            // if (templateAdapter.getGroupCount() > 0) {
            // customExpandableListView.expandGroup(0, false);
            // }
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

}
