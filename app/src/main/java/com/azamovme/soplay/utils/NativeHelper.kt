package com.azamovme.soplay.utils

object NativeHelper {
  init { System.loadLibrary("native-lib") }

  /** Returns "users/{uuid}" */
  @JvmStatic external fun getDostupPath(): String

  /** Returns "Access Denied" */
  @JvmStatic external fun getDialogTitle(): String

  /** Returns "You do not have permission to use this app." */
  @JvmStatic external fun getDialogMessage(): String

  /** Returns "Exit" */
  @JvmStatic external fun getButtonTextExit(): String
}
