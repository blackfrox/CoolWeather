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

#-optimizationpasses 5 # 指定代码的压缩级别
#-dontusemixedcaseclassnames # 是否使用大小写混合
#-dontpreverify # 混淆时是否做预校验
#-verbose # 混淆时是否记录日志
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* # 混淆时所采用的算法
#-keep public class * extends android.app.Activity # 保持哪些类不被混淆
#-keep public class * extends android.app.Application # 保持哪些类不被混淆
#-keep public class * extends android.app.Service # 保持哪些类不被混淆
#-keep public class * extends android.content.BroadcastReceiver # 保持哪些类不被混淆
#-keep public class * extends android.content.ContentProvider # 保持哪些类不被混淆
#-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
#-keep public class * extends android.preference.Preference # 保持哪些类不被混淆
#-keep public class com.android.vending.licensing.ILicensingService # 保持哪些类不被混淆
#-keepclasseswithmembernames class * {# 保持 native 方法不被混淆 native <methods>;
#}
##-keep class android.** {	*;}
##-ignorewarnings
#-keep class com.google.android.gms.** { *; }
#-dontwarn com.google.android.gms.**
#
-keep class *

-keepnames class *

-keepclassmembers class *
-keep class * {
    public private *;
}

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

                       # litepal
-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

-keep class * extends org.litepal.crud.LitePalSupport {
    *;
}

                   #bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

                    #recyclerAdapterHelper
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

                #eventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}