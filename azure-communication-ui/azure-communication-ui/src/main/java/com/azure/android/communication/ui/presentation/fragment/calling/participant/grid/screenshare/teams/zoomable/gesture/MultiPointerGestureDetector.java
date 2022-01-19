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

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MotionEvent;

/**
 * Component that detects and tracks multiple pointers based on touch events.
 * <p>
 * Each time a pointer gets pressed or released, the current gesture (if any) will end, and a new
 * one will be started (if there are still pressed pointers left). It is guaranteed that the number
 * of pointers within the single gesture will remain the same during the whole gesture.
 */
final class MultiPointerGestureDetector {
    private static final int MAX_POINTERS = 2;

    private final int mId[] = new int[MAX_POINTERS];
    private final float mStartX[] = new float[MAX_POINTERS];
    private final float mStartY[] = new float[MAX_POINTERS];
    private final float mCurrentX[] = new float[MAX_POINTERS];
    private final float mCurrentY[] = new float[MAX_POINTERS];

    private boolean mGestureInProgress;

    @IntRange(from = 0,
              to = MAX_POINTERS)
    private int mPointerCount;

    @IntRange(from = 0,
              to = MAX_POINTERS)
    private int mNewPointerCount;

    @Nullable
    private Listener mListener;

    private MultiPointerGestureDetector() {
        reset();
    }

    /**
     * Factory method that creates a new instance of MultiPointerGestureDetector
     */
    static MultiPointerGestureDetector newInstance() {
        return new MultiPointerGestureDetector();
    }

    /**
     * Gets the number of pressed pointers (fingers down).
     */
    private static int getPressedPointerCount(@NonNull MotionEvent event) {
        int count = event.getPointerCount();
        int action = event.getActionMasked();
        return (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) ? count - 1
                                                                                            : count;
    }

    /**
     * Gets whether there is a gesture in progress
     */
    boolean isGestureInProgress() {
        return mGestureInProgress;
    }

    /**
     * Gets the number of pointers after the current gesture
     */
    int getNewPointerCount() {
        return mNewPointerCount;
    }

    /**
     * Gets the number of pointers in the current gesture
     */
    int getPointerCount() {
        return mPointerCount;
    }

    /**
     * Gets the start X coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    float[] getStartX() {
        return mStartX;
    }

    /**
     * Gets the start Y coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    float[] getStartY() {
        return mStartY;
    }

    /**
     * Gets the current X coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    float[] getCurrentX() {
        return mCurrentX;
    }

    /**
     * Gets the current Y coordinates for the all pointers
     * Mutable array is exposed for performance reasons and is not to be modified by the callers.
     */
    float[] getCurrentY() {
        return mCurrentY;
    }

    /**
     * Sets the listener.
     *
     * @param listener listener to set
     */
    void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * Resets the component to the initial state.
     */
    void reset() {
        mGestureInProgress = false;
        mPointerCount = 0;
        for (int i = 0; i < MAX_POINTERS; i++) {
            mId[i] = MotionEvent.INVALID_POINTER_ID;
        }
    }

    /**
     * Handles the given motion event.
     *
     * @param event event to handle
     * @return whether or not the event was handled
     */
    boolean onTouchEvent(final @NonNull MotionEvent event) {
        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_MOVE:
                updateGesture(event);
                break;

            // restart gesture whenever the number of pointers changes
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                numberOfPointersChanged(event);
                break;

            case MotionEvent.ACTION_CANCEL:
                gestureWasCancelled();
                break;
            default:
                // Nothing
        }

        return true;
    }

    /**
     * Restarts the current gesture (if any).
     */
    void restartGesture() {
        if (!mGestureInProgress) {
            return;
        }
        stopGesture();
        for (int i = 0; i < MAX_POINTERS; i++) {
            mStartX[i] = mCurrentX[i];
            mStartY[i] = mCurrentY[i];
        }
        startGesture();
    }

    private void updateGesture(@NonNull MotionEvent event) {
        // update pointers
        updatePointersOnMove(event);

        // start a new gesture if not already started
        if (!mGestureInProgress && mPointerCount > 0) {
            startGesture();
        }

        // notify listener
        if (mGestureInProgress && mListener != null) {
            mListener.onGestureUpdate(this);
        }
    }

    private void gestureWasCancelled() {
        mNewPointerCount = 0;
        stopGesture();
        reset();
    }

    private void numberOfPointersChanged(@NonNull MotionEvent event) {
        mNewPointerCount = getPressedPointerCount(event);
        stopGesture();
        updatePointersOnTap(event);
        if (mPointerCount > 0) {
            startGesture();
        }
    }

    /**
     * Starts a new gesture and calls the listener just before starting it.
     */
    private void startGesture() {
        if (!mGestureInProgress) {
            if (mListener != null) {
                mListener.onGestureBegin(this);
            }
            mGestureInProgress = true;
        }
    }

    /**
     * Stops the current gesture and calls the listener right after stopping it.
     */
    private void stopGesture() {
        if (mGestureInProgress) {
            mGestureInProgress = false;
            if (mListener != null) {
                mListener.onGestureEnd(this);
            }
        }
    }

    /**
     * Gets the index of the i-th pressed pointer.
     * Normally, the index will be equal to i, except in the case when the pointer is released.
     *
     * @return index of the specified pointer or {@code MotionEvent.INVALID_POINTER_ID} if not found (i.e. not enough pointers are down)
     */
    private int getPressedPointerIndex(@NonNull MotionEvent event, int i) {
        final int count = event.getPointerCount();
        final int action = event.getActionMasked();
        final int index = event.getActionIndex();
        int tmp = i;
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) && tmp >= index) {
            tmp++;
        }
        return (tmp < count) ? tmp
                             : MotionEvent.INVALID_POINTER_ID;
    }

    private void updatePointersOnTap(@NonNull MotionEvent event) {
        mPointerCount = 0;
        for (int i = 0; i < MAX_POINTERS; i++) {
            int index = getPressedPointerIndex(event, i);
            if (index != MotionEvent.INVALID_POINTER_ID) {
                mId[i] = event.getPointerId(index);
                mStartX[i] = event.getX(index);
                mStartY[i] = event.getY(index);
                mPointerCount++;
            } else {
                mId[i] = MotionEvent.INVALID_POINTER_ID;
            }
        }

        System.arraycopy(mStartX, 0, mCurrentX, 0, MAX_POINTERS);
        System.arraycopy(mStartY, 0, mCurrentY, 0, MAX_POINTERS);
    }

    private void updatePointersOnMove(@NonNull MotionEvent event) {
        for (int i = 0; i < MAX_POINTERS; i++) {
            int index = event.findPointerIndex(mId[i]);
            if (index != MotionEvent.INVALID_POINTER_ID) {
                mCurrentX[i] = event.getX(index);
                mCurrentY[i] = event.getY(index);
            }
        }
    }

    /**
     * The listener for receiving notifications when gestures occur.
     */
    interface Listener {
        /**
         * A callback called right before the gesture is about to start.
         */
        void onGestureBegin(MultiPointerGestureDetector detector);

        /**
         * A callback called each time the gesture gets updated.
         */
        void onGestureUpdate(MultiPointerGestureDetector detector);

        /**
         * A callback called right after the gesture has finished.
         */
        void onGestureEnd(MultiPointerGestureDetector detector);
    }
}
