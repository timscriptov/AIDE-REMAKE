package com.trunksku.asmobile.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;

import com.trunksku.asmobile.utils.Decompress;
import com.trunksku.asmobile.utils.FileUtils;

import java.io.File;

public class CreateProjectTask extends AsyncTask<Void, Void, Void> {

    Exception ex;
    private CreateProjectTask.Listener listener;
    private String packageNane;
    private String projectName;
    private String projectType;
    private File dest;
    private ProgressDialog pd;
    private Context act;

    //private final File createLocation;

    public CreateProjectTask(Context activity, File file, Listener listener) {
        act = activity;
        dest = file;
        this.listener = listener;
        this.pd = ProgressDialog.show(act, (CharSequence) null, "Createing Project...", true, false);
    }

    public static void showExDialog(Context context, String str, Throwable th) {
        // AlertDialog create = new Builder(context).setTitle(str).setMessage(th.toString() == null ? (String) null : th.toString().trim()).setPositiveButton(17039361, new AnonymousClass100000000(th)).setNegativeButton(17039360, (OnClickListener) null).setNeutralButton(isCN() ? "详情" : "Detail", new AnonymousClass100000002(context, th)).create();
        //create.show();
        //setMessageIsSelectable(create, false);
    }

    public static void setMessageIsSelectable(AlertDialog alertDialog, boolean z) {
        TextView textView = (TextView) alertDialog.findViewById(16908299);
        if (textView != null) {
            textView.setTextIsSelectable(z);
        }
    }

    public void setTemplate(String type) {
        this.projectType = type;
    }

    public void setPackageName(String packagen) {
        this.packageNane = packagen;
    }

    public void setProjectName(String projectn) {
        this.projectName = projectn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.start();
    }

    @Override
    protected void onPostExecute(Void voidR) {
        super.onPostExecute(voidR);
        listener.done();
        //create.dismiss();
        if (this.ex != null) {
            showExDialog(act, "Test", this.ex);
            return;
        }
		/*try {
		 File file = !TextUtils.isEmpty(projectInfo.getMain()) ? new File(this.dest, projectInfo.getMain()) : this.dest;
		 ArrayList arrayList = new ArrayList();
		 for (String str : projectInfo.getOpenFile()) {
		 if (str.trim().length() != 0) {
		 File file2 = new File(file, str.replace("$package_name$", packageName.replace(".", "/")));
		 if (file2.exists() && file2.isFile()) {
		 arrayList.add(file2);
		 }
		 }
		 }
		 AIDEUtils.openProject(file.getAbsolutePath(), arrayList);
		 } catch (Throwable th) {
		 this.pd.dismiss();
		 }*/
        this.pd.dismiss();
    }

    @Override
    protected Void doInBackground(Void[] voidArr) {
        try {
            //File file = new File(act.getCacheDir(), "templates_temp.zip");
            //ResourceUtils.copyFileFromAssets(PUtils.getAssetsFile(this.this$0.projectInfo.getTemplate()), file.getAbsolutePath());

            //dest = new File(createLocation);
			/*switch (projectType) {
				case "appcompat":
                    //Decompress.unzip
					Decompress.unzip(act.getAssets().open("template/template_appcompat.zip"), dest.getAbsolutePath());
					break;
				case "androidx":
					Decompress.unzip(act.getAssets().open("template/template_androidx.zip"), dest.getAbsolutePath());
					break;
				case "default":
					Decompress.unzip(act.getAssets().open("template/template_GradleApp.zip"), dest.getAbsolutePath());
					break;
			}*/
            Decompress.unzip(act.getAssets().open("template/" + projectType), dest.getAbsolutePath());
            //file.delete();
            replaces(dest);
            //FileUtils.writeFile(new File(dest, "setting"), "project configuration");
        } catch (Exception e) {
            this.ex = e;
        }
        return (Void) null;
    }

    private void replaces(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                File file3;
                if (file2.getName().contains("$package_name$")) {
                    file3 = new File(file2.getParentFile(), file2.getName().replace("$package_name$", file2.isDirectory() ? packageNane.replace(".", "/") : packageNane));
                    if (file3.isDirectory()) {
                        file3.mkdirs();
                    } else {
                        file3.getParentFile().mkdirs();
                    }
                    file2.renameTo(file3);
                } else {
                    file3 = file2;
                }
                if (file3.isFile()) {
                    if (!file3.getAbsolutePath().endsWith(".png")) {
                        String readFile2String = FileUtils.readFile2String(file3.getAbsolutePath());
                        if (!TextUtils.isEmpty(readFile2String)) {
                            FileUtils.writeFile(file3, readFile2String.replace("$package_name$", packageNane).replace("$project_name$", projectName));
                        }
                    } else if (file3.getAbsolutePath().endsWith(".gradle")) {
                        String readFile2String = FileUtils.readFile2String(file3.getAbsolutePath());
                        if (!TextUtils.isEmpty(readFile2String)) {
                            FileUtils.writeFile(file3, readFile2String.replace("compile", "implementation").replace("api", "implementation").replace("$package_name$", packageNane).replace("$project_name$", projectName));
                        }
                    } else if (file3.getAbsolutePath().endsWith(".jar")) {
                        file3.delete();
                    }
                } else {
                    replaces(file3);
                }
            }
        }
    }

    public interface Listener {
        public void start();

        public void done();
    }
}
