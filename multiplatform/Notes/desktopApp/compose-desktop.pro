# Keep core Jetpack Compose runtime structures
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.text.platform.ReflectionUtil { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }

# Keep Kotlinx Coroutines to avoid async runtime issues
-keep class kotlinx.coroutines.** { *; }

# Keep your main application entry point intact so the JVM can execute it
-keep class MainKt {
    public static void main(java.lang.String[]);
}

# TODO: Revisit these rules
-keep class com.google.firebase.** { *; }
-keep class android.content.** { *; }
-keep class android.os.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class org.sqlite.** { *; }

# Cloud Firestore & Generated Protobuf Rules
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firestore.v1.** { *; }
-keepclassmembers class com.google.firestore.v1.** { *; }

# General Protobuf Lite/Full Message preservation
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}
-keepclassmembers class * extends com.google.protobuf.GeneratedMessage {
    <fields>;
}

# gRPC Core and OkHttp Transport Rules
-keep class io.grpc.** { *; }
-keep interface io.grpc.** { *; }
-keepclassmembers class io.grpc.** { *; }

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