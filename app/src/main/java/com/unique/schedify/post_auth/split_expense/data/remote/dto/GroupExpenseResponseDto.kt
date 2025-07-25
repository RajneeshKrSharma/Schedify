package com.unique.schedify.post_auth.split_expense.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class GroupExpenseResponseDto(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("created_on")
    val createdOn: String?,
    @SerializedName("createdBy")
    val createdBy: Int?,
    @SerializedName("collaborators")
    val collaborators: ArrayList<Collaborator?>?
) : Parcelable {

    @Parcelize
    data class Collaborator(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("createdBy")
        val createdBy: Int?,
        @SerializedName("collabUserId")
        val collabUserId: Int?,
        @SerializedName("collaboratorName")
        val collaboratorName: String?,
        @SerializedName("groupId")
        val groupId: Int?,
        @SerializedName("created_on")
        val createdOn: String?,
        @SerializedName("isActive")
        val isActive: Boolean?,
        @SerializedName("collabEmailId")
        val collabEmailId: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("settle_modes")
        val settleModes: List<String>?,
        @SerializedName("settle_mediums")
        val settleMediums: List<String>?,
        @SerializedName("requested_payment_qr_url")
        val requestedPaymentQrUrl: String?,
        @SerializedName("redirect_upi_url")
        val redirectUpiUrl: String?,
        @SerializedName("detail")
        val detail: String? = null,
        @SerializedName("expenses")
        val expenses: AllExpenses?

    ) : Parcelable {

        @Parcelize
        data class AllExpenses(
            @SerializedName("self")
            val self: List<Expense>,
            @SerializedName("lend")
            val lend: List<Expense>,
            @SerializedName("owe")
            val owe: List<Expense>,

            ) : Parcelable {
            @Parcelize
            data class Expense(
                @SerializedName("id")
                val id: Int?,

                @SerializedName("eName")
                val eName: String,

                @SerializedName("eRawAmt")
                val eRawAmt: Double,

                @SerializedName("eAmt")
                val eAmt: Double,

                @SerializedName("eQty")
                val eQty: Int,

                @SerializedName("eQtyUnit")
                val eQtyUnit: String,

                @SerializedName("eDescription")
                val eDescription: String,

                @SerializedName("eExpenseType")
                val eExpenseType: String,

                @SerializedName("addedByCollaboratorId")
                val addedByCollaboratorId: Int,

                @SerializedName("expenseForCollaborator")
                val expenseForCollaborator: Int,

                @SerializedName("groupId")
                val groupId: Int,

                @SerializedName("created_on")
                val createdOn: String,

                @SerializedName("eCreationId")
                val eCreationId: String
            ) : Parcelable
        }

        fun collabName() : String
                = collaboratorName?.takeIf { name -> name.isNotEmpty() } ?: collabEmailId ?: "Clb - $id"

        fun getTotalExpense(): Int {
            return expenses?.let { expense ->
                expense.self + expense.lend + expense.owe
            }?.size ?: 0
        }
    }

    fun getTotalCollaborator(): Int {
        return collaborators?.size ?: 0
    }

    fun getTotalExpense(): Int {
        return collaborators
            ?.filterNotNull()
            ?.sumOf { collaborator ->
                buildList {
                    collaborator.expenses?.self?.let { addAll(it) }
                    collaborator.expenses?.lend?.let { addAll(it) }
                    collaborator.expenses?.owe?.let { addAll(it) }
                }
                .distinctBy { it.expenseForCollaborator }
                .size
            } ?: 0
    }

    fun getTotalExpenseById(collaboratorId: Int?): Int {
        return collaborators
            ?.firstOrNull { it?.id == collaboratorId }
            ?.expenses
            ?.let { expenses ->
                val allExpenses = buildList {
                    addAll(expenses.self)
                    addAll(expenses.lend)
                    addAll(expenses.owe)
                }
                allExpenses.distinctBy { it.eCreationId }.size
            } ?: 0
    }

    fun getTotalExpenseValueById(collaboratorId: Int?): Double {
        val total = collaborators
            ?.firstOrNull { it?.id == collaboratorId }
            ?.expenses
            ?.let { expenses ->
                buildList {
                    addAll(expenses.self)
                    addAll(expenses.lend)
                    addAll(expenses.owe)
                }
                .distinctBy { it.eCreationId }
                .sumOf { it.eRawAmt }
            } ?: 0.0

        return String.format(Locale.ROOT, "%.2f", total).toDouble()
    }

    fun getCollaboratorName(id:Int): String?
    = collaborators?.firstOrNull { it?.id == id }?.let { collaborator ->
        collaborator.collaboratorName?.takeIf { name -> name.isNotEmpty() } ?:
        collaborator.collabEmailId?.split("@")?.firstOrNull() ?: "Clb - ${collaborator.id}"
    }
}