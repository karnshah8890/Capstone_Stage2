# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Softwares\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
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


-dontobfuscate

# picasso
-dontwarn com.squareup.okhttp.**

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Play Services
-dontnote com.google.android.gms.**

-dontnote com.google.common.util.concurrent.**
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn com.google.common.**
-dontwarn android.support.**
-dontwarn com.squareup.javapoet.**

-dontwarn ckm.simple.sql_provider.processor.**
-dontwarn org.slf4j.**
-dontwarn okio.**

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-dontwarn com.fasterxml.jackson.databind.**
#-keep class com.fasterxml.jackson.databind.** {*;}
#-keep class com.fasterxml.jackson.databind.ObjectMapper {
#    public <methods>;
#    protected <methods>;
#}
#-keep class com.fasterxml.jackson.databind.ObjectWriter {
#    public ** writeValueAsString(**);
#}
#-keep class org.w3c.dom.bootstrap.** {*;}

#jraw
-dontwarn net.dean.**
-keep class net.dean.** {*;}
