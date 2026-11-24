package io.github.aegiraexx.icelandickeyboard

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import java.util.Locale

/**
 * Instructions screen: explains what the app fixes, opens the physical keyboard settings, and tests
 * the key right of P.
 *
 * The test reads key events in the activity itself, with no text field, so no on-screen keyboard is
 * shown. Events from virtual devices (IMEs, `adb shell input`) are ignored, so an on-screen keyboard
 * cannot produce a false pass. [KeyEvent.getUnicodeChar] resolves through the key character map of
 * the device that sent the event, which is the layout selected for that keyboard.
 */
class MainActivity : Activity(), InputManager.InputDeviceListener {

    private lateinit var status: TextView
    private lateinit var inputManager: InputManager

    /** Result of the last key test, or null if no key has been pressed yet. */
    private var result: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputManager = getSystemService(Context.INPUT_SERVICE) as InputManager

        val layoutName = getString(R.string.keyboard_layout_icelandic)
        findViewById<TextView>(R.id.steps).text = getString(R.string.setup_steps, layoutName)
        findViewById<View>(R.id.open_settings).setOnClickListener { openKeyboardSettings() }
        status = findViewById(R.id.status)

        applySystemBarInsets(findViewById(R.id.root))
    }

    override fun onResume() {
        super.onResume()
        inputManager.registerInputDeviceListener(this, null)
        updateStatus()
    }

    override fun onPause() {
        inputManager.unregisterInputDeviceListener(this)
        super.onPause()
    }

    override fun onInputDeviceAdded(deviceId: Int) = updateStatus()
    override fun onInputDeviceRemoved(deviceId: Int) = updateStatus()
    override fun onInputDeviceChanged(deviceId: Int) = updateStatus()

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_LEFT_BRACKET && isPhysical(event)) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                result = describe(event)
                updateStatus()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun describe(event: KeyEvent): CharSequence {
        val c = event.getUnicodeChar(event.metaState)
        if (c == 0 || c and KeyCharacterMap.COMBINING_ACCENT != 0) {
            return getString(R.string.test_modifiers)
        }
        val glyph = String(Character.toChars(c))
        val codePoint = String.format(Locale.ROOT, "U+%04X", c)
        val layoutName = getString(R.string.keyboard_layout_icelandic)
        return when (c) {
            ETH_SMALL, ETH_CAPITAL -> getString(R.string.test_pass, glyph, codePoint)
            D_STROKE_SMALL, D_STROKE_CAPITAL ->
                getString(R.string.test_stock_layout, glyph, codePoint, layoutName)
            else -> getString(R.string.test_other_layout, glyph, codePoint, layoutName)
        }
    }

    private fun updateStatus() {
        status.text = result ?: getString(
            if (hasPhysicalKeyboard()) R.string.test_waiting else R.string.test_no_keyboard,
        )
    }

    /**
     * Opens the physical keyboard settings (API 24+). Falls back to the input method settings, where
     * older versions put the physical keyboard section, and then to the settings root, since OEMs
     * are not required to handle every settings action.
     */
    private fun openKeyboardSettings() {
        val actions = arrayOf(
            if (Build.VERSION.SDK_INT >= 24) Settings.ACTION_HARD_KEYBOARD_SETTINGS else null,
            Settings.ACTION_INPUT_METHOD_SETTINGS,
            Settings.ACTION_SETTINGS,
        )
        for (action in actions) {
            if (action == null) continue
            try {
                startActivity(Intent(action))
                return
            } catch (e: ActivityNotFoundException) {
                // Try the next one.
            }
        }
    }

    /**
     * targetSdk 35+ makes the window draw behind the system bars on API 35+, so pad the content by
     * the bar and cutout insets. On older versions the decor view consumes them and this gets zeros.
     */
    private fun applySystemBarInsets(root: View) {
        if (Build.VERSION.SDK_INT < 20) return
        root.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= 30) {
                val bars = insets.getInsets(
                    WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout(),
                )
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            } else {
                @Suppress("DEPRECATION")
                view.setPadding(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom,
                )
            }
            insets
        }
    }

    private companion object {
        const val ETH_SMALL = 0x00F0
        const val ETH_CAPITAL = 0x00D0
        const val D_STROKE_SMALL = 0x0111
        const val D_STROKE_CAPITAL = 0x0110

        fun isPhysical(event: KeyEvent): Boolean =
            event.deviceId != KeyCharacterMap.VIRTUAL_KEYBOARD && event.device?.isVirtual == false

        /**
         * Any non-virtual alphabetic keyboard uses the selected layout, so it counts. This includes
         * built-in keyboards (e.g. laptops running Android apps), which is why
         * [InputDevice.isExternal] is not checked.
         */
        fun hasPhysicalKeyboard(): Boolean = InputDevice.getDeviceIds().any { id ->
            val device = InputDevice.getDevice(id)
            device != null && !device.isVirtual &&
                device.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC
        }
    }
}
