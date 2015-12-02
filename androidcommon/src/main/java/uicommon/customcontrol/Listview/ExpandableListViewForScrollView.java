package uicommon.customcontrol.Listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by echen on 2015/11/20.
 */
public class ExpandableListViewForScrollView extends ExpandableListView {
    public ExpandableListViewForScrollView(Context context) {
        super(context);
    }

    public ExpandableListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
