package com.yyl.recyclerviewvideo.view;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yyl on 2016/5/31/031.
 */
public class RecyclerViewVideo extends RelativeLayout {
    String tag = "RecyclerViewVideo";
    @Visibility
    private int intendedVisibility = VISIBLE;
    private int downTranslation;
    private boolean hidden = false;
    private boolean recyclerWantsTouch;
    private boolean isVertical;
    private boolean isAttachedToRecycler;

    private boolean isFullState;

    private RecyclerViewDelegate recyclerView;
    private LayoutManagerDelegate layoutManager;

    public RecyclerViewVideo(Context context) {
        super(context);
    }

    public RecyclerViewVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    RecyclerView recyclerRoot;

    /**
     * Attaches <code>RecyclerViewHeader</code> to <code>RecyclerView</code>.
     * Be sure that <code>setLayoutManager(...)</code> has been called for <code>RecyclerView</code> before calling this method.
     *
     * @param recycler <code>RecyclerView</code> to attach <code>RecyclerViewHeader</code> to.
     */
    public final void attachTo(@NonNull final RecyclerView recycler) {
        validate(recycler);
        this.recyclerRoot = recycler;
        this.recyclerView = RecyclerViewDelegate.with(recycler);
        this.layoutManager = LayoutManagerDelegate.with(recycler.getLayoutManager());
        isVertical = layoutManager.isVertical();
        isAttachedToRecycler = true;
        recyclerView.setHeaderDecoration(new HeaderItemDecoration());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onScrollChanged();
            }
        });
        recyclerView.setOnChildAttachListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Log.i(tag, "onChildViewDetachedFromWindow    hidden=" + hidden);
                recycler.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.invalidateItemDecorations();
                        onScrollChanged();
                    }
                });
            }
        });
    }

    /**
     * Detaches <code>RecyclerViewHeader</code> from <code>RecyclerView</code>.
     */
    public final void detach() {
        if (isAttachedToRecycler) {
            isAttachedToRecycler = false;
            recyclerWantsTouch = false;
            recyclerView.reset();
            recyclerView = null;
            layoutManager = null;
        }
    }

    private void onScrollChanged() {
        hidden = recyclerView.hasItems() && !layoutManager.isFirstRowVisible();
        // RecyclerViewVideo.super.setVisibility(hidden ? INVISIBLE : intendedVisibility);
        changeInVisibleState(hidden);
        Log.i(tag, "onScrollChanged    hidden=" + hidden);
    }

    private void changeInVisibleState(boolean hidden) {
        if (hidden) {
            smallState();
        } else {
            maxState();
            if (isFullState) return;
            final int translation = calculateTranslation();
            if (isVertical) {
                setTranslationY(translation);
            } else {
                setTranslationX(translation);
            }
        }
    }



    public void setFullVideo(boolean fullVideo) {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        if (fullVideo) {
            saveOldParams();//保存全屏之前的参数
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        isFullState = fullVideo;
        if (fullVideo) {
            //hide actionbar
            if (activity.getSupportActionBar() != null)
                activity.getSupportActionBar().hide();


            recyclerRoot.setEnabled(false);

            changeMaxState();
            setScaleRecoverView();
        } else {
            //show actionbar
            if (activity.getSupportActionBar() != null)
                activity.getSupportActionBar().show();
            recyclerRoot.setEnabled(true);
            setScaleVideo();
        }
    }

    boolean setLayout = false;

    public void setScaleVideo() {
        setLayout = true;
        if (layoutParams != null) {
            setLayoutParams(layoutParams);
            if (hidden) {
                setTranslationY(translationY);
                setTranslationX(translationX);
                setScaleX(scaleX);
                setScaleY(scaleY);
            }
        }
    }

    ViewGroup.LayoutParams layoutParams;
    float translationX;
    float translationY;
    float scaleX;
    float scaleY;

    public void saveOldParams() {
        if (layoutParams == null) {
            layoutParams = getLayoutParams();
        }
        if (hidden) {
            translationY = getTranslationY();
            translationX = getTranslationX();
            scaleX = getScaleX();
            scaleY = getScaleY();
        }

    }

    public void setScaleRecoverView() {
        setLayout = true;
        if (layoutParams == null) {
            layoutParams = getLayoutParams();
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
    }

    enum VState {
        Small, Max
    }

    VState state = VState.Max;

    private void smallState() {
        if (isFullState) return;
        //  if (state == VState.Small) return;
        setScaleX(0.5f);
        setScaleY(0.5f);
        setTranslationX(getWidth() >> 2);
        setTranslationY(-(getHeight() >> 2));
        state = VState.Small;
        if (changeVisivibleState != null) {
            changeVisivibleState.change(hidden);
        }
    }

    private void maxState() {
        //  if (state == VState.Max) return;
        changeMaxState();
    }

    private void changeMaxState() {
        setScaleX(1f);
        setScaleY(1f);
        setTranslationX(0);
        setTranslationY(0);
        state = VState.Max;
        if (changeVisivibleState != null) {
            changeVisivibleState.change(hidden);
        }
    }

    private OnChangeVisivibleState changeVisivibleState;

    public void setOnChangeVisivibleState(OnChangeVisivibleState changeVisivibleState) {
        this.changeVisivibleState = changeVisivibleState;
    }

    public interface OnChangeVisivibleState {
        void change(boolean hidden);
    }

    private int calculateTranslation() {
        int offset = recyclerView.getScrollOffset(isVertical);
        int base = layoutManager.isReversed() ? recyclerView.getTranslationBase(isVertical) : 0;
        return base - offset;
    }

    @Override
    public final void setVisibility(@Visibility int visibility) {
        this.intendedVisibility = visibility;
        if (!hidden) {
            super.setVisibility(intendedVisibility);
        }
    }


    @Visibility
    @Override
    public final int getVisibility() {
        return intendedVisibility;
    }

    @Override
    protected final void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i(tag, "onLayout    changed=" + changed);
        if (changed && isAttachedToRecycler) {
            int verticalMargins = 0;
            int horizontalMargins = 0;
            if (getLayoutParams() instanceof MarginLayoutParams) {
                final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                verticalMargins = layoutParams.topMargin + layoutParams.bottomMargin;
                horizontalMargins = layoutParams.leftMargin + layoutParams.rightMargin;
            }
            recyclerView.onHeaderSizeChanged(getHeight() + verticalMargins, getWidth() + horizontalMargins);
            onScrollChanged();
        }
    }


    @Override
    @CallSuper
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isFullState) {//全屏不拦截
            return super.onInterceptTouchEvent(ev);
        }
        recyclerWantsTouch = isAttachedToRecycler && recyclerView.onInterceptTouchEvent(ev);
        if (recyclerWantsTouch && ev.getAction() == MotionEvent.ACTION_MOVE) {
            downTranslation = calculateTranslation();
        }
        Log.i(tag, "onInterceptTouchEvent    recyclerWantsTouch=" + recyclerWantsTouch);
        return recyclerWantsTouch || super.onInterceptTouchEvent(ev);
    }

    @Override
    @CallSuper
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isFullState) {//全屏不拦截
            return super.onTouchEvent(event);
        }
        if (recyclerWantsTouch) { // this cannot be true if recycler is not attached
            int scrollDiff = downTranslation - calculateTranslation();
            int verticalDiff = isVertical ? scrollDiff : 0;
            int horizontalDiff = isVertical ? 0 : scrollDiff;
            MotionEvent recyclerEvent =
                    MotionEvent.obtain(event.getDownTime(),
                            event.getEventTime(),
                            event.getAction(),
                            event.getX() - horizontalDiff,
                            event.getY() - verticalDiff,
                            event.getMetaState());
            recyclerView.onTouchEvent(recyclerEvent);
            return false;
        }
        return super.onTouchEvent(event);
    }

    private void validate(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() == null) {
            throw new IllegalStateException("Be sure to attach RecyclerViewHeader after setting your RecyclerView's LayoutManager.");
        }
    }

    private class HeaderItemDecoration extends RecyclerView.ItemDecoration {
        private int headerHeight;
        private int headerWidth;
        private int firstRowSpan;

        public HeaderItemDecoration() {
            firstRowSpan = layoutManager.getFirstRowSpan();
        }

        public void setWidth(int width) {
            headerWidth = width;
        }

        public void setHeight(int height) {
            headerHeight = height;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            final boolean headerRelatedPosition = parent.getChildLayoutPosition(view) < firstRowSpan;
            int heightOffset = headerRelatedPosition && isVertical ? headerHeight : 0;
            int widthOffset = headerRelatedPosition && !isVertical ? headerWidth : 0;
            if (layoutManager.isReversed()) {
                outRect.bottom = heightOffset;
                outRect.right = widthOffset;
            } else {
                outRect.top = heightOffset;
                outRect.left = widthOffset;
            }
        }
    }

    private static class RecyclerViewDelegate {

        @NonNull
        private final RecyclerView recyclerView;
        private HeaderItemDecoration decoration;
        private RecyclerView.OnScrollListener onScrollListener;
        private RecyclerView.OnChildAttachStateChangeListener onChildAttachListener;

        private RecyclerViewDelegate(final @NonNull RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public static RecyclerViewDelegate with(@NonNull RecyclerView recyclerView) {
            return new RecyclerViewDelegate(recyclerView);
        }

        public final void onHeaderSizeChanged(int height, int width) {
            if (decoration != null) {
                decoration.setHeight(height);
                decoration.setWidth(width);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateItemDecorations();
                    }
                });
            }
        }

        private void invalidateItemDecorations() {
            if (!recyclerView.isComputingLayout()) {
                recyclerView.invalidateItemDecorations();
            }
        }

        public final int getScrollOffset(boolean isVertical) {
            return isVertical ? recyclerView.computeVerticalScrollOffset() : recyclerView.computeHorizontalScrollOffset();
        }

        public final int getTranslationBase(boolean isVertical) {
            return isVertical ?
                    recyclerView.computeVerticalScrollRange() - recyclerView.getHeight() :
                    recyclerView.computeHorizontalScrollRange() - recyclerView.getWidth();
        }

        public final boolean hasItems() {
            return recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() != 0;
        }

        public final void setHeaderDecoration(HeaderItemDecoration decoration) {
            clearHeaderDecoration();
            this.decoration = decoration;
            recyclerView.addItemDecoration(this.decoration, 0);
        }

        public final void clearHeaderDecoration() {
            if (decoration != null) {
                recyclerView.removeItemDecoration(decoration);
                decoration = null;
            }
        }

        public final void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
            clearOnScrollListener();
            this.onScrollListener = onScrollListener;
            recyclerView.addOnScrollListener(this.onScrollListener);
        }

        public final void clearOnScrollListener() {
            if (onScrollListener != null) {
                recyclerView.removeOnScrollListener(onScrollListener);
                onScrollListener = null;
            }
        }

        public final void setOnChildAttachListener(RecyclerView.OnChildAttachStateChangeListener onChildAttachListener) {
            clearOnChildAttachListener();
            this.onChildAttachListener = onChildAttachListener;
            recyclerView.addOnChildAttachStateChangeListener(this.onChildAttachListener);
        }

        public final void clearOnChildAttachListener() {
            if (onChildAttachListener != null) {
                recyclerView.removeOnChildAttachStateChangeListener(onChildAttachListener);
                onChildAttachListener = null;
            }
        }

        public final void reset() {
            clearHeaderDecoration();
            clearOnScrollListener();
            clearOnChildAttachListener();
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return recyclerView.onInterceptTouchEvent(ev);
        }

        public boolean onTouchEvent(MotionEvent ev) {
            return recyclerView.onTouchEvent(ev);
        }

    }

    private static class LayoutManagerDelegate {
        @Nullable
        private final LinearLayoutManager linear;
        @Nullable
        private final GridLayoutManager grid;
        @Nullable
        private final StaggeredGridLayoutManager staggeredGrid;

        private LayoutManagerDelegate(@NonNull RecyclerView.LayoutManager manager) {
            final Class<? extends RecyclerView.LayoutManager> managerClass = manager.getClass();
            if (managerClass == LinearLayoutManager.class) { //not using instanceof on purpose
                linear = (LinearLayoutManager) manager;
                grid = null;
                staggeredGrid = null;
            } else if (managerClass == GridLayoutManager.class) {
                linear = null;
                grid = (GridLayoutManager) manager;
                staggeredGrid = null;
//            } else if (manager instanceof StaggeredGridLayoutManager) { //TODO: 05.04.2016 implement staggered
//                linear = null;
//                grid = null;
//                staggeredGrid = (StaggeredGridLayoutManager) manager;
            } else {
                throw new IllegalArgumentException("Currently RecyclerViewHeader supports only LinearLayoutManager and GridLayoutManager.");
            }
        }

        public static LayoutManagerDelegate with(@NonNull RecyclerView.LayoutManager layoutManager) {
            return new LayoutManagerDelegate(layoutManager);
        }

        public final int getFirstRowSpan() {
            if (linear != null) {
                return 1;
            } else if (grid != null) {
                return grid.getSpanCount();
//            } else if (staggeredGrid != null) {
//                return staggeredGrid.getSpanCount(); //TODO: 05.04.2016 implement staggered
            }
            return 0; //shouldn't get here
        }

        public final boolean isFirstRowVisible() {
            if (linear != null) {
                return linear.findFirstVisibleItemPosition() == 0;
            } else if (grid != null) {
                return grid.findFirstVisibleItemPosition() == 0;
//            } else if (staggeredGrid != null) {
//                return staggeredGrid.findFirstCompletelyVisibleItemPositions() //TODO: 05.04.2016 implement staggered
            }
            return false; //shouldn't get here
        }

        public final boolean isReversed() {
            if (linear != null) {
                return linear.getReverseLayout();
            } else if (grid != null) {
                return grid.getReverseLayout();
//            } else if (staggeredGrid != null) {
//                return ; //TODO: 05.04.2016 implement staggered
            }
            return false; //shouldn't get here
        }

        public final boolean isVertical() {
            if (linear != null) {
                return linear.getOrientation() == LinearLayoutManager.VERTICAL;
            } else if (grid != null) {
                return grid.getOrientation() == LinearLayoutManager.VERTICAL;
//            } else if (staggeredGrid != null) {
//                return ; //TODO: 05.04.2016 implement staggered
            }
            return false; //shouldn't get here
        }
    }

    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Visibility {
    }

}