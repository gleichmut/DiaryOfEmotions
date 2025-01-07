# Минимизируем и оптимизируем классы
-optimizations !code/allocation/variable

# Сохраняем имена методов и классов, необходимых для работы с Reflection
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
  static final long serialVersionUID;
  private static final java.lang.String SELF_SERIALIZED_NAME;
  private void writeObject(java.io.ObjectOutputStream);
  private void readObject(java.io.ObjectInputStream);
  java.lang.Object writeReplace();
  java.lang.Object readResolve();
}

# Сохраняем активности и фрагменты
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider