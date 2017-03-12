package com.austin.viewpagerindicator.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.austin.viewpagerindicator.R;

import java.util.List;

/**
 * Created by Austin on 2017/3/12.
 */

public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;

    private int mTriangleWidth;
    private int mTriangleHeight;
    private float mTriangleCornerRadis = 3;

    private Path mPath;
    private static final float RATIO_TRIANGLE_WIDTH = 1/6f;

    private int mInitTranslationX;
    private int mTranslateX;

    private int mVisiableTabCount;
    private static final int DEFAULT_VISIBLE_TAB_COUNT = 3;
    private List<String> mTitles;

    private static final int TAB_TEXT_COLOR = 0x77ffffff;
    private static final int TAB_TEXT_COLOR_HIGHLIGHT = 0xffffffff;
    private int mTabTextColor = TAB_TEXT_COLOR;
    private ViewPager mViewPager;

    private final int TRIANGLE_WIDTH_MAX = (int) (this.getScreenWidth() / 3 * RATIO_TRIANGLE_WIDTH);

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mVisiableTabCount = a.getInt(R.styleable.ViewPagerIndicator_visibleTabCount, DEFAULT_VISIBLE_TAB_COUNT);
        if (mVisiableTabCount < 0) {
            mVisiableTabCount = DEFAULT_VISIBLE_TAB_COUNT;
        }
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(mTriangleCornerRadis));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslateX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.weight = 0;
            layoutParams.width = getScreenWidth() / mVisiableTabCount;
            view.setLayoutParams(layoutParams);
        }
        setOnTabClick();
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mTriangleWidth = (int) (w / mVisiableTabCount * RATIO_TRIANGLE_WIDTH);
        mTriangleWidth = Math.min(mTriangleWidth, TRIANGLE_WIDTH_MAX);
        mTriangleHeight = (int) (Math.tan(Math.PI / 6) * mTriangleWidth / 2);
        mInitTranslationX = w / mVisiableTabCount / 2 - mTriangleWidth / 2;
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth() / mVisiableTabCount;
        mTranslateX = (int) (tabWidth * (position + positionOffset));
        if (position >= mVisiableTabCount - 2 && positionOffset > 0 && getChildCount() > mVisiableTabCount) {
            int gap = position - mVisiableTabCount + 2;

            if (mVisiableTabCount != 1) {
                scrollTo((int) (tabWidth * (gap + positionOffset)), 0);
            }else{
                scrollTo((int) ((position + positionOffset) * tabWidth), 0);
            }
        }
        invalidate();
    }


    public void setTabTitles(List<String> titles, int mVisiableTabCount) {
        this.mVisiableTabCount = mVisiableTabCount;
        if (titles != null && titles.size() > 0) {
            removeAllViews();
            mTitles = titles;
            for (String mTitle : mTitles) {
                addView(generateTextView(mTitle));
            }
            setTextHighlight(0);
            setOnTabClick();
        }

    }

    private View generateTextView(String mTitle) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = getScreenWidth() / mVisiableTabCount;
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setText(mTitle);
        textView.setTextColor(mTabTextColor);
        return textView;
    }


    public interface OnPageChangeListener{
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);


        public void onPageSelected(int position);


        public void onPageScrollStateChanged(int state);
    }

    private OnPageChangeListener listener;


    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }


    public void setViewPager(ViewPager viewPager, int currentPosition) {

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (listener != null) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {

                if (listener != null) {
                    listener.onPageSelected(position);
                }

                setTextHighlight(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        });

    }


    private void setTextHighlight(int position) {
        resetTextColor();
        View childAt = getChildAt(position);
        if (null != childAt && childAt instanceof TextView) {
            ((TextView) childAt).setTextColor(TAB_TEXT_COLOR_HIGHLIGHT);
        }
    }

    private void resetTextColor() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setTextColor(TAB_TEXT_COLOR);
            }
        }
    }


    private void setOnTabClick(){
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            final int finalI = i;
            childAt.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(finalI);
                }
            });
        }

    }
}
