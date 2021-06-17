package com.nuls.naboxpro.entity;

import java.util.Date;

public class AppVersionBean {


    private Long id;




    Long versionCode;

    public Long getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Long versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 版本号
     */
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getDocCode() {
        return docCode;
    }

    public void setDocCode(String docCode) {
        this.docCode = docCode;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public String getMarkDownContext() {
        return markDownContext;
    }

    public void setMarkDownContext(String markDownContext) {
        this.markDownContext = markDownContext;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 下载地址
     */
    private String downUrl;

    /**
     * 是否强制更新
     */
    private Boolean forceUpdate;

    /**
     * 文档code
     */
    private String docCode;

    /**
     * 文档扩展参数
     */
    private String extParams;


    private String markDownContext;


    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
