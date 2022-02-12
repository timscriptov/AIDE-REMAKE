package com.trunksku.asmobile.activity;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.adapter.ManifestAdapter;
import com.trunksku.asmobile.dialog.PermissionDialog;
import com.trunksku.asmobile.model.Permission;
import com.trunksku.asmobile.view.ManifestView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

public class ManifestActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener, Callback, ManifestAdapter.Listener {
    private ActionMode actionMode;
    private boolean dataChange;
    private TextView emptyView;
    private MenuItem item_save;
    private ManifestAdapter mainAdapter;
    private File manifestFile;
    private ManifestView manifestView;

    public boolean isDataChange() {
        return this.dataChange;
    }

    public void setDataChange(boolean z) {
        this.dataChange = z;
        if (this.item_save != null) {
            this.item_save.setEnabled(z);
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getContentView());
        getSupportActionBar().setTitle("Permission Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        load();
    }

    private void load() {
        try {
            if (getIntent().hasExtra("path")) {
                this.manifestFile = new File(getIntent().getStringExtra("path"));
            }
            this.mainAdapter = new ManifestAdapter(this, this.manifestView.load(this.manifestFile));
            this.mainAdapter.setListener(this);
            this.manifestView.setAdapter(this.mainAdapter);
            onDataSetChanged();
            this.manifestView.setOnItemClickListener(this);
            this.manifestView.setOnItemLongClickListener(this);
        } catch (Throwable th) {
            Toast.makeText(this, th.getMessage(), 0).show();
            finish();
        }
    }

    private View getContentView() {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
        Toolbar tb = new Toolbar(this);
        ll.addView(tb);
        setSupportActionBar(tb);
        FrameLayout frameLayout = new FrameLayout(this);
        this.manifestView = new ManifestView(this);
        frameLayout.addView(this.manifestView);
        this.emptyView = new TextView(this);
        this.emptyView.setGravity(17);
        this.emptyView.setText("No Permission");
        this.emptyView.setTextAppearance(this, 16973890);
        this.emptyView.setTextSize((float) 18);
        this.emptyView.setVisibility(8);
        frameLayout.addView(this.emptyView, -1, -1);
        ll.addView(frameLayout);

        return ll;
    }

    @Override
    public void onDataSetChanged() {
        if (this.mainAdapter.getCount() == 0) {
            this.emptyView.setVisibility(0);
        } else {
            this.emptyView.setVisibility(8);
        }
    }

    @Override
    public void onSelection(Permission permission, boolean z) {
        if (this.actionMode != null) {
            this.actionMode.setSubtitle(String.format("%d selected items", new Object[]{new Integer(this.mainAdapter.getSelected().size())}));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        final Permission item = this.mainAdapter.getItem(i);
        if (mainAdapter.isSelectMode()) {
            mainAdapter.setSelected(item, ((ManifestAdapter.ItemView) view).holder.invertSelection());
            mainAdapter.notifyDataSetChanged();
            return;
        }
        CharSequence charSequence;
        Builder message = new Builder(this).setTitle(item.getLabel()).setMessage(item.getDescribe());

        charSequence = "Delete";

        AlertDialog create = message.setPositiveButton(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                ManifestAdapter.remove(mainAdapter.getCurrentList(), item);
                mainAdapter.notifyDataSetChanged();
                setDataChange(true);
            }
        }).setNegativeButton(17039360, (OnClickListener) null).create();
        create.show();
        create.getButton(-1).setTextColor(-65536);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.mainAdapter.isSelectMode()) {
            return false;
        }
        multipleMode();
        ManifestAdapter.ViewHolder viewHolder = ((ManifestAdapter.ItemView) view).holder;
        Permission item = this.mainAdapter.getItem(i);
        viewHolder.setSelection(true);
        this.mainAdapter.setSelected(item, true);
        this.mainAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CharSequence charSequence;
        this.item_save = menu.add("Save").setOnMenuItemClickListener(new OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                save(false);
                return false;
            }
        }).setEnabled(false);
        this.item_save.setShowAsAction(2);
        charSequence = "Add";

        menu.add(charSequence).setOnMenuItemClickListener(new OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new PermissionDialog().show(ManifestActivity.this, mainAdapter.getCurrentList(), new PermissionDialog.Listener() {


                    @Override
                    public void onAdd(List<Permission> list) {
                        mainAdapter.setCurrentList(list);
                        mainAdapter.getSelected().clear();
                        mainAdapter.notifyDataSetChanged();
                        setDataChange(true);
                    }
                });
                return false;
            }
        }).setShowAsAction(2);
        menu.add("Comment permission").setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setChecked(!menuItem.isChecked());
                return getSp().edit().putBoolean("annotation_permission", menuItem.isChecked()).commit();
            }
        }).setCheckable(true).setChecked(isAnnotationPermission());
        menu.add("Multi select").setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                multipleMode();
                return false;
            }
        });
        // load();
        return super.onCreateOptionsMenu(menu);
    }

    private void multipleMode() {
        this.actionMode = startActionMode(this);
        this.actionMode.setTitle("Multi select");
        this.actionMode.setSubtitle((CharSequence) null);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        CharSequence charSequence;
        this.mainAdapter.setSelectMode(true);
        this.mainAdapter.notifyDataSetChanged();
        charSequence = "Delete";

        menu.add(charSequence).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                for (String remove : mainAdapter.getSelected().keySet()) {
                    ManifestAdapter.remove(mainAdapter.getCurrentList(), remove);
                }
                setDataChange(true);
                mainAdapter.notifyDataSetChanged();
                finish();
                return false;
            }
        }).setShowAsAction(2);
        menu.add(17039373).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Map selected = mainAdapter.getSelected();
                for (int i = 0; i < mainAdapter.getCount(); i++) {
                    selected.put(mainAdapter.getItem(i).getName(), new Boolean(true));
                }
                mainAdapter.notifyDataSetChanged();
                return false;
            }
        }).setShowAsAction(0);
        menu.add("Unselect all").setOnMenuItemClickListener(new OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mainAdapter.getSelected().clear();
                mainAdapter.notifyDataSetChanged();
                return false;
            }
        }).setShowAsAction(0);
        charSequence = "Invert Select";

        menu.add(charSequence).setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Map selected = mainAdapter.getSelected();
                for (int i = 0; i < mainAdapter.getCount(); i++) {
                    String name = mainAdapter.getItem(i).getName();
                    if (mainAdapter.isSelected(name)) {
                        selected.remove(name);
                    } else {
                        selected.put(name, true);
                    }
                }
                mainAdapter.notifyDataSetChanged();
                return false;
            }
        }).setShowAsAction(0);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        this.mainAdapter.setSelectMode(false);
        this.mainAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }

    boolean isAnnotationPermission() {
        return getSp().getBoolean("annotation_permission", true);
    }

    SharedPreferences getSp() {
        return getSharedPreferences("PermissionEditor", 0);
    }

    @Override
    public void onBackPressed() {
        if (isDataChange()) {
            CharSequence charSequence;
            Builder builder = new Builder(this);
            charSequence = "Save modified";

            builder = builder.setTitle(charSequence);
            charSequence = "Data has changed, do you want to save?";

            builder = builder.setMessage(charSequence);
            charSequence = "Save";

            builder = builder.setPositiveButton(charSequence, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    save(true);
                }
            });
            charSequence = "No save";

            builder.setNegativeButton(charSequence, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setResult(1);
                    finish();
                }
            }).create().show();
            return;
        }
        setResult(1);
        finish();
    }

    private void save(boolean z) {
        FileWriter fileWriter;
        try {
            String save = ManifestUtils.save(mainAdapter.getCurrentList(), DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile), isAnnotationPermission());
            fileWriter = new FileWriter(manifestFile);

            fileWriter.write(save);
            fileWriter.flush();
            setDataChange(false);
            /*Toast success = Toasty.success(Utils.isCN() ? "\u4fdd\u5b58\u6210\u529f" : "Saved success");
             success.setGravity(17, 0, 0);
             success.show();*/
            if (z) {
                setResult(1);
                finish();
            }
        } catch (Throwable th2) {
            System.out.println(th2.getMessage());
        }

    }

    public static class ManifestUtils {
        private static final int indent = 4;
        private final boolean isAnnotation;
        private final List<Permission> permission;
        private final StringBuffer sb = new StringBuffer();
        private boolean isAdded = false;

        public ManifestUtils(List<Permission> list, @NonNull Document document, boolean z) {
            this.permission = list;
            this.isAnnotation = z;
            this.sb.append("<?xml version='1.0' encoding='utf-8'?>");
            loadNodeList(sb, document.getChildNodes(), null, 0);
        }

        public static String save(List<Permission> list, Document document, boolean z) {
            return new ManifestUtils(list, document, z).result();
        }

        @NonNull
        private String getSpace(int i) {
            StringBuilder stringBuffer = new StringBuilder();
            for (int i2 = 0; i2 < i; i2++) {
                stringBuffer.append(" ");
            }
            return stringBuffer.toString();
        }

        private void loadNodeAttributes(StringBuffer stringBuffer, NamedNodeMap namedNodeMap, String str) {
            if (namedNodeMap != null) {
                int length = namedNodeMap.getLength();
                for (int i = 0; i < length; i++) {
                    Node item = namedNodeMap.item(i);
                    String nodeName = item.getNodeName();
                    String nodeValue = item.getNodeValue();
                    if (length > 1) {
                        stringBuffer.append("\n");
                        stringBuffer.append(str);
                    } else if (length > 0) {
                        stringBuffer.append(" ");
                    }
                    stringBuffer.append(nodeName);
                    stringBuffer.append("=\"");
                    stringBuffer.append(nodeValue);
                    stringBuffer.append("\"");
                }
            }
        }

        private void loadNodeList(StringBuffer stringBuffer, @NonNull NodeList nodeList, Node node, int i) {
            int length = nodeList.getLength();
            for (int i2 = 0; i2 < length; i2++) {
                Node item = nodeList.item(i2);
                String nodeName = item.getNodeName();
                NamedNodeMap attributes = item.getAttributes();
                if (!nodeName.startsWith("#") && (this.isAdded || ((node != null && !"manifest".equals(node.getNodeName())) || !"uses-permission".equals(nodeName) || attributes.getLength() > 1))) {
                    NodeList childNodes = item.getChildNodes();
                    String space = getSpace(i);
                    String space2 = getSpace(indent + i);
                    if (i > 0) {
                        stringBuffer.append("\n\n");
                    } else {
                        stringBuffer.append("\n");
                    }
                    if (this.permission.size() != 0 && !this.isAdded && node != null && "manifest".equals(node.getNodeName())) {
                        for (Permission permission : this.permission) {
                            if (this.isAnnotation) {
                                stringBuffer.append(space);
                                stringBuffer.append("<!--");
                                stringBuffer.append(permission.getLabel());
                                stringBuffer.append("-->\n");
                            }
                            stringBuffer.append(space);
                            stringBuffer.append("<uses-permission android:name=\"");
                            stringBuffer.append(permission.getName());
                            stringBuffer.append("\"/>");
                            stringBuffer.append("\n\n");
                        }
                        this.isAdded = true;
                    }
                    if (i > 0) {
                        stringBuffer.append(space);
                    }
                    stringBuffer.append("<");
                    stringBuffer.append(nodeName);
                    loadNodeAttributes(stringBuffer, attributes, space2);
                    if (childNodes.getLength() == 0) {
                        stringBuffer.append("/>");
                    } else {
                        stringBuffer.append(">");
                        loadNodeList(stringBuffer, childNodes, item, indent + i);
                        stringBuffer.append("\n\n");
                        if (i > 0) {
                            stringBuffer.append(space);
                        }
                        stringBuffer.append("</");
                        stringBuffer.append(nodeName);
                        stringBuffer.append(">");
                    }
                }
            }
        }

        public String result() {
            return this.sb.toString();
        }
    }
}

