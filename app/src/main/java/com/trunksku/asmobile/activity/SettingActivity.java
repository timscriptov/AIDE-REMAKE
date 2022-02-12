package com.trunksku.asmobile.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.fragment.SettingFragment;
import com.trunksku.asmobile.utils.FileUtil;
import com.trunksku.asmobile.utils.Utils;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
        setupToolbar("Setting");
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.content, new SettingFragment());
        beginTransaction.commit();


    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (title != null) {
            getSupportActionBar().setTitle(title);
        } else {

            ActivityInfo activityInfo;
            try {
                activityInfo = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
                String currentTitle = activityInfo.loadLabel(getPackageManager()).toString();
                getSupportActionBar().setTitle(currentTitle);

            } catch (PackageManager.NameNotFoundException ignored) {

            }

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(Menu.NONE, 5, Menu.NONE, "Update Log").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                showUpdateLogDialog(SettingActivity.this, "Update Log");
                return true;
            }
        });
        menu.add(Menu.NONE, 5, Menu.NONE, "About").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                showAboutDialog(SettingActivity.this, "About");
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void showAboutDialog(Activity activity, CharSequence charSequence) {
        String readAssets2String = FileUtil.readFileAssetString(activity, "data/menucode/about.html");
        AlertDialog create = new AlertDialog.Builder(activity).setTitle(charSequence).setMessage(Build.VERSION.SDK_INT >= 24 ? Html.fromHtml(readAssets2String, 0) : Html.fromHtml(readAssets2String)).setPositiveButton(17039370, null).create();
        create.show();
        Utils.setMessageIsSelectable(create);
        Utils.setAlertDialogMovementMethod(create);
    }

    private void showUpdateLogDialog(Activity activity, CharSequence charSequence) {
        new AlertDialog.Builder(activity).setTitle(charSequence).setMessage(FileUtil.readFileAssetString(activity, "data/updatelog.txt")).setPositiveButton(17039370, null).create().show();
    }
}
