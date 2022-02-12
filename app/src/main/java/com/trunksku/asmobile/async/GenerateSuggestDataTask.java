package com.trunksku.asmobile.async;

import android.content.Context;
import android.os.AsyncTask;

import com.trunksku.asmobile.editor.completion.parser.CompiledClassLoader;
import com.trunksku.asmobile.model.SuggestItem;
import com.trunksku.asmobile.utils.TextUtils;
import com.trunksku.asmobile.view.SuggestionEditor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GenerateSuggestDataTask extends AsyncTask<Void, Void, ArrayList<SuggestItem>> {
    private static final Comparator<SuggestItem> RESULT_SORTER = new Comparator<SuggestItem>() {

        @Override
        public int compare(SuggestItem p1, SuggestItem p2) {
            //  String s1 = p1.getSortText() == null ? p1.getLabel() : p1.getSortText();
            //  String s2 = p2.getSortText() == null ? p2.getLabel() : p2.getSortText();
            //return s1.compareTo(s2);
            return p1.getName().compareTo(p2.getName());
        }
    };
    ArrayList<String> JAVA_KEYWORDS = new ArrayList<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"));
    private SuggestionEditor mEditor;
    private Context mContext;
    private GenerateSuggestDataTask.Editor mContent;

    public GenerateSuggestDataTask(android.content.Context cnt, SuggestionEditor editor) {
        this.mContext = cnt;
        this.mEditor = editor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String content = mEditor.getText().toString();
        int selectionStart = mEditor.getSelectionStart();
        mContent = new Editor(content, selectionStart);
    }

    @Override
    protected ArrayList<SuggestItem> doInBackground(Void[] p1) {

        return getSuggestions(mContent);
    }

    @Override
    protected void onPostExecute(ArrayList<SuggestItem> result) {
        super.onPostExecute(result);
        if (isCancelled()) {
            return;
        }
        if (result == null) {
            mEditor.setSuggestData(new ArrayList<SuggestItem>());
        } else {
            mEditor.setSuggestData(result);
        }
    }

    public ArrayList<SuggestItem> getSuggestions(Editor editor) {
        ArrayList<SuggestItem> item = new ArrayList<>();
        item.clear();
        final ArrayList<Class> test = new CompiledClassLoader(new File(mContext.getFilesDir(), "sdk/android.jar"), mContext.getDataDir()).getCompiledClassesFromProject(null);
        String statement = TextUtils.getCurrentWord(mEditor);

        for (String code : JAVA_KEYWORDS) {
            if (statement.isEmpty()) {

            } else if (code.startsWith(statement)) {
                SuggestItem si = new SuggestItem();
                si.setName(code);
                si.setDescription("keyword");
                si.setTypeHeader("k");
                item.add(si);
            }
        }
        for (Class cl : test) {

            //CharSequence statement = mEditor.getText().subSequence(mEditor.getSelectionStart(), mEditor.getSelectionEnd());
            if (statement.isEmpty()) {

            } else if (statement.contains(".")) {
                statement = statement.split(".")[1];
                System.out.println(statement);
                for (Method method : cl.getMethods()) {
                    if (method.getName().startsWith(statement)) {
                        SuggestItem si = new SuggestItem();
                        si.setName(method.getName());
                        si.setDescription(cl.getName());
                        si.setTypeHeader("m");
                        item.add(si);
                    }
                }
            } else {
                if (cl.getSimpleName().startsWith(statement)) {
                    if (cl.getSimpleName().isEmpty()) {

                    } else if (cl.getName().contains("$")) {

                    } else {
                        SuggestItem si = new SuggestItem();
                        si.setName(cl.getSimpleName());
                        si.setDescription(cl.getName());
                        si.setTypeHeader("c");
                        item.add(si);
                    }
                }

				//for (Method method : cl.getMethods()) {
				//	if (method.getName().startsWith(statement)) {
				//		SuggestItem si = new SuggestItem();
				//		si.setName(method.getName());
				//		si.setDescription(cl.getName());
				//		item.add(si);
				//	}
				//}
            }

        }
        Collections.sort(item, RESULT_SORTER);
        return item;
    }

    public class Editor {
        private final String text;
        private final int cursor;

        public Editor(String text, int cursor) {
            this.text = text;
            this.cursor = cursor;
        }

        public String getText() {
            return text;
        }

        public int getCursor() {
            return cursor;
        }

        public int length() {
            return text.length();
        }

        @Override
        public String toString() {
            return "Editor{" +
                    "text='" + text + '\'' +
                    ", cursor=" + cursor +
                    '}';
        }
    }

}
