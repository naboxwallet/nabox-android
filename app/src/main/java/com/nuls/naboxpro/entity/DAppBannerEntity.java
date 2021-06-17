package com.nuls.naboxpro.entity;

public class DAppBannerEntity {


    /**
     * id : 9
     * chain : Ethereum
     * fileType : BANNER
     * status : 1
     * sort : null
     * url : null
     * icon : https://nabox.oss-cn-hongkong.aliyuncs.com/20210527/992da4e5c3df4e62b0742bdaf858feb4.jpg
     * fileName : null
     * fileNameEn : null
     * fileDesc : null
     * fileDescEn : null
     * createDate : 2021-05-27 09:59:40
     */

    private int id;
    private String chain;
    private String fileType;
    private int status;
    private Object sort;
    private Object url;
    private String icon;
    private Object fileName;
    private Object fileNameEn;
    private Object fileDesc;
    private Object fileDescEn;
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Object getFileName() {
        return fileName;
    }

    public void setFileName(Object fileName) {
        this.fileName = fileName;
    }

    public Object getFileNameEn() {
        return fileNameEn;
    }

    public void setFileNameEn(Object fileNameEn) {
        this.fileNameEn = fileNameEn;
    }

    public Object getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(Object fileDesc) {
        this.fileDesc = fileDesc;
    }

    public Object getFileDescEn() {
        return fileDescEn;
    }

    public void setFileDescEn(Object fileDescEn) {
        this.fileDescEn = fileDescEn;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
