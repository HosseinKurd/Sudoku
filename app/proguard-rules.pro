# Add any project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
# https://developer.android.com/studio/build/shrink-code

# Keep line numbers and source file information for debugging.
# This increases APK size but is useful for crash reports. Remove in highly optimized builds.
-keepattributes SourceFile,LineNumberTable

# Kotlin specific rules
# Keep metadata for Kotlin classes. Essential for reflection and some libraries.
-keep class kotlin.Metadata { *; }
# Keep data classes' members and constructors.
# R8 often handles this, but it's good to be explicit if you encounter issues.
-keepclassmembers class **.model.** {
    <init>(...);
    copy(...);
    equals(...);
    hashCode();
    toString();
}
# Also specifically for your data classes
-keep class com.offlineganes.sudoku.model.** { *; }

# Jetpack Compose specific rules
# Keep Compose-related classes. R8 is generally good at this, but these are common.
-keep class androidx.compose.** { *; }
# Keep Composable functions and their containing classes
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
# For @Preview annotations
-keepclasseswithmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
# Keep the application's entry point (MainActivity)
-keep class com.offlineganes.sudoku.MainActivity { *; }

# AndroidX Lifecycle (ViewModel, LiveData, Flow)
# Keep ViewModel constructors, as they are often instantiated by the framework.
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
# If you are using SavedStateHandle with your ViewModel:
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(androidx.lifecycle.SavedStateHandle);
}
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application,androidx.lifecycle.SavedStateHandle);
}

# Kotlinx Coroutines and Flow
# These libraries generally provide their own rules, but adding these doesn't hurt.
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
# More specific rules if you hit issues with coroutines and ServiceLoader:
#-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler
#-keep class kotlinx.coroutines.android.AndroidDispatcherFactory

# AndroidX Navigation Compose
# Navigation components might use reflection to find Composables or arguments.
-keep class androidx.navigation.NavController {
    <init>(...);
    # Keep specific methods if issues arise with navigation arguments or deep links
    # private void navigate(androidx.navigation.NavDestination, android.os.Bundle, androidx.navigation.NavOptions, androidx.navigation.Navigator$Extras);
}
# If you are passing custom Parcelable or Serializable objects as navigation arguments,
# you might need to keep them:
-keepnames class * implements android.os.Parcelable
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements android.os.Parcelable {
    static final android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Rules for your specific application classes (optional, but good practice for domain/model)
# If you have specific classes you want to ensure are not obfuscated or removed, add them here.
# For example, if your Difficulty enum or SudokuBoard/SudokuCell models were
# used in reflection or serialization outside the ViewModel scope.
-keep class com.offlineganes.sudoku.model.Difficulty { *; }
-keep class com.offlineganes.sudoku.model.SudokuBoard { *; }
-keep class com.offlineganes.sudoku.model.SudokuCell { *; }
-keep class com.offlineganes.sudoku.ui.viewmodel.SudokuViewModel { *; } # Keep ViewModel itself

# Suppress warnings that are safe to ignore
# This helps keep your build output clean.
-dontwarn android.annotation.SuppressLint
-dontwarn com.google.android.material.** # Common for Material Design components if you use them in XML
-dontwarn org.jetbrains.annotations.**

# If you use reflection for any custom classes (e.g., custom factories, dynamic loading)
# Example: -keep class com.offlineganes.sudoku.somepackage.MyCustomFactory { *; }