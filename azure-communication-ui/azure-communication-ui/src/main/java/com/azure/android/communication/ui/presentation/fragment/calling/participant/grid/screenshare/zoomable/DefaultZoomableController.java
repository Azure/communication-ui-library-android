/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.gesture.TransformGestureDetector;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.LimitFlag;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.ZoomableController;


/**
 * Zoomable controller that calculates transformation based on touch events.
 */
public class DefaultZoomableController implements ZoomableController,
        TransformGestureDetector.Listener {
    private static final float DEFAULT_MIN_SCALE = 1.0F;
    private static final float DEFAULT_MAX_SCALE = 4.0F;

    // View bounds, in view-absolute coordinates
    private final RectF mViewBounds = new RectF();

    // Non-transformed image bounds, in view-absolute coordinates
    private final RectF mImageBounds = new RectF();

    // Transformed image bounds, in view-absolute coordinates
    private final RectF mTransformedImageBounds = new RectF();

    private final Matrix mPreviousTransform = new Matrix();
    private final Matrix mActiveTransform = new Matrix();
    private final Matrix mActiveTransformInverse = new Matrix();
    private final RectF mTempRect = new RectF();
    private final float[] mTempValues = new float[9];

    private final TransformGestureDetector mGestureDetector;
    private Listener mListener = null;
    private boolean mIsEnabled = false;
    private boolean mIsScaleEnabled = true;
    private boolean mIsTranslationEnabled = true;
    private float mMinScaleFactor = DEFAULT_MIN_SCALE;
    private float mMaxScaleFactor = DEFAULT_MAX_SCALE;

    DefaultZoomableController(final TransformGestureDetector gestureDetector) {
        mGestureDetector = gestureDetector;
        mGestureDetector.setListener(this);
    }

    /**
     * Checks whether the specified limit flag is present in the limits provided.
     *
     * <p> If the flag contains multiple flags together using a bitwise OR, this only checks that at
     * least one of the flags is included.
     *
     * @param limits the limits to apply
     * @param flag   the limit flag(s) to check for
     * @return true if the flag (or one of the flags) is included in the limits
     */
    private static boolean shouldLimit(@LimitFlag final int limits, @LimitFlag final int flag) {
        return (limits & flag) != LimitFlag.LIMIT_NONE;
    }

    /**
     * Gets the gesture detector.
     */
    TransformGestureDetector getDetector() {
        return mGestureDetector;
    }

    @Override
    public boolean isScaleEnabled() {
        return mIsScaleEnabled;
    }

    @Override
    public void setScaleEnabled(final boolean enabled) {
        mIsScaleEnabled = enabled;
    }

    @Override
    public boolean isTranslationEnabled() {
        return mIsTranslationEnabled;
    }

    @Override
    public void setTranslationEnabled(final boolean enabled) {
        mIsTranslationEnabled = enabled;
    }

    @Override
    public float getMinScaleFactor() {
        return mMinScaleFactor;
    }

    @Override
    public void setMinScaleFactor(final float minScaleFactor) {
        mMinScaleFactor = minScaleFactor;
    }

    @Override
    public float getMaxScaleFactor() {
        return mMaxScaleFactor;
    }

    @Override
    public void setMaxScaleFactor(final float maxScaleFactor) {
        mMaxScaleFactor = maxScaleFactor;
    }

    /**
     * Gets whether the controller is enabled or not.
     */
    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    /**
     * Sets whether the controller is enabled or not.
     */
    @Override
    public void setEnabled(final boolean enabled) {
        mIsEnabled = enabled;
        if (!enabled) {
            reset();
        }
    }

    /**
     * Returns true if the zoomable transform is identity matrix.
     */
    @Override
    public boolean isIdentity() {
        return isMatrixIdentity(mActiveTransform, 1e-3f);
    }

    /**
     * Gets the current scale factor.
     */
    @Override
    public float getScaleFactor() {
        return getMatrixScaleFactor(mActiveTransform);
    }

    /**
     * Gets the matrix that transforms image-absolute coordinates to view-absolute coordinates.
     * The zoomable transformation is taken into account.
     * <p>
     * Internal matrix is exposed for performance reasons and is not to be modified by the callers.
     */
    @Override
    public Matrix getTransform() {
        return mActiveTransform;
    }

    /**
     * Sets a new zoom transformation.
     */
    @Override
    public void setTransform(final Matrix newTransform) {
        mActiveTransform.set(newTransform);
        onTransformChanged();
    }

    /**
     * Sets the zoomable listener.
     */
    @Override
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    /**
     * Sets the image bounds, in view-absolute coordinates.
     */
    @Override
    public void setImageBounds(final RectF imageBounds) {
        if (!imageBounds.equals(mImageBounds)) {
            mImageBounds.set(imageBounds);
            onTransformChanged();
        }
    }

    /**
     * Sets the view bounds.
     */
    @Override
    public void setViewBounds(final RectF viewBounds) {
        mViewBounds.set(viewBounds);
    }

    @Override
    public int computeHorizontalScrollRange() {
        return (int) mTransformedImageBounds.width();
    }

    @Override
    public int computeHorizontalScrollOffset() {
        return (int) (mViewBounds.left - mTransformedImageBounds.left);
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return (int) mViewBounds.width();
    }

    @Override
    public int computeVerticalScrollRange() {
        return (int) mTransformedImageBounds.height();
    }

    @Override
    public int computeVerticalScrollOffset() {
        return (int) (mViewBounds.top - mTransformedImageBounds.top);
    }

    @Override
    public int computeVerticalScrollExtent() {
        return (int) mViewBounds.height();
    }

    /**
     * Notifies controller of the received touch event.
     */
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return mIsEnabled && mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onGestureBegin(final TransformGestureDetector detector) {
        mPreviousTransform.set(mActiveTransform);
    }

    @Override
    public void onGestureUpdate(final TransformGestureDetector detector) {
        final boolean transformCorrected = calculateGestureTransform(mActiveTransform, LimitFlag.LIMIT_ALL);
        onTransformChanged();
        if (transformCorrected) {
            mGestureDetector.restartGesture();
        }
        // A transformation happened, but was it without correction?
    }

    @Override
    public void onGestureEnd(final TransformGestureDetector detector) {
    }

    /**
     * Rests the controller.
     */
    public void reset() {
        mGestureDetector.reset();
        mPreviousTransform.reset();
        mActiveTransform.reset();
        onTransformChanged();
    }

    /**
     * Zooms to the desired scale and positions the image so that the given image point corresponds
     * to the given view point.
     *
     * @param scale      desired scale, will be limited to {min, max} scale factor
     * @param imagePoint 2D point in image's relative coordinate system (i.e. 0 <= x, y <= 1)
     * @param viewPoint  2D point in view's absolute coordinate system
     */
    public void zoomToPoint(final float scale, final PointF imagePoint, final PointF viewPoint) {
        calculateZoomToPointTransform(mActiveTransform, scale, imagePoint, viewPoint, LimitFlag.LIMIT_ALL);
        onTransformChanged();
    }

    /**
     * Maps point from view-absolute to image-relative coordinates.
     * This takes into account the zoomable transformation.
     */
    @Override
    public PointF mapViewToImage(final PointF viewPoint) {
        final float[] points = mTempValues;
        points[0] = viewPoint.x;
        points[1] = viewPoint.y;
        mActiveTransform.invert(mActiveTransformInverse);
        mActiveTransformInverse.mapPoints(points, 0, points, 0, 1);
        mapAbsoluteToRelative(points, points, 1);
        return new PointF(points[0], points[1]);
    }

    /**
     * Calculates the zoom transformation that would zoom to the desired scale and position the image
     * so that the given image point corresponds to the given view point.
     *
     * @param outTransform the matrix to store the result to
     * @param scale        desired scale, will be limited to {min, max} scale factor
     * @param imagePoint   2D point in image's relative coordinate system (i.e. 0 <= x, y <= 1)
     * @param viewPoint    2D point in view's absolute coordinate system
     * @param limitFlags   whether to limit translation and/or scale.
     * @return whether or not the transform has been corrected due to limitation
     */
    boolean calculateZoomToPointTransform(@NonNull final Matrix outTransform,
                                          final float scale,
                                          @NonNull final PointF imagePoint,
                                          @NonNull final PointF viewPoint,
                                          @LimitFlag final int limitFlags) {
        final float[] viewAbsolute = mTempValues;
        viewAbsolute[0] = imagePoint.x;
        viewAbsolute[1] = imagePoint.y;
        mapRelativeToAbsolute(viewAbsolute, viewAbsolute, 1);
        final float distanceX = viewPoint.x - viewAbsolute[0];
        final float distanceY = viewPoint.y - viewAbsolute[1];
        outTransform.setScale(scale, scale, viewAbsolute[0], viewAbsolute[1]);
        boolean transformCorrected = limitScale(outTransform, viewAbsolute[0], viewAbsolute[1], limitFlags);
        outTransform.postTranslate(distanceX, distanceY);
        transformCorrected |= limitTranslation(outTransform, limitFlags);
        return transformCorrected;
    }

    /**
     * Calculates the zoom transformation based on the current gesture.
     *
     * @param outTransform the matrix to store the result to
     * @param limitTypes   whether to limit translation and/or scale.
     * @return whether or not the transform has been corrected due to limitation
     */
    private boolean calculateGestureTransform(@NonNull final Matrix outTransform,
                                              @LimitFlag final int limitTypes) {
        final TransformGestureDetector detector = mGestureDetector;
        outTransform.set(mPreviousTransform);

        if (mIsScaleEnabled) {
            final float scale = detector.getScale();
            outTransform.postScale(scale, scale, detector.getPivotX(), detector.getPivotY());
        }

        boolean transformCorrected = limitScale(outTransform, detector.getPivotX(), detector.getPivotY(), limitTypes);
        if (mIsTranslationEnabled) {
            outTransform.postTranslate(detector.getTranslationX(), detector.getTranslationY());
        }

        transformCorrected |= limitTranslation(outTransform, limitTypes);
        return transformCorrected;
    }

    /**
     * Maps array of 2D points from view-absolute to image-relative coordinates.
     * This does NOT take into account the zoomable transformation.
     * Points are represented by a float array of [x0, y0, x1, y1, ...].
     *
     * @param destPoints destination array (may be the same as source array)
     * @param srcPoints  source array
     * @param numPoints  number of points to map
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    private void mapAbsoluteToRelative(final float[] destPoints,
                                       final float[] srcPoints, final int numPoints) {
        for (int i = 0; i < numPoints; i++) {
            destPoints[i * 2 + 0] = (srcPoints[i * 2 + 0] - mImageBounds.left) / mImageBounds.width();
            destPoints[i * 2 + 1] = (srcPoints[i * 2 + 1] - mImageBounds.top) / mImageBounds.height();
        }
    }

    /**
     * Maps array of 2D points from image-relative to view-absolute coordinates.
     * This does NOT take into account the zoomable transformation.
     * Points are represented by float array of [x0, y0, x1, y1, ...].
     *
     * @param destPoints destination array (may be the same as source array)
     * @param srcPoints  source array
     * @param numPoints  number of points to map
     */
    @SuppressWarnings("PointlessArithmeticExpression")
    private void mapRelativeToAbsolute(final float[] destPoints,
                                       final float[] srcPoints, final int numPoints) {
        for (int i = 0; i < numPoints; i++) {
            destPoints[i * 2 + 0] = srcPoints[i * 2 + 0] * mImageBounds.width() + mImageBounds.left;
            destPoints[i * 2 + 1] = srcPoints[i * 2 + 1] * mImageBounds.height() + mImageBounds.top;
        }
    }

    private void onTransformChanged() {
        mActiveTransform.mapRect(mTransformedImageBounds, mImageBounds);
        if (mListener != null && isEnabled()) {
            mListener.onTransformChanged(mActiveTransform);
        }
    }

    /**
     * Keeps the scaling factor within the specified limits.
     *
     * @param pivotX     x coordinate of the pivot point
     * @param pivotY     y coordinate of the pivot point
     * @param limitTypes whether to limit scale.
     * @return whether limiting has been applied or not
     */
    private boolean limitScale(final Matrix transform,
                               final float pivotX,
                               final float pivotY,
                               @LimitFlag final int limitTypes) {
        if (!shouldLimit(limitTypes, LimitFlag.LIMIT_SCALE)) {
            return false;
        }
        final float currentScale = getMatrixScaleFactor(transform);
        final float targetScale = limit(currentScale, mMinScaleFactor, mMaxScaleFactor);
        if (targetScale != currentScale) {
            final float scale = targetScale / currentScale;
            transform.postScale(scale, scale, pivotX, pivotY);
            return true;
        }
        return false;
    }

    /**
     * Limits the translation so that there are no empty spaces on the sides if possible.
     *
     * <p> The image is attempted to be centered within the view bounds if the transformed image is
     * smaller. There will be no empty spaces within the view bounds if the transformed image is
     * bigger. This applies to each dimension (horizontal and vertical) independently.
     *
     * @param limitTypes whether to limit translation along the specific axis.
     * @return whether limiting has been applied or not
     */
    @Override
    public boolean limitTranslation(@NonNull final Matrix transform,
                                    @LimitFlag final int limitTypes) {
        if (!shouldLimit(limitTypes, LimitFlag.LIMIT_TRANSLATION_X | LimitFlag.LIMIT_TRANSLATION_Y)) {
            return false;
        }
        final RectF b = mTempRect;
        b.set(mImageBounds);
        transform.mapRect(b);
        final float offsetLeft = shouldLimit(limitTypes, LimitFlag.LIMIT_TRANSLATION_X)
                ? getOffset(b.left, b.right, mViewBounds.left, mViewBounds.right, mImageBounds.centerX()) : 0;
        final float offsetTop = shouldLimit(limitTypes, LimitFlag.LIMIT_TRANSLATION_Y)
                ? getOffset(b.top, b.bottom, mViewBounds.top, mViewBounds.bottom, mImageBounds.centerY()) : 0;
        if (offsetLeft != 0 || offsetTop != 0) {
            transform.postTranslate(offsetLeft, offsetTop);
            return true;
        }
        return false;
    }

    /**
     * Returns the offset necessary to make sure that:
     * - the image is centered within the limit if the image is smaller than the limit
     * - there is no empty space on left/right if the image is bigger than the limit
     */
    private float getOffset(final float imageStart,
                            final float imageEnd,
                            final float limitStart,
                            final float limitEnd,
                            final float limitCenter) {
        final float imageWidth = imageEnd - imageStart;
        final float limitWidth = limitEnd - limitStart;
        final float limitInnerWidth = Math.min(limitCenter - limitStart, limitEnd - limitCenter) * 2;
        // center if smaller than limitInnerWidth
        if (imageWidth < limitInnerWidth) {
            return limitCenter - (imageEnd + imageStart) / 2;
        }
        // to the edge if in between and limitCenter is not (limitLeft + limitRight) / 2
        if (imageWidth < limitWidth) {
            if (limitCenter < (limitStart + limitEnd) / 2) {
                return limitStart - imageStart;
            } else {
                return limitEnd - imageEnd;
            }
        }
        // to the edge if larger than limitWidth and empty space visible
        if (imageStart > limitStart) {
            return limitStart - imageStart;
        }
        if (imageEnd < limitEnd) {
            return limitEnd - imageEnd;
        }
        return 0;
    }

    /**
     * Limits the value to the given min and max range.
     */
    private float limit(final float value, final float min, final float max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Gets the scale factor for the given matrix.
     * This method assumes the equal scaling factor for X and Y axis.
     */
    private float getMatrixScaleFactor(@NonNull final Matrix transform) {
        transform.getValues(mTempValues);
        return mTempValues[Matrix.MSCALE_X];
    }

    /**
     * Checks whether the given matrix is close enough to the identity matrix:
     * 1 0 0
     * 0 1 0
     * 0 0 1
     * <p>
     * Or equivalently to the zero matrix, after subtracting 1.0f from the diagonal elements:
     * 0 0 0
     * 0 0 0
     * 0 0 0
     * <p>
     * Same as {@code Matrix.isIdentity()}, but with tolerance {@code eps}.
     */
    private boolean isMatrixIdentity(@NonNull final Matrix transform, final float eps) {
        transform.getValues(mTempValues);
        mTempValues[0] -= 1.0f;
        mTempValues[4] -= 1.0f;
        mTempValues[8] -= 1.0f;
        for (float tempValue : mTempValues) {
            if (Math.abs(tempValue) > eps) {
                return false;
            }
        }
        return true;
    }
}
