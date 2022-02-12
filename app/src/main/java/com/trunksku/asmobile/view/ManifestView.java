package com.trunksku.asmobile.view;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ListView;

import com.trunksku.asmobile.adapter.ManifestAdapter;
import com.trunksku.asmobile.model.Permission;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ManifestView extends ListView {
    private Context mContext;

    public ManifestView(Context context) {
        super(context);
        this.mContext = context;
    }

    public List<Permission> load(File file) throws ParserConfigurationException, IOException, SAXException {
        return load(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));
    }

    public List<Permission> load(Document document) throws SAXException {
        NodeList elementsByTagName = document.getElementsByTagName("manifest");
        if (elementsByTagName.getLength() != 0) {
            return load(elementsByTagName.item(0).getChildNodes());
        }
        throw new SAXException("No manifest node found");
    }

    public List<Permission> load(NodeList nodeList) {
        List<Permission> arrayList = new ArrayList<Permission>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            NamedNodeMap attributes = item.getAttributes();
            if ("uses-permission".equals(item.getNodeName()) && attributes.getLength() == 1) {
                for (int i2 = 0; i2 < attributes.getLength(); i2++) {
                    Node item2 = attributes.item(i2);
                    if ("android:name".equals(item2.getNodeName())) {
                        String nodeValue = item2.getNodeValue();
                        if (!(TextUtils.isEmpty(nodeValue.trim()) || ManifestAdapter.hasAdded(arrayList, nodeValue))) {
                            arrayList.add(new Permission(this.mContext.getPackageManager(), nodeValue));
                        }
                    }
                }
				
				/*int i2 = 0;
                while (i2 < attributes.getLength()) {
                    Node item2 = attributes.item(i);
                    if ("android:name".equals(item2.getNodeName())) {
                        String nodeValue = item2.getNodeValue();
                        if (!(TextUtils.isEmpty(nodeValue.trim()) || ManifestAdapter.hasAdded(arrayList, nodeValue))) {
                            arrayList.add(new Permission(this.mContext.getPackageManager(), nodeValue));
                        }
                    } else {
                        i2++;
                    }
                }*/
            }
        }
        return arrayList;
    }

    @Override
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
    }
}

