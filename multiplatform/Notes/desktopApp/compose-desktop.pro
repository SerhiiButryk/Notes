-printmapping build/desktop-mapping.txt

# Keep main application entry point
-keep class MainKt {
    public static void main(java.lang.String[]);
}

# TODO: Revisit these rules
-keep class com.google.firebase.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class org.sqlite.** { *; }
-keep class android.app.Fragment { *; }
-keep class android.content.** { *; }
-keep class android.util.ArrayMap
#-keep class android.content.Context { *; }
#-keep class android.content.Intent { *; }
#-keep class android.content.IntentFilter { *; }
#-keep class android.content.BroadcastReceiver { *; }
-keep class android.os.Bundle { *; }
-keep class android.os.Parcelable { *; }
-keep class android.os.Message { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.sqlite.** { *; }
-keepclassmembers class androidx.sqlite.** { *; }

# Keep Jetpack DataStore and internal Protobuf classes
-keep class androidx.datastore.preferences.protobuf.** { *; }
-keep class androidx.datastore.preferences.PreferencesProto.** { *; }
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite { *; }
-keep class androidx.datastore.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

# Cloud Firestore & Generated Protobuf Rules
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firestore.v1.** { *; }

# General Protobuf Lite/Full Message preservation
-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep class * extends com.google.protobuf.GeneratedMessage { *; }

# Google Tink Rules
-keep class com.google.crypto.tink.** { *; }
-keep class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
    <fields>;
    <methods>;
}

# gRPC Core and OkHttp Transport Rules
-keep class io.grpc.** { *; }
-keep interface io.grpc.** { *; }
-keep class io.grpc.** { *; }

# Preserve Enum integrity for gRPC credentials and features
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Ignore missing classes during the ProGuard build
-dontwarn org.slf4j.**
-dontwarn com.google.**
-dontwarn androidx.**
-dontwarn android.**
-dontwarn okhttp3.internal.**
-dontwarn libcore.util.**
-dontwarn io.grpc.**
-dontwarn com.squareup.**
-dontwarn androidx.compose.material.**
-dontwarn kotlin.**