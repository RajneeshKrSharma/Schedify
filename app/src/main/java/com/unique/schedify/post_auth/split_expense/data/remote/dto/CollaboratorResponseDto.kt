package com.unique.schedify.post_auth.split_expense.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Data.Collaborator
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollaboratorResponseDto(
    @SerializedName("data")
    val data: List<Collaborator?>?
) : Parcelable