package willi.fiend

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import java.util.Timer
import java.util.TimerTask

class GuardianService : Service() {
    private var timer: Timer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        timer = Timer().also {
            it.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    try {
                        if (!JobWakeUpService.isMainServiceRunning) {
                            val intent = Intent(this@GuardianService, MainService::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(intent)
                            } else {
                                startService(intent)
                            }
                        }
                    } catch (e: Exception) {
                        // ✅ تجاهل الأخطاء بهدوء لمنع الانهيار
                    }
                }
            }, 0, 5000)
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}
