package com.trunksku.asmobile.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassUtils {


    public static void showDialog(final Activity activity) {
        LinearLayout linearLayout = new LinearLayout(activity);
        final EditText editText = new EditText(activity);
        try {
            editText.setText(Class.forName("android.app.Activity").getCanonicalName());
            editText.setHint("Class name");
            linearLayout.addView(editText, -1, -2);
            // linearLayout.setPadding(Utils.dp2px((float) 24), 0, Utils.dp2px((float) 24), 0);
            AlertDialog create = new Builder(activity).setTitle("Specify class analysis").setView(linearLayout).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            create.show();
            create.getButton(-1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View p1) {
                    try {
                        //PathClassLoader classLoader1 = new PathClassLoader(activity.getDataDir().getAbsolutePath(), activity.getClassLoader());

                        Class cls = Class.forName(editText.getText().toString());
                        StringBuffer stringBuffer = new StringBuffer();

                        stringBuffer.append(new StringBuffer().append("Class path\uff1a").append(cls.getCanonicalName()).toString());
                        stringBuffer.append(new StringBuffer().append("\nNumber of fields: ").append(ClassUtils.getDeclaredFieldCount(cls)).toString());
                        stringBuffer.append(new StringBuffer().append("\nN number of fields (including parent class): ").append(ClassUtils.getFieldCount(cls)).toString());
                        stringBuffer.append(new StringBuffer().append("\nNumber of methods:").append(ClassUtils.getDeclaredMethodCount(cls)).toString());
                        stringBuffer.append(new StringBuffer().append("\nNumber of methods (including parent class): ").append(ClassUtils.getMethodCount(cls)).toString());
                        stringBuffer.append(new StringBuffer().append("\n\nAll parent classes:\n").append(ClassUtils.getSuperClassName(cls)).toString());
                        stringBuffer.append("\n\nAll Method:\n" + ClassUtils.getMethod(cls));
                        AlertDialog create = new Builder(activity).setTitle(cls.getSimpleName()).setMessage(stringBuffer.toString()).setPositiveButton(17039361, null).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                        create.show();
                        //  Utils.setMessageIsSelectable(create);
                    } catch (Throwable th) {
                        //Utils.showExDialog(this.val$activity, th);
                    }
                }
            });
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static String getSuperClassName(Class<?> cls) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(cls.getCanonicalName());
        while (true) {
            cls = cls.getSuperclass();
            if (cls == null) {
                return stringBuffer.toString();
            }
            stringBuffer.append("\n");
            stringBuffer.append(cls.getCanonicalName());
        }
    }

    public static String getMethod(Class<?> cls) {
        Method[] methods = cls.getMethods();
        StringBuilder sb = new StringBuilder();
        for (Method method : methods) {
            sb.append(method.toString() + "\n\n");
        }
        return sb.toString();
    }

    public static int getMethodCount(Class<?> cls) {
        Method[] methods = cls.getMethods();
        return methods == null ? -1 : methods.length;
    }

    public static int getDeclaredMethodCount(Class<?> cls) {
        Method[] declaredMethods = cls.getDeclaredMethods();
        return declaredMethods == null ? -1 : declaredMethods.length;
    }

    public static int getFieldCount(Class<?> cls) {
        Field[] fields = cls.getFields();
        return fields == null ? -1 : fields.length;
    }

    public static int getDeclaredFieldCount(Class<?> cls) {
        Field[] declaredFields = cls.getDeclaredFields();
        return declaredFields == null ? -1 : declaredFields.length;
    }
}

