package com.nuls.naboxpro.utils;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.nuls.naboxpro.R;
import com.nuls.naboxpro.net.Api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放一些工具类
 */
public class NaboxUtils {



//    public static String buildWebUrl(@Nullable String docCode, Integer readState, String msgId, String extParams){
//        Map<String,String> extparmsMap =  new Gson().fromJson(extParams,HashMap.class);
//        StringBuffer url = new StringBuffer();
//        url.append(Api.DOC_DOMAIN);
//        url.append("code="+docCode);
//        url.append("&language="+PreferencesHelper.getStringData(Constant.LANGUAGE));
//        if(readState!=null){
//            url.append("&readState="+readState);
//        }
//        url.append("&terminal="+PreferencesHelper.getStringData(Constant.IMEI));
//        if(!TextUtils.isEmpty(msgId)){
//            url.append("&msgId="+msgId);
//        }
//        if(extparmsMap!=null){
//            for (Map.Entry<String, String> entry : extparmsMap.entrySet()) {
//                url.append("&"+entry.getKey()+"="+entry.getValue());
//            }
//        }
//        return url.toString();
//    }


    /**
     * 设置皮肤
     * @param view  要设置皮肤的view
     * @param colorIndex 皮肤代码
     */
    public static void initWalletSkin(View view,int colorIndex){

        switch (colorIndex){
            case 1:
                view.setBackgroundResource(R.mipmap.skin2);
                break;
            case 2:
                view.setBackgroundResource(R.mipmap.skin3);
                break;
            case 3:
                view.setBackgroundResource(R.mipmap.skin4);
                break;
            case 4:
                view.setBackgroundResource(R.mipmap.skin5);
                break;
            case 0:
            default:
                view.setBackgroundResource(R.mipmap.skin1);
                break;



        }
    }


//    /**
//     * 加载合约资产图标
//     * @param context
//     * @param tokenUrl
//     * @param imageView
//     */
//    public static void loadTokenIcon(Context context,String tokenUrl, ImageView imageView,int iconStatus){
//        if(TextUtils.isEmpty(tokenUrl)){
//            if(iconStatus==2||iconStatus==1||iconStatus==0){
//                imageView.setBackgroundResource(R.mipmap.token_logo);
//            }else{
//                if(PreferencesHelper.getStringData(Constant.LANGUAGE).equals(LanguageEnum.CHS.code())){
//                    imageView.setBackgroundResource(R.mipmap.token_status_chn);
//                }else{
//                    imageView.setBackgroundResource(R.mipmap.token_status_en);
//                }
//            }
//        }else{
//            Glide.with(context).load(tokenUrl).into(imageView);
//        }
//    }


    /**
     *  换算token金额
     * @param tokenAmount 输入的token金额
     * @param decimals 小数点位数
     * @return
     */
    public static BigInteger tokenValueOf(String tokenAmount, int decimals){
        return  new BigDecimal(tokenAmount).movePointRight(decimals).setScale(decimals, RoundingMode.HALF_DOWN).toBigInteger();
    }





    /**
     * string 转金额
     * @param value
     * @param decimals
     * @return
     */
    public static BigInteger getAmount(String value, int decimals) {
        return new BigDecimal(value).multiply(BigDecimal.TEN.pow(decimals)).toBigInteger();
    }




//    /**
//     *  换算金额
//     * @param tokenAmount 输入的token金额
//     * @param decimals 小数点位数
//     * @return
//     */
//    public static BigInteger coinValueOf(String tokenAmount, int decimals){
//        return  new BigDecimal(tokenAmount).movePointLeft(decimals).setScale(decimals, RoundingMode.HALF_DOWN).toBigInteger();
//    }


    /**
     * 换算金额
     * @param amount  金额
     * @param decimals 小数点位数
     * @return
     */
    public static String coinValueOf(BigInteger amount,int decimals){
        if(Double.parseDouble(amount.toString())==0){
            return "0";
        }
       return Arith.divNoGrouping(amount.doubleValue(),addZero(decimals),decimals);
    }
    static Double addZero(int number){
        double num = 1.0;
        for(int i=0;i<number;i++){
            num = num*10;
        }
        return num;
    }

    /**
     * 截取字符串后六位
     * @param oldStr 原字符串
     * @return
     */
    public static  String  getStringEnd8(String oldStr){
        if(TextUtils.isEmpty(oldStr)){
            return "";
        }else{
            if(oldStr.length()>8){
                return  oldStr.substring(oldStr.length()-8,oldStr.length());
            }
            return  oldStr;
        }
    }


//    /**
//     * 获取信用星级
//     * @param num
//     * @return
//     */
//    public static  CreditLevelEntity getRatingStar(Double num){
//        CreditLevelEntity creditLevelEntity = new CreditLevelEntity();
//
//        if(num>-1&&num<=-0.6){
//            creditLevelEntity.setCreditStar(1);
//            creditLevelEntity.setCreditContent("极差");
//        }else if(num>-0.6&&num<=-0.2){
//            creditLevelEntity.setCreditStar(2);
//            creditLevelEntity.setCreditContent("差");
//        }else if(num>-0.2&&num<=0.2){
//            creditLevelEntity.setCreditStar(3);
//            creditLevelEntity.setCreditContent("中");
//        }else if(num>0.2&&num<=0.6){
//            creditLevelEntity.setCreditStar(4);
//            creditLevelEntity.setCreditContent("良");
//        }else if(num>0.6&&num<=1){
//            creditLevelEntity.setCreditStar(5);
//            creditLevelEntity.setCreditContent("优");
//        }
//        return creditLevelEntity;
//    }

    /**
     * 判断密码安全等级
     * 密码安全度显示规则期望：
     * 可输入：数字+小写英文+大写英文组合+特殊字符​
     * 0危险：小于6位
     * 1弱：一种单一字符
     * 2中：两种组合
     * 3强：三种或以上
     * @param password
     * @return
     */
    public static Integer checkPasswordLevel(String password) {
        Integer level = 0;
        if (password.length() < 8) {
            return level;
        }
        if (password.matches(Api.MATCH_NUMBER)) {
            level++;
        }
        if (password.matches(Api.MATCH_LOWERCASE_WORD)) {
            level++;
        }
        if (password.matches(Api.MATCH_UPERCASE_WORD)) {
            level++;
        }
        if (password.matches(Api.MATCH_SPECIAL_WORD)) {
            level++;
        }
        return level;
    }


    /**
     * 创建文档url
     * @param docCode
     */
//    public static String buildDocUrl(@Nullable String docCode){
//        StringBuffer url = new StringBuffer();
//        url.append(Api.DOC_DOMAIN);
//        url.append("code="+docCode);
//        url.append("&language="+PreferencesHelper.getStringData(Constant.LANGUAGE));
//        return url.toString();
//    }






}
