package com.trunksku.asmobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.compile.util.ErrorsParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CodeErrorFragment extends Fragment {

    private ListView list_view;
    private ArrayList<HashMap<String, String>> error = new ArrayList<>();

    private CodeErrorFragment.Stringers adapter;

    private TextView error_title;

    private ImageView error_icon;

    private CodeErrorFragment.OnErrorSelectedListener mCallback;

    public static CodeErrorFragment newInstance(@Nullable String project) {
        CodeErrorFragment fragment = new CodeErrorFragment();
        //fragment.setError(project);
        return fragment;
    }

    public void setOnErrorSelectedListener(OnErrorSelectedListener listen) {
        mCallback = listen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout base = new LinearLayout(getActivity());
        base.setOrientation(1);
        LinearLayout head_base = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        head_base.setLayoutParams(params);
        head_base.setOrientation(LinearLayout.HORIZONTAL);
        //base.setBackgroundColor(Color.WHITE);
        head_base.setGravity(Gravity.CENTER_VERTICAL);
        head_base.setPadding(7, 7, 7, 7);
        error_icon = new ImageView(getActivity());
        error_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        head_base.addView(error_icon, 55, 55);
        error_icon.setImageResource(R.drawable.checked_icon);

        error_title = new TextView(getActivity());
        error_title.setPadding(10, 0, 0, 0);
        error_title.setTextSize(11f);
        head_base.addView(error_title, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        error_title.setText(R.string.code_no_errors);
        base.addView(head_base);
        list_view = new ListView(getActivity());
        list_view.setDivider(null);
        base.addView(list_view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        loadList();
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                mCallback.onErrorSelected(new File(error.get(p3).get("path")), Integer.parseInt(error.get(p3).get("line")));
            }
        });
        return base;
    }

    /* public static CodeErrorFragment newInstance(@Nullable File project) {
     CodeErrorFragment fragment = new CodeErrorFragment();
     fragment.loadFromFile(project.getAbsolutePath());
     return fragment;
     }*/

    public void clearError() {
        error.clear();
        error_icon.setImageResource(R.drawable.checked_icon);
        error_title.setText(R.string.code_no_errors);
    }

    public void setError(ArrayList<ErrorsParser.Error> errors) {
        //  error.clear();
        for (ErrorsParser.Error t : errors) {
            if (t.getType() == 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", Integer.toString(t.getType()));
                map.put("error", t.getFile().getName() + getString(R.string.code_error_line) + t.getLineNumber() + ")\n" + t.getMessage());
                map.put("path", t.getFile().getAbsolutePath());
                map.put("line", Integer.toString(t.getLineNumber()));
                error.add(map);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        }
        if (error.size() > 0) {
            error_icon.setImageResource(R.drawable.error_icon);
            error_title.setText(error.size() + getString(R.string.code_errors));
        } else {
            error_icon.setImageResource(R.drawable.checked_icon);
            error_title.setText(R.string.code_no_errors);
        }
    }

    public void loadList() {
        /*HashMap<String, String> map = new HashMap<>();
         map.put("path", "Example Compiler Error");

         error.add(map);*/
        Collections.sort(error, new Comparator<HashMap<String, String>>() {

            @Override
            public int compare(HashMap<String, String> p1, HashMap<String, String> p2) {
                return p1.get(0).compareTo(p2.get(0));
            }

        });
        adapter = new Stringers(error);
        list_view.setAdapter(adapter);
        ((BaseAdapter) list_view.getAdapter()).notifyDataSetChanged();
    }

    public interface OnErrorSelectedListener {
        public void onErrorSelected(File path, int line);
    }

    public class Stringers extends BaseAdapter {
        ArrayList<HashMap<String, String>> _data;

        public Stringers(ArrayList<HashMap<String, String>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, String> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            LinearLayout base = new LinearLayout(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            base.setLayoutParams(params);
            base.setOrientation(LinearLayout.HORIZONTAL);
            //base.setBackgroundColor(Color.WHITE);
            base.setGravity(Gravity.CENTER_VERTICAL);
            base.setPadding(7, 7, 7, 7);
            ImageView icon = new ImageView(getActivity());
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            base.addView(icon, 55, 55);
            if (error.get(_position).get("type").toString().equals("0")) {
                icon.setImageResource(R.drawable.error_icon);
            } else {
                icon.setImageResource(R.drawable.warning_icon);
            }
            TextView title = new TextView(getActivity());
            title.setPadding(10, 0, 0, 0);
            title.setTextSize(11f);
            base.addView(title, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            title.setText(error.get(_position).get("error").toString());
            /*if (FileUtil.isDirectory(files.get((int)_position).get("path").toString())) {
             icon.setImageResource(0x7F070049);
             } else {
             icon.setImageResource(0x7F070047);
             }*/

            return base;
        }


    }
}
