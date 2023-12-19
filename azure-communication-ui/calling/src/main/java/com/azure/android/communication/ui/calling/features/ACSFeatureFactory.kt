// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.features

import com.azure.android.communication.ui.calling.features.interfaces.ISupportFilesFeature

//import com.azure.android.communication.calling.testapp.features.AcceptCallOptionsFeatureImp
//import com.azure.android.communication.calling.testapp.features.CallRecordingFeatureImpl
//import com.azure.android.communication.calling.testapp.features.CaptionsFeatureImpl
//import com.azure.android.communication.calling.testapp.features.DataChannelFeatureImpl
//import com.azure.android.communication.calling.testapp.features.DiagnosticsCallFeatureImpl
//import com.azure.android.communication.calling.testapp.features.IAcceptCallOptionsFeature
//import com.azure.android.communication.calling.testapp.features.ICallRecordingFeature
//import com.azure.android.communication.calling.testapp.features.ICaptionsFeature
//import com.azure.android.communication.calling.testapp.features.IDataChannelFeature
//import com.azure.android.communication.calling.testapp.features.IDiagnosticsCallFeature
//import com.azure.android.communication.calling.testapp.features.IMediaStatsFeature
//import com.azure.android.communication.calling.testapp.features.IMuteOthersFeature
//import com.azure.android.communication.calling.testapp.features.IMuteSpeakerFeature
//import com.azure.android.communication.calling.testapp.features.IRaiseHandFeature
//import com.azure.android.communication.calling.testapp.features.IRecordingV2Feature
//import com.azure.android.communication.calling.testapp.features.IReverseProxyFeature
//import com.azure.android.communication.calling.testapp.features.IShareSupportFilesFeature
//import com.azure.android.communication.calling.testapp.features.ISpotlightFeature
//import com.azure.android.communication.calling.testapp.features.ISurveyFeature
//import com.azure.android.communication.calling.testapp.features.IVideoConstraintsFeature
//import com.azure.android.communication.calling.testapp.features.MediaStatsFeatureImpl
//import com.azure.android.communication.calling.testapp.features.MuteOthersFeatureImpl
//import com.azure.android.communication.calling.testapp.features.MuteSpeakerFeatureImp
//import com.azure.android.communication.calling.testapp.features.RaiseHandFeatureImpl
//import com.azure.android.communication.calling.testapp.features.RecordingV2FeatureImpl
//import com.azure.android.communication.calling.testapp.features.ReverseProxyFeatureImpl
//import com.azure.android.communication.calling.testapp.features.ShareSupportFilesFeatureImp
//import com.azure.android.communication.calling.testapp.features.SpotlightFeatureImpl
//import com.azure.android.communication.calling.testapp.features.SurveyFeatureImpl



/**
 * Factory for getting the different UIFeatureType not available in all release channels.
 */
internal class ACSFeaturesFactory
/**
 * Constructor used by the factory
 */
private constructor() {
    /**
     * Return and instance of the required feature
     *
     * @param feature interface of the required feature
     * @param <F> type of the feature interface
     * @return requested interface feature
    </F> */
    fun <F : ACSFeature?> getACSFeature(feature: Class<F>): F? {
        return if (featureList!!.containsKey(feature)) {
            featureList!![feature] as F?
        } else null
    }

    /**
     * Add all available feature within the ACS SDK
     */
    private fun RegisterACSFeatures() {
        featureList = HashMap()
        RegisterACSFeature<ISupportFilesFeature>(SupportFilesFeature())
//        RegisterACSFeature(IRaiseHandFeature::class.java, RaiseHandFeatureImpl())
//        RegisterACSFeature(ICallRecordingFeature::class.java, CallRecordingFeatureImpl())
//        RegisterACSFeature(IAcceptCallOptionsFeature::class.java, AcceptCallOptionsFeatureImp())
//        RegisterACSFeature(IMuteSpeakerFeature::class.java, MuteSpeakerFeatureImp())
//        RegisterACSFeature(IRecordingV2Feature::class.java, RecordingV2FeatureImpl())
//        RegisterACSFeature(IDiagnosticsCallFeature::class.java, DiagnosticsCallFeatureImpl())
//        RegisterACSFeature(IDataChannelFeature::class.java, DataChannelFeatureImpl())
//        RegisterACSFeature(IMediaStatsFeature::class.java, MediaStatsFeatureImpl())
//        RegisterACSFeature(IVideoConstraintsFeature::class.java, VideoConstraintsFeatureImpl())
//        RegisterACSFeature(ISpotlightFeature::class.java, SpotlightFeatureImpl())
//        RegisterACSFeature(IShareSupportFilesFeature::class.java, ShareSupportFilesFeatureImp())
//        RegisterACSFeature(IReverseProxyFeature::class.java, ReverseProxyFeatureImpl())
//        RegisterACSFeature(ISurveyFeature::class.java, SurveyFeatureImpl())
//        RegisterACSFeature(IMuteOthersFeature::class.java, MuteOthersFeatureImpl())
    }

    /**
     * Add a feature to the factory map
     *
     * @param _interface key of the feature instance
     * @param _instance feature instance
     * @param <I> type of the feature interface
     * @param <O> type of the feature instance
    </O></I> */
    private inline fun <reified I : ACSFeature> RegisterACSFeature(_instance: I) {
        featureList!![I::class.java] = _instance
    }
    companion object {
        private var featureList: MutableMap<Class<*>, ACSFeature>? = null
        val instance: ACSFeaturesFactory by lazy {
                val factory = ACSFeaturesFactory()
                factory.RegisterACSFeatures()
                factory
            }

    }
}