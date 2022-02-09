// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp.diagnostics

import com.microsoft.office.outlook.magnifierlib.memory.FileDescriptorInfo
import com.microsoft.office.outlook.magnifierlib.memory.HeapMemoryInfo
import com.microsoft.office.outlook.magnifierlib.memory.MemoryMonitor
import com.microsoft.office.outlook.magnifierlib.memory.ThreadInfo

class MemoryMonitorListener(private val memoryViewer: MemoryViewer) :
    MemoryMonitor.OnSampleListener {
    override fun onSampleHeap(
        heapMemoryInfo: HeapMemoryInfo,
        sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo,
    ) {
        memoryViewer.display(heapMemoryInfo.pssMemoryMB.toInt())
    }

    override fun onSampleFile(
        fileDescriptorInfo: FileDescriptorInfo,
        sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo,
    ) {
    }

    override fun onSampleThread(
        threadInfo: ThreadInfo,
        sampleInfo: MemoryMonitor.OnSampleListener.SampleInfo,
    ) {
    }
}
