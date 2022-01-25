// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.OnTouchEventListener;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.ZoomScaleType;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.ZoomableContentListener;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.ZoomableController;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * FrameLayout that has zoomable capabilities.
 * <p>
 * Once the image loads, pinch-to-zoom and translation gestures are enabled.
 */
public class ZoomableFrameLayout extends FrameLayout
        implements ScrollingView, ZoomableContentListener, OnTouchEventListener, IZoomableControllerProvider {
    private final RectF mImageBounds = new RectF();
    private final RectF mViewBounds = new RectF();
    private Matrix mScaleTypeTransform;
    private Matrix mCurrentTransform;
    private final ZoomableController.Listener mZoomableListener = new ZoomableController.Listener() {
        @Override
        public void onTransformChanged(final Matrix transform) {
            ZoomableFrameLayout.this.onTransformChanged(transform);
        }
    };
    private Function0<Unit> showFloatingHeaderCallBackCallBack;
    private ZoomableController mZoomableController;
    private GestureDetector mTapGestureDetector;
    private OnTouchEventListener mOnTouchEventListener;
    private Fling mFling;
    private final GestureListenerWrapper mTapListenerWrapper = new GestureListenerWrapper(this) {
        @Override
        public boolean onDown(final MotionEvent e) {
            mFling.stop();
            return super.onDown(e);
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                               final float velocityX, final float velocityY) {
            mFling.stop().start(velocityX, velocityY);
            return true;
        }
    };
    private boolean mLayoutReady = false;

    @Nullable
    private OnLayoutReadyListener mOnLayoutReadyListener = null;

    @ZoomScaleType
    private int mZoomScaleType = ZoomScaleType.FIT_INSIDE;

    private int mContentWidth;

    private int mContentHeight;

    private float mRoiScale;

    //Size of content view, e.g. TextureView or WebView..
    //Used as a fallback if this.getWidth() or this.getHeight() return 0
    private int mContentViewWidth = 0;
    private int mContentViewHeight = 0;

    private boolean mAllowTouchInterceptionWhileZoomed = true;

    @Nullable
    private RegionOfInterest mRegionOfInterest;

    public ZoomableFrameLayout(final Context context) {
        super(context);
        init();
    }

    public ZoomableFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZoomableFrameLayout(final Context context,
                               final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public static ContentSize calculateFitInside(final int parentWidth,
                                                 final int parentHeight,
                                                 final int contentWidth,
                                                 final int contentHeight) {
        float scaleWidth = 1;
        float scaleHeight = 1;

        if (contentWidth > 0 && parentWidth > 0) {
            scaleWidth = (float) parentWidth / contentWidth;
        }
        if (contentHeight > 0 && parentHeight > 0) {
            scaleHeight = (float) parentHeight / contentHeight;
        }

        final float scale = Math.min(scaleWidth, scaleHeight);

        final int scaledWidth = (int) (scale * contentWidth);
        final int scaledHeight = (int) (scale * contentHeight);

        return new ContentSize(scaledWidth, scaledHeight, scale);
    }

    public void setRoiScale(final float scale) {
        mRoiScale = scale;
    }

    @Nullable
    public RegionOfInterest getRegionOfInterest() {
        return mRegionOfInterest;
    }

    // clear region of interest when the scaleType is select manually and while zooming or touch events
    public void setRegionOfInterest(@Nullable final RegionOfInterest regionOfInterest) {
        mRegionOfInterest = regionOfInterest;
    }

    /**
     * Gets the zoomable controller.
     * <p>
     * <p> Zoomable controller can be used to zoom to point, or to map point from view to image
     * coordinates for instance.
     */
    @Override
    public ZoomableController getZoomableController() {
        return mZoomableController;
    }

    /**
     * Sets a custom zoomable controller, instead of using the default one.
     */
    public void setZoomableController(@NonNull final ZoomableController zoomableController) {
        mZoomableController.setListener(null);
        mZoomableController = zoomableController;
        mZoomableController.setListener(mZoomableListener);
    }

    /**
     * Gets the current scale factor.
     */
    public float getScaleFactor() {
        final float[] transformValues = new float[9];
        mScaleTypeTransform.getValues(transformValues);

        // X and Y scales should always be the same
        float result = transformValues[Matrix.MSCALE_X];

        // Ignore if current transform is not available
        if (mCurrentTransform != null) {
            mCurrentTransform.getValues(transformValues);
            result *= transformValues[Matrix.MSCALE_X];
        }

        return result;
    }

    /**
     * If this is set to true, parent views can intercept touch events while the view is zoomed.
     * For example, this can be used to swipe between images in a view pager while zoomed.
     *
     * @param allowTouchInterceptionWhileZoomed true if the parent needs to intercept touches
     */
    public void setAllowTouchInterceptionWhileZoomed(final boolean allowTouchInterceptionWhileZoomed) {
        mAllowTouchInterceptionWhileZoomed = allowTouchInterceptionWhileZoomed;
    }

    public void setTouchEventListener(@NonNull final OnTouchEventListener onTapListener) {
        mOnTouchEventListener = onTapListener;
    }

    /**
     * Sets the tap listener.
     */
    public void setTapListener(final GestureDetector.SimpleOnGestureListener tapListener) {
        mTapListenerWrapper.setListener(tapListener);
    }

    /**
     * Sets whether long-press tap detection is enabled.
     * Unfortunately, long-press conflicts with onDoubleTapEvent.
     */
    public void setIsLongpressEnabled(final boolean enabled) {
        mTapGestureDetector.setIsLongpressEnabled(enabled);
    }

    public void disableDoubleTap() {
        mTapListenerWrapper.disableDoubleTap();
    }

    void setOnLayoutReadyListener(@Nullable final OnLayoutReadyListener onLayoutReadyListener) {
        if (mLayoutReady && onLayoutReadyListener != null) {
            onLayoutReadyListener.onLayoutReady();
            return;
        }

        mOnLayoutReadyListener = onLayoutReadyListener;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mTapGestureDetector.onTouchEvent(event)) {
            return true;
        }

        if (mZoomableController.onTouchEvent(event)) {
            if (!mAllowTouchInterceptionWhileZoomed && !mZoomableController.isIdentity()) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            return true;
        }
        if (super.onTouchEvent(event)) {
            return true;
        }

        // None of our components reported that they handled the touch event. Upon returning false
        // from this method, our parent won't send us any more events for this gesture. Unfortunately,
        // some components may have started a delayed action, such as a long-press timer, and since we
        // won't receive an ACTION_UP that would cancel that timer, a false event may be triggered.
        // To prevent that we explicitly send one last cancel event when returning false.
        final MotionEvent cancelEvent = MotionEvent.obtain(event);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
        mTapGestureDetector.onTouchEvent(cancelEvent);
        mZoomableController.onTouchEvent(cancelEvent);
        cancelEvent.recycle();
        return false;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return mZoomableController.computeHorizontalScrollRange();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return mZoomableController.computeHorizontalScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return mZoomableController.computeHorizontalScrollExtent();
    }

    @Override
    public int computeVerticalScrollRange() {
        return mZoomableController.computeVerticalScrollRange();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return mZoomableController.computeVerticalScrollOffset();
    }

    @Override
    public int computeVerticalScrollExtent() {
        return mZoomableController.computeVerticalScrollExtent();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final int saveCount = canvas.save();
        canvas.concat(mZoomableController.getTransform());
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onLayout(final boolean changed, final int left,
                            final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getImageBounds(mImageBounds);
        updateZoomableControllerBounds();
        onLayoutReady();
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        final int saveCount = canvas.save();
        canvas.concat(mCurrentTransform);
        canvas.concat(mScaleTypeTransform);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @ZoomScaleType
    public int getScaleType() {
        return mZoomScaleType;
    }

    @Override
    public void setScaleType(@ZoomScaleType final int scaleType) {
        mZoomScaleType = scaleType;
        calculateTransformationBasedOnScaleType(true, 0);
    }

    @Override
    public void onContentReady(@IntRange(from = 0) final int width,
                               @IntRange(from = 0) final int height, final boolean forceReset) {

        onContentReady(width, height, mContentViewWidth, mContentViewHeight, forceReset);
    }

    @Override
    public void onContentReady(@IntRange(from = 0) final int width, @IntRange(from = 0) final int height,
                               final int contentViewWidth, final int contentViewHeight, final boolean forceReset) {
        if (!forceReset && mContentWidth == width && mContentHeight == height
                && mContentViewWidth == contentViewWidth && mContentViewHeight == contentViewHeight) {
            return;
        }


        mContentWidth = width;
        mContentHeight = height;
        mContentViewWidth = contentViewWidth;
        mContentViewHeight = contentViewHeight;
        calculateTransformationBasedOnScaleType(true, 0);
    }

    @Override
    public void resetTransformations() {
        calculateTransformationBasedOnScaleType(true, 0);
    }

    @Override
    public void disableInteractions() {
        mZoomableController.setEnabled(false);
    }

    @Override
    public void enableInteractions() {
        mZoomableController.setEnabled(true);
    }

    @Override
    public boolean onTap() {
        showFloatingHeaderCallBackCallBack.invoke();
        return mOnTouchEventListener != null && mOnTouchEventListener.onTap();
    }

    @Override
    public boolean onDoubleTap() {
        if (mOnTouchEventListener != null && mOnTouchEventListener.onDoubleTap()) {
            return true;
        }

        switch (mZoomScaleType) {
            case ZoomScaleType.CENTER:
                return false;
            case ZoomScaleType.FIT_INSIDE:
                setScaleType(ZoomScaleType.ZOOM_TO_FIT);
                return true;
            case ZoomScaleType.ZOOM_TO_FIT:
                setScaleType(ZoomScaleType.FIT_INSIDE);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onLongPress() {
        if (mOnTouchEventListener != null) {
            mOnTouchEventListener.onLongPress();
        }
    }

    @Override
    public boolean onSwipeLeft() {
        return false;
    }

    @Override
    public boolean onSwipeRight() {
        return false;
    }

    public void removeTouchEventListener() {
        mOnTouchEventListener = null;
    }

    /**
     * Gets the original image bounds, in view-absolute coordinates.
     * <p>
     * <p> The original image bounds are those reported by the hierarchy. The hierarchy itself may
     * apply scaling on its own (e.g. due to scale type) so the reported bounds are not necessarily
     * the same as the actual bitmap dimensions. In other words, the original image bounds correspond
     * to the image bounds within this view when no zoomable transformation is applied, but including
     * the potential scaling of the hierarchy.
     * Having the actual bitmap dimensions abstracted away from this view greatly simplifies
     * implementation because the actual bitmap may change (e.g. when a high-res image arrives and
     * replaces the previously set low-res image). With proper hierarchy scaling (e.g. FIT_CENTER),
     * this underlying change will not affect this view nor the zoomable transformation in any way.
     */
    protected void getImageBounds(final RectF outBounds) {
        // This assumes the content is centered in the view, until we can add ScaleTypes
        final View childView = getChildAt(0);
        if (childView != null) {
            final int width = childView.getWidth();
            final int height = childView.getHeight();
            final int offsetX = (getWidth() - width) >> 1;
            final int offsetY = (getHeight() - height) >> 1;
            outBounds.set(offsetX, offsetY, offsetX + width, offsetY + height);
        }
    }

    /**
     * Gets the bounds used to limit the translation, in view-absolute coordinates.
     * <p>
     * <p> These bounds are passed to the zoomable controller in order to limit the translation. The
     * image is attempted to be centered within the limit bounds if the transformed image is smaller.
     * There will be no empty spaces within the limit bounds if the transformed image is bigger.
     * This applies to each dimension (horizontal and vertical) independently.
     * <p> Unless overridden by a subclass, these bounds are same as the view bounds.
     */
    protected void getLimitBounds(final RectF outBounds) {
        outBounds.set(0, 0, getWidth(), getHeight());
    }

    protected void onTransformChanged(final Matrix transform) {
        mCurrentTransform = transform;
        invalidate();
    }

    protected void updateZoomableControllerBounds() {
        getLimitBounds(mViewBounds);
        mZoomableController.setViewBounds(mViewBounds);
        mZoomableController.setImageBounds(mImageBounds);
    }

    protected ZoomableController createZoomableController() {
        return AnimatedZoomableController.newInstance();
    }

    void removeOnLayoutReadyListener() {
        mOnLayoutReadyListener = null;
    }

    private void onLayoutReady() {
        mLayoutReady = true;

        if (mOnLayoutReadyListener == null) {
            return;
        }

        mOnLayoutReadyListener.onLayoutReady();
        mOnLayoutReadyListener = null;
    }

    private boolean areInteractionsEnabled() {
        return mZoomableController.isEnabled();
    }

    private void calculateTransformationBasedOnScaleType(final boolean rePostIfDimensionsInvalid,
                                                         final int delayMillis) {
        // Queue all transformations to ensure they are done in the right order
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentTransform != null) {
                    mCurrentTransform.reset();
                }

                mScaleTypeTransform.reset();

                // calculate transformation for fit to ROI if region of interest is available
                if (mRegionOfInterest != null) {
                    calculateTransformationFitRoi(mScaleTypeTransform, true);
                } else {
                    switch (mZoomScaleType) {
                        case ZoomScaleType.CENTER:
                            calculateTransformationCentering(mScaleTypeTransform,
                                    mImageBounds, rePostIfDimensionsInvalid);
                            break;
                        case ZoomScaleType.ZOOM_TO_FIT:
                            calculateTransformationZoomFit(mScaleTypeTransform,
                                    mImageBounds, rePostIfDimensionsInvalid);
                            break;
                        case ZoomScaleType.FIT_INSIDE:
                        default:
                            calculateTransformationFitInside(mScaleTypeTransform,
                                    mImageBounds, rePostIfDimensionsInvalid);
                            break;
                    }
                }

                updateZoomableControllerBounds();
                invalidate();
            }
        }, delayMillis);
    }

    private void calculateTransformationZoomFit(@NonNull final Matrix scaleTransform,
                                                @NonNull final RectF imageBounds,
                                                final boolean rePostIfDimensionsInvalid) {
        int containerWidth = getWidth();
        int containerHeight = getHeight();

        if (rePostIfDimensionsInvalid && (containerWidth == 0 || containerHeight == 0)) {

            calculateTransformationBasedOnScaleType(false, 100);
            return;
        }

        //To fix the issue that video is cropped by 1/4.
        //If the container size(width or height) is still 0 after retry then fallback to have them
        //as the same size as content view size, since actually content view size is same as Zoomable layout size.
        if (containerWidth == 0 || containerHeight == 0) {

            containerWidth = mContentViewWidth;
            containerHeight = mContentViewHeight;
        }

        float scaleWidth = 1;
        float scaleHeight = 1;

        if (containerWidth > 0 && mContentWidth > 0) {
            scaleWidth = (float) containerWidth / mContentWidth;
        }
        if (containerHeight > 0 && mContentHeight > 0) {
            scaleHeight = (float) containerHeight / mContentHeight;
        }

        final float scale = Math.max(scaleWidth, scaleHeight);
        final int scaledWidth = (int) (scale * mContentWidth);
        final int scaledHeight = (int) (scale * mContentHeight);

        final float offsetX = (containerWidth - scaledWidth) / 2;
        final float offsetY = (containerHeight - scaledHeight) / 2;
        imageBounds.set(offsetX, offsetY, containerWidth - offsetX, containerHeight - offsetY);
        scaleTransform.setScale(scale, scale);
        scaleTransform.postTranslate(offsetX, offsetY);

        final float baseScale = Math.min(scaleWidth, scaleHeight);
        final float baseMinScale = baseScale / scale;
        mZoomableController.setMinScaleFactor(baseMinScale);
        mZoomableController.setMaxScaleFactor(baseMinScale * 4);
    }

    private void calculateTransformationFitInside(@NonNull final Matrix scaleTransform,
                                                  @NonNull final RectF imageBounds,
                                                  final boolean rePostIfDimensionsInvalid) {
        int containerWidth = getWidth();
        int containerHeight = getHeight();

        if (rePostIfDimensionsInvalid && (containerWidth == 0 || containerHeight == 0)) {

            calculateTransformationBasedOnScaleType(false, 100);
            return;
        }

        final ContentSize scaledContentSize = calculateFitInside(containerWidth,
                containerHeight, mContentWidth, mContentHeight);
        final int scaledContentSizeWidth = scaledContentSize.getWidth();
        final int scaledContentSizeHeight = scaledContentSize.getHeight();

        //See comment of calculateTransformationZoomFit()
        if (containerWidth == 0 || containerHeight == 0) {

            containerWidth = mContentViewWidth;
            containerHeight = mContentViewHeight;
        }

        final float offsetX = (containerWidth - scaledContentSizeWidth) / 2;
        final float offsetY = (containerHeight - scaledContentSizeHeight) / 2;
        imageBounds.set(offsetX, offsetY, containerWidth - offsetX, containerHeight - offsetY);
        scaleTransform.setScale(scaledContentSize.getScale(), scaledContentSize.getScale());
        scaleTransform.postTranslate(offsetX, offsetY);
        mZoomableController.setMinScaleFactor(1);
        mZoomableController.setMaxScaleFactor(4);
    }

    private void calculateTransformationCentering(@NonNull final Matrix scaleTransform,
                                                  @NonNull final RectF imageBounds,
                                                  final boolean rePostIfDimensionsInvalid) {
        int containerWidth = getWidth();
        int containerHeight = getHeight();

        if (rePostIfDimensionsInvalid && (containerWidth == 0 || containerHeight == 0)) {

            calculateTransformationBasedOnScaleType(false, 100);
            return;
        }

        //See comment of calculateTransformationZoomFit()
        if (containerWidth == 0 || containerHeight == 0) {

            containerWidth = mContentViewWidth;
            containerHeight = mContentViewHeight;
        }

        final float offsetX = (containerWidth - mContentWidth) / 2;
        final float offsetY = (containerHeight - mContentHeight) / 2;
        imageBounds.set(offsetX, offsetY, containerWidth - offsetX, containerHeight - offsetY);
        scaleTransform.postTranslate(offsetX, offsetY);
        mZoomableController.setMinScaleFactor(1);
        mZoomableController.setMaxScaleFactor(4);
    }

    private void calculateTransformationFitRoi(@NonNull final Matrix scaleTransform,
                                               final boolean rePostIfDimensionsInvalid) {
        int containerWidth = getWidth();
        int containerHeight = getHeight();

        if (rePostIfDimensionsInvalid && (containerWidth == 0 || containerHeight == 0)) {

            calculateTransformationBasedOnScaleType(false, 100);
            return;
        }

        //See comment of calculateTransformationZoomFit()
        if (containerWidth == 0 || containerHeight == 0) {

            containerWidth = mContentViewWidth;
            containerHeight = mContentViewHeight;
        }

        if (mRegionOfInterest == null) {

            return;
        }

        // video is scaled on onSizeChanged event, apply the captured scale on roi as well for adjusting it in the view.
        float scaledOffsetX = mRoiScale * mRegionOfInterest.offsetX;
        float scaledOffsetY = mRoiScale * mRegionOfInterest.offsetY;
        float scaledWidth = mRoiScale * mRegionOfInterest.width;
        float scaledHeight = mRoiScale * mRegionOfInterest.height;

        // remove decimals to make sure the roi dimensions are in containers dimensions
        scaledOffsetX = removeDecimal(scaledOffsetX);
        scaledOffsetY = removeDecimal(scaledOffsetY);
        scaledWidth = removeDecimal(scaledWidth);
        scaledHeight = removeDecimal(scaledHeight);

        if (scaledHeight > containerHeight
                || scaledWidth > containerWidth
                || scaledHeight < 0 || scaledWidth < 0) {

            return;
        }

        final RectF contentRectF = new RectF(scaledOffsetX, scaledOffsetY,
                scaledWidth + scaledOffsetX, scaledHeight + scaledOffsetY);
        final RectF containerRectF = new RectF(0, 0, containerWidth, containerHeight);

        mZoomableController.setMinScaleFactor(1);
        mZoomableController.setMaxScaleFactor(4);
    }

    private float removeDecimal(final float n) {
        return (float) ((int) n);
    }

    private void init() {
        mZoomableController = createZoomableController();
        mZoomableController.setListener(mZoomableListener);
        mTapGestureDetector = new GestureDetector(getContext(), mTapListenerWrapper);
        mScaleTypeTransform = new Matrix();
        mFling = new Fling().setListener(new Fling.Listener() {
            @Override
            public void onTranslated(final float dx, final float dy) {
                mCurrentTransform.postTranslate(dx, dy);
                mZoomableController.limitTranslation(mCurrentTransform, LimitFlag.LIMIT_ALL);
                ZoomableFrameLayout.this.invalidate();
            }
        });
        this.setTapListener(new DoubleTapGestureListener(this));
    }

    public void addHeaderNotification(@NotNull final Function0<Unit> showFloatingHeaderCallBack) {
        showFloatingHeaderCallBackCallBack = showFloatingHeaderCallBack;
    }

    /**
     * listener for when the content is ready to be used
     */
    interface OnLayoutReadyListener {

        void onLayoutReady();

    }

    public static class RegionOfInterest {
        float offsetX;
        float offsetY;
        float width;
        float height;

        public RegionOfInterest(final float offsetX, final float offsetY,
                                final float width, final float height) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.height = height;
            this.width = width;
        }
    }
}
