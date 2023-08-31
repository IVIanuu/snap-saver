/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.snapsaver

import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityScope
import com.ivianuu.essentials.accessibility.EsAccessibilityService
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.recentapps.CurrentApp
import com.ivianuu.essentials.result.catch
import com.ivianuu.essentials.result.onFailure
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

@Provide fun snapSaverFeature(
  appContext: AppContext,
  currentApp: Flow<CurrentApp?>,
  logger: Logger,
  prefsDataStore: DataStore<SnapSaverPrefs>,
  service: EsAccessibilityService
) = ScopeWorker<AccessibilityScope> {
  prefsDataStore.data
    .map { it.snapSaverEnabled }
    .distinctUntilChanged()
    .flatMapLatest {
      if (!it) infiniteEmptyFlow()
      else currentApp
    }
    .collectLatest {
      if (it?.value != "com.snapchat.android") return@collectLatest
      
      logger.log { "start snap saver" }
      
      val snapchatContext = appContext.createPackageContext(
        "com.snapchat.android", 0
      )

      val id = snapchatContext.resources.getIdentifier(
        "attachments_chat_link_action_save", "string", "com.snapchat.android"
      )

      val saveDesc = snapchatContext.resources.getString(id)

      while (true) {
        catch {
          service.rootInActiveWindow
            .firstNodeOrNullWithChildren {
              logger.log { "node $it" }
              it.packageName == "com.snapchat.android" &&
                  it.text == saveDesc
            }
            ?.firstNodeOrNullWithParents { it.isClickable }
            ?.also { logger.log { "auto save $it" } }
            ?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }.onFailure { it.printStackTrace() }
        delay(200.milliseconds)
      }
    }
}

private fun AccessibilityNodeInfo.firstNodeOrNullWithChildren(
  predicate: (AccessibilityNodeInfo) -> Boolean
): AccessibilityNodeInfo? {
  if (predicate(this)) return this

  (0 until childCount)
    .forEach {
      getChild(it).firstNodeOrNullWithChildren(predicate)
        ?.let { return it }
    }

  return null
}

private fun AccessibilityNodeInfo.firstNodeOrNullWithParents(
  predicate: (AccessibilityNodeInfo) -> Boolean
): AccessibilityNodeInfo? {
  if (predicate(this)) return this
  return parent?.firstNodeOrNullWithChildren(predicate)
}

@Provide val snapSaverAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    flags = AccessibilityServiceInfo.CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT or
        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
  )
