package com.unique.schedify.core.util.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun requestPermission(
        context: Context,
        permissionName: String,
        permissionRequestLauncher: ManagedActivityResultLauncher<String, Boolean>,
    ) {
        if (isPermissionPending(context = context, permissionName = permissionName)) {
            permissionRequestLauncher.launch(permissionName)
        }
    }

    fun isPermissionPending(
        context: Context,
        permissionName: String
    ): Boolean = ContextCompat.checkSelfPermission(context, permissionName) != PackageManager.PERMISSION_GRANTED
}