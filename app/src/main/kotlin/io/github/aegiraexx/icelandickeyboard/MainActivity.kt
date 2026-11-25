package io.github.aegiraexx.icelandickeyboard

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import java.util.Locale

/**
 * Two tabs. "Set up" explains what the app fixes and opens the physical keyboard settings. "Test"
 * tests the key right of P. The app switches to "Test" when the user comes back from the settings
 * through the button, and when the key is pressed on "Set up", so the result is never hidden.
 *
 * The test reads key events in the activity itself, with no text field, so no on-screen keyboard is
 * shown. Events from virtual devices (IMEs, `adb shell input`) are ignored, so an on-screen keyboard
 * cannot produce a false pass. [KeyEvent.getUnicodeChar] resolves through the key character map of
 * the device that sent the event, which is the layout selected for that keyboard.
 */
class MainActivity : Activity(), InputManager.InputDeviceListener {

    private lateinit var status: TextView
    private lateinit var inputManager: InputManager
    private lateinit var tabs: Array<View>
    private lateinit var tabIndicators: Array<View>
    private lateinit var pages: Array<View>

    /** Index of the tab shown: [TAB_SETUP] or [TAB_TEST]. */
    private var currentTab = TAB_SETUP

    /** True after the button opened the settings, until the user comes back. */
    private var openedSettings = false

    /**
     * Result of the last key test, or null if no key has been pressed since the screen was last
     * shown. Cleared on resume, because the user may have changed the layout in the settings.
     */
    private var result: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inputManager = getSystemService(Context.INPUT_SERVICE) as InputManager

        val layoutName = getString(R.string.keyboard_layout_icelandic)
        findViewById<TextView>(R.id.steps).text = getString(R.string.setup_steps, layoutName)
        findViewById<View>(R.id.open_settings).setOnClickListener { openKeyboardSettings() }
        status = findViewById(R.id.status)

        tabs = arrayOf(findViewById(R.id.tab_setup), findViewById(R.id.tab_test))
        tabIndicators = arrayOf(
            findViewById(R.id.tab_setup_indicator),
            findViewById(R.id.tab_test_indicator),
        )
        pages = arrayOf(findViewById(R.id.page_setup), findViewById(R.id.page_test))
        val accent = themeColor(
            if (Build.VERSION.SDK_INT >= 21) android.R.attr.colorAccent
            else android.R.attr.textColorPrimary,
        )
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener { showTab(index) }
            tabIndicators[index].setBackgroundColor(accent)
        }
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(STATE_TAB, TAB_SETUP)
            openedSettings = savedInstanceState.getBoolean(STATE_OPENED_SETTINGS)
        }
        showTab(currentTab)

        applySystemBarInsets(findViewById(R.id.root))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_TAB, currentTab)
        outState.putBoolean(STATE_OPENED_SETTINGS, openedSettings)
    }

    override fun onResume() {
        super.onResume()
        inputManager.registerInputDeviceListener(this, null)
        result = null
        if (openedSettings) {
            openedSettings = false
            showTab(TAB_TEST)
        }
        updateStatus()
    }

    override fun onPause() {
        inputManager.unregisterInputDeviceListener(this)
        super.onPause()
    }

    override fun onInputDeviceAdded(deviceId: Int) = updateStatus()
    override fun onInputDeviceRemoved(deviceId: Int) {
        result = null
        updateStatus()
    }

    override fun onInputDeviceChanged(deviceId: Int) = updateStatus()

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_LEFT_BRACKET && isPhysical(event)) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                result = describe(event)
                showTab(TAB_TEST)
                updateStatus()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun describe(event: KeyEvent): CharSequence {
        // Ctrl, Alt and the like match no behavior for this key in the KCM, so they give 0.
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

    private fun showTab(index: Int) {
        currentTab = index
        for (i in tabs.indices) {
            val selected = i == index
            tabs[i].isSelected = selected
            tabs[i].alpha = if (selected) 1f else 0.6f
            tabIndicators[i].visibility = if (selected) View.VISIBLE else View.INVISIBLE
            pages[i].visibility = if (selected) View.VISIBLE else View.GONE
        }
    }

    /** Resolves a color theme attribute, which may point to a color or a color state list. */
    private fun themeColor(attr: Int): Int {
        val value = TypedValue()
        if (!theme.resolveAttribute(attr, value, true)) return 0xFF808080.toInt()
        if (value.resourceId == 0) return value.data
        return if (Build.VERSION.SDK_INT >= 23) {
            getColorStateList(value.resourceId).defaultColor
        } else {
            @Suppress("DEPRECATION")
            resources.getColorStateList(value.resourceId).defaultColor
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
                openedSettings = true
                return
            } catch (e: ActivityNotFoundException) {
                // Try the next one.
            }
        }
    }

    /**
     * targetSdk 35+ makes the window draw behind the system bars on API 35+, so pad the content by
     * the bar and cutout insets. The root does not scroll, so nothing draws under the bars. On older
     * versions the decor view consumes the insets and this gets zeros.
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
        const val TAB_SETUP = 0
        const val TAB_TEST = 1
        const val STATE_TAB = "tab"
        const val STATE_OPENED_SETTINGS = "opened_settings"

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
