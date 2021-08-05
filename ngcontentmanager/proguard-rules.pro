#library issues
-dontwarn rx.internal.**
-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn com.google.common.**
-dontwarn android.databinding.**
-dontwarn com.squareup.okhttp.**
-dontwarn org.reactivestreams..**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.JavaWriter
-dontwarn lombok.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe


# Uncomment this if you use Mockito
-dontwarn org.mockito.**

-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }

-keep class com.nousdigital.ngcontentmanager.data.db.** {
    public protected *;
}
#general
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepclassmembers enum * { *; } #in order to avoid problems with deserialized enums

#rxjava
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }

#dbflow
-keep class com.raizlabs.android.dbflow.** { *; }
-keep class * extends com.raizlabs.android.dbflow.config.DatabaseHolder { *; }
-keep class com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder
-keep class * extends com.raizlabs.android.dbflow.config.BaseDatabaseDefinition { *; }

#jjwt
-keepnames class com.fasterxml.jackson.databind.** { *; }
-dontwarn com.fasterxml.jackson.databind.*
-keepattributes InnerClasses

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn io.jsonwebtoken.impl.Base64Codec

-keepnames class com.fasterxml.jackson.** { *; }
-keepnames interface com.fasterxml.jackson.** { *; }

-keep public enum com.nous.jmb.mode.ModeDestination$** {
    **[] $VALUES;
    public *;
}