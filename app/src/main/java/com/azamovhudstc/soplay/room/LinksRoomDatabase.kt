package com.azamovhudstc.soplay.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.azamovhudstc.soplay.room.FavRoomModel
import com.azamovhudstc.soplay.room.LinkDao

@Database(entities = [FavRoomModel::class], version = 1)
abstract class LinksRoomDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
}