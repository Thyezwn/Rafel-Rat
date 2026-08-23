package wifi.fiend

import android.app.ActivityManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class JobWakeUpService : JobService() {

    companion object {
        @Volatile
        var isMainServiceRunning = false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // التحقق من حالة الخدمة
        if (!isMainServiceRunning) {
            try {
                val serviceIntent = Intent(this, MainService::class.java)
                // تشغيل الخدمة حسب إصدار أندرويد
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                // تحديث الحالة
                isMainServiceRunning = true
            } catch (e: Exception) {
                Log.e("JobWakeUpService", "Failed to start MainService", e)
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // إعادة تعيين الحالة عند إيقاف الجوب
        isMainServiceRunning = false
        return false
    }

    // لم نعد نحتاج هذه الدالة لأننا نستخدم المتغير، لكن أبقيتها للتوافق
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return try {
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }
}
