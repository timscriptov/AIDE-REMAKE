package com.trunksku.asmobile.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.adapter.HighlightAdapter;
import com.trunksku.asmobile.editor.highlight.ColorDefault;
import com.trunksku.asmobile.editor.highlight.Colors;
import com.trunksku.asmobile.model.Highlight;

import java.util.ArrayList;
import java.util.List;

public class HighlightActivity extends AppCompatActivity implements OnItemClickListener, TabListener {


    private HighlightAdapter adapter;
    private boolean isDataChangeed = false;
    private List<Highlight> list = new ArrayList();
    private ListView mListView;

    
    

    /*class AnonymousClass100000001 implements OnClickListener {
        private final HighlightActivity this$0;
        private final Item val$item;

        AnonymousClass100000001(HighlightActivity highlightActivity, Item item) {
            this.this$0 = highlightActivity;
            this.val$item = item;
        }

        static HighlightActivity access$0(AnonymousClass100000001 anonymousClass100000001) {
            return anonymousClass100000001.this$0;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            HighlightUtils.getHSp().edit().putString(this.val$item.getSpKey(this.this$0.adapter.isLight()), this.val$item.getDefaultColor()).commit();
            this.this$0.adapter.notifyDataSetChanged();
            this.this$0.isDataChangeed = true;
        }
    }*/

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction p2) {
        //toggleList(/*"p1".equals(tab.getTag())*/true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab p1, android.support.v4.app.FragmentTransaction p2) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab p1, android.support.v4.app.FragmentTransaction p2) {
    }


    @Override
    protected void onCreate(Bundle bundle) {
        //ADRTLogCatReader.onContext(this, "com.aide.ui");
        super.onCreate(bundle);
        setTheme(R.style.Theme_AppCompat);
        this.mListView = new ListView(this);
        setContentView(this.mListView);
        this.mListView.setOnItemClickListener(this);
        initActionBar();
        toggleList(false);
        this.mListView.setAdapter(this.adapter);

    }


    private void toggleList(boolean z) {
        this.list = new ArrayList();
        Colors[] values = new ColorDefault().values();
        for (Colors colors : values) {
            this.list.add(new Highlight(colors.label, colors.name, z ? colors.lightColor : colors.darkColor));
        }
        if (this.adapter == null) {
            this.adapter = new HighlightAdapter(this, this.list);
            this.adapter.setIsLight(z);
            adapter.notifyDataSetChanged();
            return;
        }
        this.adapter.setIsLight(z);
        this.adapter.setList(this.list);
        this.adapter.notifyDataSetChanged();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("HighLight");
        actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setNavigationMode(2);
       /* if (!AppTheme.THEME_INDIGO.equals(AppTheme.getThemeName())) {
            actionBar.addTab(actionBar.newTab().setText(Utils.isCN() ? "\u4eae\u4e3b\u9898\u9ad8\u4eae\u914d\u7f6e" : "Light").setTag("p1").setTabListener(this));
        }*/
        // actionBar.addTab(actionBar.newTab().setText("Dark").setTag("p2").setTabListener(this));
        /*if (!AppTheme.THEME_INDIGO.equals(AppTheme.getThemeName())) {
            actionBar.getTabAt(0).select();
            if (!AIDEUtils.isLightTheme()) {
                actionBar.getTabAt(1).select();
            }
        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
       /* Item item = this.adapter.getItem(i);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, Color.parseColor(item.getColor(this.adapter.isLight())));
        colorPickerDialog.setTitle(item.getTitle());
        colorPickerDialog.setOnColorChangedListener(new AnonymousClass100000000(this, item));
        colorPickerDialog.setButton(-3, getResources().getString(2131560101), new AnonymousClass100000001(this, item));
        colorPickerDialog.show();*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CharSequence charSequence;
        if (Utils.isCN()) {
            charSequence = "\u8fd8\u539f";
        } else {
            charSequence = "Restore";
        }
        menu.add(charSequence).setOnMenuItemClickListener(new OnMenuItemClickListener(this) {
                private final HighlightActivity this$0;

                {
                    this.this$0 = r1;
                }

                static HighlightActivity access$0(AnonymousClass100000002 anonymousClass100000002) {
                    return anonymousClass100000002.this$0;
                }

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Editor edit = HighlightUtils.getHSp().edit();
                    for (Item item : this.this$0.list) {
                        edit.putString(item.getSpKey(this.this$0.adapter.isLight()), item.getDefaultColor());
                    }
                    edit.commit();
                    this.this$0.onTabSelected(this.this$0.getActionBar().getSelectedTab(), (FragmentTransaction) null);
                    this.this$0.isDataChangeed = true;
                    Toasty.success(Utils.isCN() ? "\u5df2\u8fd8\u539f\u81f3\u9ed8\u8ba4" : "Restored to default").show();
                    return false;
                }
            });
        if (Utils.isCN()) {
            charSequence = "\u8fd8\u539f\u4eae/\u6697";
        } else {
            charSequence = "Reset light/dark";
        }
        menu.add(charSequence).setOnMenuItemClickListener(new OnMenuItemClickListener(this) {
                private final HighlightActivity this$0;

                {
                    this.this$0 = r1;
                }

                static HighlightActivity access$0(AnonymousClass100000003 anonymousClass100000003) {
                    return anonymousClass100000003.this$0;
                }

                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Editor edit = HighlightUtils.getHSp().edit();
                    edit.clear();
                    Colors[] values = HighlightUtils.getHighlightColor().values();
                    for (Colors colors : values) {
                        edit.putString(HighlightUtils.getSpKey(colors.name, true), colors.lightColor);
                        edit.putString(HighlightUtils.getSpKey(colors.name, false), colors.darkColor);
                    }
                    edit.commit();
                    this.this$0.onTabSelected(this.this$0.getActionBar().getSelectedTab(), (FragmentTransaction) null);
                    this.this$0.isDataChangeed = true;
                    Toasty.success(Utils.isCN() ? "\u5df2\u8fd8\u539f\u81f3\u9ed8\u8ba4" : "Reset to default").show();
                    return false;
                }
            });
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        /*if (getActionBar().getTabCount() < 2 || !getActionBar().getSelectedTab().getTag().equals("p2")) {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            return;
        }
        getActionBar().getTabAt(0).select();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (this.isDataChangeed) {
            AIDEUtils.getAIDEEditorPager().Zo();
            AIDEUtils.getMainActivity().setEditorBackground();
        }*/
    }
}

