package com.trunksku.asmobile.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class StringFragment extends Fragment {

    private String mData;
    private ListView list_view;
    private ArrayList<HashMap<String, String>> files = new ArrayList<>();

    public static StringFragment newInstance(@Nullable String project) {
        StringFragment fragment = new StringFragment();
        fragment.setProject(project);
        return fragment;
    }

    public static StringFragment newInstance(@Nullable File project) {
        StringFragment fragment = new StringFragment();
        fragment.loadFromFile(project.getAbsolutePath());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout base = new LinearLayout(getActivity());
        base.setOrientation(LinearLayout.HORIZONTAL);
        list_view = new ListView(getActivity());
        base.addView(list_view);
        loadList();
        return base;
    }

    public void loadList() {
        String xml = mData; // getting XML
        Document doc = getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName("string");
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            Element e = (Element) nl.item(i);

            HashMap<String, String> map = new HashMap<>();
            map.put("path", getValue(e));
            // adding each child node to HashMap key => value
            /*map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
            map.put(KEY_COST, "Rs." + parser.getValue(e, KEY_COST));
            map.put(KEY_DESC, parser.getValue(e, KEY_DESC));
*/
            // adding HashList to ArrayList
            files.add(map);
        }
        list_view.setAdapter(new Stringers(files));
        ((BaseAdapter) list_view.getAdapter()).notifyDataSetChanged();
        //mLangFiles.add(a);
    }

    public void setProject(String str) {
        mData = str;
    }

    public void loadFromFile(String path) {
        mData = read(path);
    }

    public String read(String path) {
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

    public String getValue(Element item) {
        // NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(item);
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (Exception e) {
            // Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    public class Stringers extends BaseAdapter {
        ArrayList<HashMap<String, String>> _data;

        public Stringers(ArrayList<HashMap<String, String>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, String> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }

        @Override
        public View getView(final int _position, View _v, ViewGroup _container) {
            LinearLayout base = new LinearLayout(getActivity());

            base.setOrientation(0);
            //base.setBackgroundColor(Color.WHITE);
            base.setGravity(Gravity.CENTER_VERTICAL);
            base.setPadding(7, 7, 7, 7);
            ImageView icon = new ImageView(getActivity());
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            base.addView(icon, 55, 55);

            TextView title = new TextView(getActivity());
            title.setPadding(10, 0, 0, 0);
            title.setTextSize(20f);
            base.addView(title, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            title.setText(files.get((int) _position).get("path").toString());
            /*if (FileUtil.isDirectory(files.get((int)_position).get("path").toString())) {
                icon.setImageResource(0x7F070049);
            } else {
                icon.setImageResource(0x7F070047);
            }*/

            return base;
        }
    }
}