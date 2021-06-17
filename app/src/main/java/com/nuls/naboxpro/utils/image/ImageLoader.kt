package com.zhuangku.app.utils.image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.nuls.naboxpro.utils.DensityUtil.dip2px
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

import java.io.File
import java.net.URL


object ImageLoader {


    /**
     * 展示网络图片
     *
     * @param imgView     图片控件对象
     * @param url         图片加载地址
     * @param placeholder 图片占位符
     * @param errorholder 图片加载错误时的占位符
     * @param isCircle    是否是圆形图片
     * @param radius      圆角图形的圆角值，单位为dip
     * @param cornerType  圆角位置
     */
    fun displayImage(
        imgView: ImageView,
        url: Any?,
        placeholder: Int=0,
        errorholder: Int=0,
        isCircle: Boolean=false,
        radius: Int=0,
        cornerType: String=""
    ) {
        var cornerType = cornerType
        try {
            var requestOptions = RequestOptions()
            if (placeholder != 0) requestOptions.placeholder(placeholder)
            if (errorholder != 0) requestOptions.error(errorholder)
            if (isCircle) requestOptions.circleCrop() else if (radius > 0) {
                if (TextUtils.isEmpty(cornerType)) cornerType = "all"
                requestOptions = requestOptions.transforms(
                    CenterCrop(),
                    RoundedCornersTransformation(
                        dip2px(imgView.context, radius.toFloat()),
                        0, RoundedCornersTransformation.CornerType.valueOf(cornerType.toUpperCase())
                    )
                )
            }
            displayImage(
                imgView,
                requestOptions,
                url,
                imgView
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun displayImageCustom(
        imgView: ImageView,
        url: Any?,
        placeholder: Int=0,
        errorholder: Int=0,
        isCircle: Boolean=false,
        radius: Int=0,
        cornerType: String=""
    ) {
        var cornerType = cornerType
        try {
            var requestOptions = RequestOptions()
            if (placeholder != 0) requestOptions.placeholder(placeholder)
            if (errorholder != 0) requestOptions.error(errorholder)
            if (isCircle) requestOptions.fitCenter() else if (radius > 0) {
                if (TextUtils.isEmpty(cornerType)) cornerType = "all"
                requestOptions = requestOptions.transforms(
                    CenterCrop(),
                    RoundedCornersTransformation(
                        dip2px(imgView.context, radius.toFloat()),
                        0, RoundedCornersTransformation.CornerType.valueOf(cornerType.toUpperCase())
                    )
                )
            }
            displayImage(
                imgView,
                requestOptions,
                url,
                imgView
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 展示网络图片
     *
     * @param imgView     图片控件对象
     * @param url         图片加载地址
     * @param placeholder 图片占位符
     * @param errorholder 图片加载错误时的占位符
     * @param isCircle    是否是圆形图片
     * @param radius      圆角图形的圆角值，单位为dip
     * @param cornerType  圆角位置
     */
    fun displayImageNoCenterCrop(
        imgView: ImageView,
        url: Any?,
        placeholder: Int=0,
        errorholder: Int=0,
        isCircle: Boolean=false,
        radius: Int=0,
        cornerType: String=""
    ) {
        var cornerType = cornerType
        try {
            var requestOptions = RequestOptions()
            if (placeholder != 0) requestOptions.placeholder(placeholder)
            if (errorholder != 0) requestOptions.error(errorholder)
            if (isCircle) requestOptions.circleCrop() else if (radius > 0) {
                if (TextUtils.isEmpty(cornerType)) cornerType = "all"
                requestOptions = requestOptions.transforms(
                    FitCenter(),
                    RoundedCornersTransformation(
                        dip2px(imgView.context, radius.toFloat()),
                        0, RoundedCornersTransformation.CornerType.valueOf(cornerType.toUpperCase())
                    )
                )
            }
            displayImage(
                imgView,
                requestOptions,
                url,
                imgView
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 图像加载
     * @param withContext 上下文对象，取值:[View],[Fragment],[Activity],[Context]
     * @param reqOptions 参数设置, 默认：[new RequestOptions()]
     * @param uri 图片路径
     * @param target 图片控件对象
     * @param reqListener 图片数据加载监听
     */
    @Suppress("DEPRECATION")
    @JvmStatic
    @JvmOverloads
    fun displayImage(
        withContext: Any?,
        reqOptions: RequestOptions = RequestOptions(),
        uri: Any?,
        target: ImageView,
        reqListener: RequestListener<Drawable>? = null
    ) {
        try {
            val glideInstance = when (withContext) {
                is View -> Glide.with(withContext)
                is Fragment -> Glide.with(withContext)
                is Activity -> Glide.with(withContext)
                is Context -> Glide.with(withContext)
                else -> throw Exception("UnKnown withContext ??? ")
            }
            val reqBuilderDrawable = when (uri) {
                is Uri -> glideInstance.load(uri)
                is String -> glideInstance.load(uri)
                is File -> glideInstance.load(uri)
                is Drawable -> glideInstance.load(uri)
                is Int -> glideInstance.load(uri)
                is ByteArray -> glideInstance.load(uri)
                is URL -> glideInstance.load(uri)
                is Bitmap -> glideInstance.load(uri)
                else -> glideInstance.load(uri)
            }.apply(reqOptions)
            reqListener?.let { reqBuilderDrawable.listener(reqListener) }
            reqBuilderDrawable.into(target)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 图像加载
     * @param withContext 上下文对象，取值:[View],[Fragment],[Activity],[Context]
     * @param reqOptions 参数设置, 默认：[new RequestOptions()]
     * @param uri 图片路径
     * @param funOnPublishResult 图片数据加载监听
     */
    @Suppress("DEPRECATION")
    @JvmStatic
    @JvmOverloads
    fun applyImageDrawable(
        withContext: Any?,
        reqOptions: RequestOptions = RequestOptions(),
        uri: Any?,
        funOnPublishResult: (isSuccessful: Boolean, result: Drawable?) -> Unit
    ) {
        try {
            val glideInstance = when (withContext) {
                is View -> Glide.with(withContext)
                is Fragment -> Glide.with(withContext)
                is Activity -> Glide.with(withContext)
                is Context -> Glide.with(withContext)
                else -> throw Exception("UnKnown withContext ??? ")
            }
            when (uri) {
                is Uri -> glideInstance.load(uri)
                is String -> glideInstance.load(uri)
                is File -> glideInstance.load(uri)
                is Drawable -> glideInstance.load(uri)
                is Int -> glideInstance.load(uri)
                is ByteArray -> glideInstance.load(uri)
                is URL -> glideInstance.load(uri)
                is Bitmap -> glideInstance.load(uri)
                else -> glideInstance.load(uri)
            }.apply(reqOptions)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        funOnPublishResult(true, resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        funOnPublishResult(false, errorDrawable)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
