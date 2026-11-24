# Testing

How the layout was verified, and how to repeat it. Results are listed per device with dates.

## Results

| Date | Device | Android | Build | Result |
|---|---|---|---|---|
| 24.11.2026 | Emulator, `google_apis_playstore` x86_64 image | 17 (API 37) | debug, v0.1.0 | **Pass.** Layout listed and selectable; `[` / `{` type U+00F0 / U+00D0. Stock "Icelandic" types U+0111 / U+0110 (bug reproduced). |
| 24.11.2026 | Same emulator | 17 (API 37) | debug, v0.1.0 + instructions screen | **Pass.** In-app key test: corrected layout reports U+00F0 / U+00D0; stock layout reports U+0111 / U+0110; a non-Icelandic layout reports U+005B. `adb shell input keyevent KEYCODE_LEFT_BRACKET` (virtual device) is ignored. Button opens `Settings$PhysicalKeyboardActivity`. Insets correct under edge-to-edge; EN/IS and light/dark checked by screenshot; adaptive icon renders in the launcher. |
| 24.11.2026 | Same emulator, Gboard with English (US) + Íslenska (Ísland) | 17 (API 37) | debug, v0.1.0 | **Pass** with the corrected layout selected under both languages (key test U+00F0 with either subtype). Auto-selection under Íslenska picks the stock layout; see `layout-selection.md`. |
| 24.11.2026 | Same emulator, Gboard with English (US) + Íslenska (Ísland) | 17 (API 37) | debug, v0.1.0 + `keyboardLocale="is-IS"` (D-012) | **Pass.** Clean install, no manual selection: AT Translated Set 2 keyboard shows "Automatic: Icelandic (corrected Ð)" under Íslenska and "Automatic: English (US)" under English (US). qwerty2 stays "Automatic: English (US)" under both (hardware language tag, see `layout-selection.md`). Six setup steps render correctly. |

Not yet tested: real devices, Samsung One UI, Android versions below 17 (minSdk is 16, D-010).

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
  stock layout over ours (tie, broken by label), so users must select ours by hand under each
  language. `keyboardLocale="is-IS"` makes ours win for Gboard's `is-IS` subtype (verified on the
  emulator; applied in D-012).

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

Not done yet. A USB keyboard occupies the USB port, so use Wireless debugging (`adb pair`) or a
Bluetooth keyboard. Record the exact settings path per device and OEM, since it feeds the user
instructions (D-001).
