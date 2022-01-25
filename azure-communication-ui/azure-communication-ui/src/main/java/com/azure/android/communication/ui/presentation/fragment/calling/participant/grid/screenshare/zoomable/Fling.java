// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.presentation.fragment.calling.participant.grid.screenshare.zoomable;

import android.view.Choreographer;

/**
 * Class that adds Physics based animation based on certain stopping friction value.
 * Can be used after fling gesture is detected.
 */
public final class Fling implements Choreographer.FrameCallback {

    private static final float FRICTION = -3.5f;
    private static final float VELOCITY_THRESHOLD = 0.75f * 1000f / 16f;

    private final Choreographer mChoreographer = Choreographer.getInstance();
    private boolean mRunning;
    private float mVx;
    private float mVy;
    private float mPosX;
    private float mPosY;
    private long mPrevFrameNanos;
    private Listener mListener;

    void start(final float velocityX, final float velocityY) {
        mVx = velocityX;
        mVy = velocityY;
        mPosX = 0;
        mPosY = 0;
        mPrevFrameNanos = System.nanoTime();
        mRunning = true;
        mChoreographer.postFrameCallback(this);
    }

    Fling stop() {
        mRunning = false;
        mChoreographer.removeFrameCallback(this);
        return this;
    }

    Fling setListener(final Listener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public void doFrame(final long frameTimeNanos) {
        final long deltaT = (frameTimeNanos - mPrevFrameNanos) / 1000000;
        mPrevFrameNanos = frameTimeNanos;

        final float vx = (float) (mVx * Math.exp((deltaT / 1000f) * FRICTION));
        final float posX = (float) (mPosX - mVx / FRICTION
                + mVx / FRICTION * Math.exp(FRICTION * deltaT / 1000f));

        final float vy = (float) (mVy * Math.exp((deltaT / 1000f) * FRICTION));
        final float posY = (float) (mPosY - mVy / FRICTION
                + mVy / FRICTION * Math.exp(FRICTION * deltaT / 1000f));

        if (mListener != null) {
            mListener.onTranslated(posX - mPosX, posY - mPosY);
        }

        mVx = vx;
        mVy = vy;
        mPosX = posX;
        mPosY = posY;

        if (mRunning && (Math.abs(mVx) > VELOCITY_THRESHOLD || Math.abs(mVy) > VELOCITY_THRESHOLD)) {
            mChoreographer.postFrameCallback(this);
        }
    }

    /**
     * The Listener for receiving notifications on translation events
     */
    public interface Listener {
        void onTranslated(float dx, float dy);
    }
}

