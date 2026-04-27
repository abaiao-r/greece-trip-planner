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

        /** For installs still on DB v1 (pre-v1.3.0). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // v1→v2 was a no-op schema change in v1.3.0
            }
        }

        /** Adds custom_templates table for custom routes feature. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(CREATE_CUSTOM_TEMPLATES)
            }
        }

        /** Direct jump from v1 to v3. */
        val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(CREATE_CUSTOM_TEMPLATES)
            }
        }
    }
}
