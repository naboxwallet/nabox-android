package com.nuls.naboxpro.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

/** 
 * 进行BigDecimal对象的加减乘除，四舍五入等运算的工具类 
 * @author ameyume 
 * 
 */  
public class Arith {   
  
    /**  
    * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精  
    * 确的浮点数运算，包括加减乘除和四舍五入。  
    */   
    //默认除法运算精度   
    private static final int DEF_DIV_SCALE = 8;
          
    //这个类不能实例化   
    private Arith(){   
    }   
  
    /**  
     * 提供精确的加法运算。  
     * @param v1 被加数  
     * @param v2 加数  
     * @return 两个参数的和  
     */   
    public static double add(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.add(b2).doubleValue();   
    }   
      
    /**  
     * 提供精确的减法运算。  
     * @param v1 被减数  
     * @param v2 减数  
     * @return 两个参数的差  
     */   
    public static double sub(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        return b1.subtract(b2).doubleValue();
    }


    /**
     *  * 提供精确的减法运算 取消科学法显示
     * @param v1
     * @param v2
     * @return
     */
    public static String subNoGrouping(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(8);
        nf.setGroupingUsed(false);
        return nf.format(b1.subtract(b2).doubleValue());
    }

    /**  
     * 提供精确的乘法运算。  
     * @param v1 被乘数  
     * @param v2 乘数  
     * @return 两个参数的积  
     */   
    public static double mul(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.multiply(b2).doubleValue();   
    }


    /**
     * 提供精确的乘法运算。  不使用科学计数法
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static String mulNorGrouping(double v1,double v2,int decimal){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(decimal);
        nf.setGroupingUsed(false);
        return nf.format(b1.multiply(b2).doubleValue());
    }

    /**  
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到  
     * 小数点以后10位，以后的数字四舍五入。  
     * @param v1 被除数  
     * @param v2 除数  
     * @return 两个参数的商  
     */   
    public static double div(double v1,double v2){   
        return div(v1,v2,DEF_DIV_SCALE);   
    }   
  
