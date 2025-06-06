package com.unique.schedify.post_auth.schedule_list.data.remote.model

import okhttp3.MultipartBody

data class UploadFileRequestDto(
    val scheduleId: String,
    val files: List<MultipartBody.Part>
)
