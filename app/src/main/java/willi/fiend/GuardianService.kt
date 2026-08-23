package willi.fiend

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Timer
import java.util.TimerTask

class GuardianService : Service() {
    private var timer: Timer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // مهمة دورية كل 5 ثواني للتحقق من الخدمة الرئيسية
        timer = Timer().also {
            it.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (!JobWakeUpService.isMainServiceRunning) {
                        // إعادة تشغيل الخدمة الرئيسية إذا توقفت
                        startService(Intent(this@GuardianService, MainService::class.java))
                    }
                }
            }, 0, 5000)
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        // إذا تم إيقاف الحارس، أعد تشغيله فوراً (حلقة لا نهائية)
        startService(Intent(this, GuardianService::class.java))
        super.onDestroy()
    }
}
