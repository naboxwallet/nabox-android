package com.nuls.naboxpro.entity;

import org.greenrobot.greendao.annotation.Entity;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


@Entity
public class DAppBean implements Serializable {

    private static final long serialVersionUID = -4621715087305128173L;
    /**
     * id : 3
     * chain : Ethereum
     * fileType : DAPP
     * status : 2
     * sort : 1
     * url : http://dapp.com
     * icon : https://nabox.oss-cn-hongkong.aliyuncs.com/20210525/576cb2a82e6b4da48eb713422b42e26c.png
     * fileName : dapp
     * fileNameEn : dapp
     * fileDesc : dapp
     * fileDescEn : dapp
     * createDate : 2021-05-25 18:31:28
     */


    @Id(autoincrement = true)
    Long keyId;

    private int id;
    private String chain;
    private String fileType;
    private int status;
    private int sort;
    private String url;
    private String icon;
    private String fileName;
    private String fileNameEn;
    private String fileDesc;
    private String fileDescEn;
    private String createDate;
    Long updateTime;

    @Generated(hash = 700247334)
    public DAppBean(Long keyId, int id, String chain, String fileType, int status, int sort, String url,
            String icon, String fileName, String fileNameEn, String fileDesc, String fileDescEn,
            String createDate, Long updateTime) {
        this.keyId = keyId;
        this.id = id;
        this.chain = chain;
        this.fileType = fileType;
        this.status = status;
        this.sort = sort;
        this.url = url;
        this.icon = icon;
        this.fileName = fileName;
        this.fileNameEn = fileNameEn;
        this.fileDesc = fileDesc;
        this.fileDescEn = fileDescEn;
        this.createDate = createDate;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1108766103)
    public DAppBean() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNameEn() {
        return fileNameEn;
    }

    public void setFileNameEn(String fileNameEn) {
        this.fileNameEn = fileNameEn;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getFileDescEn() {
        return fileDescEn;
    }

    public void setFileDescEn(String fileDescEn) {
        this.fileDescEn = fileDescEn;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getKeyId() {
        return this.keyId;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }
}
