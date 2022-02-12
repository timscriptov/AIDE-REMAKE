package com.trunksku.asmobile.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import com.trunksku.asmobile.Applications;

import java.util.ArrayList;
import java.util.List;

public class CustomSignDialog {

    private Context mContext;

    private DialogInterface.OnClickListener click;


    private AlertDialog mAlert;

    public CustomSignDialog(Context c, DialogInterface.OnClickListener a) {
        mContext = c;
        click = a;
    }

    public void show() {
        final SharedPreferences sp = Applications.getDefSp();
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(1);
        ll.setPadding(25, 0, 25, 0);
        ll.setLayoutParams(layoutParams);
        Spinner spin = new Spinner(mContext);

        List<String> categories = new ArrayList<String>();
        categories.add("JKS");
        categories.add("BKS");
        categories.add("PKCS12");
        //categories.add("Education");
        //categories.add("Personal");
        //categories.add("Travel");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spin.setAdapter(dataAdapter);
        spin.setSelection(dataAdapter.getPosition(sp.getString("keyType", "JKS")));
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                sp.edit().putString("keyType", p1.getItemAtPosition(p3).toString()).commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });
        final TextInputLayout til = new TextInputLayout(mContext);
        final AppCompatEditText acet = new AppCompatEditText(mContext);
        acet.setHint("KeyStore Path");
        acet.setSingleLine(true);
        acet.setText(sp.getString("keyPath", ""));
        acet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void afterTextChanged(Editable p1) {
                // TODO: Implement this method
                sp.edit().putString("keyPath", p1.toString()).commit();
            }
        });
        til.addView(acet);
        final TextInputLayout til2 = new TextInputLayout(mContext);
        final AppCompatEditText acet2 = new AppCompatEditText(mContext);
        acet2.setHint("KeyStore Alias");
        acet2.setSingleLine(true);
        acet2.setText(sp.getString("keyAlias", ""));
        acet2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void afterTextChanged(Editable p1) {
                // TODO: Implement this method
                sp.edit().putString("keyAlias", p1.toString()).commit();
            }
        });
        til2.addView(acet2);
        final TextInputLayout til3 = new TextInputLayout(mContext);
        final AppCompatEditText acet3 = new AppCompatEditText(mContext);
        acet3.setHint("KeyStore Pass");
        acet3.setSingleLine(true);
        acet3.setText(sp.getString("keyPass", ""));
        acet3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void afterTextChanged(Editable p1) {
                // TODO: Implement this method
                sp.edit().putString("keyPass", p1.toString()).commit();
            }
        });
        til3.addView(acet3);
        final TextInputLayout til4 = new TextInputLayout(mContext);
        final AppCompatEditText acet4 = new AppCompatEditText(mContext);
        acet4.setHint("Cert Pass");
        acet4.setSingleLine(true);
        acet4.setText(sp.getString("keyCerPass", ""));
        acet4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                // TODO: Implement this method
            }

            @Override
            public void afterTextChanged(Editable p1) {
                // TODO: Implement this method
                sp.edit().putString("keyCertPass", p1.toString()).commit();
            }
        });
        til4.addView(acet4);
        ll.addView(spin);
        ll.addView(til);
        ll.addView(til2);
        ll.addView(til3);
        ll.addView(til4);
        mAlert = new AlertDialog.Builder(mContext)
                .setTitle("Custom KeyStore")
                //.setMessage("Enter a valid keystore")
                .setView(ll)
                .setNeutralButton("CHOOSE", null)
                .setPositiveButton("SAVE", click)
                .setNegativeButton("Cancel", null)
                .create();
        mAlert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) mAlert).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        //dialog.show();
                        //Dismiss once everything is OK.
                        //mAlert.dismiss();
                    }
                });
            }
        });
        mAlert.show();
    }
}


