package com.trunksku.asmobile.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.trunksku.asmobile.adapter.DependencieAdapter;
import com.trunksku.asmobile.model.ArtifactMapping;
import com.trunksku.asmobile.model.Dependencie;
import com.trunksku.asmobile.view.ItemView;

import java.util.ArrayList;
import java.util.List;

public class DependencieDialog implements OnClickListener, OnItemClickListener, DependencieAdapter.OnSelectionListener {
    static final int FLAG_SHOW_ANDROIDX = 2;
    static final int FLAG_SHOW_APPCOMPAT = 1;
    static List<Dependencie> list_androidx = new ArrayList();
    static List<Dependencie> list_appcompat = new ArrayList();
    private static List<Dependencie> dependencieList = new ArrayList();

    final Activity activity;
    int currentShow = FLAG_SHOW_APPCOMPAT;
    private DependencieAdapter adapter;
    private AlertDialog dialog;
    private DialogView dialogView;

    private DependencieDialog.Listener listener;

    public DependencieDialog(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public static int dp2px(float f) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
    }

    public void show(List<Dependencie> selected) {
        list_androidx = new ArrayList<>();
        list_appcompat = new ArrayList<>();
        for (int i = 0; i < ArtifactMapping.DATAS.length; i += FLAG_SHOW_APPCOMPAT) {
            list_appcompat.add(new Dependencie(ArtifactMapping.DATAS[i][0]));
            list_androidx.add(new Dependencie(ArtifactMapping.DATAS[i][1], true));
        }
        this.dialogView = new DialogView(this.activity);
        this.dialog = new Builder(this.activity).setTitle("Add Dependency").setView(this.dialogView).setPositiveButton("Add", this).setNegativeButton(android.R.string.cancel, (OnClickListener) null).setNeutralButton("More", (OnClickListener) null).create();
        this.dialog.show();
        this.dialog.getButton(-1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                List selectedList = adapter.getSelectedList();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < selectedList.size(); i += DependencieDialog.FLAG_SHOW_APPCOMPAT) {
                    Dependencie dependencie = (Dependencie) selectedList.get(i);
                    sb.append(dependencie);
                    // Toast.makeText(activity, dependencie.toString(), 1).show();
                }
                listener.onSave(selectedList);
                dialog.dismiss();
                //  System.out.println(sb);
                    /*if (selectedList.size() == 0) {
						Toasty.warning(Utils.isCN() ? "未选择依赖" : "No dependency selected").show();
						return;
					}

					String fileBrowserCurrentDir = AIDEUtils.getFileBrowserCurrentDir();
					ReflectUtils reflect = ReflectUtils.reflect("abcd.Vi");
					Object[] objArr = new Object[DependencieDialog.FLAG_SHOW_APPCOMPAT];
					objArr[0] = fileBrowserCurrentDir;
					if (((Boolean) reflect.method("BT", objArr).get()).booleanValue()) {
						objArr = new Object[DependencieDialog.FLAG_SHOW_APPCOMPAT];
						objArr[0] = fileBrowserCurrentDir;
						String str = (String) reflect.method("v5", objArr).get();
						for (int i = 0; i < selectedList.size(); i += DependencieDialog.FLAG_SHOW_APPCOMPAT) {
							Dependencie dependencie = (Dependencie) selectedList.get(i);
							Object[] objArr2 = new Object[DependencieDialog.FLAG_SHOW_APPCOMPAT];
							objArr2[0] = str;
							objArr2 = new Object[DependencieDialog.FLAG_SHOW_APPCOMPAT];
							objArr2[0] = dependencie.toString();
							ReflectUtils.reflect("abcd.Be").newInstance().method("j6", objArr2).method("Hw", objArr2);
						}
						ReflectUtils.reflect("com.aide.ui.U").method("vy").method("yS");
						Toasty.success(Utils.isCN() ? "已添加到项目" : "Added to project").show();
						this.this$0.dialog.dismiss();
					}*/
            }

        });
        this.dialog.getButton(-3).setOnClickListener(new View.OnClickListener() {
            private DependencieDialog dependencieDialog;

            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(dependencieDialog.activity, view);
                Menu menu = popupMenu.getMenu();
                menu.add(DependencieDialog.FLAG_SHOW_APPCOMPAT, 0, 0, "AppCompat").setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        toggleList(DependencieDialog.FLAG_SHOW_APPCOMPAT, getAppCompatList());
                        return false;
                    }

                });
                menu.add(DependencieDialog.FLAG_SHOW_APPCOMPAT, 0, 0, "AndroidX").setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        toggleList(DependencieDialog.FLAG_SHOW_ANDROIDX, getAndroidXList());
                        return false;
                    }

                });
                menu.setGroupCheckable(DependencieDialog.FLAG_SHOW_APPCOMPAT, true, true);
                popupMenu.show();
                if (dependencieDialog.currentShow == DependencieDialog.FLAG_SHOW_APPCOMPAT) {
                    menu.getItem(0).setChecked(true);
                } else if (dependencieDialog.currentShow == DependencieDialog.FLAG_SHOW_ANDROIDX) {
                    menu.getItem(DependencieDialog.FLAG_SHOW_APPCOMPAT).setChecked(true);
                }
            }

            private void toggleList(int i, List<Dependencie> list) {
                dependencieDialog.currentShow = i;
                dependencieDialog.adapter.setBackuplist(list);
                dependencieDialog.adapter.setList(list);
                dependencieDialog.adapter.notifyDataSetChanged();
                String editable = dependencieDialog.dialogView.searchView.getText().toString();
                if (editable.trim().length() != 0) {
                    dependencieDialog.adapter.search(editable);
                }
            }

        });
        this.dialogView.getListView().setOnItemClickListener(this);
        this.dialogView.getSearchView().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                adapter.search(charSequence.toString());
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        this.adapter = new DependencieAdapter(this.activity, this.currentShow == FLAG_SHOW_ANDROIDX ? getAndroidXList() : getAppCompatList());
        this.adapter.setOnSelectionListener(this);
        this.dialogView.setAdapter(this.adapter);
        updateDialogTitle();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Dependencie item = this.adapter.getItem(i);
        ItemView itemView = (ItemView) view;
        boolean z = !this.adapter.isSelection(item);
        itemView.setChecked(z);
        this.adapter.setSelected(item, z);
    }

    List<Dependencie> getList(String[] strArr) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < strArr.length; i += FLAG_SHOW_APPCOMPAT) {
            arrayList.add(new Dependencie(strArr[i]));
        }
        return arrayList;
    }

    List<Dependencie> getAndroidXList() {
        return list_androidx;
    }

    List<Dependencie> getAppCompatList() {
        return list_appcompat;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
        }
    }

    @Override
    public void onSelection(Dependencie dependencie, boolean z) {
        updateDialogTitle();
    }

    void updateDialogTitle() {
        AlertDialog alertDialog = this.dialog;
        String str = "Add Dependency (%d items selected)";
        Object[] objArr = new Object[FLAG_SHOW_APPCOMPAT];
        objArr[0] = new Integer(this.adapter.selectedMap.size());
        alertDialog.setTitle(String.format(str, objArr));
    }

    public interface Listener {
        public void onSave(List<Dependencie> list);
    }

    static class DialogView extends LinearLayout {
        Context ctx;
        private ListView listView;
        private EditText searchView;

        public DialogView(Context context) {
            super(context);
            this.ctx = context;
            setOrientation(LinearLayout.VERTICAL);
            this.searchView = new EditText(context);
            this.searchView.setHint("input search content..");
            this.searchView.setSingleLine(true);
            this.listView = new ListView(context);
            this.listView.setFastScrollEnabled(true);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setPadding(dp2px((float) 16), 0, dp2px((float) 16), 0);
            linearLayout.addView(this.searchView, -1, -2);
            addView(linearLayout, -1, -2);
            addView(this.listView, -1, -2);
        }

        public ListView getListView() {
            return this.listView;
        }

        public EditText getSearchView() {
            return this.searchView;
        }

        public void setAdapter(ListAdapter listAdapter) {
            this.listView.setAdapter(listAdapter);
        }
    }
}
