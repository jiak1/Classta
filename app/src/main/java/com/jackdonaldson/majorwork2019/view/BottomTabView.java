package com.jackdonaldson.majorwork2019.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.jackdonaldson.majorwork2019.R;

public class BottomTabView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ImageView _centerImage;
    private ImageView _leftImage;
    private ImageView _rightImage;

    private View _indicator;
    private ArgbEvaluator _argbEvaluator;

    private int whiteColor;
    private int buttonColor;

    private int indicatorTranslationX;

    public BottomTabView(@NonNull Context context) {
        this(context,null);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setUpWithViewPager(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(1);
        onPageScrolled(1,0,0);

        _leftImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0){
                    viewPager.setCurrentItem(0);
                }
            }
        });

        _centerImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 1){
                    viewPager.setCurrentItem(1);
                }
            }
        });

        _rightImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 2){
                    viewPager.setCurrentItem(2);
                }
            }
        });
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_tabs,this,true);

        _centerImage = findViewById(R.id.vbt_center_image);
        _leftImage = findViewById(R.id.vbt_left_image);
        _rightImage = findViewById(R.id.vbt_right_image);

        _indicator = findViewById(R.id.vbt_indicator);

        _argbEvaluator = new ArgbEvaluator();

        indicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics());

        whiteColor = ContextCompat.getColor(getContext(),R.color.icon);
        buttonColor = ContextCompat.getColor(getContext(),R.color.white);

        _indicator.setBackgroundColor(buttonColor);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position == 0 && positionOffset >= 0){
            //Going away from left page, fade icon out
            setColor(_leftImage,true,positionOffset);
            setColor(_centerImage,false,positionOffset);
        }else if(position == 0 && positionOffset <= 0){
            setColor(_leftImage,false,positionOffset);
        }

        if(position == 1 && positionOffset >= 0){
            setColor(_centerImage,true,positionOffset);
            setColor(_rightImage,false,positionOffset);
        }else if(position == 1 && positionOffset <= 0){
            setColor(_centerImage,true,positionOffset);
            setColor(_leftImage,false,positionOffset);
        }

        if(position == 2 && positionOffset <= 0){
            setColor(_rightImage,true,positionOffset);
        }

        if(position == 0){
            //_indicator.setAlpha(1-positionOffset);
            _indicator.setScaleX(1-positionOffset);

            _indicator.setTranslationX((positionOffset-1)*indicatorTranslationX);
        }else if(position==1){
            //_indicator.setAlpha(positionOffset);
            _indicator.setScaleX(positionOffset);

            _indicator.setTranslationX(positionOffset*indicatorTranslationX);
        }
        _indicator.setAlpha(0);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setColor(ImageView _v, boolean fadeOut, float fractionFromCenter){

        if(fadeOut){
            fractionFromCenter = 1-fractionFromCenter;
        }

        int color = (int)_argbEvaluator.evaluate(fractionFromCenter,whiteColor,buttonColor);

        _v.setColorFilter(color);

        float scale = 1f + (fractionFromCenter*0.1f);
        _v.setScaleX(scale);
        _v.setScaleY(scale);
    }
}
