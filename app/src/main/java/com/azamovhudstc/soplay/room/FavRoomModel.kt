package com.azamovhudstc.soplay.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_table")
data class FavRoomModel(
    @PrimaryKey @ColumnInfo(name = "favId") val linkString: String,
    @ColumnInfo(name = "favRating") val favRating: String,
    @ColumnInfo(name = "year") val favYear: String,
     @ColumnInfo(name = "genre") val favGenre: String,
    @ColumnInfo(name = "favPic") val picLinkString: String,
    @ColumnInfo(name = "favName") val nameString: String,
    @ColumnInfo(name = "favSource") val sourceString: String?
)