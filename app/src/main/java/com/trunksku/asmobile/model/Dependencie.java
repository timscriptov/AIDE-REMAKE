package com.trunksku.asmobile.model;

public class Dependencie {
    private boolean androidx;
    private String dependencies;

    public Dependencie(String str, boolean z) {
        this.dependencies = str;
        this.androidx = z;
    }

    public Dependencie(String str) {
        this.dependencies = str;
    }

    @Override
    public String toString() {
        return this.dependencies + ":" + (this.androidx ? "+" : "+");
    }

    public String getDependencies() {
        return this.dependencies;
    }

    public void setDependencies(String str) {
        this.dependencies = str;
    }

    public String getName() {
        return this.dependencies;
    }

    public boolean isAndroidx() {
        return this.androidx;
    }

    public void setAndroidx(boolean z) {
        this.androidx = z;
    }
}
