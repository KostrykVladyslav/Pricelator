import java.util.Properties

object App {
    const val namespace = "com.kostryk.pricelator"
    const val compileSdk = 36
    const val minSdk = 29
    const val targetSdk = 36
    const val versionName = "1.1.6"
    const val versionCode = 13
    val properties = Properties()
}

fun Properties.storeFilePath(): String = getProperty("STORE_FILE")
fun Properties.storePassword(): String = getProperty("STORE_PASSWORD")
fun Properties.keyPassword(): String = getProperty("KEY_PASSWORD")
fun Properties.keyAlias(): String = getProperty("KEY_ALIAS")