package com.trunksku.asmobile.model;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;

public class Permission implements Comparable<Permission> {
    private String describe;
    private PermissionInfo info;
    private String label;
    private String name;
    private PackageManager pm;

    private String charSequence;

    /*public int compareTo(Object obj) {
        return compareTo((Permission) obj);
    }*/

    public Permission(PackageManager packageManager, String str) {
        this.pm = packageManager;
        this.name = str;
        try {
            this.info = packageManager.getPermissionInfo(str, 0);
        } catch (NameNotFoundException e) {
        }
    }

    public String getLabel() {
        if (this.label != null) {
            return this.label;
        }
        if (this.info != null) {
            CharSequence loadLabel = this.info.loadLabel(this.pm);
            if (!(loadLabel == null || loadLabel.toString().equals(getName()))) {
                String charSequence = loadLabel.toString();
                this.label = charSequence;
                return charSequence;
            }
        }
        int lastIndexOf = this.name.lastIndexOf(".");
        if (lastIndexOf >= 0) {
            charSequence = this.name.substring(lastIndexOf + 1);
            this.label = charSequence;
            return charSequence;
        }
        charSequence = this.name;
        this.label = charSequence;
        return charSequence;
    }

    public void setLabel(String str) {
        this.label = str;
    }

    public String getName() {
        return this.name;
    }

    public String getDescribe() {
        if (this.describe != null) {
            return this.describe;
        }
        String charSequence;
        if (this.info != null) {
            CharSequence loadDescription = this.info.loadDescription(this.pm);
            if (loadDescription != null) {
                charSequence = loadDescription.toString();
                this.describe = charSequence;
                return charSequence;
            }
        }
        charSequence = "No description";
        this.describe = charSequence;
        return charSequence;
    }

    public void setDescribe(String str) {
        this.describe = str;
    }

    @Override
    public int compareTo(Permission permission) {
        return permission.getLabel().compareTo(getLabel());
    }
}

