package com.andresilva.greecetripplanner.di

import android.content.Context
import androidx.room.Room
import com.andresilva.greecetripplanner.data.db.TripDatabase
import com.andresilva.greecetripplanner.data.db.TripDayDao
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
        ).build()

    @Provides
    fun provideTripDayDao(db: TripDatabase): TripDayDao = db.tripDayDao()
}
