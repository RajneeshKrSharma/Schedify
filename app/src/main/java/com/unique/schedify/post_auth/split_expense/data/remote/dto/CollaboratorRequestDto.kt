package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CollaboratorRequestDto (
    @SerializedName("collaborator_name")
    val collaboratorName: String,
    @SerializedName("group_id")
    val groupId: Int,
)