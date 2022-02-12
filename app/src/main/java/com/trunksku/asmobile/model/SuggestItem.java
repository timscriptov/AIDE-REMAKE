package com.trunksku.asmobile.model;

import android.support.annotation.Nullable;

import com.trunksku.asmobile.view.SuggestionEditor;

/**
 * The item will be display in list suggestion
 * <p>
 * Type|name|description
 * <p>
 * Created by Duy on 20-Jul-17.
 */

public class SuggestItem {

    private String mDescription;

    private String mName;

    private String mTypeHeader = "v";

    /**
     * Display name
     */
    @Nullable
    public String getName() {
        return mName;
    }

    public void setName(String n) {
        mName = n;
    }

    /**
     * Display description
     */
    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String d) {
        mDescription = d;
    }

    /**
     * Display type
     */
    @Nullable
    public String getReturnType() {
        return "test";
    }

    public int getSuggestionPriority() {
        return 0;
    }

    public String getTypeHeader() {
        return mTypeHeader;
    }

    /**
     * Return a single character to display type of suggestion
     * v: variable
     * f: function
     * m: method
     * etc
     */
    public void setTypeHeader(String type) {
        mTypeHeader = type;
    }

    /**
     * @param editAreaView - editor you will modify for this suggestion
     */
    public void onSelectThis(SuggestionEditor editAreaView) {

    }
}
