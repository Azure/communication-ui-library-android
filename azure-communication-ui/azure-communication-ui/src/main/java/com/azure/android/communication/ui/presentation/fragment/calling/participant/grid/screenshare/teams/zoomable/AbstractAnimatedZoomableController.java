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
package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable;

import android.graphics.Matrix;
import android.graphics.PointF;
import androidx.annotation.Nullable;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.gesture.TransformGestureDetector;
import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.interfaces.LimitFlag;


/**
 * Abstract class for ZoomableController that adds animation capabilities to
 * DefaultZoomableController.
 */
abstract class AbstractAnimatedZoomableController extends DefaultZoomableController {
    private final float[] mStartValues = new float[9];
    private final float[] mStopValues = new float[9];
    private final float[] mCurrentValues = new float[9];
    private final Matrix mNewTransform = new Matrix();
    private final Matrix mWorkingTransform = new Matrix();
    private boolean mIsAnimating;


    AbstractAnimatedZoomableController(TransformGestureDetector transformGestureDetector) {
        super(transformGestureDetector);
    }

    boolean isAnimating() {
        return mIsAnimating;
    }

    void setAnimating(boolean isAnimating) {
        mIsAnimating = isAnimating;
    }

    float[] getStartValues() {
        return mStartValues;
    }

    float[] getStopValues() {
        return mStopValues;
    }

    Matrix getWorkingTransform() {
        return mWorkingTransform;
    }

    private void setTransformImmediate(final Matrix newTransform) {
        stopAnimation();
        mWorkingTransform.set(newTransform);
        super.setTransform(newTransform);
        getDetector().restartGesture();
    }

    /**
     * Returns true if the zoomable transform is identity matrix, and the controller is idle.
     */
    @Override
    public boolean isIdentity() {
        return !isAnimating() && super.isIdentity();
    }

    @Override
    public void onGestureBegin(TransformGestureDetector detector) {
        stopAnimation();
        super.onGestureBegin(detector);
    }

    @Override
    public void onGestureUpdate(TransformGestureDetector detector) {
        if (isAnimating()) {
            return;
        }
        super.onGestureUpdate(detector);
    }

    @Override
    public void reset() {
        stopAnimation();
        mWorkingTransform.reset();
        mNewTransform.reset();
        super.reset();
    }

    /**
     * Zooms to the desired scale and positions the image so that the given image point corresponds
     * to the given view point.
     *
     * <p>If this method is called while an animation or gesture is already in progress,
     * the current animation or gesture will be stopped first.
     *
     * @param scale desired scale, will be limited to {min, max} scale factor
     * @param imagePoint 2D point in image's relative coordinate system (i.e. 0 <= x, y <= 1)
     * @param viewPoint 2D point in view's absolute coordinate system
     */
    @Override
    public void zoomToPoint(float scale,
                            PointF imagePoint,
                            PointF viewPoint) {
        zoomToPoint(scale, imagePoint, viewPoint, LimitFlag.LIMIT_ALL, 0, null);
    }

    public abstract void setTransformAnimated(final Matrix newTransform,
                                              long durationMs,
                                              @Nullable final Runnable onAnimationComplete);

    /**
     * Zooms to the desired scale and positions the image so that the given image point corresponds
     * to the given view point.
     *
     * <p>If this method is called while an animation or gesture is already in progress,
     * the current animation or gesture will be stopped first.
     *
     * @param scale desired scale, will be limited to {min, max} scale factor
     * @param imagePoint 2D point in image's relative coordinate system (i.e. 0 <= x, y <= 1)
     * @param viewPoint 2D point in view's absolute coordinate system
     * @param limitFlags whether to limit translation and/or scale.
     * @param durationMs length of animation of the zoom, or 0 if no animation desired
     * @param onAnimationComplete code to run when the animation completes. Ignored if durationMs=0
     */
    public void zoomToPoint(float scale,
                            PointF imagePoint,
                            PointF viewPoint,
                            @LimitFlag int limitFlags,
                            long durationMs,
                            @Nullable Runnable onAnimationComplete) {
        calculateZoomToPointTransform(
                mNewTransform,
                scale,
                imagePoint,
                viewPoint,
                limitFlags);
        setTransform(mNewTransform, durationMs, onAnimationComplete);
    }

    protected abstract void stopAnimation();

    void calculateInterpolation(Matrix outMatrix, float fraction) {
        for (int i = 0; i < 9; i++) {
            mCurrentValues[i] = (1f - fraction) * mStartValues[i] + fraction * mStopValues[i];
        }
        outMatrix.setValues(mCurrentValues);
    }

    /**
     * Sets a new zoomable transformation and animates to it if desired.
     *
     * <p>If this method is called while an animation or gesture is already in progress,
     * the current animation or gesture will be stopped first.
     *
     * @param newTransform new transform to make active
     * @param durationMs duration of the animation, or 0 to not animate
     * @param onAnimationComplete code to run when the animation completes. Ignored if durationMs=0
     */
    private void setTransform(Matrix newTransform,
                              long durationMs,
                              @Nullable Runnable onAnimationComplete) {
        if (durationMs <= 0) {
            setTransformImmediate(newTransform);
        } else {
            setTransformAnimated(newTransform, durationMs, onAnimationComplete);
        }
    }
}
