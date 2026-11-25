# Testing

How the layout was verified, and how to repeat it. Results are listed per device with dates.

## Results

| Date | Device | Android | Build | Result |
|---|---|---|---|---|
| 24.11.2026 | Emulator, `google_apis_playstore` x86_64 image | 17 (API 37) | debug, v0.1.0 | **Pass.** Layout listed and selectable; `[` / `{` type U+00F0 / U+00D0. Stock "Icelandic" types U+0111 / U+0110 (bug reproduced). |
| 24.11.2026 | Same emulator | 17 (API 37) | debug, v0.1.0 + instructions screen | **Pass.** In-app key test: corrected layout reports U+00F0 / U+00D0; stock layout reports U+0111 / U+0110; a non-Icelandic layout reports U+005B. `adb shell input keyevent KEYCODE_LEFT_BRACKET` (virtual device) is ignored. Button opens `Settings$PhysicalKeyboardActivity`. Insets correct under edge-to-edge; EN/IS and light/dark checked by screenshot; adaptive icon renders in the launcher. |
| 24.11.2026 | Same emulator, Gboard with English (US) + Íslenska (Ísland) | 17 (API 37) | debug, v0.1.0 | **Pass** with the corrected layout selected under both languages (key test U+00F0 with either subtype). Auto-selection under Íslenska picks the stock layout; see `layout-selection.md`. |
| 24.11.2026 | Same emulator, Gboard with English (US) + Íslenska (Ísland) | 17 (API 37) | debug, v0.1.0 + `keyboardLocale="is-IS"`, then again with `"is-IS,is-Latn"` (D-012) | **Pass** (both). Clean install, no manual selection: AT Translated Set 2 keyboard shows "Automatic: Icelandic (corrected Ð)" under Íslenska and "Automatic: English (US)" under English (US). qwerty2 stays "Automatic: English (US)" under both (hardware language tag, see `layout-selection.md`). Six setup steps render correctly. |

| 24.11.2026 | OnePlus 12 (CPH2581), OxygenOS 16.0.10.601 (EX01); Drop Preonic rev3 (USB-C, QMK) | 16 (API 36) | none (stock only) | **Bug reproduced.** Stock "Icelandic" (an earlier manual choice) under FUTO and under Gboard `is-IS`: typed `x[{y` gives U+0078 U+0111 U+0110 U+0079. |
| 24.11.2026 | Same phone and Preonic | 16 (API 36) | debug, v0.1.0 at `d80dfd5` | **Pass.** "Icelandic (corrected Ð)" selected by hand under FUTO and under Gboard `is-IS`: U+0078 U+00F0 U+00D0 U+0079 with both. Installed over adb with no on-device prompt. |
| 24.11.2026 | Same phone; Ducky keyboard (`04d9:0295`, "USB-HID Keyboard"), never connected before | 16 (API 36) | debug, v0.1.0 at `d80dfd5` | **Pass (D-012 on real hardware).** No manual selection: Settings shows "Automatic: Icelandic (corrected Ð)" under Gboard Íslenska (Ísland), and the key types U+00F0 U+00D0. Under FUTO it shows "Default" and types U+005B U+007B (no Icelandic layout). |
| 24.11.2026 | Same phone, both keyboards | 16 (API 36) | debug, v0.1.0 at `d80dfd5` | **Pass.** In-app key test: Preonic (ours selected) reports Ð U+00D0 as correct; Ducky under FUTO ("Default") reports `[` U+005B with the "not an Icelandic layout" message. Button opens `Settings$PhysicalKeyboardActivity`. **Bug:** scrolled content draws under the status bar. |

Not yet tested: Samsung One UI, Android versions below 16 on real hardware (minSdk is 16, D-010),
Bluetooth keyboards, tablets.

## Findings (emulator, API 37, 24.11.2026)

- **Discovery works without the app ever being launched.** The app has no activity. After `adb install`,
  `cmd package query-receivers -a android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS` listed three
  providers: `com.android.inputdevices/.InputDeviceReceiver`, Gboard's
  `com.google.android.apps.inputmethod.libs.kcm.InputDeviceReceiver`, and ours. So the "stopped" state
  after install does not hide the receiver (verified on API 37 only).
