package com.nuls.naboxpro.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.nuls.naboxpro.R
import kotlin.math.max


/**
 * @function 仿抖音  加载框
 * Created by xhw on 2020/3/18 20:14
 */
class DYLoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    //默认值
    private val RADIUS = dp2px(6f)
    private val GAP = dp2px(0.8f)

    //属性
    var radius1: Float = 0.toFloat()
        private set //初始时左小球半径
    var radius2: Float = 0.toFloat()
        private set //初始时右小球半径
    var gap: Float = 0.toFloat()
        private set //两小球直接的间隔
    var rtlScale: Float = 0.toFloat()
        private set //小球从右边移动到左边时大小倍数变化(rtl = right to left)
    var ltrScale: Float = 0.toFloat()
        private set//小球从左边移动到右边时大小倍数变化
    var color1: Int = 0
        private set//初始左小球颜色
    var color2: Int = 0
        private set//初始右小球颜色
    var mixColor: Int = 0
        private set//两小球重叠处的颜色
    var duration: Int = 0
        private set //小球一次移动时长
    var pauseDuration: Int = 0
        private set//小球一次移动后停顿时长
    var scaleStartFraction: Float = 0.toFloat()
        private set //小球一次移动期间，进度在[0,scaleStartFraction]期间根据rtlScale、ltrScale逐渐缩放，取值为[0,0.5]
    var scaleEndFraction: Float = 0.toFloat()
        private set//小球一次移动期间，进度在[scaleEndFraction,1]期间逐渐恢复初始大小,取值为[0.5,1]

    //绘图
    private var paint1: Paint? = null
    private var paint2: Paint? = null
    private var mixPaint: Paint? = null
    private var ltrPath: Path? = null
    private var rtlPath: Path? = null
    private var mixPath: Path? = null
    private var distance: Float = 0.toFloat() //小球一次移动距离(即两球圆点之间距离）

    //动画
    private var anim: ValueAnimator? = null
    private var fraction: Float = 0.toFloat() //小球一次移动动画的进度百分比
    internal var isAnimCanceled = false
    internal var isLtr = true//true = 【初始左球】当前正【从左往右】移动,false = 【初始左球】当前正【从右往左】移动

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.DYLoadingView)
        radius1 = ta.getDimension(R.styleable.DYLoadingView_radius1, RADIUS)
        radius2 = ta.getDimension(R.styleable.DYLoadingView_radius2, RADIUS)
        gap = ta.getDimension(R.styleable.DYLoadingView_gap, GAP)
        rtlScale = ta.getFloat(R.styleable.DYLoadingView_rtlScale, RTL_SCALE)
        ltrScale = ta.getFloat(R.styleable.DYLoadingView_ltrScale, LTR_SCALR)
        color1 = ta.getColor(R.styleable.DYLoadingView_color1, LEFT_COLOR)
        color2 = ta.getColor(R.styleable.DYLoadingView_color2, RIGHT_COLOR)
        mixColor = ta.getColor(R.styleable.DYLoadingView_mixColor, MIX_COLOR)
        duration = ta.getInt(R.styleable.DYLoadingView_duration, DURATION)
        pauseDuration = ta.getInt(R.styleable.DYLoadingView_pauseDuration, PAUSE_DUARTION)
        scaleStartFraction =
            ta.getFloat(R.styleable.DYLoadingView_scaleStartFraction, SCALE_START_FRACTION)
        scaleEndFraction =
            ta.getFloat(R.styleable.DYLoadingView_scaleEndFraction, SCALE_END_FRACTION)
        ta.recycle()

        checkAttr()
        distance = gap + radius1 + radius2

        initDraw()

        initAnim()
    }


    /**
     * 属性合法性检查校正
     */
    private fun checkAttr() {
        radius1 = if (radius1 > 0) radius1 else RADIUS
        radius2 = if (radius2 > 0) radius2 else RADIUS
        gap = if (gap >= 0) gap else GAP
        rtlScale = if (rtlScale >= 0) rtlScale else RTL_SCALE
        ltrScale = if (ltrScale >= 0) ltrScale else LTR_SCALR
        duration = if (duration > 0) duration else DURATION
        pauseDuration = if (pauseDuration >= 0) pauseDuration else PAUSE_DUARTION
        if (scaleStartFraction < 0 || scaleStartFraction > 0.5f) {
            scaleStartFraction = SCALE_START_FRACTION
        }
        if (scaleEndFraction < 0.5 || scaleEndFraction > 1) {
            scaleEndFraction = SCALE_END_FRACTION
        }
    }

    /**
     * 初始化绘图数据
     */
    private fun initDraw() {
        paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        mixPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint1?.color = color1
        paint2?.color = color2
        mixPaint?.color = mixColor

        ltrPath = Path()
        rtlPath = Path()
        mixPath = Path()
    }

    private fun initAnim() {
        fraction = 0.0f

        stop()

        anim = ValueAnimator.ofFloat(0.0f, 1.0f)
        anim?.duration = duration.toLong()
        if (pauseDuration > 0) {
            anim?.startDelay = pauseDuration.toLong()
            anim?.interpolator = AccelerateDecelerateInterpolator()
        } else {
            anim?.repeatCount = ValueAnimator.INFINITE
            anim?.repeatMode = ValueAnimator.RESTART
            anim?.interpolator = LinearInterpolator()
        }
        anim?.addUpdateListener { animation ->
            fraction = animation.animatedFraction
            invalidate()
        }
        anim?.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                isLtr = !isLtr
            }

            override fun onAnimationRepeat(animation: Animator) {
                isLtr = !isLtr
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimCanceled = true
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!isAnimCanceled) {
                    if (anim == null) {
                        initAnim()
                    }
                    anim?.start()
                }
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var wSize = MeasureSpec.getSize(widthMeasureSpec)
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var hSize = MeasureSpec.getSize(heightMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)

        //WRAP_CONTENT时控件大小为最大可能的大小,保证显示的下
        var maxScale = max(rtlScale, ltrScale)
        maxScale = max(maxScale, 1f)
        if (wMode != MeasureSpec.EXACTLY) {
            wSize = (gap + (2 * radius1 + 2 * radius2) * maxScale + dp2px(1f)).toInt()  //宽度= 间隙 + 2球直径*最大比例 + 1dp
        }
        if (hMode != MeasureSpec.EXACTLY) {
            hSize = (2f * max(radius1, radius2) * maxScale + dp2px(1f)).toInt() // 高度= 1球直径*最大比例 + 1dp
        }
        setMeasuredDimension(wSize, hSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = measuredHeight / 2.0f
        val ltrInitRadius: Float
        val rtlInitRadius: Float
        val ltrPaint: Paint?
        val rtlPaint: Paint?

        //确定当前【从左往右】移动的是哪颗小球
        if (isLtr) {
            ltrInitRadius = radius1
            rtlInitRadius = radius2
            ltrPaint = paint1
            rtlPaint = paint2
        } else {
            ltrInitRadius = radius2
            rtlInitRadius = radius1
            ltrPaint = paint2
            rtlPaint = paint1
        }

        var ltrX = measuredWidth / 2.0f - distance / 2.0f
        ltrX += distance * fraction//当前从左往右的球的X坐标

        var rtlX = measuredWidth / 2.0f + distance / 2.0f
        rtlX -= distance * fraction//当前从右往左的球的X坐标

        //计算小球移动过程中的大小变化
        val ltrBallRadius: Float
        val rtlBallRadius: Float
        when {
            fraction <= scaleStartFraction -> { //动画进度[0,scaleStartFraction]时，球大小由1倍逐渐缩放至ltrScale/rtlScale倍
                val scaleFraction = 1.0f / scaleStartFraction * fraction //百分比转换 [0,scaleStartFraction]] -> [0,1]
                ltrBallRadius = ltrInitRadius * (1 + (ltrScale - 1) * scaleFraction)
                rtlBallRadius = rtlInitRadius * (1 + (rtlScale - 1) * scaleFraction)
            }
            fraction >= scaleEndFraction -> { //动画进度[scaleEndFraction,1]，球大小由ltrScale/rtlScale倍逐渐恢复至1倍
                val scaleFraction = (fraction - 1) / (scaleEndFraction - 1) //百分比转换，[scaleEndFraction,1] -> [1,0]
                ltrBallRadius = ltrInitRadius * (1 + (ltrScale - 1) * scaleFraction)
                rtlBallRadius = rtlInitRadius * (1 + (rtlScale - 1) * scaleFraction)
            }
            else -> { //动画进度[scaleStartFraction,scaleEndFraction]，球保持缩放后的大小
                ltrBallRadius = ltrInitRadius * ltrScale
                rtlBallRadius = rtlInitRadius * rtlScale
            }
        }

        ltrPath?.reset()
        ltrPath?.addCircle(ltrX, centerY, ltrBallRadius, Path.Direction.CW)
        rtlPath?.reset()
        rtlPath?.addCircle(rtlX, centerY, rtlBallRadius, Path.Direction.CW)
        ltrPath?.let { rtlPath?.let { it1 -> mixPath?.op(it, it1, Path.Op.INTERSECT) } }


        canvas.drawPath(ltrPath?:Path(), ltrPaint?:Paint(Paint.ANTI_ALIAS_FLAG))
        canvas.drawPath(rtlPath?:Path(), rtlPaint?:Paint(Paint.ANTI_ALIAS_FLAG))
        canvas.drawPath(mixPath?:Path(), mixPaint?:Paint(Paint.ANTI_ALIAS_FLAG))
    }


