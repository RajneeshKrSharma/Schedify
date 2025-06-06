package com.unique.schedify.post_auth.schedule_list.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttachedFileResponseDto(
    @SerializedName("attachments")
    val attachments: List<String?>?
): Parcelable