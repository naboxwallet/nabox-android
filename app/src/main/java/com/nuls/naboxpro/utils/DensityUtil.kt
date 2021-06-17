package com.nuls.naboxpro.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * Created by xxx on 2017/3/15.
 * dip dp sp px 转换函数
 */
object DensityUtil {

    /**
     * dp转px
     *
     * @param context 上下文对象
     * @param dpVal   dip值
     */
    @JvmStatic
    fun dip2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics).toInt()
    }

    /**
     * sp转px
     *
     * @param context 上下文对象
     * @param spVal   sp值
     */
    @JvmStatic
    fun sp2px(context: Context, spVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            spVal, context.resources.displayMetrics).toInt()
    }

    /**
     * px转dp
     *
     * @param context 上下文对象
     * @param pxVal   px值
     */
    @JvmStatic
    fun px2dip(context: Context, pxVal: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxVal / scale
    }

    /**
     * px转sp
     *
     * @param context 上下文对象
     * @param pxVal   px值
     */
    @JvmStatic
    fun px2sp(context: Context, pxVal: Float): Float {
        return pxVal / context.resources.displayMetrics.scaledDensity
    }

    fun getDisplayMetrics(context: Context): DisplayMetrics? {
        return context.resources.displayMetrics
    }

    /**
     * 获取应用屏幕胡宽度
     */
     fun getScreenWidth( context:Context): Int? {
        return getDisplayMetrics(context)?.widthPixels;
    }

}
