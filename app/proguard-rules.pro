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

# --- RETROFIT ---
-keep class retrofit2.** { *; }

# Keep model classes with Gson annotations (IMP)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep DTO with inner class used in response (IMP)
-keep class com.unique.schedify.auth.login.**.dto.** { *; }
-keep class com.unique.schedify.pre_auth.pre_auth_loading.**.dto.** { *; }
-keep class com.unique.schedify.pre_auth.pre_auth_loading.**.dto.** { *; }
-keep class com.unique.schedify.post_auth.post_auth_loading.**.dto.** { *; }

# Keep coroutine (IMP)
-keep class kotlin.coroutines.** { *; }
