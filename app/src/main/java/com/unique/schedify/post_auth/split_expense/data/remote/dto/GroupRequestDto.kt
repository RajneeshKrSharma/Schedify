package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GroupRequestDto (
    @SerializedName("grp_name")
    val grpName: String
)