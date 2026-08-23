package wifi.fiend

import android.app.ActivityManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log // إضافة للوغات لتتبع الأخطاء

class JobWakeUpService : JobService() {

    // متغير ثابت لتتبع حالة الخدمة (أفضل وأسرع من البحث في النظام)
    companion object {
        @Volatile
        var isMainServiceRunning = false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // التعليق: هنا يشغل MainService
        if (!isMainServiceRunning) {
            try {
                val serviceIntent = Intent(this, MainService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                // قمنا بتحديث المتغير ليعرف أن الخدمة انطلقت
                isMainServiceRunning = true 
            } catch (e: Exception) {
                // معالجة الأخطاء إذا فشل التشغيل (مثلاً بسبب صلاحيات أو Android 12+ restrictions)
                Log.e("JobWakeUpService", "Failed to start MainService", e)
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // عند إيقاف الجوب، نقوم بإعادة تعيين الحالة
        isMainServiceRunning = false
        return false
    }

    // ملاحظة: لم نعد نحتاج دالة isServiceRunning() القديمة التي تعتمد على النظام
    // لكن إذا أردت الاحتفاظ بها للتحقق من شيء آخر، يمكنك استخدام هذه الطريقة الآمنة:
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // هذه الطريقة قد تعمل على الأندرويد القديم فقط، لكننا لا نعتمد عليها الآن
        // الحل الأفضل هو الاعتماد على المتغير isMainServiceRunning
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
