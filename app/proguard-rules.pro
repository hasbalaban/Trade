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

-keep class com.finance.trade_learn.** { *; }

-keepattributes SourceFile,LineNumberTable
-keep class com.google.firebase.crashlytics.** { *; }


-keepclassmembers enum * { *; }
-keep,allowshrinking class com.google.firebase.** { *; }




-keepattributes Signature
-keepattributes *Annotation*


-keep class com.finance.trade_learn.models.WrapResponse { *; }
-keep class com.finance.trade_learn.models.ErrorResponse { *; }




-keep class com.google.gson.stream.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * extends com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.JsonSerializer
-keep class * extends com.google.gson.JsonDeserializer




-keepattributes Exceptions
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }

