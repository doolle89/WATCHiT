package dusan.stefanovic.trainingapp.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ReflectionViewPager extends ViewPager {
	
	private float SCROLL_DURATION_FACTOR = 7f;
	
	public ReflectionViewPager(Context context) {
		super(context);
		setScrollDurationFactor(SCROLL_DURATION_FACTOR);
	}

	public ReflectionViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScrollDurationFactor(SCROLL_DURATION_FACTOR);
	}
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    
	private void setScrollDurationFactor(final float scrollDurationFactor) {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            Scroller mScroller = new Scroller(getContext(), (Interpolator) interpolator.get(null)) {
            	
            	@Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, (int) (duration * scrollDurationFactor));
                }
            	
            };
            scroller.set(this, mScroller);
        } catch (Exception e) {
        	
        }
    }
}
