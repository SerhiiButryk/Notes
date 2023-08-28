# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Code which is kept
-printseeds build/output/mapping/seeds.txt
# Code which is removed
-printusage build/output/mapping/usage.txt
# Mapping
-printmapping build/output/mapping/mapping.txt
# Used rules
-printconfiguration build/output/mapping/configurations.txt

# Called from JNI
-keepclassmembers public class com.serhii.apps.notes.activities.AuthorizationActivity {
    void showAlertDialog(int);
}

# Called from JNI
-keepclassmembers public class com.serhii.apps.notes.activities.NotesViewActivity {
    void onUserAuthorized();
}

# Called from JNI
-keepclassmembers public class com.serhii.apps.notes.activities.AuthorizationActivity {
    void onAuthorizationFinished();
    void userRegistered();
}

# Keep names for classes which we use for json serialization/deserialization
#-keepnames class com.serhii.apps.notes.control.backup.data.**  { *; }