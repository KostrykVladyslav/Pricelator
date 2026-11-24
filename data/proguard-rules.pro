# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn java.lang.invoke.StringConcatFactory

# Keep all data module classes
-keep class com.kostryk.pricelator.data.** { *; }
-keep interface com.kostryk.pricelator.data.** { *; }

# Keep domain model classes used by Room
-keep class com.kostryk.pricelator.domain.model.** { *; }

# Keep Room annotations
-keepattributes *Annotation*

# Keep DI modules and generated factories
-keep class com.kostryk.pricelator.data.di.DataModule { *; }
-keep class com.kostryk.pricelator.data.di.DataModule_* { *; }
-keep class com.kostryk.pricelator.data.di.NetworkModule { *; }
-keep class com.kostryk.pricelator.data.di.NetworkModule_* { *; }

# Keep all @Provides methods
-keepclassmembers class com.kostryk.pricelator.data.di.** {
    @dagger.Provides *;
}

# Keep Retrofit service interfaces
-keep interface com.kostryk.pricelator.data.remote.api.** { *; }

# Keep Room DAOs
-keep interface com.kostryk.pricelator.data.room.dao.** { *; }
-keep class com.kostryk.pricelator.data.room.dao.** { *; }

# Keep Room Database
-keep class com.kostryk.pricelator.data.room.AppDatabase { *; }
-keep class com.kostryk.pricelator.data.room.AppDatabase_Impl { *; }

# Keep Workers
-keep class com.kostryk.pricelator.data.worker.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep repository implementations
-keep class com.kostryk.pricelator.data.repository.** { *; }

# Keep manager implementations
-keep class com.kostryk.pricelator.data.manager.** { *; }

# Keep exception handler
-keep class com.kostryk.pricelator.data.exception.** { *; }

# Keep utilities
-keep class com.kostryk.pricelator.data.util.** { *; }

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data classes used for serialization
-keep,includedescriptorclasses class com.kostryk.pricelator.data.remote.model.**$$serializer { *; }
-keepclassmembers class com.kostryk.pricelator.data.remote.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.kostryk.pricelator.data.remote.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep remote models
-keep class com.kostryk.pricelator.data.remote.model.** { *; }