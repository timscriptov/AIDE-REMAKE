package com.trunksku.asmobile.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.async.Worker;
import com.trunksku.asmobile.utils.FileUtil;

import java.io.File;

public class CreateDialog {
    static String[] items = null;

    public static void showDialog(final Activity activity, final File path, final Listener listen) {
        if (path.isFile()) {
            items = new String[]{"Delete"};
        } else if (path.getAbsolutePath().endsWith("java")) {
            items = new String[]{"Folder", "Delete"};
        } else if (path.getAbsolutePath().contains("java")) {
            items = new String[]{"Java", "Folder", "Delete"};
        } else if (path.getAbsolutePath().endsWith("res")) {
            items = new String[]{"Folder", "Delete"};
        } else if (path.getAbsolutePath().contains("res")) {
            items = new String[]{"File", "Folder", "XML", "Delete"};
        } else {
            items = new String[]{"File", "Folder", "Delete"};
        }
        AlertDialog.Builder view = new AlertDialog.Builder(activity).setTitle("Create");
        view.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                switch (items[p2]) {
                    case "Folder":
                        CreateFolderDialog.showDialog(activity, path.getAbsolutePath(), listen);
                        //Toast.makeText(activity, items[p2], 0).show();
                        break;
                    case "XML":
                        new CreateXMLDialog().showDialog(activity, path.getAbsolutePath(), listen);
                        //activity.startActivity(new Intent(activity, XmlLayoutDesignActivity.class));

                        //Toast.makeText(activity, items[p2], 0).show();
                        break;
                    case "Java":
                        CreateClassDialog.showDialog(activity, path.getAbsolutePath(), listen);
                        //Toast.makeText(activity, items[p2], 0).show();
                        break;
                    case "Delete":
                        new Worker(new Worker.WorkerAction() {
                            @Override
                            public void run() {
                                FileUtil.deleteFile(path.getAbsolutePath());
                                //getFiles(assets_path);
                            }

                            @Override
                            public void runFirst() {
                            }

                            @Override
                            public void runLast() {
                                listen.success();
                                Toast.makeText(activity, R.string.deleted, Toast.LENGTH_SHORT).show();
                            }
                        }).execute();
                        break;
                }

            }
        });
        final AlertDialog create = view.setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
        create.show();

    }

    public interface Listener {
        public void success();
    }

}
