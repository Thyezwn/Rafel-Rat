# ✅ الحفاظ على الخدمات والمستقبلات (لـ Android 14+)
-keep class willi.fiend.MainService { *; }
-keep class willi.fiend.GuardianService { *; }
-keep class willi.fiend.JobWakeUpService { *; }
-keep class willi.fiend.Receiver.** { *; }
-keep class willi.fiend.Utils.** { *; }

# ✅ الحفاظ على فئات NotificationListenerService
-keep class willi.fiend.Utils.AppNotificationListener { *; }

# ✅ الحفاظ على فئات Dexter (المكتبة المستخدمة للأذونات)
-keep class com.karumi.dexter.** { *; }

# ✅ الحفاظ على فئات OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ✅ الحفاظ على فئات Gson
-keep class com.google.gson.** { *; }

# ✅ الحفاظ على فئات Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# ✅ الحفاظ على أسماء الفئات المستخدمة في الانعكاس
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
