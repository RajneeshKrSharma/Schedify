package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GroupRequestDto (
    @SerializedName("name")
    val grpName: String
) {
    companion object {
        fun empty () = GroupRequestDto(
            grpName = ""
        )
    }
}


data class GroupUpdateDeleteRequestPostData(
    val id: Int? = null,
    val groupRequestData: GroupRequestDto? = null
)
