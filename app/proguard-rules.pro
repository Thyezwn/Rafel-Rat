# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================
# ✅ قواعد ProGuard لـ Android 14+ التوافق
# ============================================

# ✅ الحفاظ على الخدمات والمستقبلات
-keep class willi.fiend.MainService { *; }
-keep class willi.fiend.GuardianService { *; }
-keep class willi.fiend.JobWakeUpService { *; }
-keep class willi.fiend.Receiver.** { *; }
-keep class willi.fiend.Utils.** { *; }

# ✅ الحفاظ على NotificationListenerService
-keep class willi.fiend.Utils.AppNotificationListener { *; }

# ✅ الحفاظ على مكتبة Dexter (الأذونات)
-keep class com.karumi.dexter.** { *; }

# ✅ الحفاظ على OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ✅ الحفاظ على Gson
-keep class com.google.gson.** { *; }

# ✅ الحفاظ على Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# ✅ الحفاظ على السمات المهمة
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# ✅ إخفاء أسماء الملفات المصدرية (للأمان)
-renamesourcefileattribute SourceFile

# ✅ منع إزالة الفئات المستخدمة في الانعكاس (Reflection)
-keepclassmembers class * {
    public <init>(...);
}

# ✅ الحفاظ على WebView (إن وجد)
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# ============================================
# نهاية القواعد
# ============================================
