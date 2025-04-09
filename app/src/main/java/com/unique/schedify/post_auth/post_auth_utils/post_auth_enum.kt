package com.unique.schedify.post_auth.post_auth_utils

enum class SplitAddRadioOptions(val description: String) {
    UpdateGroup("Update group"),
    Group("Create new group"),
    Collaborator("Add new collaborator"),
    UpdateCollaborator("Update collaborator"),
    DEFAULT("")
}

enum class GroupState {
    CREATE,
    UPDATE,
    DELETE,
    READ,
    DEFAULT
}

enum class CollaboratorState {
    CREATE,
    UPDATE,
    DELETE,
    READ,
    DEFAULT
}

enum class ExpenseState {
    CREATE,
    UPDATE,
    DELETE,
    READ,
    DEFAULT
}



enum class ScheduleListProcess {
    CREATE,
    UPDATE,
    DELETE,
    READ,
    DEFAULT
}

enum class HomeGridCellType(val description: String) {
    SIMPLE("Simple"),
    SPLIT("Split")
}


enum class GroupScreenMenus(val description: String) {
    ViewMode("ViewMode"),
    More("More");

    companion object {
        fun fromDescription(description: String): GroupScreenMenus {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: ViewMode
        }
    }
}

enum class GroupViewMode(val description: String) {
    COLLABORATOR_LIST("Collaborator List View"),
    EXPENSE_LIST("Expense List View");

    companion object {
        fun fromDescription(description: String): GroupViewMode {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: GroupViewMode.COLLABORATOR_LIST
        }
    }
}


enum class SplitScheduleListMoreOption(val description: String) {
    ADD_COLLABORATOR("Add Collaborator"),
    ADD_EXPENSES("Add Expense");

    companion object {
        fun fromDescription(description: String): SplitScheduleListMoreOption {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: ADD_COLLABORATOR
        }
    }
}

enum class AddCollaboratorFields(val description: String) {
    COLLABORATOR_NAME("Collaborator Name"),
    PAYMENT_QR_URL("Payment QR LINK"),
    REDIRECT_UPI_URL("Redirect UPI LINK"),
    PREFERRED_SETTLE_MODE("Preferred Settle Mode"),
    PREFERRED_SETTLE_MEDIUM("Preferred Settle Medium");

    companion object {
        fun fromDescription(description: String): AddCollaboratorFields {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: COLLABORATOR_NAME
        }
    }
}

enum class OnlinePaymentsOptions(val description: String) {
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    NEFT("NEFT"),
    DEBIT_CARD("DEBIT CARD"),
    CREDIT_CARD("CREDIT CARD"),
    RTGS("RTGS"),
    DRAFT("DRAFT");

    companion object {
        fun fromDescription(description: String): OnlinePaymentsOptions {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: UPI
        }
    }
}


enum class OfflinePaymentsOptions(val description: String) {
    CASH("CASH"),
    BARTER("BARTER");

    companion object {
        fun fromDescription(description: String): OfflinePaymentsOptions {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: CASH
        }
    }
}