    /**  
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指  
     * 定精度，以后的数字四舍五入。  
     * @param v1 被除数  
     * @param v2 除数  
     * @param scale 表示表示需要精确到小数点以后几位。  
     * @return 两个参数的商  
     */   
    public static double div(double v1,double v2,int scale){
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指    不使用科学计数法
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static String divNoGrouping(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        if(v1==0){
            return "0";
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        NumberFormat nf = NumberFormat.getInstance();
//        nf.setMaximumFractionDigits(scale);
//        nf.setGroupingUsed(false);
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指    不使用科学计数法
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static Double divNoGroupingByDouble(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(8);
        nf.setGroupingUsed(false);
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }



    /**  
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到  
     * 小数点以后10位，有小数点，向前一位加  
     * @param v1 被除数  
     * @param v2 除数  
     * @return 两个参数的商  
     */   
    public static double divUp(double v1,double v2){   
        return divDown(v1,v2,DEF_DIV_SCALE);   
    }   
    
    /**  
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指  
     * 定精度，有小数点，向前一位加  
     * @param v1 被除数  
     * @param v2 除数  
     * @param scale 表示表示需要精确到小数点以后几位。  
     * @return 两个参数的商  
     */   
    public static double divUp(double v1,double v2,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.divide(b2,scale,BigDecimal.ROUND_UP).doubleValue();   
    }
    
    /**  
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到  
     * 小数点以后10位，以后的数字，直接舍去
     * @param v1 被除数  
     * @param v2 除数  
     * @return 两个参数的商  
     */   
    public static double divDown(double v1,double v2){   
        return divDown(v1,v2,DEF_DIV_SCALE);   
    }   
    
    /**  
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指  
     * 定精度，以后的数字，直接舍去
     * @param v1 被除数  
     * @param v2 除数  
     * @param scale 表示表示需要精确到小数点以后几位。  
     * @return 两个参数的商  
     */   
    public static double divDown(double v1,double v2,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b1 = new BigDecimal(Double.toString(v1));   
        BigDecimal b2 = new BigDecimal(Double.toString(v2));   
        return b1.divide(b2,scale,BigDecimal.ROUND_DOWN).doubleValue();   
    }
  
    /**  
     * 提供精确的小数位四舍五入处理。  
     * @param v 需要四舍五入的数字  
     * @param scale 小数点后保留几位  
     * @return 四舍五入后的结果  
     */   
    public static double round(double v,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b = new BigDecimal(Double.toString(v));   
        BigDecimal one = new BigDecimal("1");   
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
    }   
    
    /**  
     * 提供精确的小数位向上舍入处理。  
     * @param v 需要向上舍入的数字  
     * @param scale 小数点后保留几位  
     * @return 向上舍入后的结果  
     */   
    public static double roundRoundUp(double v,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b = new BigDecimal(Double.toString(v));   
        BigDecimal one = new BigDecimal("1");   
        return b.divide(one,scale,BigDecimal.ROUND_UP).doubleValue();   
    }   
    
    /**  
     * 提供精确的小数位向下舍去处理。  
     * @param v 需要向下舍去的数字  
     * @param scale 小数点后舍去几位  
     * @return 向下舍去后的结果  
     */   
    public static double roundRoundDown(double v,int scale){   
        if(scale<0){   
            throw new IllegalArgumentException(   
                "The scale must be a positive integer or zero");   
        }   
        BigDecimal b = new BigDecimal(Double.toString(v));   
        BigDecimal one = new BigDecimal("1");   
        return b.divide(one,scale,BigDecimal.ROUND_DOWN).doubleValue();   
    }   
    
      
   /**  
    * 提供精确的类型转换(Float)  
    * @param v 需要被转换的数字  
    * @return 返回转换结果  
    */   
    public static float convertsToFloat(double v){   
        BigDecimal b = new BigDecimal(v);   
        return b.floatValue();   
    }   
      
    /**  
    * 提供精确的类型转换(Int)不进行四舍五入  
    * @param v 需要被转换的数字  
    * @return 返回转换结果  
    */   
    public static int convertsToInt(double v){   
        BigDecimal b = new BigDecimal(v);   
        return b.intValue();   
    }   
  
    /**  
    * 提供精确的类型转换(Long)  
    * @param v 需要被转换的数字  
    * @return 返回转换结果  
    */   
    public static long convertsToLong(double v){   
        BigDecimal b = new BigDecimal(v);   
        return b.longValue();   
    }   
  
    /**  
    * 返回两个数中大的一个值  
    * @param v1 需要被对比的第一个数  
    * @param v2 需要被对比的第二个数  
    * @return 返回两个数中大的一个值  
    */   
    public static double returnMax(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(v1);   
        BigDecimal b2 = new BigDecimal(v2);   
        return b1.max(b2).doubleValue();   
    }   
  
    /**  
    * 返回两个数中小的一个值  
    * @param v1 需要被对比的第一个数  
    * @param v2 需要被对比的第二个数  
    * @return 返回两个数中小的一个值  
    */   
    public static double returnMin(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(v1);   
        BigDecimal b2 = new BigDecimal(v2);   
        return b1.min(b2).doubleValue();   
    }   
  
    /**  
    * 精确对比两个数字  
    * @param v1 需要被对比的第一个数  
    * @param v2 需要被对比的第二个数  
    * @return 如果两个数一样则返回0，如果第一个数比第二个数大则返回1，反之返回-1  
    */   
    public static int compareTo(double v1,double v2){   
        BigDecimal b1 = new BigDecimal(v1);   
        BigDecimal b2 = new BigDecimal(v2);   
        return b1.compareTo(b2);   
    }  
  
	/**
	 * 四舍五入(保留小数点后2位)
	 * @param price 价格
	 * @return
	 */
	public static Double toRoundingBy2(Double price) {
		return new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();    // 保留2位小数点
	}
	
	/**
	 * 四舍五入(保留小数点后0位)
	 * @param price 价格
	 * @return
	 */
	public static Double toRounding(Double price) {
		return new BigDecimal(price).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();    // 保留0位小数点
	}
	/**
	 * 四舍五入
	 * @param price 价格
	 * @param scale 保留几位小数
	 * @return
	 */
	public static Double toRounding(Double price,int scale) {
		return new BigDecimal(price).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	/**
	 * 四舍五入(先从小数点第3位开始，然后第2位)
	 * @param price 价格
	 * @return
	 */
	public static Double toRounding2(Double price) {
		price = new BigDecimal(price).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return price;
	}
}