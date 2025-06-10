package com.unique.schedify.pre_auth.pre_auth_loading.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppTourEntity(
    val image: String?,
    val subtitle: String?,
    val title: String?,
    @PrimaryKey val id: Int? = null,
)

@Entity(tableName = "HomeCarouselBanner")
data class HomeCarouselBannerEntity(
    val title: String?,
    val subtitle: String?,
    val imageUrl: String?,
    val colorLight: String?,
    val colorDark: String?,
    @PrimaryKey val id: Int,
)

@Entity(tableName = "HomeCellDetail")
data class HomeCellDetailEntity(
    val title: String?,
    val imageUrl: String?,
    val description: String?,
    val colorLight: String?,
    val colorDark: String?,
    val titleColor: String?,
    @PrimaryKey val id: Int,
)