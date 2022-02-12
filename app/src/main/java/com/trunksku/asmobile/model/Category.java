package com.trunksku.asmobile.model;


import java.util.List;

public class Category {
    private String describes;
    private String icon;
    private List<Templates> templates;
    private String title;

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getDescribes() {
        return this.describes;
    }

    public void setDescribes(String str) {
        this.describes = str;
    }

    public List<Templates> getTemplates() {
        return this.templates;
    }

    public void setTemplates(List<Templates> list) {
        this.templates = list;
    }

    public static class Templates {
        private String describes;
        private String icon;
        private String project;
        private String title;

        public String getIcon() {
            return this.icon;
        }

        public void setIcon(String str) {
            this.icon = str;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String str) {
            this.title = str;
        }

        public String getDescribes() {
            return this.describes;
        }

        public void setDescribes(String str) {
            this.describes = str;
        }

        public String getProject() {
            return this.project;
        }

        public void setProject(String project) {
            this.project = project;
        }
    }
}

