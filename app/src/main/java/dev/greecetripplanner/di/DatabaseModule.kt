package dev.greecetripplanner.di

import android.content.Context
import androidx.room.Room
import dev.greecetripplanner.data.db.CustomTemplateDao
import dev.greecetripplanner.data.db.TripDatabase
import dev.greecetripplanner.data.db.TripDayDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TripDatabase =
        Room.databaseBuilder(
            context,
            TripDatabase::class.java,
            "greece_trip.db"
        )
            .addMigrations(
                TripDatabase.MIGRATION_1_2,
                TripDatabase.MIGRATION_2_3,
                TripDatabase.MIGRATION_1_3,
            )
            .build()

    @Provides
    fun provideTripDayDao(db: TripDatabase): TripDayDao = db.tripDayDao()

    @Provides
    fun provideCustomTemplateDao(db: TripDatabase): CustomTemplateDao = db.customTemplateDao()
}
