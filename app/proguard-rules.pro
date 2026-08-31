-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# Kotlinx Serialization
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Google API Client
-keep class com.google.api.services.drive.** { *; }
-dontwarn com.google.api.client.**