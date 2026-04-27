package dev.greecetripplanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TripDayEntity::class, CustomTemplateEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDayDao(): TripDayDao
    abstract fun customTemplateDao(): CustomTemplateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS custom_templates (
                        `key` TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        icon TEXT NOT NULL,
                        description TEXT NOT NULL,
                        regionsJson TEXT NOT NULL,
                        dayPoisJson TEXT NOT NULL
                    )""".trimIndent()
                )
            }
        }
    }
}
