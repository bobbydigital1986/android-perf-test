package com.example.myapplication

import android.app.Application
import android.util.Log
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import com.bugsnag.android.okhttp.BugsnagOkHttpPlugin
import com.bugsnag.android.performance.BugsnagPerformance
import com.bugsnag.android.performance.PerformanceConfiguration
import com.bugsnag.android.performance.okhttp.BugsnagPerformanceOkhttp
import com.example.myapplication.BugsnagVars.interactive_start_span
import okhttp3.OkHttpClient

class AFMobileApplication : Application() {

    val httpClient = OkHttpClient.Builder()
        .eventListener(BugsnagPerformanceOkhttp())
        .build()

    init {
        Log.i("BugsnagPerf","Coldstart1")
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("BugsnagPerf","Coldstart2")
        Bugsnag.start(this)
        BugsnagPerformance.start(this)
//        interactive_start_span = BugsnagPerformance.startSpan("interactive app start")
        Log.i("BugsnagPerf","Coldstart3")
    }

    companion object {
        init {
            BugsnagPerformance.reportApplicationClassLoaded()
        }
    }
}