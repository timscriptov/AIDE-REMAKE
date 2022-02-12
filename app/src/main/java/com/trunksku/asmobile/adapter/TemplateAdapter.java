package com.trunksku.asmobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.model.Category;
import com.trunksku.asmobile.view.TemplateView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateAdapter extends BaseExpandableListAdapter {
    private Context context;
    private Map<Object, Bitmap> iconMap = new HashMap();
    private List<Category> list = new ArrayList();

    public TemplateAdapter(Context context) {
        this.context = context;
    }

    public TemplateAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
    }

    public static Bitmap getIcon(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(new StringBuffer().append("icons/").append(str).toString()));
        } catch (IOException e) {
            return (Bitmap) null;
        }
    }

    public /* bridge */ Object m0getChild(int i, int i2) {
        return getChild(i, i2);
    }

    public /* bridge */ Object m1getGroup(int i) {
        return getGroup(i);
    }

    public TemplateAdapter add(Category category) {
        getList().add(category);
        return this;
    }

    @Override
    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        View templateView;
        if (view == null) {
            templateView = new TemplateView(this.context);
        } else {
            templateView = view;
        }
        TemplateView.ViewHolder viewHolder = ((TemplateView) templateView).holder;
        Category group = getGroup(i);
        try {
            viewHolder.setIcon(getIcon(group.getIcon()), true);
            viewHolder.setTitle(group.getTitle());
            viewHolder.setSubtitle(group.getDescribes());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), 1).show();
        }
        return templateView;
    }

    @Override
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        View templateView;
        if (view == null) {
            templateView = new TemplateView(this.context);
        } else {
            templateView = view;
        }
        try {
            TemplateView.ViewHolder viewHolder = ((TemplateView) templateView).holder;
            Category.Templates child = getChild(i, i2);

            viewHolder.setIcon(R.drawable.ic_androidstudio/*getIcon(child.getIcon())*/, false);
            viewHolder.setTitle(child.getTitle());
            viewHolder.setSubtitle(child.getDescribes());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), 1).show();
        }
        return templateView;
    }

    private Bitmap getIcon(String str) {
        Bitmap bitmap = (Bitmap) this.iconMap.get(str);
        if (bitmap != null) {
            return bitmap;
        }
        bitmap = getIcon(this.context, str);
        this.iconMap.put(str, bitmap);
        return bitmap;
    }

    public List<Category> getList() {
        return this.list;
    }

    public void setList(List<Category> list) {
        this.list = list;
    }

    @Override
    public int getGroupCount() {
        return getList().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return getGroup(i).getTemplates().size();
    }

    @Override
    public Category getGroup(int i) {
        return getList().get(i);
    }

    @Override
    public Category.Templates getChild(int i, int i2) {
        return getGroup(i).getTemplates().get(i2);
    }

    @Override
    public long getGroupId(int i) {
        return (long) i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return (long) i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }
}

