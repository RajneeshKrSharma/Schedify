package com.unique.schedify.post_auth.post_auth_utils

enum class GroupState {
    CREATE,
    UPDATE,
    DELETE,
    DEFAULT
}

enum class CollaboratorState {
    CREATE,
    UPDATE,
    DELETE,
    DEFAULT
}

enum class CollaboratorAlterState {
    CREATE,
    UPDATE,
}

enum class GroupAlterState {
    CREATE,
    UPDATE,
}

enum class ExpenseAlterState {
    CREATE,
    UPDATE,
}

enum class ExpenseState {
    CREATE,
    UPDATE,
    DELETE,
    DEFAULT
}

enum class HomeGridCellType(val description: String) {
    SIMPLE("Simple"),
    SPLIT("Split")
}


enum class SplitScheduleListMoreOption(val description: String) {
    COLLABORATOR_SCREEN("collaboratorScreen"),
    ADD_COLLABORATOR("Add Collaborator"),
    EXPENSES_SCREEN("Expense Screen");

    companion object {
        fun fromDescription(description: String): SplitScheduleListMoreOption {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: ADD_COLLABORATOR
        }
    }
}

enum class AddCollaboratorFields(val description: String) {
    COLLABORATOR_EMAIL_ID("Collaborator EmailId");

    companion object {
        fun fromDescription(description: String): AddCollaboratorFields {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: COLLABORATOR_EMAIL_ID
        }
    }
}

enum class EditCollaboratorFields(val description: String) {
    COLLABORATOR_NAME("Collaborator Name"),
    PAYMENT_QR_URL("Payment QR LINK"),
    REDIRECT_UPI_URL("Redirect UPI LINK"),
    PREFERRED_SETTLE_MODE("Preferred Settle Mode"),
    PREFERRED_SETTLE_MEDIUM("Preferred Settle Medium");

    companion object {
        fun fromDescription(description: String): EditCollaboratorFields {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: PREFERRED_SETTLE_MODE
        }
    }
}

enum class OnlinePaymentsOptions(val description: String) {
    UPI("UPI"),
    NET_BANKING("Net Banking"),
    NEFT("NEFT");

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


enum class ExpenseType(val description: String) {
    SELF("self"),
    SHARED_EQUALLY("shared-equally"),
    CUSTOM("Custom Split");

    companion object {
        fun fromDescription(description: String): ExpenseType {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: SELF
        }
    }
}

enum class SettledStatus(val description: String) {
    PENDING("PENDING"),
    SETTLED("SETTLED"),
    OWNED("OWNED");

    companion object {
        fun fromDescription(description: String): SettledStatus {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: PENDING
        }
    }
}

enum class AddGroupFields(val description: String) {
    GROUP_NAME("Group Name");

    companion object {
        fun fromDescription(description: String): AddGroupFields {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: GROUP_NAME
        }
    }
}

enum class AddExpenseFields(val description: String) {
    ITEM_NAME("Item Name ( Eg. Apple, vegetable )"),
    ITEM_QUANTITY("Quantity ( Eg. 2 or 3)"),
    ITEM_QUANTITY_UNIT("Quantity Unit ( Eg. Kg / Ltr / gram )"),
    ITEM_AMOUNT("Amount in Rupees"),
    ITEM_DESCRIPTION("Description ( If Any )"),
    EXPENSE_TYPE("Expense type"),
    ITEM_CUSTOM_UNIT("Enter Quantity Unit"),
    COLLABORATOR_CUSTOM_SHARE("Shared expense amount for Collaborator");

    companion object {
        fun fromDescription(description: String): AddExpenseFields {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: ITEM_NAME
        }

        fun fromId(id: String): AddExpenseFields {
            return entries.find { it.name.equals(id, ignoreCase = true) }
                ?: ITEM_NAME
        }
    }
}


enum class ExpenseQuantityUnitsOptions(private val label: String) {
    Kg("Kilogram"),
    Gram("Gram"),
    Ltr("Litre"),
    Ml("Millilitre"),
    Pair("Pair"),
    Piece("Piece"),
    Dozen("Dozen"),
    Meter("Meter"),
    Cm("Centimeter"),
    Inch("Inch"),
    Ft("Foot"),
    SqMeter("Square Meter"),
    SqFt("Square Foot"),
    Pack("Pack"),
    Box("Box"),
    Set("Set"),
    Bundle("Bundle"),
    Roll("Roll"),
    Other("Other");

    companion object {
        fun unitOptions(): List<String> = entries.map { it.name }
    }
}
