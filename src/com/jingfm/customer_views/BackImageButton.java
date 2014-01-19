package com.jingfm.customer_views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class BackImageButton extends ImageButton{
	OnClickListener mOnClickListener;
	public BackImageButton(Context context) {
		super(context, null);
    }

    public BackImageButton(Context context, AttributeSet attrs) {
    		super(context, attrs);
    }

    public BackImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public void setOnClickListener(OnClickListener onClickListener){
    		mOnClickListener = onClickListener;
    		super.setOnClickListener(mOnClickListener);
    }
    
    public OnClickListener getOnClickListener(){
    		return mOnClickListener;
    }
	
}
