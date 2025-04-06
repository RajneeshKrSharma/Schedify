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

enum class GroupViewMode(val description: String) {
    COLLABORATOR_LIST("Collaborator list"),
    EXPENSE_LIST("Expense List");

    companion object {
        fun fromDescription(description: String): GroupViewMode {
            return entries.find { it.description.equals(description, ignoreCase = true) }
                ?: GroupViewMode.COLLABORATOR_LIST
        }
    }
}

