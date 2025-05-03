package com.unique.schedify.post_auth.split_expense.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollaboratorResponseDto(
    @SerializedName("data")
    val data: List<GroupExpenseResponseDto.Collaborator?>?
) : Parcelable