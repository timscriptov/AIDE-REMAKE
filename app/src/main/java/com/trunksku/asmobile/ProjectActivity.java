package com.trunksku.asmobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.async.CreateProjectTask;
import com.trunksku.asmobile.async.Worker;
import com.trunksku.asmobile.dialog.ChangeLog;
import com.trunksku.asmobile.dialog.CreateProject;
import com.trunksku.asmobile.utils.FileUtil;
import com.trunksku.asmobile.utils.ProjectFileUtil;
import com.trunksku.asmobile.utils.Utils;
import com.trunksku.asmobile.view.CustomSwipeRefreshLayout;
import com.trunksku.asmobile.view.FilePicker;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ProjectActivity extends AppCompatActivity {


    private double index = 0;
    private String my_path = "";
    private String assets_path;
    private ArrayList<String> string_list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> file_list = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<String> fileList = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> files = new ArrayList<>();


    private Toolbar tb;

    private ListView projectList;

    private DrawerLayout drawerLayout;

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;

    public static void listDir(String path, ArrayList<String> list) {
        File dir = new File(path);
        if (!dir.exists() || dir.isFile()) return;

        File[] listFiles = dir.listFiles();
        if (listFiles == null || listFiles.length <= 0) return;

        if (list == null) return;
        list.clear();
        for (File file : listFiles) {
            if (file.getName().startsWith(".")) {

            } else if (file.isFile()) {

            } else if (file.isDirectory()) {
                list.add(file.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assets_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidStudio";
        setContentView(R.layout.activity_project);

        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        drawerLayout = (DrawerLayout) findViewById(R.id.maindrawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.shitstuff);
        Menu menu = navigationView.getMenu();
        menu.add("Community").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                Utils.GetTelegram(ProjectActivity.this, "apkeditor2021");
                return true;
            }
        });
        menu.add("Open Data").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                final FilePicker filePicker = new FilePicker(ProjectActivity.this, new File(getDataDir().getAbsolutePath()), new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return true;
                        // return pathname.isDirectory() || pathname.getName().matches(".+(.lua)$");
                    }
                });

                filePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
                new AlertDialog.Builder(ProjectActivity.this)
                        .setTitle("Select File")
                        .setView(filePicker)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                File f = filePicker.getSelectedFile();
                                Toast.makeText(ProjectActivity.this, f.getName(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                return false;
            }
        });
        menu.add("Setting");
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.container);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFiles(assets_path);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        projectList = (ListView) findViewById(R.id.projectList);

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
                //Toast.makeText(ProjectActivity.this, files.get((int)_param3).get("path").toString(), 0).show();
                ProjectFileUtil.setCurrentProject(files.get(_param3).get("path").toString());
                startActivity(new Intent(ProjectActivity.this, MainActivity.class));
                finish();
					/*final int _position = _param3;
					 if (FileUtil.isDirectory(files.get((int)_position).get("path").toString())) {
					 my_path = files.get((int)_position).get("path").toString();
					 getFiles(files.get((int)_position).get("path").toString());
					 } */
            }
        });
        projectList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4) {
                new AlertDialog.Builder(ProjectActivity.this)
                        .setMessage("Are you sure you want to delete this project?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                new Worker(new Worker.WorkerAction() {
                                    @Override
                                    public void run() {
                                        FileUtil.deleteFile(files.get(p3).get("path").toString());

                                    }

                                    @Override
                                    public void runFirst() {
                                    }

                                    @Override
                                    public void runLast() {
                                        getFiles(assets_path);
                                        Toast.makeText(ProjectActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                }).execute();

                            }
                        })
                        .setNegativeButton("NO", null)
                        .create().show();
                return true;
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.scoped_storage_title);
            dialog.setMessage(R.string.scoped_storage_msg);
            dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    startActivity(intent);
                    dialog.cancel();
                }
            });
            dialog.show();
        }
        getFiles(assets_path);
        new ChangeLog(this, PreferenceManager.getDefaultSharedPreferences(this)).show();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                //premission to read storage
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFiles(assets_path);
                    Toast.makeText(this, "Application permissiom granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please grant Permission to Access more features.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void getFiles(final String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File maven = new File(dir, ".maven");
        if (!maven.exists()) {
            maven.mkdirs();
        }

        File nomedia = new File(dir, ".nomedia");

        if (!nomedia.exists() || !nomedia.isFile()) {
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                // Ln.d(e);
            }
        }

        list.clear();
        files.clear();
        folderList.clear();
        fileList.clear();
        listDir(path, list);
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        index = 0;
        for (int _repeat13 = 0; _repeat13 < (int) (list.size()); _repeat13++) {
            if (new File(list.get((int) (index))).isDirectory()) {
                folderList.add(list.get((int) (index)));
            } else {
                fileList.add(list.get((int) (index)));
            }
            index++;
        }
        index = 0;
        for (int _repeat37 = 0; _repeat37 < (int) (folderList.size()); _repeat37++) {
            {
                HashMap<String, Object> _item = new HashMap<>();
                _item.put("path", folderList.get((int) (index)));
                files.add(_item);
            }
            index++;
        }
        index = 0;
        for (int _repeat54 = 0; _repeat54 < (int) (fileList.size()); _repeat54++) {
            {
                HashMap<String, Object> _item = new HashMap<>();
                _item.put("path", fileList.get((int) (index)));
                files.add(_item);
            }
            index++;
        }
        projectList.setAdapter(new Assets(files));
        ((BaseAdapter) projectList.getAdapter()).notifyDataSetChanged();
    }

    private void check(LinearLayout base_text, LinearLayout base_list) {
        base_text.setVisibility(View.GONE);
        base_list.setVisibility(View.VISIBLE);
        if (!new File(assets_path).exists()) {
            new File(assets_path).mkdirs();
        }
		/*if (ProjectUtils.getCurrentProject() == null) {
			Toast.makeText(getApplication(), "Open a project first.", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			base_text.setVisibility(View.GONE);
			base_list.setVisibility(View.VISIBLE);

			assets_path = ProjectUtils.getCurrentProject().toString() + "/src/main/assets/";
			if (!FileUtil.isDirectory(assets_path)) {
				FileUtil.makeDir(assets_path);
			}
		}*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "New").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CreateProject.showCreateDialog(ProjectActivity.this, new CreateProjectTask.Listener() {
                    @Override
                    public void start() {
                    }

                    @Override
                    public void done() {
                        getFiles(assets_path);
                    }
                });
					/*CreateProject.createProject(ProjectActivity.this, new CreateProjectTask.Listener(){
							@Override
							public void start() {
							}

							@Override
							public void done() {
								getFiles(assets_path);
							}
						});*/
                //ProjectDialog.showCreateDialog(ProjectActivity.this);
                return false;
            }


        });
        menu.add(Menu.NONE, 1, Menu.NONE, "Import").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //importFile();
                final FilePicker filePicker = new FilePicker(ProjectActivity.this, new File(Environment.getExternalStorageDirectory().getAbsolutePath()), new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return true;
                        // return pathname.isDirectory() || pathname.getName().matches(".+(.lua)$");
                    }
                });

                filePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
                new AlertDialog.Builder(ProjectActivity.this)
                        .setTitle("Select File")
                        .setView(filePicker)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                File f = filePicker.getSelectedFile();
                                Toast.makeText(ProjectActivity.this, f.getName(), Toast.LENGTH_LONG).show();
                            }
                        })
                        /*(dialog2, which) -> {
                        File f =filePicker.getSelectedFile();
                        if(f!=null){
                            loadFile(f.getAbsolutePath());
                        }
                    })*/.show();
                return false;
            }

        });
        menu.add(Menu.NONE, 1, Menu.NONE, "Refresh").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //View v = getContentView();
                getFiles(assets_path);
                return false;
            }

        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                finish();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class Assets extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;

        public Assets(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            HashMap<String, Object> mData = _data.get(_position);

            FrameLayout fl = new FrameLayout(ProjectActivity.this);
            /*CardView cv = new CardView(ProjectActivity.this);
            //cv.setForeground(R.attr.selectableItemBackground);
            LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,      
                LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            cv.setLayoutParams(params);*/
            LayoutParams params = new LayoutParams(
                    45,
                    45
            );
            LinearLayout base = new LinearLayout(ProjectActivity.this);
            base.setOrientation(LinearLayout.HORIZONTAL);
            //base.setBackgroundColor(Color.WHITE);
            base.setGravity(Gravity.CENTER_VERTICAL);
            //base.setPadding(10, 10, 10, 10);
            ImageView icon = new ImageView(ProjectActivity.this);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setPadding(20, 20, 20, 20);
            base.addView(icon, dp2px(70), dp2px(70));

            TextView title = new TextView(ProjectActivity.this);
            //title.setPadding(10, 0, 0, 0);
            //title.settextf
            title.setTextSize(16f);
            base.addView(title, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            title.setText(new File(mData.get("path").toString()).getName());
			/*if (FileUtil.isDirectory(file_list.get((int)(_position)))) {
			 icon.setImageResource(0x7F070049);
			 } else {
			 icon.setImageResource(0x7F070047);
			 }*/
            icon.setImageResource(R.drawable.ic_androidstudio);
            TextView status = new TextView(ProjectActivity.this);
            status.setPadding(0, 0, 10, 0);
            //status.setTextSize(17f);
            status.setGravity(Gravity.RIGHT);
            base.addView(status, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);


            //cv.addView(base);
            fl.addView(base);
            return fl;
        }

        public int dp2px(float f) {
            return (int) ((Resources.getSystem().getDisplayMetrics().density * f) + 0.5f);
        }

    }


}


