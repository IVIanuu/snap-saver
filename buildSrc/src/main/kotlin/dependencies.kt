/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "com.ivianuu.snapsaver"
  const val compileSdk = 34
  const val minSdk = 28
  const val targetSdk = 31
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  object Essentials {
    private const val version = "0.0.1-dev1225"
    const val accessibility = "com.ivianuu.essentials:accessibility:$version"
    const val android = "com.ivianuu.essentials:android:$version"
    const val gradlePlugin = "com.ivianuu.essentials:gradle-plugin:$version"
    const val permission = "com.ivianuu.essentials:permission:$version"
    const val recentApps = "com.ivianuu.essentials:recent-apps:$version"
    const val rubik = "com.ivianuu.essentials:rubik:$version"
  }
}
