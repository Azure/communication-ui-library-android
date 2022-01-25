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

import androidx.annotation.NonNull;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable.interfaces.OnTouchEventListener;


/**
 * Wrapper for SimpleOnGestureListener as GestureDetector does not allow changing its listener.
 */
public class GestureListenerWrapper extends GestureDetector.SimpleOnGestureListener {
    private GestureDetector.SimpleOnGestureListener mDelegate;
    private final OnTouchEventListener mOnTouchEventListener;
    private boolean mDisableDoubleTap;

    public GestureListenerWrapper(@NonNull final OnTouchEventListener onTouchEventListener) {
        mDelegate = new GestureDetector.SimpleOnGestureListener();
        this.mOnTouchEventListener = onTouchEventListener;
    }

    public void setListener(final GestureDetector.SimpleOnGestureListener listener) {
        mDelegate = listener;
    }

    @Override
    public boolean onSingleTapUp(final MotionEvent e) {
        return mDelegate.onSingleTapUp(e);
    }

    @Override
    public void onLongPress(final MotionEvent e) {
        mDelegate.onLongPress(e);
        mOnTouchEventListener.onLongPress();
    }

    @Override
    public boolean onScroll(final MotionEvent e1, final MotionEvent e2,
                            final float distanceX, final float distanceY) {
        return mDelegate.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                           final float velocityX, final float velocityY) {
        return mDelegate.onFling(e1, e2, velocityX, velocityY);
    }

    @Override
    public void onShowPress(final MotionEvent e) {
        mDelegate.onShowPress(e);
    }

    @Override
    public boolean onDown(final MotionEvent e) {
        return mDelegate.onDown(e);
    }

    @Override
    public boolean onDoubleTap(final MotionEvent e) {
        return mDelegate.onDoubleTap(e);
    }

    public void disableDoubleTap() {
        mDisableDoubleTap = true;
    }

    @Override
    public boolean onDoubleTapEvent(final MotionEvent e) {
        //return false if double tap disabled, else call onDoubleTapEvent()
        return !mDisableDoubleTap && mDelegate.onDoubleTapEvent(e);
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent e) {
        return mDelegate.onSingleTapConfirmed(e) || mOnTouchEventListener.onTap();
    }
}
