-dontshrink
-dontoptimize

-dontnote

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions,Record,PermittedSubclasses,SourceFile,LineNumberTable

-renamesourcefileattribute Obfuscated

-repackageclasses 'av'

-keepdirectories

-keep class com.alphaver.AlphaVer {
    public <init>();
    public void onInitialize();
}
-keep class com.alphaver.AlphaVerClient {
    public <init>();
    public void onInitializeClient();
}

-keep class com.alphaver.mixin.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
