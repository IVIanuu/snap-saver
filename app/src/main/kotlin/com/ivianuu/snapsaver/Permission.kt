package com.ivianuu.snapsaver

import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.permission.PermissionRevokeHandler
import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermission
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf

@Provide class PocketModeAccessibilityPermission :
  AccessibilityServicePermission(
    serviceClass = EsAccessibilityService::class,
    title = "Accessibility"
  )

val snapSaverPermissionKeys = listOf(typeKeyOf<PocketModeAccessibilityPermission>())

@Provide fun pocketModePermissionRevokeHandler(
  pref: DataStore<SnapSaverPrefs>
) = PermissionRevokeHandler(snapSaverPermissionKeys) {
  pref.updateData { copy(snapSaverEnabled = false) }
}
