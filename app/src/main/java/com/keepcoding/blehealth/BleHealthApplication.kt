package com.keepcoding.blehealth

import android.app.Application

class BleHealthApplication : Application() {
    val container: AppContainer by lazy { AppContainer(applicationContext) }
}
