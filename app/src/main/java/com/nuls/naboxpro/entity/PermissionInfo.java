package com.nuls.naboxpro.entity;

/**
 * created by yange on 2020/4/7 0007
 * 描述：
 */
public class PermissionInfo {

    /**
     * 图标地址
     */
    int icon;
    /**
     * 权限名称
     */
    String perStr;
    /**
     * 权限用处
     */
    String contentStr;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getPerStr() {
        return perStr;
    }

    public void setPerStr(String perStr) {
        this.perStr = perStr;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }


    public PermissionInfo(int icon, String perStr , String contentStr){
        this.icon = icon;
        this.perStr = perStr;
        this.contentStr = contentStr;
    }
}
