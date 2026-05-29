package com.example.snaphunt.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.snaphunt.photos.PendingAttempt

@Dao
interface PendingAttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attempt: PendingAttemptEntity): Long

    @Query("SELECT * FROM pending_attempts WHERE synced = 0")
    fun getPending(): List<PendingAttemptEntity>

    @Delete
    fun delete(attempt: PendingAttemptEntity)
}

@Database(
    entities = [PendingAttemptEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingAttemptDao(): PendingAttemptDao
}

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "snap_hunt_db"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}