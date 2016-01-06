package uicommon.customcontrol.Listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ExpandableListView;

/**
 * Created by echen on 2015/11/20.
 */
public class ExpandableListViewForScrollView extends ExpandableListView {
    private static final String TAG = "ScrolledExpandableList";

    public ExpandableListViewForScrollView(Context context) {
        super(context);
    }

    public ExpandableListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                setParentScrollAble(false);//当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
                Log.d(TAG, "onInterceptTouchEvent down");
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                Log.d(TAG, "onInterceptTouchEvent move");
            }
            break;
            case MotionEvent.ACTION_UP: {
                Log.d(TAG, "onInterceptTouchEvent up");
            }
            break;
            case MotionEvent.ACTION_CANCEL: {
                setParentScrollAble(true);//当手指松开时，让父ScrollView重新拿到onTouch权限
                Log.d(TAG, "onInterceptTouchEvent cancel");
            }
            break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void setParentScrollAble(boolean flag) {
        getParent().requestDisallowInterceptTouchEvent(flag); //getParent is the scrollview which is wrapping listview
    }
}
