package com.unique.schedify.post_auth.schedule_list

enum class AttachmentType {
    PDF, IMAGE, TEXT
}

enum class ScheduleTab(val title: String) {
    ALL("All"),
    PINNED("Pinned"),
    ARCHIVED("Archived")
}