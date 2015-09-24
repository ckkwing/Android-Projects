package uicommon.customcontrol;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * Created by echen on 2015/9/24.
 */
public class CustomPopupWindow extends PopupWindow {

    public static class PopupWindowExtraParam {
        public Drawable Background = new BitmapDrawable();
        //x is HorizontalOffset, y is VerticalOffset
        public Point OffsetPoint = new Point(0, 0);
        public int ArrowUpResId = -1;
        public int ArrowDownResId = -1;

        public PopupWindowExtraParam() {

        }
    }

    public enum PositionRelativeToAnchor {
        Unknown,
        Top,
        Bottom
    }

    protected View m_contentView = null;
    protected View m_anchor = null;
    protected PopupWindow m_window = null;
    protected WindowManager m_windowManager = null;
    protected LayoutInflater m_inflater = null;
    protected ImageView m_arrowUp = null;
    protected ImageView m_arrowDown = null;
    //    private Animation m_TrackAnim = null;
    protected PopupWindowExtraParam m_extraParam = null;
    protected PositionRelativeToAnchor m_positionRelativeToAnchor = PositionRelativeToAnchor.Unknown;

    protected CustomPopupWindow(View anchor, PopupWindowExtraParam extraParam) {
        super(anchor);
        this.m_anchor = anchor;
        this.m_window = new PopupWindow(m_anchor.getContext());
        this.m_window.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    CustomPopupWindow.this.m_window.dismiss();
                    return true;
                }
                return false;
            }
        });
        this.m_windowManager = (WindowManager) m_anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
        this.m_inflater = (LayoutInflater) m_anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_extraParam = (extraParam == null) ? new PopupWindowExtraParam() : extraParam;
        this.m_arrowUp = (ImageView) m_contentView.findViewById(m_extraParam.ArrowUpResId);
        this.m_arrowDown = (ImageView) m_contentView.findViewById(m_extraParam.ArrowDownResId);
    }

    public CustomPopupWindow(View anchor, View contentView, PopupWindowExtraParam extraParam) {
        this(anchor, extraParam);
        m_contentView = contentView;
    }

    public CustomPopupWindow(View anchor, int resIdOfContentView, PopupWindowExtraParam extraParam) {
        this(anchor, extraParam);
        m_contentView = m_inflater.inflate(resIdOfContentView, null);
    }

    public void show() {
        preShow();
        int[] anchorLocation = new int[2];
        //get location of m_anchor
        m_anchor.getLocationOnScreen(anchorLocation);
        //create a rectangle base on location of m_anchor
        Rect anchorRect = new Rect(anchorLocation[0], anchorLocation[1],
                anchorLocation[0] + m_anchor.getWidth(),
                anchorLocation[1] + m_anchor.getHeight());
        m_contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        m_contentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int contentViewWidth = m_contentView.getMeasuredWidth();
        int contentViewHeight = m_contentView.getMeasuredHeight();
        int screenWidth = m_windowManager.getDefaultDisplay().getWidth();
        int screenHeight = m_windowManager.getDefaultDisplay().getHeight();
        int xPos = (screenWidth - contentViewWidth) / 2; //default x pos

        //Offset with half of anchor
//        if ((anchorRect.left - anchorRect.width()/2 + contentViewWidth) < screenWidth)
//            xPos = anchorRect.left - anchorRect.width()/2;
//        else
//        {
//            if ((anchorRect.right + anchorRect.width()/2 - contentViewWidth) > 0)
//                xPos = anchorRect.right + anchorRect.width()/2 - contentViewWidth;
//        }

        //No offset
//        if ((anchorRect.left  + contentViewWidth) < screenWidth)
//            xPos = anchorRect.left;
//        else
//        {
//            if ((anchorRect.right - contentViewWidth) > 0)
//                xPos = anchorRect.right - contentViewWidth;
//        }

        if ((anchorRect.left - m_extraParam.OffsetPoint.x + contentViewWidth) < screenWidth)
            xPos = anchorRect.left - m_extraParam.OffsetPoint.x;
        else {
            if ((anchorRect.right + m_extraParam.OffsetPoint.x - contentViewWidth) > 0)
                xPos = anchorRect.right + m_extraParam.OffsetPoint.x - contentViewWidth;
        }

        int yPos = anchorRect.bottom; //default pop up on bottom
        if ((yPos + contentViewHeight) > screenHeight) {
            yPos = anchorRect.top - contentViewHeight;
            if (yPos > 0) {
                m_positionRelativeToAnchor = PositionRelativeToAnchor.Top;
            }
        } else
            m_positionRelativeToAnchor = PositionRelativeToAnchor.Bottom;

        switch (m_positionRelativeToAnchor) {
            case Unknown: {
                if (null != m_arrowUp)
                    m_arrowUp.setVisibility(View.INVISIBLE);
                if (null != m_arrowDown)
                    m_arrowDown.setVisibility(View.INVISIBLE);
            }
            break;
            case Top: {
                if (null != m_arrowUp)
                    m_arrowUp.setVisibility(View.INVISIBLE);
                if (null != m_arrowDown)
                    m_arrowDown.setVisibility(View.VISIBLE);
            }
            break;
            case Bottom: {
                if (null != m_arrowUp)
                    m_arrowUp.setVisibility(View.VISIBLE);
                if (null != m_arrowDown)
                    m_arrowDown.setVisibility(View.INVISIBLE);
            }
            break;
        }

        m_window.showAtLocation(this.m_anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    protected void preShow() {
        if (null == m_contentView) {
            throw new IllegalStateException("ContentView mustn't be null.");
        }

        m_window.setBackgroundDrawable(m_extraParam.Background);
        m_window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        m_window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        m_window.setTouchable(true);
        m_window.setFocusable(true);
        m_window.setOutsideTouchable(true);
        m_window.setContentView(m_contentView);
    }
}
