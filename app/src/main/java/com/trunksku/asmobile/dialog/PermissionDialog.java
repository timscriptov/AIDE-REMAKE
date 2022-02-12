package com.trunksku.asmobile.dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.trunksku.asmobile.adapter.ManifestAdapter;
import com.trunksku.asmobile.adapter.SearchAdapter;
import com.trunksku.asmobile.model.Permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionDialog {
    private static List<Permission> permissionList;
    private Activity activity;
    private SearchAdapter adapter;
    private AlertDialog dialog;
    private ListView mv;
    private EditText searchView;
    private List<Permission> selectedList;

    public void updateDialogTitle() {
        dialog.setTitle(String.format("Add permission (%d selected items)", new Integer(adapter.getSelected().size())));
    }

    @SuppressLint("WrongConstant")
    public void show(final Activity activity, List<Permission> list, final Listener listener) {
        this.activity = activity;
        selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            selectedList.add(list.get(i));
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        searchView = new EditText(activity);
        searchView.setHint("Enter search...");
        linearLayout.addView(searchView, -1, -2);
        mv = new ListView(activity);
        mv.setFastScrollEnabled(true);
        linearLayout.addView(mv, -1, -1);
        dialog = new AlertDialog.Builder(activity).setTitle("Add permission").setView(linearLayout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i2) {
                listener.onAdd(selectedList);
            }
        }).setNegativeButton("Cancel", null).create();
        dialog.show();
        if (permissionList == null) {
            try {
                permissionList = new ArrayList<>();
                try {
                    Field[] declaredFields = Class.forName("android.Manifest$permission").getDeclaredFields();
                    for (Field field : declaredFields) {
                        try {
                            field.setAccessible(true);
                            Object obj = field.get(null);
                            if (obj != null && (obj instanceof String)) {
                                permissionList.add(new Permission(activity.getPackageManager(), (String) obj));
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                    Collections.sort(permissionList);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            } catch (Throwable th2) {
                th2.printStackTrace();
            }
        }
        adapter = new SearchAdapter(activity, permissionList);
        adapter.bind(searchView);
        adapter.setListener(new ManifestAdapter.Listener() {
            @Override
            public void onDataSetChanged() {
            }

            @Override
            public void onSelection(Permission permission, boolean z) {
                updateDialogTitle();
            }
        });
        adapter.setSelectMode(true);
        mv.setAdapter(adapter);
        mv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j) {
                Permission item = adapter.getItem(i2);
                boolean invertSelection = ((ManifestAdapter.ItemView) view).holder.invertSelection();
                adapter.setSelected(item, invertSelection);
                if (invertSelection) {
                    selectedList.add(item);
                } else {
                    ManifestAdapter.remove(selectedList, item);
                }
                adapter.notifyDataSetChanged();
            }
        });
        mv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i2, long j) {
                Permission item = adapter.getItem(i2);
                new AlertDialog.Builder(activity).setTitle(item.getLabel()).setMessage(item.getDescribe()).setPositiveButton(adapter.isSelected(item.getName()) ? "取消选择" : "选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i3) {
                        Permission item2 = adapter.getItem(i3);
                        boolean invertSelection = ((ManifestAdapter.ItemView) dialogInterface).holder.invertSelection();
                        adapter.setSelected(item2, invertSelection);
                        if (invertSelection) {
                            selectedList.add(item2);
                        } else {
                            ManifestAdapter.remove(selectedList, item2);
                        }
                    }
                }).setNegativeButton("Cancel", null).create().show();
                return true;
            }
        });
        int i2 = -1;
        for (int i3 = 0; i3 < permissionList.size(); i3++) {
            for (int i4 = 0; i4 < list.size(); i4++) {
                if (permissionList.get(i3).getName().equals(list.get(i4).getName())) {
                    adapter.setSelected(permissionList.get(i3), true);
                    if (i2 < 0) {
                        i2 = i3;
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (i2 > 0) {
            mv.setSelection(i2);
        }
        updateDialogTitle();
    }

    public interface Listener {
        void onAdd(List<Permission> list);
    }
}
