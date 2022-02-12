package com.trunksku.asmobile.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.widget.EditText;

import com.trunksku.asmobile.model.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchAdapter extends ManifestAdapter {
    private final List<Permission> backuplist;
    Map<String, TextHighlight> highlight = new HashMap<>();
    private SearchTask mSearchTask;

    public SearchAdapter(Context context, List<Permission> list) {
        super(context, list);
        this.backuplist = list;
    }

    public static int getColorAccent(Context context) {
        if (VERSION.SDK_INT < 21) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        return typedValue.data;
    }

    public void bind(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                search(charSequence.toString());
            }
        });
    }

    public void search(String str) {
        this.highlight.clear();
        if (this.mSearchTask != null) {
            this.mSearchTask.cancel();
        }
        this.mSearchTask = new SearchTask(this);
        this.mSearchTask.execute(new String[]{str});
    }

    @Override
    public CharSequence getItemTitle(int i) {
        Permission item = getItem(i);
        TextHighlight textHighlight = (TextHighlight) this.highlight.get(item.getName());
        if (textHighlight == null) {
            return item.getLabel();
        }
        return makeHighlightText(item.getLabel(), textHighlight.title.start, textHighlight.title.end);
    }

    @Override
    public CharSequence getItemSubTitle(int i) {
        String name = getItem(i).getName();
        TextHighlight textHighlight = (TextHighlight) this.highlight.get(name);
        if (textHighlight == null) {
            return name;
        }
        return makeHighlightText(name, textHighlight.subtitle.start, textHighlight.subtitle.end);
    }

    private SpannableStringBuilder makeHighlightText(String str, int i, int i2) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(str);
        if (i >= 0) {
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getColorAccent(this.context));
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(-1);
            spannableString.setSpan(backgroundColorSpan, i, i2, 33);
            spannableString.setSpan(foregroundColorSpan, i, i2, 33);
        }
        spannableStringBuilder.append(spannableString);
        return spannableStringBuilder;
    }

    public class Highlight {
        private final SearchAdapter searchAdapter;
        public int end = -1;
        public int start = -1;


        public Highlight(SearchAdapter searchAdapter) {
            this.searchAdapter = searchAdapter;
        }
    }

    class SearchTask extends AsyncTask<String, Void, List<Permission>> {
        private final SearchAdapter searchAdapter;
        boolean cancel;

        public SearchTask(SearchAdapter searchAdapter) {
            this.searchAdapter = searchAdapter;
        }

        void cancel() {
            this.cancel = true;
            cancel(true);
        }

        @Override
        protected List<Permission> doInBackground(String[] strArr) {
            String str = strArr[0];
            if (str == null || str.trim().length() == 0) {
                return searchAdapter.backuplist;
            }
            String toLowerCase = str.toLowerCase(Locale.ENGLISH);
            List<Permission> arrayList = new ArrayList();
            for (int i = 0; i < searchAdapter.backuplist.size(); i++) {
                if (this.cancel) {
                    return arrayList;
                }
                Permission permission = (Permission) searchAdapter.backuplist.get(i);
                String toLowerCase2 = permission.getLabel().toLowerCase(Locale.ENGLISH);
                String toLowerCase3 = permission.getName().toLowerCase(Locale.ENGLISH);
                int indexOf = toLowerCase2.indexOf(toLowerCase);
                int indexOf2 = toLowerCase3.indexOf(toLowerCase);
                if (indexOf >= 0 || indexOf2 >= 0) {
                    arrayList.add(permission);
                    TextHighlight textHighlight = new TextHighlight(searchAdapter);
                    textHighlight.title.start = indexOf;
                    textHighlight.title.end = indexOf + toLowerCase.length();
                    textHighlight.subtitle.start = indexOf2;
                    textHighlight.subtitle.end = indexOf2 + toLowerCase.length();
                    searchAdapter.highlight.put(permission.getName(), textHighlight);
                }
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(List<Permission> list) {
            super.onPostExecute(list);
            if (!this.cancel) {
                searchAdapter.setCurrentList(list);
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    public class TextHighlight {

        private final SearchAdapter searchAdapter;
        public Highlight title;
        public Highlight subtitle;

        public TextHighlight(SearchAdapter searchAdapter) {
            this.searchAdapter = searchAdapter;
            title = new Highlight(searchAdapter);
            subtitle = new Highlight(searchAdapter);
        }
    }
}

