package com.azamovme.soplay.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavRoomModel::class], version = 1)
abstract class LinksRoomDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
}