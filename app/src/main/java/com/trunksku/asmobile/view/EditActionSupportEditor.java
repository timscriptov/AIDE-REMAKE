package com.trunksku.asmobile.view;

import android.content.Context;
import android.util.AttributeSet;

public abstract class EditActionSupportEditor extends GestureSupportEditor {
    // private IUndoManager mUndoManager;
    // private ClipboardCompat mClipboard;
    //private KeyListener mLastKeyListener;

    public EditActionSupportEditor(Context context) {
        super(context);
        init(context);
    }

    public EditActionSupportEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

  /*  public EditActionSupportEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }*/

    private void init(Context context) {
        //  mUndoManager = new UndoManager(this);
        // mClipboard = new ClipboardCompat(context);
    }

   /* public void redo() {
        mUndoManager.redo();
    }

    public void undo() {
        mUndoManager.undo();
    }

    @Override
    public boolean doCopy() {
        if (!onTextContextMenuItem(android.R.id.copy)) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            if (selectionStart != selectionEnd && selectionStart >= 0) {
                mClipboard.setClipboard(getText().subSequence(selectionStart, selectionEnd));
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean doPaste() {
        CharSequence clipboard = mClipboard.getClipboard();
        if (clipboard != null) {
            //clear all span

            int selectionStart = getSelectionStart();
            selectionStart = Math.max(0, selectionStart);
            int selectionEnd = getSelectionEnd();
            selectionEnd = Math.max(0, selectionEnd);
            getText().delete(selectionStart, selectionEnd);
            getText().insert(selectionStart, cleanupForPaste(clipboard));
            return true;
        }
        return false;
    }

    @Override
    public void insert(@NonNull CharSequence text) {
        int selectionStart = getSelectionStart();
        selectionStart = Math.max(0, selectionStart);
        getText().insert(selectionStart, text);
        setSelection(selectionStart + text.length());
    }

    
    @NonNull
    private String cleanupForPaste(CharSequence clipboard) {
        String result = clipboard.toString();
        result = result.replace("\r\n", "\n");
        return result;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.paste:
                return doPaste();
        }
        return super.onTextContextMenuItem(id);
    }

    @Override
    public boolean doCut() {
        if (!onTextContextMenuItem(android.R.id.cut)) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            if (selectionStart != selectionEnd && selectionStart >= 0) {
                mClipboard.setClipboard(getText().subSequence(selectionStart, selectionEnd));
                getText().delete(selectionStart, selectionEnd);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void duplicateSelection() {
        EditorCompat.duplicateSelection(this);
    }

    @Override
    public void enableUndoRedoFilter() {
        mUndoManager.enable();
    }

    @Override
    public void saveHistory(SharedPreferences preferences) {
        mUndoManager.save(preferences);
    }

    @Override
    public void restoreEditHistory(SharedPreferences preferences) {
        mUndoManager.restore(preferences);
    }

    @Override
    public void disableUndoRedoFilter() {
        mUndoManager.disable();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            if (mLastKeyListener == null) {
                mLastKeyListener = getKeyListener();
            }
            setKeyListener(null);
        } else {
            if (mLastKeyListener != null) {
                setKeyListener(mLastKeyListener);
                mLastKeyListener = null;
            }
        }
    }

    @Override
    public void gotoTop() {
        EditorCompat.gotoTop(this);
    }

    @Override
    public void gotoEnd() {
        EditorCompat.gotoEnd(this);
    }

    @Override
    @Nullable
    public EditorIndex getCursorIndex(int realLine, int column) {
        if (getLayout() == null) {
            return null;
        }
        if (realLine <= 0 || realLine > getLineCount())
            return null;

        int virtualLine = mLineManager.realLineToVirtualLine(realLine);
        if (virtualLine < 0)
            return null;

        int offset = getLayout().getLineStart(virtualLine);
        if (column > 0) {
            offset += column - 1; 
        }
        if (offset > length()) {
            return null;
        }
        return new EditorIndex(virtualLine, column, offset);
    }*/


    /**
     * @param virtualLine scroll editor to virtualLine
     */
    

   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (mPreferences.isUseVolumeToMove()) {
                    if (getSelectionStart() > 0) {
                        setSelection(getSelectionStart() - 1);
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mPreferences.isUseVolumeToMove()) {
                    if (getSelectionEnd() < length()) {
                        setSelection(getSelectionEnd() + 1);
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_TAB:
                if (mPreferences.isInsertSpaceForTab()) {
                    int tabSize = mPreferences.getTabSize();
                    StringBuilder tab = new StringBuilder();
                    for (int i = 0; i < tabSize; i++) {
                        tab.append(" ");
                    }
                    insert(tab);
                } else {
                    insert("\t");
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
