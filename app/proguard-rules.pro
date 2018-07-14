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

#                         1 基本指令
#混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames

#指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses

#指定不去忽略非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#不做预校验，preverify是progurad的4个步骤之一，
#Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

#有了verbose这句话，混淆后就会生成映射文件
#包含有类名 ->混淆后类名的映射关系
#然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt

#指定混淆时采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不改变


#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile.LineNumberTable

-dontskipnonpubliclibraryclasses
#用于告诉ProGuard，不要跳过对非公开类的处理。默认情况下是跳过的。因为程序中不会引用它们，
#有些情况下人们编写的代码与类库中的类在同一个包下，并且对包中内容加以引用，此时需要加入此条声明

# 不混淆Fragment的子类类名以及onCreate()、onCreateView()方法名
-keep public class * extends android.support.v4.app.Fragment {
    public void onCreate(android.os.Bundle);
    public android.view.View onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
}

#                     2 需要保留的东西
#保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
        native <methods>;
}

#保留了继承自Activity、Application这些类的子类
#因为这些子类，都有可能被外部调用
#比如说,第一行就了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

-dontwarn rx.internal.util.unsafe.*
#Retrofit
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

#Litepal
-keep class org.litepal.** {
    *;
}

-keep class * extends org.litepal.crud.DataSupport {
    *;
}

-keep class * extends org.litepal.crud.LitePalSupport {
    *;
}

#Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#BaseAdapter
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
# 不混淆包含native方法的类的类名以及native方法名
-keepclasseswithmembernames class * {
    native <methods>;
}


-keep class android.support.multidex.MultiDexApplication.**{

}
#百度混淆 真是蛋疼，网上找了半天，官方愣是没写
-keep class com.baidu.**{

}
-dontwarn com.baidu.location.**

-keep class com.baidu.location.** { *; }



#-keepn class com.android.tools.** {*; }
#Okhttp
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keepnames class com.parse.** {*; }
-dontwarn com.squareup.**
-keep class com.squareup.** { *;}
-keep class com.lljjcoder.style.citylist.**{ *;}
#保证gson数据不被混淆，不然不会显示
-keep class  com.example.weather.network.gson.**{ *;}
#碎碎念:天呐，终于混淆成功了
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule