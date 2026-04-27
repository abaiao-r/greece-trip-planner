package dev.greecetripplanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TripDayEntity::class, CustomTemplateEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class TripDatabase : RoomDatabase() {
    abstract fun tripDayDao(): TripDayDao
    abstract fun customTemplateDao(): CustomTemplateDao

    companion object {
        private val CREATE_CUSTOM_TEMPLATES = """
            CREATE TABLE IF NOT EXISTS custom_templates (
                `key` TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                icon TEXT NOT NULL,
                description TEXT NOT NULL,
                regionsJson TEXT NOT NULL,
                dayPoisJson TEXT NOT NULL
            )
        """.trimIndent()

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(CREATE_CUSTOM_TEMPLATES)
            }
        }
    }
}
