package com.nuls.naboxpro.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * greenDao  联系人
 */

@Entity
public class ContactsDaoEntity {


    /**
     * 联系人 id 默认自增
     */
    @Id(autoincrement = true)
    Long id;


    /**
     * 联系人图标
     */
    Long icon;


    /**
     * 联系人名称
     */
    String name;


    /**
     * 该地址归属的链id
     */
    String chainId;


    /**
     * 联系人地址  唯一
     */
    @Unique
    String address;


    /**
     * 预留出来的联系人真实头像地址
     */
    String avator;


    @Generated(hash = 1816668397)
    public ContactsDaoEntity(Long id, Long icon, String name, String chainId,
            String address, String avator) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.chainId = chainId;
        this.address = address;
        this.avator = avator;
    }


    @Generated(hash = 803788624)
    public ContactsDaoEntity() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getIcon() {
        return this.icon;
    }


    public void setIcon(Long icon) {
        this.icon = icon;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getChainId() {
        return this.chainId;
    }


    public void setChainId(String chainId) {
        this.chainId = chainId;
    }


    public String getAddress() {
        return this.address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getAvator() {
        return this.avator;
    }


    public void setAvator(String avator) {
        this.avator = avator;
    }






}
