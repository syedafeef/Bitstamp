package com.afeef.cryptoapi

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.afeef.cryptoapi.model.Ask
import com.afeef.cryptoapi.model.Bid
import com.afeef.cryptoapi.model.OrderBookItem
import com.afeef.cryptoapi.model.OrderBookResponse
import com.afeef.cryptoapi.ui.MainActivity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.afeef.cryptoapi", appContext.packageName)
    }

    @Test
    fun getKodein() {

    }

    @Test
    fun onCreate() {

    }

    @Test
    fun test_ActivityInView() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.searchInputlayout)).perform(click())
    }

    @Test
    fun test_isOrderBookDataVisible() {

        val orderBook = OrderBookItem(
            "abcd",
            Bid("xyz", "xyz"),
            Ask("xyz", "xyz")
        )

        //weekend call
    }


}