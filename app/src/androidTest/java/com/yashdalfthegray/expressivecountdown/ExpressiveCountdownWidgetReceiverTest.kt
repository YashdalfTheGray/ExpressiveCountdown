package com.yashdalfthegray.expressivecountdown

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test


class ExpressiveCountdownWidgetReceiverTest {
    @Test
    fun onReceive_recognizesMidnightUpdateAction() {
        val receiver = ExpressiveCountdownWidgetReceiver()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent().apply {
            action = MidnightUpdateScheduler.ACTION_MIDNIGHT_UPDATE
        }

        // NOTE - implicit assertion, if this line throws, the test fails
        receiver.onReceive(context, intent)
    }
}