package io.github.aegiraexx.icelandickeyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Anchor for the keyboard layout metadata in the manifest. The system only reads the receiver's
 * metadata and never delivers a broadcast, so there is nothing to do here.
 */
class LayoutsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = Unit
}
