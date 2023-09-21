package com.yantranet.smartagent.views

import android.app.Activity

class MainActivity : Activity() {
    override fun onResume() {
        super.onResume()
        finishAndRemoveTask()
    }
}
