package com.trunksku.asmobile.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.trunksku.asmobile.model.Dependencie;
import com.trunksku.asmobile.view.ItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DependencieAdapter extends BaseAdapter {
    public Map<Dependencie, Highlight> highlightMap = new HashMap<>();
    public Map<Dependencie, Boolean> selectedMap = new HashMap<>();
    OnSelectionListener onSelectionListener;
    private List<Dependencie> backuplist;
    private Context ctx;
    private List<Dependencie> list;
    private SearchTask searchTask;
    private List<Dependencie> selected = new ArrayList<>();

    public DependencieAdapter(Context context, List<Dependencie> list) {
        this.ctx = context;
        this.list = list;
        this.backuplist = list;
    }

    public static int getColorAccent(Context context) {
        if (VERSION.SDK_INT < 21) {
            return -65536;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843829, typedValue, true);
        return typedValue.data;
    }

    public static boolean hasAdded(List<Dependencie> list, Dependencie dependencie) {
        for (int i = 0; i < list.size(); i++) {
            if (((Dependencie) list.get(i)).equals(dependencie)) {
                return true;
            }
        }
        return false;
    }

    public static void remove(List<Dependencie> list, Dependencie dependencie) {
        for (int size = list.size() - 1; size >= 0; size--) {
            if (((Dependencie) list.get(size)).equals(dependencie)) {
                list.remove(size);
                return;
            }
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CharSequence makeHighlightText;
        View itemView = view == null ? new ItemView(this.ctx) : view;
        ItemView itemView2 = (ItemView) itemView;
        Dependencie item = getItem(i);
        itemView2.setChecked(isSelection(item));
        if (this.highlightMap.containsKey(item)) {
            Highlight highlight = (Highlight) this.highlightMap.get(item);
            makeHighlightText = makeHighlightText(item.getName(), highlight.start, highlight.end);
        } else {
            makeHighlightText = item.getName();
        }
        itemView2.setText(item.getName());
        //  itemView2.setText(new StringBuffer(String.format("%d. ", new Object[]{new BackgroundColorSpan(i + 1)})).append(makeHighlightText));
        return itemView;
    }

    public void setSelected(Dependencie dependencie, boolean z) {
        if (z) {
            this.selectedMap.put(dependencie, new Boolean(z));
            if (!hasAdded(getSelectedList(), dependencie)) {
                getSelectedList().add(dependencie);
            }
        } else {
            if (this.selectedMap.containsKey(dependencie)) {
                this.selectedMap.remove(dependencie);
            }
            if (hasAdded(getSelectedList(), dependencie)) {
                remove(getSelectedList(), dependencie);
            }
        }
        if (this.onSelectionListener != null) {
            this.onSelectionListener.onSelection(dependencie, z);
        }
    }

    private SpannableStringBuilder makeHighlightText(String str, int i, int i2) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(str);
        if (i >= 0) {
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(getColorAccent(this.ctx));
            BackgroundColorSpan backgroundColorSpan2 = new BackgroundColorSpan(-1);
            spannableString.setSpan(backgroundColorSpan, i, i2, 33);
            spannableString.setSpan(backgroundColorSpan2, i, i2, 33);
        }
        spannableStringBuilder.append(spannableString);
        return spannableStringBuilder;
    }

    public void search(String str) {
        if (this.searchTask != null) {
            this.searchTask.cancel();
        }
        this.highlightMap.clear();
        this.searchTask = new SearchTask(this);
        this.searchTask.execute(new String[]{str});
    }

    public boolean isSelection(Dependencie dependencie) {
        return !this.selectedMap.containsKey(dependencie) ? false : ((Boolean) this.selectedMap.get(dependencie)).booleanValue();
    }

    public List<Dependencie> getBackuplist() {
        return this.backuplist;
    }

    public void setBackuplist(List<Dependencie> list) {
        this.backuplist = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    public Dependencie getItem(int i) {
        return (Dependencie) this.list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) i;
    }

    public List<Dependencie> getList() {
        return this.list;
    }

    public void setList(List<Dependencie> list) {
        this.list = list;
    }

    public List<Dependencie> getSelectedList() {
        return this.selected;
    }

    public void setOnSelectionListener(OnSelectionListener onSelectionListener) {
        this.onSelectionListener = onSelectionListener;
    }

    public interface OnSelectionListener {
        void onSelection(Dependencie dependencie, boolean z);
    }

    class Highlight {
        private final DependencieAdapter dependencieAdapter;
        int end = -1;
        int start = -1;

        public Highlight(DependencieAdapter dependencieAdapter, int i, int i2) {
            this.dependencieAdapter = dependencieAdapter;
            this.start = i;
            this.end = i2;
        }


    }

    class SearchTask extends AsyncTask<String, Void, List<Dependencie>> {
        private final DependencieAdapter dependencieAdapter;
        boolean cancel;

        public SearchTask(DependencieAdapter dependencieAdapter) {
            this.dependencieAdapter = dependencieAdapter;
        }

        @Override
        protected List<Dependencie> doInBackground(String[] strArr) {
            String toLowerCase = strArr[0].toLowerCase(Locale.ENGLISH);
            if (toLowerCase.trim().length() == 0) {
                return dependencieAdapter.getBackuplist();
            }
            ArrayList arrayList = new ArrayList();
            for (Dependencie dependencie : dependencieAdapter.getBackuplist()) {
                if (this.cancel) {
                    return arrayList;
                }
                int indexOf = dependencie.getName().toLowerCase(Locale.ENGLISH).indexOf(toLowerCase);
                if (indexOf >= 0) {
                    dependencieAdapter.highlightMap.put(dependencie, new Highlight(dependencieAdapter, indexOf, toLowerCase.length() + indexOf));
                    arrayList.add(dependencie);
                }
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(List<Dependencie> list) {
            super.onPostExecute(list);
            if (!this.cancel) {
                dependencieAdapter.setList(list);
                if (list.size() == 0) {
                    dependencieAdapter.notifyDataSetInvalidated();
                } else {
                    dependencieAdapter.notifyDataSetChanged();
                }
            }
        }

        public void cancel() {
            this.cancel = true;
        }
    }


}
