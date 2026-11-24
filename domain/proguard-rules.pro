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

# Keep all domain module classes and interfaces
-keep class com.kostryk.pricelator.domain.** { *; }
-keep interface com.kostryk.pricelator.domain.** { *; }
-keep enum com.kostryk.pricelator.domain.** { *; }

# Keep all domain model classes - they're used by Room and other modules
-keep class com.kostryk.pricelator.domain.model.** { *; }

# Keep Room annotations
-keepattributes *Annotation*

# Keep repository interfaces
-keep interface com.kostryk.pricelator.domain.repository.** { *; }

# Keep use cases
-keep class com.kostryk.pricelator.domain.usecase.** { *; }

# Keep DI module
-keep class com.kostryk.pricelator.domain.di.** { *; }
-keep class com.kostryk.pricelator.domain.di.DomainModule { *; }
-keep class com.kostryk.pricelator.domain.di.DomainModule_* { *; }

# Keep manager interfaces
-keep interface com.kostryk.pricelator.domain.manager.** { *; }
-keep class com.kostryk.pricelator.domain.manager.** { *; }

# Keep AppDispatcher
-keep class com.kostryk.pricelator.domain.AppDispatcher { *; }

# Keep ExceptionHandler
-keep interface com.kostryk.pricelator.domain.ExceptionHandler { *; }

# Keep AppResult sealed class
-keep class com.kostryk.pricelator.domain.AppResult { *; }
-keep class com.kostryk.pricelator.domain.AppResult$* { *; }

# Keep utility classes
-keep class com.kostryk.pricelator.domain.util.** { *; }

# Keep extension functions
-keep class com.kostryk.pricelator.domain.ext.** { *; }
-keep class com.kostryk.pricelator.domain.ext.*Kt { *; }