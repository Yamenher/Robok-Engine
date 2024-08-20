package org.gampiot.robok.feature.util.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.content.res.Configuration
import android.view.View
import android.view.WindowInsets

import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import dev.trindadedev.lib.ui.components.dialog.PermissionDialog

import org.gampiot.robok.feature.util.R
import org.gampiot.robok.feature.util.requestStoragePerm
import org.gampiot.robok.feature.util.getStoragePermStatus
import org.gampiot.robok.feature.util.getBackPressedClickListener
import org.gampiot.robok.feature.util.PermissionListener
import org.gampiot.robok.feature.res.Strings

open class RobokActivity(
    @IdRes val fragmentLayoutResId: Int
) : AppCompatActivity(), PermissionListener {
    
    private var permissionDialog: PermissionDialog? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getStoragePermStatus(this)) {
            requestStoragePermDialog()
        }
    }
    
    open fun configureEdgeToEdge () {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        rootView.setOnApplyWindowInsetsListener { view, insets ->
            view.setPadding(
                insets.systemGestureInsets.left,
                insets.systemGestureInsets.top,
                insets.systemGestureInsets.right,
                insets.systemGestureInsets.bottom
            )
            insets.consumeSystemWindowInsets()
        }
        
        val scrimColor = Color.TRANSPARENT
        val style = SystemBarStyle.auto(scrimColor, scrimColor) 
        enableEdgeToEdge(
            statusBarStyle = style,
            navigationBarStyle = style
        )
    }

    open fun openFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(fragmentLayoutResId, fragment)
        }
    }
    
    open fun openFragment(@IdRes fragmentLayoutResId: Int, fragment: Fragment) {
        supportFragmentManager.commit {
            replace(fragmentLayoutResId, fragment)
        }
    }
    
    open fun requestStoragePermDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        permissionDialog = PermissionDialog.Builder(this)
            .setIconResId(R.drawable.ic_folder_24)
            .setText(getString(Strings.warning_storage_perm_message))
            .setAllowClickListener {
                requestStoragePerm(this@RobokActivity, this@RobokActivity)
            }
            .setDenyClickListener {
                finish()
            }
            .build()
        
        Handler(Looper.getMainLooper()).post {
            permissionDialog?.show()
        }
    }
    
    open fun configureToolbarNavigationBack(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener(getBackPressedClickListener(onBackPressedDispatcher))
    }
   
    open fun isDarkMode(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onReceive(status: Boolean) {
        if (status) {
            permissionDialog?.dismiss()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(Strings.error_storage_perm_title))
                .setMessage(getString(Strings.error_storage_perm_message))
                .setCancelable(false)
                .setPositiveButton(Strings.common_word_allow) { _, _ ->
                    requestStoragePermDialog()
                }
                .show()
        }
    }
}
