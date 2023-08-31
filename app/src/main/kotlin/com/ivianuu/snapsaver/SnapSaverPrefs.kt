/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.snapsaver

import com.ivianuu.essentials.data.DataStoreModule
import com.ivianuu.injekt.Provide
import kotlinx.serialization.Serializable

@Serializable data class SnapSaverPrefs(val snapSaverEnabled: Boolean = false) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("snap_saver_prefs") { SnapSaverPrefs() }
  }
}
