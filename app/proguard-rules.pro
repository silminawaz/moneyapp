# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/SilmiNawaz/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

### MoneyApp START ###
-keepattributes EnclosingMethod
-dontwarn android.databinding.**
-keep class android.databinding.** { *; }
-keep class com.ewise.moneyapp.data.* {*;}
-keep class com.ewise.moneyapp.adapters.** {*;}
-keep class com.ewise.moneyapp.Utils.** {*;}
### MoneyApp END ###

### PDV SDK START ###

-keepattributes Exceptions
-keepattributes InnerClasses,EnclosingMethod
-keepattributes Signature

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


-keep class com.ewise.android.pdv.* {*;}
-keep class com.ewise.android.pdv.api.* {*;}
-keep class com.ewise.android.pdv.api.callbacks.CallbackStatus {*;}
-keep class com.ewise.android.pdv.api.callbacks.PdvApiCallback {*;}
-keep public interface com.ewise.android.pdv.api.callbacks.PdvApiCallback$** {*;}
-keep class com.ewise.android.pdv.api.exception.** {*;}
-keep class com.ewise.android.pdv.api.model.** {*;}
-keep class com.ewise.android.pdv.api.security.* {*;}
-keep class com.ewise.android.pdv.api.security.user.* {*;}
-keep class com.ewise.android.pdv.db.* {*;}
-keep class com.ewise.android.pdv.db.validation.exeception.* {*;}
-keep class com.ewise.android.pdv.jsbridge.AcaLogger {*;}
-keep class com.ewise.android.pdv.api.util.UpdateTransactionConfig {*;}
-keepclassmembers class com.ewise.android.pdv.jsbridge.** {*;}

-dontnote android.support.**
-dontwarn javax.mail.**
-dontwarn javax.naming.Context
-dontwarn javax.naming.InitialContext
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn sun.misc.Unsafe

-keep class com.squareup.** { *; }

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**


-keepclasseswithmembernames class *
 {
    native <methods>;
 }

-keepclasseswithmembernames class *
 {
    public <init>(android.content.Context, android.util.AttributeSet);
 }

-keepclasseswithmembernames class *
 {
    public <init>(android.content.Context, android.util.AttributeSet, int);
 }

-keepclassmembers enum *
 {
    public static **[] values();
    public static ** valueOf(java.lang.String);
 }

-keep class * implements android.os.Parcelable
 {
    public static final android.os.Parcelable$Creator *;
 }

### XWALK START ###
-keepclassmembers class * {
 static final %                *;
 static final java.lang.String *;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
 *** get*();
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
public void *(android.view.View);
public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
 static ** CREATOR;
}

-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
 <fields>;
}

-adaptresourcefilenames    **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# Keep native & callbacks
-keepclasseswithmembernames class *{
 native <methods>;
}

-keepattributes JNINamespace
-keepattributes CalledByNative
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Too many hard code reflections between xwalk wrapper and bridge,so
# keep all xwalk classes.
-keep class org.xwalk.**{ *; }
-keep interface org.xwalk.**{ *; }
-keep class com.example.extension.**{ *; }
-keep class org.crosswalkproject.**{ *; }

# Rules for org.chromium classes:
# Keep annotations used by chromium to keep members referenced by native code
-keep class org.chromium.base.*Native*
-keep class org.chromium.base.annotations.JNINamespace
-keepclasseswithmembers class org.chromium.** {
 @org.chromium.base.AccessedByNative <fields>;
}
-keepclasseswithmembers class org.chromium.** {
 @org.chromium.base.*Native* <methods>;
}

-keep class org.chromium.** {
 native <methods>;
}

# Keep methods used by reflection and native code
-keep class org.chromium.base.UsedBy*
-keep @org.chromium.base.UsedBy* class *
-keepclassmembers class * {
 @org.chromium.base.UsedBy* *;
}

-keep @org.chromium.base.annotations.JNINamespace* class *
-keepclassmembers class * {
 @org.chromium.base.annotations.CalledByNative* *;
}

# Suppress unnecessary warnings.
-dontnote org.chromium.net.AndroidKeyStore
# Objects of this type are passed around by native code, but the class
# is never used directly by native code. Since the class is not loaded, it does
# not need to be preserved as an entry point.
-dontnote org.chromium.net.UrlRequest$ResponseHeadersMap

# Generate by aapt. may only need for testing, just add them here.
-keep class org.chromium.ui.ColorPickerAdvanced { <init>(...); }
-keep class org.chromium.ui.ColorPickerMoreButton { <init>(...); }
-keep class org.chromium.ui.ColorPickerSimple { <init>(...); }

-dontwarn org.chromium.**
-dontwarn org.xwalk.**
-dontwarn com.google.android.**

### XWALK END ###

### DBFlow START ###

-keepattributes Annotation
-keep class net.sqlcipher.** { *; }
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }
-dontwarn net.sqlcipher.

### DBFlow END ###

### PDV SDK END ###

### Butterknife START ###

-dontwarn butterknife.internal.**

# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

-keepclasseswithmembernames class * {
@butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
@butterknife.* <methods>;
}

-keep class **$$ViewBinder { *; }

### Butterknife END ###
