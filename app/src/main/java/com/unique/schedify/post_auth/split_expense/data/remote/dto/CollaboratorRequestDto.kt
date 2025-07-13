package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CollaboratorRequestDto (
    @SerializedName("id")
    val collaboratorId: Int? = null,
    @SerializedName("collaboratorName")
    val collaboratorName: String?,
    @SerializedName("groupId")
    val groupId: Int? = null,
    @SerializedName("emailId")
    val emailId: String? = null,
) {
    companion object {
        fun empty() = CollaboratorRequestDto(
            collaboratorId = null,
            collaboratorName = null,
            groupId = 0,
            emailId = null,
        )
    }
}