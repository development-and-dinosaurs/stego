package uk.co.developmentanddinosaurs.stego.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import uk.co.developmentanddinosaurs.stego.app.di.appModule

class StegoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@StegoApplication)
            modules(appModule)
        }
    }
}
