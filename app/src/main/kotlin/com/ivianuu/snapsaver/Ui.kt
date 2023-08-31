
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.snapsaver

import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.ui.AppColors
import com.ivianuu.essentials.ui.common.IconPlaceholder
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.RootScreen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.prefs.SwitchListItem
import com.ivianuu.injekt.Provide

@Provide val snapSaverAppColors = AppColors(
  primary = Color(0xFF487EB0),
  secondary = Color(0xFFFBC531)
)

@Provide class HomeScreen : RootScreen

@Provide val homeUi = Ui<HomeScreen, HomeModel> { model ->
  Scaffold(topBar = { AppBar { Text(R.string.app_name) } }) {
    VerticalList {
      item {
        SwitchListItem(
          value = model.snapSaverEnabled,
          onValueChange = model.updateSnapSaverEnabled,
          leading = { IconPlaceholder() },
          title = { Text("Enabled") }
        )
      }
    }
  }
}

data class HomeModel(
  val snapSaverEnabled: Boolean,
  val updateSnapSaverEnabled: (Boolean) -> Unit
)

@Provide fun homeModel(
  permissionManager: PermissionManager,
  prefsDataStore: DataStore<SnapSaverPrefs>
) = Model {
  val prefs by prefsDataStore.data.collectAsState(SnapSaverPrefs())
  HomeModel(
    snapSaverEnabled = prefs.snapSaverEnabled,
    updateSnapSaverEnabled = action { value ->
      if (!value || permissionManager.requestPermissions(snapSaverPermissionKeys))
        prefsDataStore.updateData { copy(snapSaverEnabled = value) }
    }
  )
}
