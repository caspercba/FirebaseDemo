package ar.com.demo.firebase.firebasedemo

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsEngine(val context: Context) {

    private var analytics: FirebaseAnalytics? = null

    init {
        analytics = FirebaseAnalytics.getInstance(context)
    }

    fun signUp() {
        AnalyticsEvent(FirebaseAnalytics.Event.SIGN_UP, "UserRegistered").send(analytics)
    }

    fun login() {
        AnalyticsEvent(FirebaseAnalytics.Event.LOGIN, "UserLoggedin").send(analytics)
    }
}

class AnalyticsEvent(val eventType: String, val name: String) {
    fun getBundle() : Bundle {
        var result = Bundle()
        result.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        return result
    }

    fun send(analytics: FirebaseAnalytics?) {
        analytics?.logEvent(eventType, getBundle())
    }
}