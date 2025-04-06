package com.unique.schedify.post_auth.split_expense.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupExpenseResponseDto(
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: List<Data?>?
): Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("collaborators")
        val collaborators: List<Collaborator?>?,
        @SerializedName("created_by_google_auth_user")
        val createdByGoogleAuthUser: String?,
        @SerializedName("created_by_user")
        val createdByUser: Int?,
        @SerializedName("grp_name")
        val grpName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("last_settled_amt")
        val lastSettledAmt: String?,
        @SerializedName("last_settled_date_time")
        val lastSettledDateTime: String?,
        @SerializedName("t_amt")
        val tAmt: String?,
        @SerializedName("t_item")
        val tItem: Int?
    ): Parcelable {
        @Parcelize
        data class Collaborator(
            @SerializedName("collaborator_name")
            val collaboratorName: String?,
            @SerializedName("collab_google_auth_user")
            val collabGoogleAuthUser: Int?,
            @SerializedName("collab_user")
            val collabUser: Int?,
            @SerializedName("expenses")
            val expenses: List<Expense?>?,
            @SerializedName("group_expense_id")
            val groupExpenseId: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("redirect_upi_url")
            val redirectUpiUrl: String?,
            @SerializedName("requested_payment_qr_url")
            val requestedPaymentQrUrl: String?,
            @SerializedName("settle_medium")
            val settleMedium: String?,
            @SerializedName("settle_mode")
            val settleMode: String?,
            @SerializedName("status")
            val status: String?
        ): Parcelable {
            @Parcelize
            data class Expense(
                @SerializedName("date_time")
                val dateTime: String?,
                @SerializedName("i_amt")
                val iAmt: String?,
                @SerializedName("i_desp")
                val iDesp: String?,
                @SerializedName("i_name")
                val iName: String?,
                @SerializedName("i_notes")
                val iNotes: String?,
                @SerializedName("i_qty")
                val iQty: String?,
                @SerializedName("id")
                val id: Int?,
                @SerializedName("is_settled")
                val isSettled: Boolean?
            ): Parcelable
        }

        fun getTotalCollaborator(): Int {
            return collaborators?.size ?: 0
        }

        fun getTotalExpensesCount(): Int {
            return collaborators?.sumOf { collaborator ->
                collaborator?.expenses?.size ?: 0
            } ?: 0
        }
    }

    companion object {
        fun empty() = GroupExpenseResponseDto(message = "", data = emptyList())
    }
}