package com.trunksku.asmobile;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trunksku.asmobile.activity.HighlightActivity;
import com.trunksku.asmobile.activity.SettingActivity;
import com.trunksku.asmobile.adapter.ArrayFragmentPagerAdapter;
import com.trunksku.asmobile.async.Worker;
import com.trunksku.asmobile.compile.Project;
import com.trunksku.asmobile.compile.helper.ApkMaker;
import com.trunksku.asmobile.compile.helper.BuildCallback;
import com.trunksku.asmobile.compile.maven.Maven;
import com.trunksku.asmobile.compile.util.ErrorsParser;
import com.trunksku.asmobile.dependency.Dependency;
import com.trunksku.asmobile.dialog.CreateDialog;
import com.trunksku.asmobile.dialog.CustomSignDialog;
import com.trunksku.asmobile.editor.completion.parser.CompiledClassLoader;
import com.trunksku.asmobile.editor.format.JsonFormatter;
import com.trunksku.asmobile.editor.format.XmlFormatter;
import com.trunksku.asmobile.fragment.CodeErrorFragment;
import com.trunksku.asmobile.fragment.CodeViewFragment;
import com.trunksku.asmobile.fragment.DebugFragment;
import com.trunksku.asmobile.fragment.FileChangeListener;
import com.trunksku.asmobile.fragment.FolderStructureFragment;
import com.trunksku.asmobile.fragment.ImageFragment;
import com.trunksku.asmobile.fragment.ProjectFileContract;
import com.trunksku.asmobile.utils.ClassUtils;
import com.trunksku.asmobile.utils.DebugLog;
import com.trunksku.asmobile.utils.FileUtil;
import com.trunksku.asmobile.utils.MenuCode;
import com.trunksku.asmobile.utils.ProjectFileUtil;
import com.trunksku.asmobile.view.SymbolBarLayout;
import com.trunksku.asmobile.view.VPDrawerLayout;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FileChangeListener {

    protected TabLayout mTabLayout;
    protected Toolbar toolbar;

    // private SlidingUpPanelLayout mSlidingUpPanelLayout;
    protected AppBarLayout appBarLayout;
    protected VPDrawerLayout mDrawerLayout;
    protected ViewPager mViewPager;
    protected ProjectFileContract.Presenter mFilePresenter;
    ArrayList<File> mOpenedFilelist = new ArrayList<>();
    ArrayList<ErrorsParser.Error> errors = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;
    private MenuItem previewMenu;
    private SymbolBarLayout mSymbolBar;
    private SharedPreferences sp;
    private FolderStructureFragment folderStructureFragment;
    private ProgressBar loadProg;
    private MainActivity.iCodeAdapter adapter;
    private ViewPager viewPager;
    private LinearLayout mWelcomeLay;
    private MenuItem undoMenu;
    private MenuItem redoMenu;
    private ViewPager viewPagerDrawer;
    private CodeErrorFragment codeErrStructureFragment;

    @Override
    public void onLongClick(File file) {
        CreateDialog.showDialog(this, file, new CreateDialog.Listener() {
            @Override
            public void success() {
                folderStructureFragment.refreshFiles();
            }
        });
    }

    @Override
    public void onFileDeleted(File deleted) {
        Toast.makeText(this, deleted.getAbsolutePath(), 1).show();
    }

    @Override
    public void onFileCreated(File newFile) {

        //CreateDialog.showDialog(this, newFile);
        //Toast.makeText(this, newFile.getAbsolutePath(), 1).show();
    }

    @Override
    public void doOpenFile(File toEdit) {
        String test = "";
        String[] validFiles = new String[]{".java", ".xml", ".gradle", ".aidl", ".pro", ".json", ".txt", ".png"};
        for (String validFile : validFiles) {
            if (toEdit.getName().endsWith(validFile)) {
                boolean exist = false;
                int mOpenedFilePos = 0;
                for (int i = 0; i < mOpenedFilelist.size(); i++) {
                    File f = mOpenedFilelist.get(i);
                    if (f.getAbsolutePath().equals(toEdit.getAbsolutePath())) {
                        mOpenedFilePos = i;
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    adapter.add(toEdit);
                    viewPager.setCurrentItem(adapter.getCount());
                    initTabUi();
                    mOpenedFilelist.add(toEdit);
                } else {
                    viewPager.setCurrentItem(mOpenedFilePos);

                }
                mDrawerLayout.closeDrawers();
                break;
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = Applications.getDefSp();
        setContentView(R.layout.activity_main);


        bindView();
        setupToolbar();
        setupFileView();
        setupViewPager();
        intiDiagnosticView();
        //codeanalys();
        //analyze();
    }

    private void intiDiagnosticView() {

      /*  final View toggleView = findViewById(R.id.img_tab);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.diagnostic_panel);
		//mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
		//animation
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    toggleView.animate().rotation(180 * slideOffset).start();
                }
            });
        mSlidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });*/
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_diagnostic_list_view, new DebugFragment(), FolderStructureFragment.TAG).commit();

    }

    private void setupFileView() {
        viewPagerDrawer = (ViewPager) findViewById(R.id.drawer_viewpager);
        File project = new File(ProjectFileUtil.getCurrentProject());
        Project.loadProj(project);
        toolbar.setSubtitle(project.getName());
        ViewPagerAdapter Draweradapter = new ViewPagerAdapter(getSupportFragmentManager());
        folderStructureFragment = new FolderStructureFragment();
        codeErrStructureFragment = CodeErrorFragment.newInstance("test");
        codeErrStructureFragment.setOnErrorSelectedListener(new CodeErrorFragment.OnErrorSelectedListener() {

            @Override
            public void onErrorSelected(File path, int line) {
                boolean exist = false;
                int mOpenedFilePos = 0;
                for (int i = 0; i < mOpenedFilelist.size(); i++) {
                    File f = mOpenedFilelist.get(i);
                    if (f.getAbsolutePath().equals(path.getAbsolutePath())) {
                        mOpenedFilePos = i;
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    adapter.add(path);
                    viewPager.setCurrentItem(adapter.getCount());
                    initTabUi();
                    mOpenedFilelist.add(path);
                        /*if (adapter.getCount() != 0) {
                         ((CodeViewFragment)adapter.getFragment(viewPager.getCurrentItem())).gotoLine(line);
                         }*/
                } else {
                    viewPager.setCurrentItem(mOpenedFilePos);
                    if (adapter.getCount() != 0) {
                        ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).gotoLine(line);
                    }
                }
                mDrawerLayout.closeDrawers();
            }


        });

        Draweradapter.addFragment(folderStructureFragment, "MAIN");
        Draweradapter.addFragment(codeErrStructureFragment, "MAIN");
        //Draweradapter.addFragment(new DebugFragment(), "MAIN");

        viewPagerDrawer.setAdapter(Draweradapter);
        mDrawerLayout.setViewPager(viewPagerDrawer);
        /* FolderStructureFragment folderStructureFragment = null;

         folderStructureFragment =  FolderStructureFragment.newInstance(Project.dirProj);


         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.replace(R.id.container_file, folderStructureFragment, FolderStructureFragment.TAG).commit();
         //mFilePresenter = new ProjectFilePresenter(folderStructureFragment);
         mFilePresenter = new ProjectFilePresenter(folderStructureFragment);*/
    }

    private void setupViewPager() {

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new iCodeAdapter(getSupportFragmentManager(), viewPager, null);

        // adapter = new ViewPagerAdapter(viewPager, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
        mTabLayout.setupWithViewPager(mViewPager);
        initTabUi();
        //viewPager.set
    }

    public void initTabUi() {
        /*mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab p1) {

         }

         @Override
         public void onTabUnselected(TabLayout.Tab p1) {
         }

         @Override
         public void onTabReselected(final TabLayout.Tab p12) {

         PopupMenu pm = new PopupMenu(MainActivity.this, mTabLayout);
         Menu menu = pm.getMenu();
         menu.add("Close").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem p1) {

         adapter.remove(p12.getPosition());
         mOpenedFilelist.remove(p12.getPosition());
         initTabUi();
         return true;
         }
         });

         pm.show();
         }
         });*/
        //    mTabLayout.removeAllTabs();
        if (adapter.getCount() == 0) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            mWelcomeLay.setVisibility(View.VISIBLE);
            /*if (redoMenu != null)
             redoMenu.setVisible(false);
             if (undoMenu != null)
             undoMenu.setVisible(false);*/
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mWelcomeLay.setVisibility(View.GONE);
            /*  if (redoMenu != null)
             redoMenu.setVisible(true);
             if (undoMenu != null)
             undoMenu.setVisible(true);*/
            for (int i = 0; i < adapter.getCount(); i++) {
                mTabLayout.getTabAt(i).setCustomView(getTabView(mTabLayout, viewPager, adapter, i));
            }
            /*for (int i = 0; i < adapter.getCount(); i++) {
             CodeViewFragment cvf = (CodeViewFragment) adapter.getFragment(i);
             cvf.setErrors(errors);
             cvf.underLineErrors();
             }*/
        }


    }



    /* private void setupEditor() {
     Set<File> editorFiles = mFileManager.getEditorFiles();
     ArrayList<PageDescriptor> descriptors = new ArrayList<>();
     if (mProjectFile != null) {
     for (File editorFile : editorFiles) {
     descriptors.add(new SimplePageDescriptor(editorFile.getPath(), editorFile.getName()));
     }
     } else {
     for (File editorFile : editorFiles) {
     mFileManager.removeTabFile(editorFile.getPath());
     }
     }
     mPageAdapter = new EditorPagerAdapter(getSupportFragmentManager(), descriptors);
     mViewPager.setAdapter(mPageAdapter);
     mViewPager.setOffscreenPageLimit(mPageAdapter.getCount());
     mTabLayout.setupWithViewPager(mViewPager);

     mPagePresenter = new PagePresenter((MainActivity) this, mViewPager, mPageAdapter, mTabLayout, mFileManager);
     mPagePresenter.invalidateTab();
     }*/

    public View getTabView(final TabLayout tabLayout, final ViewPager viewPager, final iCodeAdapter adapters, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_tabs, null);
        ImageView file_icon = (ImageView) view.findViewById(R.id.image_run_file);
		/* try {
		 FolderHolder.setIcon(file_icon, (File)mOpenedFilelist.get(position));
		 } catch (Exception e) {

		 }*/

        // file_icon.setImageResource(R.drawable.ic_error_red);
        TextView title = (TextView) view.findViewById(R.id.txt_title);
        ImageView icon = (ImageView) view.findViewById(R.id.img_close);
        //icon.setVisibility(View.GONE);
        title.setText(tabLayout.getTabAt(position).getText());
        /*view.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View p1) {
         if(viewPager.getCurrentItem() == position){

         }else{
         viewPager.setCurrentItem(position);
         }

         }
         });*/
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.remove(position);
                mOpenedFilelist.remove(position);

                initTabUi();

            }
        });

        return view;
    }

    protected void bindView() {
        loadProg = (ProgressBar) findViewById(R.id.progressBar1);
        mSymbolBar = (SymbolBarLayout) findViewById(R.id.recycler_view);
        mSymbolBar.setOnSymbolCharClickListener(new SymbolBarLayout.OnSymbolCharClickListener() {
            @Override
            public void onClick(View v, String text) {
                if (adapter.getCount() != 0) {
                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).insert(text);
                }
            }
        });
        mWelcomeLay = (LinearLayout) findViewById(R.id.welcome_layout);
        mDrawerLayout = (VPDrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                    /*EditorDelegate currentEditorDelegate = getCurrentEditorDelegate();
                     if (currentEditorDelegate != null) {
                     currentEditorDelegate.getEditText().clearFocus();
                     }*/
                mDrawerLayout.requestFocus();
                // hideSoftInput();
            }
        });

        // mKeyList = findViewById(R.id.recycler_view);
        //mFileManager = new FileManager(this);
        // navigationView = findViewById(R.id.navigation_view);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // mContainerSymbol = findViewById(R.id.container_symbol);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        // mContainerOutput = findViewById(R.id.sliding_layout);
    }

    public void setupToolbar() {
        //setup action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // appBarLayout = findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        /*if (getSupportActionBar() != null) {
         getSupportActionBar().setDisplayShowTitleEnabled(false);
         }*/

        //if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        //attach listener hide/show keyboard
        MainActivity.KeyBoardEventListener mKeyBoardListener = new KeyBoardEventListener();
        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(mKeyBoardListener);
    }

    @Override
    protected void onResume() {
		/*if (adapter.getCount() != 0) {

		 for (int i = 0; i < adapter.getCount(); i++) {
		 ((CodeViewFragment)adapter.getFragment(i)).updateData();
		 }
		 }*/
        super.onResume();
    }

    // In your activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(MainActivity.this, ""+resultCode, 1).show();
        if (adapter.getCount() != 0) {
            ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).updateData();
        }
       /* for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)
                || mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            /* if (mContainerOutput.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
             mContainerOutput.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
             return;
             } else {*/
            mDrawerLayout.closeDrawers();
            return;
            // }
        } else {
            if (doubleBackToExitPressedOnce) {
                startActivity(new Intent(MainActivity.this, ProjectActivity.class));
                finish();
                //super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
            //startActivity(new Intent(MainActivity.this, ProjectActivity.class));
            //finish();
        }

//        /*
//          check can undo
//         */
//        if (getPreferences().getBoolean(getString(R.string.key_back_undo))) {
//            undo();
//            return;
//        }

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(R.string.exit)
         .setMessage(R.string.exit_mgs)
         .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
         MainActivity.super.onBackPressed();
         }
         })
         .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
         dialog.cancel();
         }
         }).create().show();*/
    }

    protected void setMenuCode(Menu menu) {
        try {
            InputStream inputStream = getAssets().open("data/menucode/menu_code.xml");
            MenuCode.add(menu, inputStream, "test.java", new MenuCode.OnMenuItemClickListener() {

                @Override
                public void onMenuItemClick(MenuItem menuItem, String str) {
                    //AIDEUtils.addToEditor(str.trim());
                    if (adapter.getCount() != 0) {
                        ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).insert(str);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        setMenuCode(menu);
        menu.add(Menu.NONE, 077, Menu.NONE, "Run").setIcon(R.drawable.ic_play_arrow_white_24dp)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        undoMenu = menu.add(Menu.NONE, 1, Menu.NONE, "Undo");
        undoMenu.setIcon(R.drawable.ic_undo_white_24dp);
        undoMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        redoMenu = menu.add(Menu.NONE, 2, Menu.NONE, "Redo");
        redoMenu.setIcon(R.drawable.ic_redo_white_24dp);
        redoMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        previewMenu = menu.add(Menu.NONE, 3, Menu.NONE, "Preview");
        previewMenu.setIcon(R.drawable.ic_l_design_dark);
        previewMenu.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //MenuItem menu_code = menu.add(Menu.NONE, 111, Menu.NONE, "Save");

        SubMenu view = menu.addSubMenu("View");
        view.add("Files").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                viewPagerDrawer.setCurrentItem(0);
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            }
        });
        view.add("Errors").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                viewPagerDrawer.setCurrentItem(1);
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            }
        });

        view.add("Debugs").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                    /*if (mSlidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
                        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }*/
                    /*if (mSlidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                     mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                     //item.setTitle(R.string.action_show);
                     } else {
                     mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                     //item.setTitle(R.string.action_hide);
                     }*/
                return true;
            }
        });

        view.add("File List").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {

                String[] items = mOpenedFilelist.toString().replace("[", "").replace("]", "").split(",");

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Open Files")
                        //.setMessage(mOpenedFilelist.toString())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dia, int which) {

                            }
                        })
                        .setPositiveButton("Ok", null)
                        //.setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            }
        });

        SubMenu project = menu.addSubMenu(Menu.NONE, 100, Menu.NONE, "Project");
        project.add("Refresh Build").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                    /*if (adapter.getCount() != 0) {
                        ((CodeViewFragment)adapter.getFragment(viewPager.getCurrentItem())).updateData();
                    }*/
                Toast.makeText(MainActivity.this, "Coming Soon!", 1).show();
                return true;
            }
        });
        project.add(Menu.NONE, 6, Menu.NONE, "Properties");
        project.add("Build Variants").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                CharSequence[] items = {"Debug", "Release"};
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Build Variants")
                        .setSingleChoiceItems(items, sp.getInt("buildVariant", 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dia, int which) {
                                sp.edit().putInt("buildVariant", which).commit();
                                dia.dismiss();
                                //Toast.makeText(getApplicationContext(), "Coming Soon!", 1).show();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });
        project.add("Close Project").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                startActivity(new Intent(MainActivity.this, ProjectActivity.class));
                finish();
                return true;
            }
        });
        menu.add("Format").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                if (adapter.getCount() != 0) {
                    CodeViewFragment editor = ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem()));
                    if (editor.getFile().getName().endsWith(".json")) {
                        editor.setProject(new JsonFormatter().format(editor.getData()).toString());
                    } else if (editor.getFile().getName().endsWith(".xml")) {
                        editor.setProject(new XmlFormatter().format(editor.getData()).toString());
                    }
                }
                return true;
            }
        });
        menu.add(Menu.NONE, 4, Menu.NONE, "Save");
        SubMenu more = menu.addSubMenu("More");
        SubMenu find = more.addSubMenu("Find");
        find.add("Find in File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                if (adapter.getCount() != 0) {
                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).getEditor().startFindMode();
                }
                //startActivity(new Intent(MainActivity.this, HighlightActivity.class));
                return true;
            }
        });
        more.add("HighLight").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                startActivity(new Intent(MainActivity.this, HighlightActivity.class));
                return true;
            }
        });
        more.add("test").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                final ArrayAdapter<String> adapteri = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
                final ArrayList<Class> test = new CompiledClassLoader(null, getDataDir()).getAllClassesFromJar(false, new File(getFilesDir(), "sdk/android.jar"));
                for (Class cl : test) {
                    adapteri.add(cl.getName());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("whatever title");
                builder.setAdapter(adapteri, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        for (Class cls : test) {
                            if (cls.getName().equals(adapteri.getItem(item).toString())) {
                                AlertDialog create = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(cls.getSimpleName())
                                        .setMessage(ClassUtils.getMethod(cls))
                                        .setPositiveButton(17039361, null)
                                        .setNegativeButton(17039360, null)
                                        .create();
                                create.show();
                                break;
                            }
                        }


                    }
                });
                builder.create().show();
                return false;
            }
        });
        more.add("Specify class").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem p1) {
                ClassUtils.showDialog(MainActivity.this);
                return false;
            }
        });
        more.add(Menu.NONE, 5, Menu.NONE, "Settings");
        if (redoMenu != null)
            redoMenu.setVisible(false);
        if (undoMenu != null)
            undoMenu.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*menu.add("Run")
         .setIcon(R.drawable.ic_launcher);
         menu.add("Clear").setIcon(R.drawable.ic_launcher);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case 077:
                //   Project.dirProj = ;
                /*if (sp.getBoolean("useConversion", false)) {
                 CharSequence[] items = {"Default Builder", "Build AAB"};
                 AlertDialog dialog = new AlertDialog.Builder(this)
                 .setTitle("Select Builder")
                 .setItems(items, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dia, int which) {

                 }
                 })
                 .create();
                 dialog.show();
                 } else {*/
                if (sp.getBoolean("customSign", false)) {
                    new CustomSignDialog(MainActivity.this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface p1, int p2) {
                            build();
                        }
                    }).show();
                    return true;
                }
                //start(apk);
                build();
                // }
                return true;
            case 1:
                if (adapter.getCount() != 0) {
                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).undo();
                }
                return true;
            case 2:
                if (adapter.getCount() != 0) {
                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).redo();
                }

                return true;
            case 3:
                if (adapter.getCount() != 0) {

                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).preview();
                }
                return true;
            case 4:
                if (adapter.getCount() != 0) {
                    ((CodeViewFragment) adapter.getFragment(viewPager.getCurrentItem())).save();
                    analyze();
                }
                // Toast.makeText(MainActivity.this, GradleFile.parseGradle(CodeViewFragment.read("/storage/emulated/0/android_ide/projects/ASMobile/app/build.gradle")), 1).show();
                // codeanalys();
                return true;
            case 5:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
				/*if (mSlidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                 mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                 //item.setTitle(R.string.action_show);
                 } else {
                 mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                 //item.setTitle(R.string.action_hide);
                 }*/
                return true;
            case 6:
                final StringBuilder sb = new StringBuilder();

                sb.append("Libraries: \n");
                new Worker(new Worker.WorkerAction() {

                    @Override
                    public void run() {
                        String data = FileUtil.readFile(new File(ProjectFileUtil.getCurrentProject(), "app/build.gradle").getAbsolutePath());

                        for (Dependency dep : Maven.propertiesDependencies(data)) {
                            sb.append(dep.toString() + "\n");
                        }
                    }

                    @Override
                    public void runFirst() {
                        Toast.makeText(MainActivity.this, "Parsing gradle...", 1).show();
                    }

                    @Override
                    public void runLast() {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Properties (app)")
                                .setMessage(sb)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }).execute();


                return true;

            default:
                break;
        }

        return false;
    }

    public void showProg() {
        loadProg.setVisibility(View.VISIBLE);
    }

    public void hideProg() {
        loadProg.setVisibility(View.GONE);
    }

    public void build() {
        // DebugFragment.clear();
        codeErrStructureFragment.clearError();
        File project = new File(ProjectFileUtil.getCurrentProject());
        if (project.exists() || project.isDirectory() || new File(project, "app/build/setting").exists()) {
            ApkMaker maker = new ApkMaker(MainActivity.this, 0);
            maker.setProjectDir(project.getAbsolutePath());
            maker.setBuildListener(new BuildCallback() {

                private ProgressDialog pd;

                //private PowerManager pm;

                //private PowerManager.WakeLock wl;

                @Override
                public void aaptOK(String output) {
                    parseErrors(ErrorsParser.TYPE_AAPT, output, true);
                }

                @Override
                public void aaptERR(String output) {
                    showErrors(R.string.app_name, ErrorsParser.TYPE_AAPT, output);
                }

                @Override
                public void javaOK(String output_err) {
                    parseErrors(ErrorsParser.TYPE_JAVA, output_err, true);
                }

                @Override
                public void javaERR(String output_err) {
                    showErrors(R.string.app_name, ErrorsParser.TYPE_JAVA, output_err);

                }

                @Override
                public void onStart() {
                    pd = new ProgressDialog(MainActivity.this);
                    pd.setCancelable(false);
                    pd.setTitle("Building app");
                    pd.setMessage("Please wait...");
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();
                    //pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                    //wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BUILDER");
                    //wl.acquire();
                    //error_textview.setText("no errors found.");*/
                }

                @Override
                public void onFailure(String message) {
                    DebugLog.i("", message);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Build Failed!")
                            .setMessage("Open Debugs")
                            .setPositiveButton("OK", null)
                            .create().show();
                    System.out.println(message);
                    pd.dismiss();
                    /**/
                    // wl.release();
                    //error_textview.setText(message);*/
                }

                @Override
                public void onProgress(String progress) {
                    DebugLog.i("", progress);
                    pd.setMessage(progress);
                    System.out.println(progress);
                }

                @Override
                public void onSuccess(File apk) {
                    //System.out.println("Build Complete.");
                    pd.dismiss();

                    // wl.release();
                    //Utils.toast(getApplicationContext(), "Build Complete.");*/
                }
            });
            maker.build();
        } else {
            Toast.makeText(this, "Project not exist!", 1).show();
        }
    }

    public void analyze() {
        // DebugFragment.clear();
        codeErrStructureFragment.clearError();
        File project = new File(ProjectFileUtil.getCurrentProject());
        if (project.exists() || project.isDirectory() || new File(project, "app/build/setting").exists()) {
            ApkMaker maker = new ApkMaker(MainActivity.this, 1);
            maker.setProjectDir(project.getAbsolutePath());
            maker.setBuildListener(new BuildCallback() {

                @Override
                public void aaptOK(String output) {
                    parseErrors(ErrorsParser.TYPE_AAPT, output, true);
                }

                @Override
                public void aaptERR(String output) {
                    showErrors(R.string.app_name, ErrorsParser.TYPE_AAPT, output);
                }

                @Override
                public void javaOK(String output_err) {
                    parseErrors(ErrorsParser.TYPE_JAVA, output_err, true);
                }

                @Override
                public void javaERR(String output_err) {
                    showErrors(R.string.app_name, ErrorsParser.TYPE_JAVA, output_err);

                }

                @Override
                public void onStart() {
                    showProg();
                }

                @Override
                public void onFailure(String message) {
                    hideProg();
                }

                @Override
                public void onProgress(String progress) {

                }

                @Override
                public void onSuccess(File apk) {
                    hideProg();
                }
            });
            maker.build();
        } else {
            Toast.makeText(this, "Project not exist!", 1).show();
        }
    }

    private void parseErrors(final int error_type, final String message, final boolean warnsOnly) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                errors = new ErrorsParser(error_type, message).getErrors();

                for (int i = 0; i < adapter.getCount(); i++) {
                    CodeViewFragment cvf = (CodeViewFragment) adapter.getFragment(i);
                    cvf.setErrors(errors);
                    cvf.underLineErrors();
                }
                // underLineErrors();
                if (warnsOnly) {
                    // setStateLayoutWarning();
                } else {
                    //  setStateLayoutError();
                }
                if (!warnsOnly) {
                    codeErrStructureFragment.setError(errors);
                    // codeErrStructureFragment.addError(message);
                }
                //  CodeErrorsDialog.show(errors, this);


            }
        });

    }

    private void showErrors(int title, final int error_type, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // lastCompiledSuccessful = false;
                if (error_type >= 0) {
                    parseErrors(error_type, message, false);
                } else {
                        /* if (needShowApk) {
                         needShowApk = false;
                         LoadingDialog.hide();
                         InfoDialog dialog = new InfoDialog(title, message);
                         dialog.show();
                         }*/
                }
                mDrawerLayout.openDrawer(GravityCompat.START);
                viewPagerDrawer.setCurrentItem(1);
                // onCompile();
            }
        });
    }

    private class iCodeAdapter extends ArrayFragmentPagerAdapter<File> {

        //private ArrayList<File> datas;

        private ViewPager viewPager;

        public iCodeAdapter(FragmentManager fm, ViewPager viewPager, ArrayList<File> datas) {
            super(fm);
            this.viewPager = viewPager;
            //this.datas = datas;
        }

        @Override
        public Fragment getFragment(File item, int position) {
            if (item.getName().endsWith(".png")) {
                return ImageFragment.newInstance(item.getAbsolutePath());
            } /*else if (item.getName().equals("strings.xml")) {
             return StringFragment.newInstance(item);
             }*/
            CodeViewFragment cvf = CodeViewFragment.newInstance(item);
            cvf.setErrors(errors);
            // cvf.underLineErrors();
            return cvf;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getName();
        }

        @Override
        public void add(File item) {

            super.add(item);

        }

        @Override
        public void remove(int position) throws IndexOutOfBoundsException {
            super.remove(position);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class KeyBoardEventListener implements ViewTreeObserver.OnGlobalLayoutListener {

        public void onGlobalLayout() {
            int i = 0;
            int navHeight = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            navHeight = navHeight > 0 ? getResources().getDimensionPixelSize(navHeight) : 0;
            int statusBarHeight = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (statusBarHeight > 0) {
                i = getResources().getDimensionPixelSize(statusBarHeight);
            }
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            if (mDrawerLayout.getRootView().getHeight() - ((navHeight + i) + rect.height()) <= 0) {
                //onHideKeyboard();
                if (redoMenu != null)
                    redoMenu.setVisible(false);
                if (undoMenu != null)
                    undoMenu.setVisible(false);

                mSymbolBar.setVisibility(View.GONE);
            } else {
                if (redoMenu != null)
                    redoMenu.setVisible(true);
                if (undoMenu != null)
                    undoMenu.setVisible(true);

                mSymbolBar.setVisibility(View.VISIBLE);
                //onShowKeyboard();
            }
        }
    }


} 

