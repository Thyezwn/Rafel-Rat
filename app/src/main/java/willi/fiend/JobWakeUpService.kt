package willi.fiend

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
        if (!isMainServiceRunning) {
            try {
                val serviceIntent = Intent(this, MainService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                isMainServiceRunning = true
            } catch (e: Exception) {
                Log.e("JobWakeUpService", "Failed to start MainService", e)
            }
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        isMainServiceRunning = false
        return false
    }
}