- **Gboard is itself a keyboard layout provider.** Whether it ships an Icelandic layout, and whether that
  one is correct, is **unverified**. Worth checking: it may be an alternative workaround for Gboard users.
- **Layout picker (API 37):** Settings → Physical keyboard → device → language (e.g. "English (US)") →
  "Layouts" list. Both "Icelandic" and "Icelandic (corrected Ð)" are listed. The choice is per keyboard
  device and per IME language. The emulator has two keyboard devices ("AT Translated Set 2 keyboard" and
  "qwerty2"), and each needed its own selection.
- **Resource versioning:** with minSdk 16, aapt2 stores `keyboard_layouts.xml` twice: the full file as
  `xml-v22`, and a base copy without `keyboardLocale` / `keyboardLayoutType` for older API levels.
  Behavior is correct (those attributes are only read on API 34+). Why aapt2 picks v22 rather than v34 is
  **unverified**.

- **Per-app locale changes which layout applies (API 37, 24.11.2026).** With the corrected layout
  selected for qwerty2 under the "English (US)" language, the in-app test passed while the app followed
  the system locale (en-US). After `cmd locale set-app-locales <app> --locales is-IS`, the same key
  typed `[` (U+005B, a non-Icelandic layout) and Settings still showed the corrected layout selected.
  **Explained (verified 24.11.2026):** Gboard switched to an `is-IS` subtype for the app, and layout
  selection is per IME subtype, so the English (US) choice didn't apply. See
  `layout-selection.md`.

## Open questions

- **Automatic selection on Android 14+ (API 34+).** *Answered 24.11.2026, see `layout-selection.md`.*
  The choice is per keyboard and per IME subtype. Under an Icelandic subtype, auto-selection picks the
  stock layout over ours (tie, broken by label), so without D-012 users must select ours by hand under each
  language (with an English or Icelandic system language; in others ours can win the tie). Adding
  `is-IS` to `keyboardLocale` makes ours win for Gboard's `is-IS` subtype (verified on the emulator;
  applied in D-012 as `is-IS,is-Latn`).

## Procedure (emulator)

`adb shell input keyevent` does **not** test this: it injects through the virtual keyboard device, which
uses `Virtual.kcm`, not the selected layout. Use the emulator console, which types through the "qwerty2"
hardware keyboard device.

