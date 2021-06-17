package com.nuls.naboxpro.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AuthorizationBean {

    /**
     * 当前连接地址
     */
    String url;
    /**
     * 当前链
     */
    String chain;

    /**
     * 当前地址
     */
    String address;

    /**
     * 是否信任
     */
    boolean isAuth;

    /**
     * 是否长期信任
     */
    boolean isLongAuth;

    @Generated(hash = 1063780323)
    public AuthorizationBean(String url, String chain, String address,
            boolean isAuth, boolean isLongAuth) {
        this.url = url;
        this.chain = chain;
        this.address = address;
        this.isAuth = isAuth;
        this.isLongAuth = isLongAuth;
    }

    @Generated(hash = 444684998)
    public AuthorizationBean() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChain() {
        return this.chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getIsAuth() {
        return this.isAuth;
    }

    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public boolean getIsLongAuth() {
        return this.isLongAuth;
    }

    public void setIsLongAuth(boolean isLongAuth) {
        this.isLongAuth = isLongAuth;
    }




}
