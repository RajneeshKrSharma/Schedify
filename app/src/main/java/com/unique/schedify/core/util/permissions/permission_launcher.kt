package com.unique.schedify.core.util.permissions

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat

@Composable
fun permissionRequestLauncher(
    permission: String,
    activity: Activity,
    onPermissionStatus: (PermissionStatus) -> Unit,
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionStatus(PermissionStatus.PermissionAction(true))
        } else {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, permission
            )

            onPermissionStatus(
                if (shouldShowRationale) {
                    PermissionStatus.PermissionDenialCause(
                        PermissionDenialCauseEnum.DENIED_TEMPORARY
                    )
                } else {
                    PermissionStatus.PermissionDenialCause(
                        PermissionDenialCauseEnum.DENIED_PERMANENTLY
                    )
                }
            )
        }
    }
}
