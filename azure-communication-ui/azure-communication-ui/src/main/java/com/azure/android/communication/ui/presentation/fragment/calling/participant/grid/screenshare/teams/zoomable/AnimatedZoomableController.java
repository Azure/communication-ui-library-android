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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import android.view.animation.DecelerateInterpolator;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.gesture.TransformGestureDetector;


/**
 * ZoomableController that adds animation capabilities to DefaultZoomableController using standard
 * Android animation classes
 *
 * ***** THIS IS CURRENTLY NOT IN USED, THIS WILL BE USEFUL FOR DOUBLE TAP AND FOR BOUNCING BEHAVIORS AND WHAT NOT ******
 */
final class AnimatedZoomableController extends AbstractAnimatedZoomableController {
    private final ValueAnimator mValueAnimator;

    public static AnimatedZoomableController newInstance() {
        return new AnimatedZoomableController(TransformGestureDetector.newInstance());
    }

    private AnimatedZoomableController(TransformGestureDetector transformGestureDetector) {
        super(transformGestureDetector);
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    public void setTransformAnimated(final Matrix newTransform,
                                     @IntRange(from = 1) long durationMs,
                                     @Nullable final Runnable onAnimationComplete) {
        stopAnimation();
        setAnimating(true);
        mValueAnimator.setDuration(durationMs);
        getTransform().getValues(getStartValues());
        newTransform.getValues(getStopValues());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                calculateInterpolation(getWorkingTransform(), (float) valueAnimator.getAnimatedValue());
                AnimatedZoomableController.super.setTransform(getWorkingTransform());
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationStopped();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationStopped();
            }

            private void onAnimationStopped() {
                if (onAnimationComplete != null) {
                    onAnimationComplete.run();
                }
                setAnimating(false);
                getDetector().restartGesture();
            }
        });
        mValueAnimator.start();
    }

    @Override
    public void stopAnimation() {
        if (!isAnimating()) {
            return;
        }

        mValueAnimator.cancel();
        mValueAnimator.removeAllUpdateListeners();
        mValueAnimator.removeAllListeners();
    }

}