//    override fun onDetachedFromWindow() {
//        stop()
//        super.onDetachedFromWindow()
//    }


    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }


    //公开方法

    /**
     * 停止动画
     */
    fun stop() {
        if (anim != null) {
            anim?.cancel()
            anim = null
        }
    }

    /**
     * 开始动画
     */
    fun start() {
        if (anim == null) {
            initAnim()
        }
        anim?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }

        post {
            isAnimCanceled = false
            isLtr = false
            if (anim == null) {
                initAnim()
            }
            anim?.start()
        }
    }

    /**
     * 设置小球半径和两小球间隔
     */
    fun setRadius(radius1: Float, radius2: Float, gap: Float) {
        stop()
        this.radius1 = radius1
        this.radius2 = radius2
        this.gap = gap
        checkAttr()
        distance = gap + radius1 + radius2
        requestLayout() //可能涉及宽高变化
    }


    /**
     * 设置小球颜色和重叠处颜色
     */
    fun setColors(color1: Int, color2: Int, mixColor: Int) {
        this.color1 = color1
        this.color2 = color2
        this.mixColor = color2
        checkAttr()
        paint1?.color = color1
        paint2?.color = color2
        mixPaint?.color = mixColor
        invalidate()
    }

    /**
     * 设置动画时长
     *
     * @param duration      [.duration]
     * @param pauseDuration [.pauseDuration]
     */
    fun setDuration(duration: Int, pauseDuration: Int) {
        this.duration = duration
        this.pauseDuration = pauseDuration
        checkAttr()
        initAnim()
    }

    /**
     * 设置移动过程中缩放倍数
     *
     * @param ltrScale [.ltrScale]
     * @param rtlScale [.rtlScale]
     */
    fun setScales(ltrScale: Float, rtlScale: Float) {
        stop()
        this.ltrScale = ltrScale
        this.rtlScale = rtlScale
        checkAttr()
        requestLayout() //可能涉及宽高变化
    }

    /**
     * 设置缩放开始、结束的范围
     *
     * @param scaleStartFraction [.scaleStartFraction]
     * @param scaleEndFraction   [.scaleEndFraction]
     */
    fun setStartEndFraction(scaleStartFraction: Float, scaleEndFraction: Float) {
        this.scaleStartFraction = scaleStartFraction
        this.scaleEndFraction = scaleEndFraction
        checkAttr()
        invalidate()
    }

    companion object {
        private val RTL_SCALE = 0.7f
        private val LTR_SCALR = 1.3f
        private val LEFT_COLOR = -0xbfc0
        private val RIGHT_COLOR = -0xff1112
        private val MIX_COLOR = Color.BLACK
        private val DURATION = 350
        private val PAUSE_DUARTION = 80
        private val SCALE_START_FRACTION = 0.2f
        private val SCALE_END_FRACTION = 0.8f
    }

}
