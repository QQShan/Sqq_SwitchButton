package com.example.sqq.sqqswitchbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by sqq on 2016/6/11.
 */
public class SqqSwitchButton extends View implements AnimatorUpdateListener {
	/*	*//** 圆圈的开始和结束位置 *//*
	private float startX, endX;*/
	/** 圆圈X位置的最小和最大值 */
	private float spotMinX, spotMaxX;
	/** 边框大小 */
	private int borderWidth = 3;
	/** 垂直中心 */
	private float centerY = borderWidth;
	/** 圆圈的颜色 */
	private int spotColor = Color.parseColor("#ffffff");
	/** 浅白色 */
	private int closeColor = Color.parseColor("#e0e0e0");
	/** 圆圈大小 */
	private int spotSize;
	/** 圆圈X位置 */
	private float spotX;
	/** 开启颜色*/
	private int onColor = Color.parseColor("#4ebb7f");
	/** 关闭颜色 */
	private int offBorderColor = Color.parseColor("#d0d0d0");
//	private int offBorderColor = Color.parseColor("#ff0000");
	/** 边框颜色 */
	private int borderColor = offBorderColor;


	private float radius;
	private RectF rect = new RectF();
	private float mFirstDownY; // 首次按下的Y
	private float mFirstDownX; // 首次按下的X
	/**
	 * 获取的系统值，长按的判定时间
	 */
//	private int mClickTimeout;
	/**
	 * 获取的系统值，手的滑动大于这个值控件才会移动
	 */
	private int mTouchSlop;
	/**
	 * 系统动画时间
	 */
	private int animationtime;
	/** 开关状态 */
	private boolean toggleOn = false;

	private boolean isFirst = true;

	private OnToggleChanged listener;

	private Animator mCuranim;

	SqqShape ss,bb;

	public SqqSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SqqSwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public SqqSwitchButton(Context context) {
		super(context);
	}

	private void init(Context context) {
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		animationtime = getResources().getInteger(android.R.integer.
				config_shortAnimTime);
	}

