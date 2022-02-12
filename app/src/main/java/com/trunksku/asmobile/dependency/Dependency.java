package com.trunksku.asmobile.dependency;

import com.trunksku.asmobile.utils.LoggerUtils;

public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
    private String type;
    /**
     * Whether this dependency is explicitly defined in the app's build.gradle file
     */
    private boolean userDefined;

    public static Dependency from(String string) {
        if (string == null) {
            throw new IllegalArgumentException("null string value");
        }

        String[] strings = string.split(":");

        if (strings.length < 3) {
            throw new IllegalArgumentException("Invalid dependency format, expected groupId:atifactId:version but got " + string);
        }

        Dependency dependency = new Dependency();
        dependency.setGroupId(strings[0]);
        dependency.setArtifactId(strings[1]);
        dependency.setVersion(strings[2]);

        return dependency;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String atrifactId) {
        this.artifactId = atrifactId;
    }

    public String getAtrifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version.startsWith("[") && version.endsWith("]")) {
            version = version.substring(1, version.length() - 1);
        }
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //androidx.compose.runtime:runtime:1.0.1
    //https://maven.google.com/androidx/compose/runtime/runtime/1.0.1/runtime-1.0.1.pom
    //jitpack.io/com/github/stfalcon/stfalcon-imageviewer/1.0.1/
    public String getPomDownloadLink() {
        LoggerUtils.writeLog("getPomDownloadLink()" + getPath() + "/" + getFileName() + ".pom");

        return getPath() + "/" + getFileName() + ".pom";
    }

    public String getAarDownloadLink() {
        LoggerUtils.writeLog("getAarDownloadLink()" + getPath() + "/" + getFileName() + ".aar");

        return getPath() + "/" + getFileName() + ".aar";
    }

    public String getJarDownloadLink() {
        LoggerUtils.writeLog("getJarDownloadLink()" + getPath() + "/" + getFileName() + ".jar");

        return getPath() + "/" + getFileName() + ".jar";
    }

    public String getPath() {
        String path = groupId
                .replace(".", "/");
        String artifact = artifactId
                .replace(".", "/");

        LoggerUtils.writeLog("getPath() " + path + "/" + artifact + "/" + version);

        return path + "/" + artifact + "/" + version;
    }

    public String getFileName() {
        LoggerUtils.writeLog("getFileName() " + artifactId + "-" + version);

        return artifactId + "-" + version;
    }

    @Override
    public String toString() {
        LoggerUtils.writeLog("toString() " + groupId + ":" + artifactId + ":" + version);

        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public int hashCode() {
        LoggerUtils.writeLog("hashCode() " + (groupId + ":" + artifactId).hashCode());
        return (groupId + ":" + artifactId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Dependency) {
            Dependency that = (Dependency) obj;
            return this.groupId.equals(that.groupId) && this.artifactId.equals(that.artifactId);
        }

        return false;
    }

    public boolean isUserDefined() {
        return userDefined;
    }

    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }
}
