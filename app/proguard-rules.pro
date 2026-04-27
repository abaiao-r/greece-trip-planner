# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# kotlinx.serialization
-keepattributes InnerClasses
-keep,includedescriptorclasses class dev.greecetripplanner.**$$serializer { *; }
-keepclassmembers class dev.greecetripplanner.** {
    *** Companion;
}
-keepclasseswithmembers class dev.greecetripplanner.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# kotlinx.serialization
-keepattributes InnerClasses
-keep,includedescriptorclasses class dev.greecetripplanner.**$$serializer { *; }
-keepclassmembers class dev.greecetripplanner.** {
    *** Companion;
}
-keepclasseswithmembers class dev.greecetripplanner.** {
    kotlinx.serialization.KSerializer serializer(...);
}
