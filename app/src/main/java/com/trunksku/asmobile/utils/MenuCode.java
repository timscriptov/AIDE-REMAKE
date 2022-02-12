package com.trunksku.asmobile.utils;


import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MenuCode implements android.view.MenuItem.OnMenuItemClickListener {
    private OnMenuItemClickListener OnMenuItemClickListener;
    private Node node;
    private Object object;

    public MenuCode(MenuItem menuItem, Node node, OnMenuItemClickListener onMenuItemClickListener) {
        this.object = menuItem;
        this.node = node;
        this.OnMenuItemClickListener = onMenuItemClickListener;
        menuItem.setOnMenuItemClickListener(this);
    }

    public MenuCode(SubMenu subMenu, Node node, OnMenuItemClickListener onMenuItemClickListener) {
        this.object = subMenu;
        this.node = node;
        this.OnMenuItemClickListener = onMenuItemClickListener;
        subMenu.getItem().setOnMenuItemClickListener(this);
    }

    public static MenuCode[] add(Menu menu, InputStream inputStream, String str, OnMenuItemClickListener onMenuItemClickListener) throws SAXException, ParserConfigurationException, IOException {
        NodeList childNodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String valueByName = getValueByName(item.getAttributes(), "format");
            if (valueByName != null && Pattern.matches(valueByName, str)) {
                return (MenuCode[]) add(menu, item.getChildNodes(), onMenuItemClickListener).toArray(new MenuCode[0]);
            }
        }
        return (MenuCode[]) null;
    }

    public static MenuCode add(Menu menu, Node node, OnMenuItemClickListener onMenuItemClickListener) {
        String nodeName = node.getNodeName();
        CharSequence valueByName = getValueByName(node.getAttributes(), "title");
        if ("item".equals(nodeName)) {
            return new MenuCode(menu.add(valueByName), node, onMenuItemClickListener);
        }
        if ("menu".equals(node.getNodeName())) {
            return new MenuCode(menu.addSubMenu(valueByName), node, onMenuItemClickListener);
        }
        return (MenuCode) null;
    }

    public static List<MenuCode> add(Menu menu, NodeList nodeList, OnMenuItemClickListener onMenuItemClickListener) {
        List<MenuCode> arrayList = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            MenuCode add = add(menu, nodeList.item(i), onMenuItemClickListener);
            if (add != null) {
                arrayList.add(add);
            }
        }
        return arrayList;
    }

    private static String getValueByName(NamedNodeMap namedNodeMap, String str) {
        int i = 0;
        while (namedNodeMap != null && i < namedNodeMap.getLength()) {
            Node item = namedNodeMap.item(i);
            if (str.equals(item.getNodeName())) {
                return item.getNodeValue();
            }
            i++;
        }
        return "unknown";
    }

    public boolean isMenuItem() {
        return this.object instanceof MenuItem;
    }

    public MenuItem getMenuItem() {
        if (isSubMenu()) {
            return getSubMenu().getItem();
        }
        return (MenuItem) this.object;
    }

    public boolean isSubMenu() {
        return this.object instanceof SubMenu;
    }

    public SubMenu getSubMenu() {
        if (isMenuItem()) {
            return getMenuItem().getSubMenu();
        }
        return (SubMenu) this.object;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (isSubMenu()) {
            Menu subMenu = getSubMenu();
            subMenu.clear();
            add(subMenu, this.node.getChildNodes(), this.OnMenuItemClickListener);
        } else if (!isMenuItem()) {
            return false;
        } else {
            this.OnMenuItemClickListener.onMenuItemClick(menuItem, this.node.getTextContent());
        }
        return true;
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem, String str);
    }
}

