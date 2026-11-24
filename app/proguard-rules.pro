# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signature info for better debugging
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# ============================================
# Hilt / Dagger
# ============================================
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection

# Keep all Hilt generated classes
-keep class **_HiltModules { *; }
-keep class **_HiltModules_* { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_Impl { *; }
-keep class **_ComponentTreeDeps { *; }
-keep class **Hilt** { *; }

# Keep Hilt aggregated dependencies (CRITICAL!)
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps.** { *; }
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps._** { *; }

# Keep specific aggregated deps for our modules
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps._com_kostryk_pricelator_data_di_DataModule { *; }
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps._com_kostryk_pricelator_data_di_NetworkModule { *; }
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps._com_kostryk_pricelator_data_worker_CurrencyUpdateWorker_HiltModule { *; }
-keep,allowobfuscation,allowoptimization class hilt_aggregated_deps._com_kostryk_pricelator_domain_di_DomainModule { *; }

# Don't warn about missing Hilt aggregated deps - they're internal to Hilt
-dontwarn hilt_aggregated_deps.**
-dontwarn hilt_aggregated_deps._com_kostryk_pricelator_data_di_DataModule
-dontwarn hilt_aggregated_deps._com_kostryk_pricelator_data_di_NetworkModule
-dontwarn hilt_aggregated_deps._com_kostryk_pricelator_data_worker_CurrencyUpdateWorker_HiltModule
-dontwarn hilt_aggregated_deps._com_kostryk_pricelator_domain_di_DomainModule

# Keep all @Module annotated classes
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.components.SingletonComponent class * { *; }

# Keep all @Provides and @Binds methods
-keepclassmembers class * {
    @dagger.Provides <methods>;
    @dagger.Binds <methods>;
}

# Keep Hilt entry points
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Keep assisted injection
-keep @dagger.assisted.AssistedInject class * { *; }
-keep @dagger.assisted.AssistedFactory class * { *; }

# ============================================
# Project Modules - Domain
# ============================================
-keep class com.kostryk.pricelator.domain.** { *; }
-keep interface com.kostryk.pricelator.domain.** { *; }
-keep enum com.kostryk.pricelator.domain.** { *; }

# Keep domain DI module
-keep class com.kostryk.pricelator.domain.di.** { *; }

# Keep managers and their implementations
-keep class com.kostryk.pricelator.domain.manager.** { *; }
-keep interface com.kostryk.pricelator.domain.manager.** { *; }

# Keep repositories
-keep interface com.kostryk.pricelator.domain.repository.** { *; }

# Keep use cases
-keep class com.kostryk.pricelator.domain.usecase.** { *; }

# Keep models and entities
-keep class com.kostryk.pricelator.domain.model.** { *; }

# Keep utility classes
-keep class com.kostryk.pricelator.domain.util.** { *; }

# Keep extension functions
-keep class com.kostryk.pricelator.domain.ext.** { *; }

# Keep sealed classes for AppResult
-keep class com.kostryk.pricelator.domain.AppResult { *; }
-keep class com.kostryk.pricelator.domain.AppResult$* { *; }

# Keep AppDispatcher
-keep class com.kostryk.pricelator.domain.AppDispatcher { *; }

# Keep ExceptionHandler
-keep interface com.kostryk.pricelator.domain.ExceptionHandler { *; }

# Keep Java
-keep class java.util.*

# ============================================
# Project Modules - Data
# ============================================
-keep class com.kostryk.pricelator.data.** { *; }
-keep interface com.kostryk.pricelator.data.** { *; }

# Keep data DI modules
-keep class com.kostryk.pricelator.data.di.DataModule { *; }
-keep class com.kostryk.pricelator.data.di.DataModule_* { *; }
-keep class com.kostryk.pricelator.data.di.NetworkModule { *; }
-keep class com.kostryk.pricelator.data.di.NetworkModule_* { *; }

# Keep Room DAOs
-keep interface com.kostryk.pricelator.data.room.dao.** { *; }
-keep class com.kostryk.pricelator.data.room.dao.** { *; }

# Keep Room Database
-keep class com.kostryk.pricelator.data.room.AppDatabase { *; }
-keep class com.kostryk.pricelator.data.room.AppDatabase_Impl { *; }

# Keep repository implementations
-keep class com.kostryk.pricelator.data.repository.** { *; }

# Keep manager implementations
-keep class com.kostryk.pricelator.data.manager.** { *; }

# Keep remote API and models
-keep class com.kostryk.pricelator.data.remote.** { *; }

# Keep utilities
-keep class com.kostryk.pricelator.data.util.** { *; }

# Keep exception handler implementation
-keep class com.kostryk.pricelator.data.exception.** { *; }

# Keep workers
-keep class com.kostryk.pricelator.data.worker.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# ============================================
# Room Database
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** sInstance;
}

-keepclassmembers @androidx.room.Entity class * {
    <fields>;
    <methods>;
}

-keep class androidx.room.** { *; }

# Keep Room generated classes
-keep class **_Impl { *; }

# ============================================
# Retrofit & OkHttp
# ============================================
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }
-keep interface okhttp3.** { *; }

-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ============================================
# Kotlinx Serialization
# ============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.kostryk.pricelator.**$$serializer { *; }
-keepclassmembers class com.kostryk.pricelator.** {
    *** Companion;
}
-keepclasseswithmembers class com.kostryk.pricelator.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================
# Kotlin
# ============================================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlinx.coroutines.** { *; }

-keepclassmembers class * {
    ** WhenMappings;
}

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# ============================================
# Jetpack Compose
# ============================================
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

-keepclassmembers class androidx.compose.runtime.** {
    public <init>(...);
}

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ============================================
# CameraX
# ============================================
-keep class androidx.camera.** { *; }
-keep interface androidx.camera.** { *; }

# ============================================
# ML Kit
# ============================================
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# ============================================
# WorkManager
# ============================================
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.ListenableWorker

-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ============================================
# Coroutines
# ============================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================
# AndroidX and Support Libraries
# ============================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# ============================================
# Enums
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# Parcelable
# ============================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================
# Serializable
# ============================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# Remove Logging
# ============================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ============================================
# Navigation Component
# ============================================
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ============================================
# Hilt ViewModel
# ============================================
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Keep ViewModels
-keep class **.*ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# ============================================
# Feature Modules
# ============================================
-keep class com.kostryk.pricelator.camera.** { *; }
-keep class com.kostryk.pricelator.onboarding.** { *; }
-keep class com.kostryk.pricelator.settings.** { *; }

# ============================================
# Core UI Module
# ============================================
-keep class com.kostryk.pricelator.core.ui.** { *; }
-keep class com.kostryk.pricelator.ui.** { *; }