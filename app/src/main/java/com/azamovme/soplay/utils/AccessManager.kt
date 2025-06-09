package com.azamovme.soplay.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

object AccessManager {

    /**
     * On first run: writes under /users/{uuid}:
     *    { "uuid": <uuid>, "isDostup": true }
     * On later runs: reads and returns the saved flag.
     */
    @SuppressLint("HardwareIds")
    suspend fun initAndCheckDostup(context: Context): Boolean {
        val uuid = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: return false

        val db = FirebaseDatabase
            .getInstance("https://soplay-bd7c8-default-rtdb.asia-southeast1.firebasedatabase.app/")

        val usersRef = db.getReference("users")
        val usersSnap = usersRef.get().await()
        if (!usersSnap.exists()) {
            usersRef.setValue(mapOf<String, Any>()).await()
        }

        val nodePath = "users/$uuid"
        val ref = db.getReference(nodePath)

        return try {
            val snap = ref.get().await()
            if (!snap.exists()) {
                ref.setValue(
                    mapOf(
                        "uuid" to uuid,
                        "isDostup" to true
                    )
                ).await()
                true
            } else {
                snap.child("isDostup")
                    .getValue(Boolean::class.java) == true
            }
        } catch (e: CancellationException) {
            return false
        } catch (e: Exception) {
            Log.e("AccessManager", "initAndCheckDostup failed", e)
            false
        }
    }
}
