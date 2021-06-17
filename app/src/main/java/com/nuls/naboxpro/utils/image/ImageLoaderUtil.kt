package com.zhuangku.app.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.nuls.naboxpro.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 *
 * @function Glide 图片加载
 * Created by xhw on 2020-01-13 11:23
 */


/**
 * 加载头像图片
 */
fun Context.loadHeadImg(imgView: ImageView, url: String?) {
    Glide.with(this).load(url).error( R.mipmap.nuls) //异常时候显示的图片
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .placeholder( R.mipmap.nuls) //加载成功前显示的图片
        .fallback( R.mipmap.nuls).into(imgView)
}

/**
 * 加载头像图片
 */
fun Context.loadCircleImg(imgView: ImageView, url: String?) {
    Glide.with(this).load(url)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(imgView)
}

fun Context.loadImg(imgView: ImageView, url: String?) {
    Glide.with(this).load(url)
        .placeholder( R.mipmap.nuls)
        .error( R.mipmap.nuls)
        .into(imgView)
}

fun Context.loadVideoImg(imgView: ImageView, url: String?) {
    Glide.with(this).load(url)
        .error( R.mipmap.nuls)
        .into(imgView)
}

//fun Context.loadGiftImg(imgView: ImageView, url: String?) {
//    Glide.with(this).load(url)
//        .placeholder(R.drawable.icon_rank_empty)
//        .error(R.drawable.icon_rank_empty)
//        .into(imgView)
//}

/**
 * 加载视频的图片
 */
fun Context.loadSmallVideoImg(imgView: ImageView, url: String?) {
    Glide.with(this).load(url).error( R.mipmap.nuls) //异常时候显示的图片
        .placeholder( R.mipmap.nuls) //加载成功前显示的图片
        .into(imgView)
}

/**
 * 加载圆角为5的图片
 *
 */
fun Context.loadImgRadius5(imgView: ImageView, url: Any?) {
    ImageLoader.displayImage(
        imgView, url,  R.mipmap.nuls,
        R.mipmap.nuls, radius = 5, cornerType = "all"
    )
}

/**
 * 加载圆角图片
 */
fun Context.loadImgRadius(imgView: ImageView, url: Any?,radius:Int) {
    ImageLoader.displayImage(
        imgView, url, R.mipmap.nuls,
        R.mipmap.nuls, radius = radius, cornerType = "all"
    )
}


/**
 * 加载圆角图片
 */
fun Context.loadImgRadiusFitCenter(imgView: ImageView, url: Any?,radius:Int) {
    ImageLoader.displayImageNoCenterCrop(
        imgView, url,  R.mipmap.nuls,
        R.mipmap.nuls, radius = radius, cornerType = "all"
    )
}



/**
 * 加载圆角图片 不显示占位图 主要是
 */
fun Context.loadImgRadiusNoEmpty(imgView: ImageView, url: Any?,radius:Int) {
    ImageLoader.displayImage(
        imgView, url, radius = radius, cornerType = "all"
    )
}


fun loadImgWithPlaceholder(context: Context, imgView: ImageView, url: String, placeholder: Int) {
    Glide.with(context).load(url)
        .placeholder(placeholder)
        .dontAnimate().into(imgView)
}

fun loadVideoThumb(context: Context, videoUrl: String, imgView: ImageView) {
    Glide.with(context).load(videoUrl)
        .apply(RequestOptions.frameOf(1).centerCrop())
        .into(imgView)
}

/**
 * 高斯模糊 左上、右上是圆角
 */
fun Context.loadVagueImage(rv: View, url: String,radius:Int=25,sampling:Int=8) {
    val multi = MultiTransformation<Bitmap>(
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP_LEFT),
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP_RIGHT),
        BlurTransformation(100)
    )
    Glide.with(this)
        .asBitmap()
        .apply(RequestOptions.bitmapTransform( BlurTransformation(radius,sampling)))// 圆角和高斯模糊,设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”
        .load(url)
        .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val drawable: Drawable = BitmapDrawable(resource)
                rv.setBackgroundDrawable(drawable)

            }

        })

}

/**
 * 高斯模糊 左上、右上是圆角
 */
fun Context.loadVagueImage(rv: ImageView, url: String,radius:Int=25,sampling:Int=8) {
    val multi = MultiTransformation<Bitmap>(
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP_LEFT),
        RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.TOP_RIGHT),
        BlurTransformation(100)
    )
    Glide.with(this)
        .asBitmap()
        .apply(RequestOptions.bitmapTransform( BlurTransformation(radius,sampling)))// 圆角和高斯模糊,设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”
        .load(url)
        .into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val drawable: Drawable = BitmapDrawable(resource)
                rv.setImageDrawable(drawable)

            }

        })

}
