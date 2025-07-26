package com.unique.schedify.core.util.permissions

sealed class PermissionStatus {
    data class PermissionAction(val isPermissionAllowed: Boolean): PermissionStatus()
    data class PermissionDenialCause(val permissionDenialCauseEnum: PermissionDenialCauseEnum): PermissionStatus()
}