	public void setToggleOn(boolean or){
		toggleOn = or;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/**
		 * 颜色改变部分
		 */
		if(toggleOn){
			borderColor = onColor;
		}else{
			borderColor = offBorderColor;
		}

		Paint paint;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		paint.setStrokeCap(Cap.ROUND);
		rect.set(0, 0, getWidth(), getHeight());
		paint.setColor(borderColor);
		canvas.drawRoundRect(rect, radius, radius, paint);

		canvas.save();
		canvas.translate(bb.getX(), bb.getY());
		Paint pain = bb.getShape().getPaint();
		pain.setColor(bb.getColor());
		bb.getShape().draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.translate(ss.getX(), ss.getY());
		ss.getShape().draw(canvas);
		// 恢复Canvas坐标系统
		canvas.restore();
		super.onDraw(canvas);
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float deltaX = Math.abs(x - mFirstDownX);
		float deltaY = Math.abs(y - mFirstDownY);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mFirstDownX = x;
				mFirstDownY = y;
				ChangeCBAnim();
				break;
			case MotionEvent.ACTION_MOVE:
				float offset = x-mFirstDownX;
				float finalx = ss.getX()+offset/8;
				if(finalx<spotMinX){
					finalx = spotMinX;
				}
				if(finalx>spotMaxX){
					finalx = spotMaxX;
				}
				ss.setX(finalx);
				break;
			case MotionEvent.ACTION_UP:
				if (deltaX == 0 || deltaY == 0 || deltaX > mTouchSlop) {
					if (deltaX > mTouchSlop) {
						if (ss.getX() > mTouchSlop) {
							toggleOn = true;
						} else {
							toggleOn = false;
						}
					}
					if(deltaX == 0 || deltaY == 0){
						toggleOn = !toggleOn;
					}
					SpringAnim(ss.getX(), toggleOn ? spotMaxX
							: spotMinX);

					if (listener != null) {
						listener.onToggle(toggleOn);
					}
				}

				break;
			default:
				break;
		}
		invalidate();
		return true;
	}

	/**
	 * @author sqq
	 *
	 */
	public interface OnToggleChanged {
		/**
		 * @param on
		 */
		public void onToggle(boolean on);
	}

	public void setOnToggleChanged(OnToggleChanged onToggleChanged) {
		listener = onToggleChanged;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator arg0) {
		this.invalidate();
	}

	/**
	 * 改变内部小的圆角矩形
	 */
	private void ChangeCBAnim() {
		/*PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
				0f);
		PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY",
				back > 0 ? 0f : 1f, back > 0 ? 1f : 0f);*/
		if (!toggleOn) {
			PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scale",
					1f, 0f);
			ObjectAnimator alpAnim = ObjectAnimator.ofPropertyValuesHolder(bb,
			/* pvhX, pvhZ, */pvhY);
			alpAnim.setDuration(100);
			alpAnim.start();
		}
	}

	private void SpringAnim(float startX,float endX){

		if(mCuranim !=null){
			mCuranim.cancel();
		}
		ObjectAnimator alpAnim = null;
		if (!toggleOn) {
			PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scale",
					0f, 1f);
			alpAnim= ObjectAnimator.ofPropertyValuesHolder(bb,pvhY);
			alpAnim.addUpdateListener(this);
		}

		if(startX!=endX){
			float back = (float) (0.05*(startX-endX));
			/**
			 * 小球滑到目标位置的动画
			 */
			ObjectAnimator sAnim = ObjectAnimator.ofFloat(ss, "x",
					startX, endX-back);
			// 设置sAnim动画的插值方式：加速插值
			sAnim.setInterpolator(new AccelerateInterpolator());
			sAnim.setDuration(120);
			sAnim.addUpdateListener(this);

			/**
			 * 小球弹到最终位置
			 */
			ObjectAnimator springAnim1 = ObjectAnimator.ofFloat(ss, "x",
					endX-back,endX);
			springAnim1.setInterpolator(new DecelerateInterpolator());
			springAnim1.addUpdateListener(this);

			// 定义一个AnimatorSet1来组合动画
			AnimatorSet animatorSet = new AnimatorSet();
			AnimatorSet animatorSet1 = new AnimatorSet();


			// 指定在播放springAnim1之前，先播放sAnim动画
			if(alpAnim !=null){
				animatorSet1.play(springAnim1).with(alpAnim);
				animatorSet1.setDuration(160);
				animatorSet.play(sAnim).before(animatorSet1);
			}else{
				animatorSet.play(sAnim).before(springAnim1);
			}
			animatorSet.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCuranim = null;
					super.onAnimationEnd(animation);
				}
				@Override
				public void onAnimationCancel(Animator animation) {
					mCuranim = null;
					super.onAnimationCancel(animation);
				}
			});
			// 开发播放动画
			animatorSet.start();
			mCuranim = animatorSet;
		}else{
			if(alpAnim!=null)
				alpAnim.start();
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		Resources r = Resources.getSystem();
		if (widthMode == MeasureSpec.UNSPECIFIED
				|| widthMode == MeasureSpec.AT_MOST) {
			widthSize = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,
					MeasureSpec.EXACTLY);
		}

		if (heightMode == MeasureSpec.UNSPECIFIED
				|| heightMode == MeasureSpec.AT_MOST) {
			// TypedValue.applyDimension是转变为标准尺寸的一个方法
			heightSize = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,
					MeasureSpec.EXACTLY);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
							int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if(isFirst){
			final int width = getWidth();
			final int height = getHeight();
			radius = Math.min(width, height) * 0.5f;
			spotSize = height - 2 * borderWidth;
			float spotR = spotSize * 0.5f;
			spotMinX = 2 * borderWidth;
			spotMaxX = width - height;
			spotX = toggleOn ? spotMaxX : spotMinX;

			// 创建一个圆
			OvalShape circle = new OvalShape();
			// 设置该椭圆的宽、高
			circle.resize(spotSize, spotSize);
			// 将圆包装成Drawable对象
			ShapeDrawable drawable = new ShapeDrawable(circle);
			ss = new SqqShape(drawable);
			ss.setX(spotX);
			ss.setY(centerY);
			Paint paints = drawable.getPaint();
			paints.setColor(spotColor);

			// 创建小圆角矩形
			float radiu = spotR;
			float[] out = {radiu,radiu,radiu,radiu,radiu,radiu,radiu,radiu};
			RoundRectShape recta = new RoundRectShape(out, null, null);
			if(toggleOn){
				recta.resize(0, 0);
			}else{
				recta.resize(width- 4 * borderWidth, height - 2 * borderWidth);
			}
			ShapeDrawable background = new ShapeDrawable(recta);
			bb = new SqqShape(background);
			bb.setX(borderWidth * 2);
			bb.setY(borderWidth);
			bb.setColor(closeColor);
			bb.setWidth(width- 4 * borderWidth);
			bb.setHeight(height - 2 * borderWidth);
			isFirst = false;
		}
	}

	/**
	 * window焦点发生变化的时候都会调用（对话款弹出关闭等），
	 * 第一次加载的时候调用这个方法时，表示这个activity对用户可见，但还是一片黑的，正在等待draw
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {

		super.onWindowFocusChanged(hasWindowFocus);
	}
}
