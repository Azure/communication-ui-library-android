package com.azure.android.communication.ui.callingcompositedemoapp


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CallLauncherActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(CallLauncherActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.WAKE_LOCK",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO"
        )

    @Test
    fun callLauncherActivityTest() {
        val materialButton = onView(
            allOf(
                withId(R.id.launchButton), withText("Launch"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    10
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatButton = onView(
            allOf(
                withId(R.id.azure_communication_ui_setup_audio_device_button), withText("Android"),
                childAtPosition(
                    allOf(
                        withId(R.id.azure_communication_ui_setup_buttons),
                        childAtPosition(
                            withId(R.id.azure_communication_ui_setup_video_layout),
                            4
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.bottom_drawer_table),
                childAtPosition(
                    withClassName(`is`("com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListView")),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(1, click()))

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.azure_communication_ui_setup_audio_device_button), withText("Speaker"),
                childAtPosition(
                    allOf(
                        withId(R.id.azure_communication_ui_setup_buttons),
                        childAtPosition(
                            withId(R.id.azure_communication_ui_setup_video_layout),
                            4
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val recyclerView2 = onView(
            allOf(
                withId(R.id.bottom_drawer_table),
                childAtPosition(
                    withClassName(`is`("com.azure.android.communication.ui.presentation.fragment.common.audiodevicelist.AudioDeviceListView")),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
