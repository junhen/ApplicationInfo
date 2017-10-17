package com.duohen.applicationinfo.model;

import android.graphics.drawable.Drawable;

/**
 * App��Ϣ��
 */
public class AppInfo {
    // ����
    private String packageName;
    // APP��
    private String appName;
    // ͼ��
    private Drawable icon;
    // �汾��
    private String versionName;
    // Ȩ��
    private String[] permissions;
    // ��Activity������
    private String launchActivityName; 
    
    public String getLaunchActivityName() {
        return launchActivityName;
    }

    public void setLaunchActivityName(String launchActivityName) {
        this.launchActivityName = launchActivityName;
    }

    public AppInfo() {}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    };
    
    
}