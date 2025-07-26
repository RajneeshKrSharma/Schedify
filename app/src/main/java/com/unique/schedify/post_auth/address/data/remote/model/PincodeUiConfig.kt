package com.unique.schedify.post_auth.address.data.remote.model

data class PincodeUiConfig(
    val message: String,
    val postOfficeList: List<PostOfficeConfig>,
) {
    data class PostOfficeConfig(
        val name: String,
        val country: String,
        val district: String,
        val region: String,
        val block: String,
        val division: String,
        val state: String,
        val pincode: String,
    )

    companion object {
        fun default() = PincodeUiConfig(
            message = "",
            postOfficeList = emptyList()
        )
    }
}
