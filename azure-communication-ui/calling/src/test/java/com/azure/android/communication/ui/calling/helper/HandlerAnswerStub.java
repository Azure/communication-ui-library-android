// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.helper;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HandlerAnswerStub implements Answer {
    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
        invocation.getArgument(0, Runnable.class).run();
        return null;
    }
}
