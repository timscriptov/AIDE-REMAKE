package com.trunksku.asmobile.fragment;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.utils.ResourceHelper;

import java.io.File;

/**
 * Fragment to view image
 */
public class ImageFragment extends Fragment {

    private String mData;

    /**
     * public Constructor
     */
    public static ImageFragment newInstance(@Nullable String project) {
        ImageFragment fragment = new ImageFragment();
        fragment.setProject(project);
        return fragment;
    }

    /**
     * Called when fragment view is created
     *
     * @param inflater           used to inflate layout resource
     * @param container          parent view
     * @param savedInstanceState state to be restored
     * @return inflated view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        File file = null;
        if (mData != null) {
            file = new File(mData);
        }

        if (file == null || !file.exists()) {
            TextView textView = new TextView(getActivity());
            int padding = ResourceHelper.dpToPx(getActivity(), 48);
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bug_report_white_24dp, 0, 0, 0);
            textView.setText("file_problem");
            return textView;
        }

        final BitmapDrawable drawable = new BitmapDrawable(getActivity().getResources(), file.getAbsolutePath());
        final ImageView imageView = new ImageView(getActivity());
        final String fileSize = getSize(file);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Snackbar snackbar = Snackbar.make(imageView, drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight() + "px " + fileSize, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });

                snackbar.show();
            }
        });

        return imageView;
    }

    public void setProject(String str) {
        mData = str;
    }

    /**
     * Gets file size
     *
     * @param f file to get size
     * @return string containing file size and measurement
     */
    private String getSize(File f) {
        long size = f.length() / 1024;
        if (size >= 1024) {
            return size / 1024 + " MB";
        } else {
            return size + " KB";
        }
    }
}