1. `./gradlew assembleDebug`, boot an emulator, `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
2. `adb shell am start -a android.settings.HARD_KEYBOARD_SETTINGS`. Open "qwerty2", then the language,
   then select "Icelandic (corrected Ð)".
3. Open any text field, e.g. `adb shell am start -a com.android.settings.action.SETTINGS_SEARCH`.
4. `adb emu event text 'x[{y'` (`[` is the key right of P, `{` is Shift plus that key).
5. Read the code points, not the glyphs:
   `adb shell uiautomator dump /sdcard/ui.xml && adb shell cat /sdcard/ui.xml`, then decode the
   EditText `text` attribute. Expected `x` U+00F0 U+00D0 `y`.
6. Control: select the stock "Icelandic" and repeat. Expected U+0111 U+0110.

## Procedure (real device)

A USB keyboard occupies the USB port, so connect adb over Wi-Fi (Android 11+). Record the exact
settings path per device and OEM, since it feeds the user instructions (D-001).

1. **Connect.** Phone and computer on the same Wi-Fi, with no client isolation. On the phone:
   Developer options → Wireless debugging → on → "Pair device with pairing code". `adb mdns services`
   lists the `_adb-tls-pairing` and `_adb-tls-connect` ports, so only the 6-digit code has to be read
   off the phone. Then `adb pair <ip>:<pairing-port> <code>`. adb auto-connects over mDNS; address
   the device by its mDNS serial (`adb-<id>._adb-tls-connect._tcp`), which survives port changes.
   If a manual `adb connect` was also made, disconnect it, or adb sees two devices.
2. **Baseline before installing.** `getprop` (`ro.build.version.release`, `ro.build.version.sdk`,
   `ro.build.display.id`), `settings get secure default_input_method`,
   `settings get secure enabled_input_methods` (enabled subtypes as hash codes), `ime list -a`
   (subtype tags), `cmd package query-receivers --brief -a
   android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS` (layout providers).
3. **Plug in the keyboard.** Check it in `dumpsys usb` (host devices) and `dumpsys input` (the
   `KeyboardType: 2` entry; `KeyboardLayoutInfo: <not set>` means it reports no language of its own).
4. **Check for earlier manual choices.** Settings shows "User selected: …" for them. They override
   auto-selection and cannot be reset (see findings), so test auto-selection only with a keyboard the
   device has never seen.
5. **Type test.** Open a text field (`am start -a com.android.settings.action.SETTINGS_SEARCH`; to
   clear it, `am force-stop com.android.settings.intelligence` and start it again). The tester types
   `x`, the key right of P, Shift plus that key, `y` on the real keyboard. Read the code points from
   `uiautomator dump`, never the glyphs. Switch IMEs with `ime set <id>` and confirm the subtype with
   `dumpsys input_method` (`mCurrentSubtype=…@<hex hash>`).
6. **Control first, then the fix:** stock layout (expect U+0111 U+0110), install, our layout
   (expect U+00F0 U+00D0), per IME.
7. **Tear down:** `adb disconnect`, turn Wireless debugging off.

## Findings (OnePlus 12, OxygenOS 16, API 36, 24.11.2026)

- **OTG is off by default and turns itself off (verified).** With `persist.sys.oplus.otg_support=0`
  the USB-C port stays at `power_role=no-power`, `data_role=no-data`, and a plugged-in keyboard does
  nothing. It had worked two days earlier, so the setting had reverted. On older OxygenOS it turns
  off after about 10 minutes without a device; that timer is **unverified** on 16. The toggle is found
  by searching Settings for "OTG". Users of OnePlus/OPPO phones need this in the setup steps.
- **One USB-C to USB-C cable was not detected at all** (`connected=false` at the Type-C level, with OTG
  on). Another C-to-C cable worked. Why the first failed is **unverified**.
- **OxygenOS has its own Physical keyboard screen.** Path: Settings → search "Physical keyboard" (or
  the app's button) → Physical keyboard (shows "Keyboard: <name>") → **Keyboard layout** → one entry
  per IME: "FUTO Keyboard layout" → "FUTO Keyboard", "Gboard layout" → "Íslenska (Ísland)" → list of
  75 layouts with radio buttons. "Icelandic" and "Icelandic (corrected Ð)" are adjacent. The summary
  on the first screen shows the layout for the **current** IME only.
- **No way back to automatic (verified).** The picker has no "Default" or "Automatic" entry (all 75
  entries read), and `cmd input` has no layout command. A manual choice is permanent for that keyboard
  and IME subtype unless the chosen layout's provider is uninstalled (`layout-selection.md`). Only
  affects testing.
- **FUTO Keyboard (Play Store build) exposes one subtype with no locale and no language tag**
  (`ime list -a`: empty `mSubtypeLocale`, hash `1095049501`). FUTO switches languages internally.
  OxygenOS lists it as "FUTO Keyboard", not as a language, and auto-selection there gives "Default"
  (the generic layout, `[` on the key). D-012 cannot help FUTO users: they must select our layout by
  hand. Manual selection under it works.
- **Neither keyboard reports a language** (`KeyboardLayoutInfo: <not set>` for the Preonic and the
  Ducky), so the IME step decides the automatic layout.
- **Gboard is a layout provider here too** (`com.google.android.apps.inputmethod.libs.kcm.InputDeviceReceiver`).
  Whether it ships an Icelandic layout is still **unverified**: the scan of the picker dropped
  duplicate labels, so a second entry also labeled "Icelandic" would not have shown.
- **In-app result shows only the latest key press.** Fine for users; testers must read it after each
  keyboard.
- **App bug:** the `ScrollView` root gets the system bar insets as padding with
  `clipToPadding="false"`, so scrolled text draws under the status bar and collides with the clock.
  The emulator check on 24.11.2026 only looked at the unscrolled screen.
