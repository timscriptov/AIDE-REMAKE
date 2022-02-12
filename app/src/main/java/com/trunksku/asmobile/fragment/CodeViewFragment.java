package com.trunksku.asmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mcal.uidesigner.XmlLayoutDesignActivity;
import com.trunksku.asmobile.R;
import com.trunksku.asmobile.activity.ManifestActivity;
import com.trunksku.asmobile.async.GenerateSuggestDataTask;
import com.trunksku.asmobile.compile.maven.Maven;
import com.trunksku.asmobile.compile.util.ErrorsParser;
import com.trunksku.asmobile.dialog.DependencieDialog;
import com.trunksku.asmobile.editor.UndoRedo;
import com.trunksku.asmobile.model.Dependencie;
import com.trunksku.asmobile.utils.FileUtils;
import com.trunksku.asmobile.utils.TextUtils;
import com.trunksku.asmobile.view.CodeEditor;
import com.trunksku.asmobile.view.SuggestionEditor;
import com.trunksku.asmobile.view.Toaster;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CodeViewFragment extends Fragment {

    public ArrayList<ErrorsParser.Error> errors = new ArrayList<>();
    private String mData;
    private UndoRedo helper;
    private SuggestionEditor cv;
    private File mPath;
    private View mView;

    public static CodeViewFragment newInstance(@Nullable File project) {
        CodeViewFragment fragment = new CodeViewFragment();
        fragment.loadFromFile(project);
        return fragment;
    }
    
    
    /*public static CodeViewFragment newInstance(@Nullable String project) {
        CodeViewFragment fragment = new CodeViewFragment();
        fragment.setProject(project);
        return fragment;
    }*/

    public static String read(String path) {
        try {
            InputStream open = new FileInputStream(path);
            ByteArrayOutputStream xf = new ByteArrayOutputStream();
            byte[] bArr = new byte[5242880];
            while (true) {
                int read = open.read(bArr);
                if (read == -1) {
                    break;
                }
                xf.write(bArr, 0, read);
            }
            return new String(xf.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_editor, container, false);
        cv = mView.findViewById(R.id.file_content);
        initHighlight();
        cv.setTextHighlighted(mData);

        helper = new UndoRedo(cv);
        underLineErrors();
        setCodeWatcher();

        // updateData();
        return mView;
    }


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

         initHighlight();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 1:
                updateData();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void updateData() {
        loadFromFile(mPath);
        initHighlight();
        // cv.setTextSize(11);
        //cv.initEditor(mData, "js");
        cv.setTextHighlighted(mData);
        helper = new UndoRedo(cv);
        underLineErrors();
        setCodeWatcher();
    }

    public void initHighlight() {
        if (mPath.getName().endsWith(".xml")) {
            cv.setType(CodeEditor.CodeType.XML);
        } else if (mPath.getName().endsWith(".java")) {
            cv.setType(CodeEditor.CodeType.JAVA);
        } else if (mPath.getName().endsWith(".gradle")) {
            cv.setType(CodeEditor.CodeType.GRADLE);
        } else if (mPath.getName().endsWith(".json")) {
            cv.setType(CodeEditor.CodeType.JS);
        }
    }

    public void underLineErrors() {
        cv.clearErrors();

        try {
            for (ErrorsParser.Error error : errors) {
                if (error.getFile().equals(mPath)) {
                    int line = error.getLineNumber() - 1;
                    int start = cv.getLineStartIndex(line);
                    int end = cv.getLineEndIndex(line);
                    if (error.getType() == ErrorsParser.Error.TYPE_ERROR) {
                        //  cv.addErrorLine(error.getLineNumber() - 1, Color.RED);
                        cv.setError(start, end);
                    } else {
                        cv.setWarning(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public File getFile() {
        return mPath;
    }

    public SuggestionEditor getEditor() {
        return cv;
    }

    public String getData() {
        return cv.getText().toString();
    }

    public void setProject(String str) {
        mData = str;
        cv.setTextHighlighted(str);
        //cv.setText(str);
        initHighlight();
    }

    public void loadFromFile(final File path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPath = path;
                mData = read(path.getAbsolutePath());

            }
        }).start();

    }

    public void setErrors(ArrayList<ErrorsParser.Error> error) {
        errors = error;
    }

    private void setCodeWatcher() {
        cv.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable p1) {

            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Get current char if it was inserted
                   /* if (charSequence.length() > start + before) {
                        // If current char is dot when we can parse class to import
                        char current_char = charSequence.charAt(start + before);
                        if (current_char == '.') {
                            String current_class = TextUtils.replaceLast(TextUtils.getCurrentWord(cv), "\\.", "");
                            // Check if word is not contains dots
                            if(!current_class.contains(".")) {
                                // Searching for class import


								new Toaster(getActivity()).setDuration(Toaster.SHORT)
									.setText("Not implemented yet!")
									.setType(Toaster.Type.ERROR)
									.show();
                            }
                        }
					}*/
                // cv.setSuggestData(item);
                if (mPath.getName().endsWith(".java")) {
                    new GenerateSuggestDataTask(getActivity(), cv).execute();
                }
                //save();


            }

        });
    }

    public void preview() {
        if (mPath.getName().equals("AndroidManifest.xml")) {
            Intent intent = new Intent(getActivity(), ManifestActivity.class);
            intent.putExtra("path", mPath.getAbsolutePath());
            startActivityForResult(intent, 1);
            // getActivity().startActivity(intent);
        } else if (mPath.getName().equals("build.gradle")) {
            new DependencieDialog(getActivity(), new DependencieDialog.Listener() {
                @Override
                public void onSave(List<Dependencie> list) {
                    StringBuilder sb = new StringBuilder();
                    for (Dependencie dep : list) {

                        sb.append("implementation '" + dep.toString() + "'");
                        sb.append("\n");
                    }
                    mData = Maven.addDependency(cv.getText().toString(), sb.toString());
                    //cv.setTextHighlighted(mData);
                    // System.out.println(sb.toString());
                }
            }).show(null);
            // System.out.println(Maven.getDependency(cv.getText().toString()));
            //Maven.parseDependencies(cv.getText().toString());
            //System.out.println(findFromLines(cv.getText().toString(), "api"));
        } else if (mPath.getAbsolutePath().contains("layout")) {
            Intent i = new Intent(getActivity(), XmlLayoutDesignActivity.class);
            i.putExtra("xml", mPath.getAbsolutePath());
            startActivityForResult(i, 1);
        } else if (mPath.getName().endsWith(".java")) {
            List<String> imports = TextUtils.parseImports(Objects.requireNonNull(cv.getText()).toString());
            StringBuffer sb = new StringBuffer();
            for (String str : imports) {
                sb.append(str);
                sb.append("\n");
            }
            new AlertDialog.Builder(getActivity())
                    .setMessage(sb.toString())
                    .create().show();
        } else {
            new Toaster(getActivity()).setDuration(Toaster.SHORT)
                    .setText(getString(R.string.not_implemented_yet))
                    .setType(Toaster.Type.ERROR)
                    .show();
        }
    }

    public void insert(String str) {
        CharSequence contentText = cv.getText();
        //CharSequence insert = (CharSequence) item.getInstance();
        int insertPos = cv.getSelectionStart();
       /* if(insertPos < contentText.length()){
            char nextChar = contentText.charAt(insertPos);
            if(ArrayUtils.in_array(new Character[]{'(', ')', '[', ']', '"', ';', ',', '.'}, nextChar)
               && ArrayUtils.in_array(new CharSequence[]{"    ", ")", "]", "\"", "'", ";", "."}, insert)){
                code.setSelection(code.getSelectionStart() + 1);
                return;
            }
        }*/
        cv.getText().insert(insertPos, str);
    }

    public void gotoLine(int line) {
        // cv.setSelection(cv.getLineEndIndex(line - 1) - 1);
        cv.gotoLine(line);
    }

    public void undo() {
        helper.undo();
    }

    public void redo() {
        helper.redo();
    }

    public void save() {
        // helper.disconnect();
        FileUtils.writeFile(mPath, cv.getText().toString());
        new Toaster(getActivity()).setDuration(Toaster.SHORT)
                .setText(getString(R.string.saved))
                .setType(Toaster.Type.SUCCESS)
                .show();
        // helper.connect();
    }

    public interface CodeChangeListener {
        public void onChanged(String change);

    }
}
