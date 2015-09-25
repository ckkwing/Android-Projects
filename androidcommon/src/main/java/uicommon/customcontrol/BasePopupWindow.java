package uicommon.customcontrol;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.echen.androidcommon.R;

import uicommon.customcontrol.Interface.IPopupWindowEvent;

/**
 * Created by echen on 2015/9/24.
 */
public class BasePopupWindow extends PopupWindow {

    public static class PopupWindowExtraParam {
        public Drawable Background = new BitmapDrawable();
        //x is HorizontalOffset, y is VerticalOffset
        public Point OffsetPoint = new Point(0, 0);
        public int PopupArrowMargin = 0;
        public int StyleResId = R.style.popup_anim_style;
        protected int arrowUpResId = -1;

        public int getArrowUpResId() {
            return arrowUpResId;
        }

        protected int arrowDownResId = -1;

        public int getArrowDownResId() {
            return arrowDownResId;
        }

        public boolean IsArrowVisible = true;

        public PopupWindowExtraParam() {

        }

        public PopupWindowExtraParam(int arrowUpResId, int arrowDownResId) {
            this.arrowUpResId = arrowUpResId;
            this.arrowDownResId = arrowDownResId;
        }
    }

    public enum PositionRelativeToAnchor {
        Unknown,
        Top,
        Bottom,
        Left,
        Right,
    }

    protected View m_contentView = null;
    protected View m_anchor = null;
    protected PopupWindow m_window = null;
    protected WindowManager m_windowManager = null;
    protected LayoutInflater m_inflater = null;
    protected ImageView m_arrowUp = null;
    protected ImageView m_arrowDown = null;
    protected PopupWindowExtraParam m_extraParam = null;
    //array 0 is Vertical, 1 is Horizontal
    protected PositionRelativeToAnchor[] m_positionRelativeToAnchor = new PositionRelativeToAnchor[2];

    protected IPopupWindowEvent m_iPopupWindowEvent = null;

    protected BasePopupWindow(View anchor, PopupWindowExtraParam extraParam) {
        super(anchor);
        this.m_anchor = anchor;
        this.m_window = new PopupWindow(m_anchor.getContext());
        this.m_window.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    BasePopupWindow.this.m_window.dismiss();
                    if (null != m_iPopupWindowEvent)
                        m_iPopupWindowEvent.WindowClosed(null);
                    return true;
                }
                return false;
            }
        });
        this.m_windowManager = (WindowManager) m_anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
        this.m_inflater = (LayoutInflater) m_anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.m_extraParam = (extraParam == null) ? new PopupWindowExtraParam() : extraParam;
    }

    public BasePopupWindow(View anchor, View contentView, PopupWindowExtraParam extraParam) {
        this(anchor, extraParam);
        m_contentView = contentView;
        initializeContentUIElements();
    }

    public BasePopupWindow(View anchor, int resIdOfContentView, PopupWindowExtraParam extraParam) {
        this(anchor, extraParam);
        m_contentView = m_inflater.inflate(resIdOfContentView, null);
        initializeContentUIElements();
    }

    public void setPopupWindowEvent(IPopupWindowEvent event)
    {
        this.m_iPopupWindowEvent = event;
    }

    protected void initializeContentUIElements()
    {
        if (m_extraParam.getArrowUpResId()>0 &&m_extraParam.getArrowDownResId() >0) {
            this.m_arrowUp = (ImageView) m_contentView.findViewById(m_extraParam.getArrowUpResId());
            this.m_arrowDown = (ImageView) m_contentView.findViewById(m_extraParam.getArrowDownResId());
        }
    }

    public void show() {
        preShow();

        Point position = new Point();
        setPopupPosition(position);
        setExtraPopupInfo();

        m_window.showAtLocation(this.m_anchor, Gravity.NO_GRAVITY, position.x, position.y);
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
        m_window.setAnimationStyle(m_extraParam.StyleResId);
    }

    protected void setPopupPosition(Point position) {
        int[] anchorLocation = new int[2];
        m_positionRelativeToAnchor[0] = PositionRelativeToAnchor.Unknown;
        m_positionRelativeToAnchor[1] = PositionRelativeToAnchor.Unknown;
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
        position.x = (screenWidth - contentViewWidth) / 2; //default x pos

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

        if ((anchorRect.left - m_extraParam.OffsetPoint.x + contentViewWidth) < screenWidth) {
            position.x = anchorRect.left - m_extraParam.OffsetPoint.x;
            m_positionRelativeToAnchor[1] = PositionRelativeToAnchor.Left;
        } else {
            if ((anchorRect.right + m_extraParam.OffsetPoint.x - contentViewWidth) > 0) {
                position.x = anchorRect.right + m_extraParam.OffsetPoint.x - contentViewWidth;
                m_positionRelativeToAnchor[1] = PositionRelativeToAnchor.Right;
            }
        }

        position.y = anchorRect.bottom; //default pop up on bottom
        if ((position.y + contentViewHeight) > screenHeight) {
            position.y = anchorRect.top - contentViewHeight;
            if (position.y > 0) {
                m_positionRelativeToAnchor[0] = PositionRelativeToAnchor.Top;
            }
        } else
            m_positionRelativeToAnchor[0] = PositionRelativeToAnchor.Bottom;
    }

    protected void setExtraPopupInfo() {
        if (m_extraParam.IsArrowVisible) {
            //set vertical arrow
            switch (m_positionRelativeToAnchor[0]) {
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

            //set horizontal
            RelativeLayout.LayoutParams upLayoutParams = (RelativeLayout.LayoutParams) m_arrowUp.getLayoutParams();
            RelativeLayout.LayoutParams downLayoutParams = (RelativeLayout.LayoutParams) m_arrowDown.getLayoutParams();
            switch (m_positionRelativeToAnchor[1]) {
                case Unknown: {
                }
                break;
                case Left: {
                    upLayoutParams.leftMargin = m_extraParam.PopupArrowMargin;
                    downLayoutParams.leftMargin = m_extraParam.PopupArrowMargin;

                }
                break;
                case Right: {
                    upLayoutParams.leftMargin = m_contentView.getMeasuredWidth() - m_extraParam.OffsetPoint.x - m_extraParam.PopupArrowMargin;
                    downLayoutParams.leftMargin = m_contentView.getMeasuredWidth() - m_extraParam.OffsetPoint.x - m_extraParam.PopupArrowMargin;
                }
                break;
            }
            m_arrowUp.setLayoutParams(upLayoutParams);
            m_arrowDown.setLayoutParams(downLayoutParams);
        }
    }
}
