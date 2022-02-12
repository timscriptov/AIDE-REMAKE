package com.trunksku.asmobile.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.trunksku.asmobile.R;
import com.unnamed.b.atv.model.TreeNode;

import java.io.File;

public class FolderHolder extends TreeNode.BaseNodeViewHolder<FolderHolder.TreeItem> {
    private static final String TAG = "FolderHolder";
    private AppCompatTextView txtName;
    private LayoutInflater inflater;
    private AppCompatImageView imgArrow;
    private boolean leaf = false;

    public FolderHolder(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
    }

    public static boolean isRoot(File root, File current) {
        try {
            return root.getAbsolutePath().equals(current.getAbsolutePath());
        } catch (Exception e) {
            return false;
        }
    }

    public static void setIcon(ImageView view, File fileDetail) {
        String fileName = fileDetail.getName();
        if (fileDetail.isDirectory()) {
            view.setImageResource(R.drawable.ic_folder_green);
        } else if (fileName.endsWith(".java")) {
            view.setImageResource(R.drawable.ic_java_file_white);
        } else if (fileName.endsWith(".jar")) {
            view.setImageResource(R.drawable.ic_jar_file_white);
        } else if (fileName.endsWith(".class")) {
            view.setImageResource(R.drawable.ic_class_file_white);
        } else if (fileName.endsWith(".xml")) {
            view.setImageResource(R.drawable.ic_xml_file_white);
        } else if (fileName.endsWith(".png")) {
            Bitmap bmImg = BitmapFactory.decodeFile(fileDetail.getAbsolutePath());
            view.setImageBitmap(bmImg);
        } else {
            view.setImageResource(R.drawable.ic_insert_drive_file_white_24dp);
        }
    }

   /* private void setIcon(AppCompatImageView view, File fileDetail) {
        view.setBackground(null);
        int color = ContextCompat.getColor(context, getColorResourceSimple(fileDetail));
        Drawable drawable = ContextCompat.getDrawable(context, getImageResource(fileDetail));
        DrawableCompat.setTint(drawable, color);
        view.setImageDrawable(drawable);
    }*/

    @Override
    public View createNodeView(final TreeNode node, final TreeItem item) {
        View view = inflater.inflate(R.layout.list_item_file, null, false);
        txtName = view.findViewById(R.id.node_value);
        txtName.setText(item.getFile().getName());
        txtName.setContentDescription(item.getFile().getPath());

        imgArrow = view.findViewById(R.id.img_arrow);
        this.leaf = node.isLeaf();
        ImageView imgNew = view.findViewById(R.id.img_add);
        ImageView imgDelete = view.findViewById(R.id.img_delete);

        if (item.getFile().isDirectory() && !node.isRoot()) {
            imgNew.setVisibility(View.GONE);
        } else {
            imgNew.setVisibility(View.GONE);
        }
        if (isRoot(item.getProjectFile(), item.getFile())) {
            imgDelete.setVisibility(View.GONE);
        } else {
            imgDelete.setVisibility(View.GONE);
        }
       /* if(item.getFile().getName().endsWith("res")){
            imgArrow.setVisibility(View.INVISIBLE);
        }*/
        if (node.isLeaf()) {
            imgArrow.setVisibility(View.INVISIBLE);
        }

        final File file = item.getFile();
        setIcon((AppCompatImageView) view.findViewById(R.id.img_icon), file);

        final ProjectFileContract.FileActionListener listener = item.getListener();

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.clickRemoveFile(file, new ProjectFileContract.Callback() {
                        @Override
                        public void onSuccess(File old) {
                            getTreeView().removeNode(node);
                        }

                        @Override
                        public void onFailed(@Nullable Exception e) {

                        }
                    });
                }
            }
        });
        imgNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onClickNewButton(file, new ProjectFileContract.Callback() {
                        @Override
                        public void onSuccess(File newf) {
                            TreeNode child = new TreeNode(new TreeItem(item.getProjectFile(), newf, listener));
                            getTreeView().addNode(node, child);
                        }

                        @Override
                        public void onFailed(@Nullable Exception e) {

                        }
                    });
                }
            }
        });
        return view;
    }

    /*public ShapeDrawable getBackground(int color) {
     ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
     int size = (int) context.getResources().getDimension(R.dimen.avatar_size);
     shapeDrawable.setIntrinsicWidth(size);
     shapeDrawable.setIntrinsicHeight(size);
     shapeDrawable.getPaint().setColor(color);
     return shapeDrawable;
     }*/

    @Override
    public void toggle(boolean active) {
        if (!leaf) {
            imgArrow.setImageResource(active ? R.drawable.ic_keyboard_arrow_down_white_24dp :
                    R.drawable.ic_keyboard_arrow_right_white_24dp);
        }
    }

    public static class TreeItem {
        private final File projectFile;
        @NonNull
        private final File file;
        @Nullable
        private final ProjectFileContract.FileActionListener listener;

        public TreeItem(@NonNull File projectFile, @Nullable File file,
                        @Nullable ProjectFileContract.FileActionListener listener) {
            this.projectFile = projectFile;
            this.file = file;
            this.listener = listener;
        }

        public File getProjectFile() {
            return projectFile;
        }

        @Nullable
        public ProjectFileContract.FileActionListener getListener() {
            return listener;
        }

        @NonNull
        public File getFile() {
            return file;
        }
    }
}

