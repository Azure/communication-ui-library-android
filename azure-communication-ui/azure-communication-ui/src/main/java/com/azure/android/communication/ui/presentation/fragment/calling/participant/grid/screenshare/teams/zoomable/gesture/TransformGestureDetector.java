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

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.teams.zoomable.gesture;


import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MotionEvent;

/**
 * Component that detects translation, scale and rotation based on touch events.
 * <p>
 * This class notifies its listeners whenever a gesture begins, updates or ends.
 * The instance of this detector is passed to the listeners, so it can be queried
 * for pivot, translation, scale or rotation.
 */
public final class TransformGestureDetector implements MultiPointerGestureDetector.Listener {
    private static final int SCALE_NO_CHANGE = 1;

    private static final int MAX_POINTERS = 2;
    private static final int FIRST_POINTER = 0;
    private static final int SECOND_POINTER = 1;

    private final MultiPointerGestureDetector mDetector;

    @Nullable
    private Listener mListener = null;

    /**
     * Factory method that creates a new instance of TransformGestureDetector
     */
    public static TransformGestureDetector newInstance() {
        return new TransformGestureDetector(MultiPointerGestureDetector.newInstance());
    }

    private TransformGestureDetector(@NonNull MultiPointerGestureDetector multiPointerGestureDetector) {
        mDetector = multiPointerGestureDetector;
        mDetector.setListener(this);
    }

    /**
     * Gets the X coordinate of the pivot point
     */
    @FloatRange(from = 0)
    public float getPivotX() {
        return calcAverage(mDetector.getStartX());
    }

    /**
     * Gets the Y coordinate of the pivot point
     */
    @FloatRange(from = 0)
    public float getPivotY() {
        return calcAverage(mDetector.getStartY());
    }

    /**
     * Gets the X component of the translation
     */
    @FloatRange(from = 0)
    public float getTranslationX() {
        return calcAverage(mDetector.getCurrentX(), mDetector.getPointerCount())
                - calcAverage(mDetector.getStartX(), mDetector.getPointerCount());
    }

    /**
     * Gets the Y component of the translation
     */
    @FloatRange(from = 0)
    public float getTranslationY() {
        return calcAverage(mDetector.getCurrentY(), mDetector.getPointerCount())
                - calcAverage(mDetector.getStartY(), mDetector.getPointerCount());
    }

    /**
     * Gets the scale
     */
    @FloatRange(from = 0)
    public float getScale() {
        if (mDetector.getPointerCount() < MAX_POINTERS) {
            return SCALE_NO_CHANGE;
        } else {
            float startDeltaX = mDetector.getStartX()[SECOND_POINTER] - mDetector.getStartX()[FIRST_POINTER];
            float startDeltaY = mDetector.getStartY()[SECOND_POINTER] - mDetector.getStartY()[FIRST_POINTER];
            float currentDeltaX = mDetector.getCurrentX()[SECOND_POINTER] - mDetector.getCurrentX()[FIRST_POINTER];
            float currentDeltaY = mDetector.getCurrentY()[SECOND_POINTER] - mDetector.getCurrentY()[FIRST_POINTER];
            float startDist = (float) Math.hypot(startDeltaX, startDeltaY);
            float currentDist = (float) Math.hypot(currentDeltaX, currentDeltaY);
            return currentDist / startDist;
        }
    }

    /**
     * Sets the listener.
     *
     * @param listener listener to set
     */
    public void setListener(@NonNull Listener listener) {
        mListener = listener;
    }

    @Override
    public void onGestureBegin(@NonNull MultiPointerGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureBegin(this);
        }
    }

    @Override
    public void onGestureUpdate(@NonNull MultiPointerGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureUpdate(this);
        }
    }

    @Override
    public void onGestureEnd(@NonNull MultiPointerGestureDetector detector) {
        if (mListener != null) {
            mListener.onGestureEnd(this);
        }
    }

    /**
     * Resets the component to the initial state.
     */
    public void reset() {
        mDetector.reset();
    }

    /**
     * Handles the given motion event.
     *
     * @param event event to handle
     * @return whether or not the event was handled
     */
    public boolean onTouchEvent(final MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /**
     * Restarts the current gesture (if any).
     */
    public void restartGesture() {
        mDetector.restartGesture();
    }

    private float calcAverage(float[] arr, int len) {
        float sum = 0;
        for (int i = 0; i < len; i++) {
            sum += arr[i];
        }
        return (len > 0) ? sum / len : 0;
    }

    private float calcAverage(@NonNull float[] values) {
        if (values.length < 1) {
            return 0;
        }

        float sum = 0.0F;
        for (float value : values) {
            sum += value;
        }

        return sum / values.length;
    }

    /**
     * The listener for receiving notifications when gestures occur.
     */
    public interface Listener {
        /**
         * A callback called right before the gesture is about to start.
         */
        void onGestureBegin(TransformGestureDetector detector);

        /**
         * A callback called each time the gesture gets updated.
         */
        void onGestureUpdate(TransformGestureDetector detector);

        /**
         * A callback called right after the gesture has finished.
         */
        void onGestureEnd(TransformGestureDetector detector);
    }
}
