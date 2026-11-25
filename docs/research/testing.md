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
| 25.11.2026 | OnePlus 12, Ducky (FUTO, "Default"); API 37 emulator | 16 (API 36); 17 (API 37) | debug, tabbed UI (D-013) | **Pass.** No text under the status bar when scrolled; bottom padding present. Returning from the settings button opens "Test". Pressing the key on "Set up" switches to "Test" and shows `[` U+005B. The selected tab survives recreation (app locale change). IS labels fit. Light theme checked on the emulator, dark on the phone. Lint clean. |
| 25.11.2026 | Honor MagicPad 2 (ROD2-W09), MagicOS 10.0.0.137 (C431E3R3P1); Gboard 18.6.3 with Íslenska `is-IS` (active), English `en-US`, Español `es-AR`; Ducky (`04d9:0295`), never connected before | 16 (API 36) | none (stock only) | **Bug reproduced.** Auto-selection gave "Gboard, Icelandic" (stock): `x`, key right of P, Shift + key, `y` typed U+0078 U+0111 U+0110 U+0079. |
| 25.11.2026 | Same tablet and Ducky, still connected | 16 (API 36) | debug, v0.1.0 at `ffaccea` | **Pass (D-012 on a second device).** Right after install, with no manual step and the keyboard still plugged in, the summary changed to "Gboard, Icelandic (corrected Ð)" and the key typed U+00F0 U+00D0. Installed over adb with no on-device prompt. |
| 25.11.2026 | Same tablet; Qulose Keyboard (Bluetooth LE), stock "Icelandic" under Gboard English (US) and Íslenska, by the user's account an earlier manual choice | 16 (API 36) | debug, v0.1.0 at `ffaccea` | **Pass after manual selection.** After install the summary stayed "Gboard, Icelandic", consistent with a manual choice blocking auto-selection. With "Icelandic (corrected Ð)" picked by hand under English (US) and Íslenska: U+0078 U+00F0 U+00D0 U+0079. First Bluetooth keyboard tested (manual selection only). |

Emulator runs for R-3 (25.11.2026). All use the debug build from `main` at `dd17aec`, the emulator's
built-in keyboard device `qwerty2` (hardware keyboard, typed with `adb emu event text`), headless, one at
a time, from a fresh AVD each (`google_apis_playstore` x86_64 images, `google_apis` for API 22, and
`google_apis` x86 for API 19 and 16). Details and findings: "Findings (emulators, API 16–35)" below.

| Date | Android | Image / Gboard | Result |
|---|---|---|---|
| 25.11.2026 | 15 (API 35) | `AE3A.240806.036`, Gboard 14.2 preload | **Pass, including auto-selection (D-012).** Icelandic added in Gboard's languages gives an `is-IS` subtype. Before install: "Automatic: Icelandic", `x[{y` types U+0111 U+0110. After `adb install`, no manual choice, app never launched: Íslenska (Ísland) shows "Automatic: Icelandic (corrected Ð)" and the keys type U+00F0 U+00D0; English (US) stays "Automatic: English (US)". App: both tabs, "Correct" on the Test tab, settings button opens `Settings$PhysicalKeyboardActivity`, edge-to-edge in portrait and landscape, dark, app locale is-IS ("Uppsetning" / "Prófun"). |
| 25.11.2026 | 14 (API 34) | `UE1A.230829.036.A4`, Gboard 12.4 preload | **Pass by manual selection; auto-selection not testable on this image.** Stock "Icelandic": U+0111 U+0110. "Icelandic (corrected Ð)" picked under "Multilingual typing": U+00F0 U+00D0. This Gboard registers no IME subtype for Icelandic (see findings), so there is no `is-IS` subtype to test D-012 against. Receiver listed right after install. App UI pass not run. |
| 25.11.2026 | 13 (API 33) | `TE1A.240213.009`, Gboard 12.4 preload | **Pass (manual).** Stock U+0111 U+0110; stock and ours both enabled: still stock; ours only: U+00F0 U+00D0. App pass (tabs, Test tab, settings button, app locale is-IS) OK; Set up tab screenshot read. Dark mode toggled, screenshot not read. |
| 25.11.2026 | 10 (API 29) | `QSR1.211112.011`, Gboard 8.0 preload | **Pass (manual).** Same as API 33. Pre-API 30 insets path: opaque status bar, nothing under it. Dark mode not exercised. |
| 25.11.2026 | 5.1.1 (API 22) | `LMY48X` (`google_apis` x86_64) | **Pass (manual).** Test tab: stock đ/Đ, both enabled still đ/Đ, ours only ð/Ð. `ACTION_HARD_KEYBOARD_SETTINGS` does not resolve; the button falls back to "Language & input" without a crash. Pre-API 23 `colorAccent` branch renders. |
| 25.11.2026 | 4.4.2 (API 19) | `KK 6761301` (`google_apis` x86) | **Pass (manual).** Same as API 22. Launches with no errors; pre-API 21 plain text color, black tab indicator. |
| 25.11.2026 | 4.1.2 (API 16) | `eng.wdu.20191213.040452` (`google_apis` x86) | **Pass (manual).** Same as API 22. `minSdk 16` holds. |

Not yet tested: auto-selection on Android 14 (API 34; the emulator's Gboard has no Icelandic subtype),
auto-selection with a Bluetooth keyboard, MagicOS PC mode, Samsung One UI and other OEMs. API 16–33
were tested on emulators by manual selection only; per-IME-subtype layouts exist from API 34.

## Findings (emulator, API 37, 24.11.2026)

- **Discovery works without the app ever being launched.** At the time the app had no activity (D-011 added one later). After `adb install`,
  `cmd package query-receivers -a android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS` listed three
  providers: `com.android.inputdevices/.InputDeviceReceiver`, Gboard's
  `com.google.android.apps.inputmethod.libs.kcm.InputDeviceReceiver`, and ours. So the "stopped" state
  after install does not hide the receiver (verified on API 37 only).
- **Gboard is itself a keyboard layout provider.** Whether it ships an Icelandic layout was
  **unverified** here. On the Honor tablet (Gboard 18.6.3, 25.11.2026) it does not add one: see the
  Honor findings below.
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
3. **Plug in the keyboard.** Check it in `dumpsys usb` (host devices, USB only) and `dumpsys input` (the
   `KeyboardType: 2` entry; `KeyboardLayoutInfo: <not set>` means it reports no language of its own).
4. **Check for earlier manual choices.** AOSP and OxygenOS show "User selected: …" for them. MagicOS
   shows no prefix: ask the device's owner. As an inference only, under Gboard's `is-IS` subtype with
   the D-012 build, a keyboard that keeps stock "Icelandic" after install was most likely set by hand.
   Manual choices override auto-selection and cannot be reset (see findings), so test auto-selection
   only with a keyboard the device has never seen.
5. **Type test.** Open a text field (`am start -a com.android.settings.action.SETTINGS_SEARCH`; to
   clear it, `am force-stop com.android.settings.intelligence` and start it again). MagicOS has no
   such intent: use Honor search, `am start -n
   com.hihonor.search/.presentation.ui.activity.AliasActivity` (force-stop `com.hihonor.search` to
   clear it; the dumped text ends with the field's own "Search online"). The tester types
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
  by searching Settings for "OTG". OnePlus users need this in the setup steps. OPPO phones share the ColorOS base and the
  `persist.sys.oplus.*` setting, so they likely need it too (**unverified**).
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
  Whether it ships an Icelandic layout is **unverified on this phone**: the scan of the picker dropped
  duplicate labels, so a second entry also labeled "Icelandic" would not have shown. Answered for
  Gboard 18.6.3 on the Honor tablet (no second entry, see below).
- **In-app result shows only the latest key press.** Fine for users; testers must read it after each
  keyboard.
- **App bug:** the `ScrollView` root gets the system bar insets as padding with
  `clipToPadding="false"`, so scrolled text draws under the status bar and collides with the clock.
  The emulator check on 24.11.2026 only looked at the unscrolled screen. Fixed in D-013.

## Findings (Honor MagicPad 2, MagicOS 10, API 36, 25.11.2026)

- **Stock AOSP settings screens.** Unlike OxygenOS, MagicOS uses the AOSP Physical keyboard screens:
  Physical keyboard → entry titled with the keyboard's name, summary "Gboard, <layout>" → a screen
  with the heading "Gboard layout" (not a separate tap) and one entry per enabled subtype, summary
  "Keyboard layout: <name>" → "Keyboard layouts" list with radio buttons. The
  summaries carry no "Automatic:" / "User selected:" prefix.
- **Path by hand (reported by the user):** Settings → System & updates → "Looking for other
  settings?" (at the bottom) → **Physical keyboard**. The link shows only while a physical keyboard is
  connected; it was not found under Languages & input. The app's button opens the screen directly.
- **"Enable PC mode" prompt.** Plugging in the USB keyboard asks whether to enable PC mode. Cancel
  works; the test ran in normal mode. PC mode itself was not tested.
- **OTG needs no toggle (verified).** The USB keyboard worked at once (`data_role=host`).
- **Install re-runs auto-selection for a connected keyboard (verified).** The Ducky was plugged in
  and auto-set to stock "Icelandic" before install. Installing the app changed the set of layouts,
  which clears the auto-selection cache (`layout-selection.md`), and the summary switched to ours
  without reconnecting the keyboard.
- **A keyboard set by hand keeps the stock layout after install.** Verified: the Qulose keyboard
  kept stock "Icelandic" after install under both English (US) and Íslenska, and picking ours by hand
  fixed it. The cause is **user-reported**: the tablet's owner had tried to change the layout to fix
  the ð. It fits the source (a stored user selection is read before auto-selection,
  `layout-selection.md`), and English (US) → Icelandic is not a plausible automatic result for a
  keyboard with no language tag (inference: Gboard 18.6.3's subtypes were not checked for a
  `physicalKeyboardHint`). Users who tried to fix the ð before will need to select the layout by hand.
- **Gboard 18.6.3 adds no second Icelandic layout (verified).** The picker under Íslenska lists 82
  layouts. Scanned with slow scrolls where every pair of consecutive screens overlapped (so nothing
  was skipped and duplicate labels would show at distinct positions): "Icelandic" appears once, next
  to "Icelandic (corrected Ð)", and no other of the 82 labels contains "Iceland" or "Ísl". Providers:
  AOSP, Gboard, ours; Honor ships none.
- **The keyboards report no language** (`KeyboardLayoutInfo: <not set>` for the Ducky and the Qulose).
- **The generic Settings intent opens Wi-Fi.** `am start -a android.settings.SETTINGS` landed on
  `WifiSettingsActivity` (two-pane layout), and the wireless adb connection dropped shortly after
  (cause **unverified**; `adb connect <ip>:<port>` restored it).

## Findings (emulators, API 16–35, 25.11.2026)

All **verified** on the emulators listed in the results table unless marked otherwise.

- **The receiver is registered right after `adb install`, with the app never launched, on every API
  from 16 to 35** (`cmd package query-receivers` on API 24+, `dumpsys package` on older images).
- **Gboard's Icelandic support differs by image.** The preload on API 35 (14.2) creates an `is-IS` IME
  subtype when Icelandic is added under Languages → ADD KEYBOARD, so Settings gets one entry per
  subtype and D-012 works as on the real devices. The preload on API 34 (12.4) does not: after adding
  Icelandic, `ime list -a -s` shows no subtypes, `enabled_input_methods` carries no subtype hash, and
  Settings shows a single "Multilingual typing" entry with the summary "Default", also after installing
  the app. So D-012 could not be tested on Android 14. Whether a current Gboard on Android 14 behaves
  like API 35's is **unverified**. Switching the active subtype without the UI:
  `settings put secure selected_input_method_subtype <hash>` (the hash is in `enabled_input_methods`).
- **Before Android 14 (API 16–33) layouts are enabled, not chosen per language.** Path: Physical
  keyboard → the keyboard (`qwerty2`) → a dialog "Choose keyboard layout" listing the enabled layouts
  ("Default" at first) → **"Set up keyboard layouts"** → a checklist of all layouts. Ours appears as
  "Icelandic (corrected Ð)" with "Icelandic Keyboard Layout" (the app name) underneath; the built-in one
  as "Icelandic" / "Android keyboard". The dialog says "To switch, press Control-Spacebar".
- **Pre-14, enabling ours next to the built-in "Icelandic" does not fix the key.** With both enabled the
  built-in one stayed active (đ/Đ). Only with the built-in one switched off did the key type ð/Ð.
  Whether Control-Spacebar switches between them was **not tested** (`adb` can't send it easily). The
  app's setup text (step 4 "choose a language", step 5 "Select ...") does not describe this; see R-4.
- **`ACTION_HARD_KEYBOARD_SETTINGS` does not resolve on API 22, 19 and 16** (expected below API 24). The
  settings button falls back to `ACTION_INPUT_METHOD_SETTINGS`: "Language & input" with a "Physical
  keyboard" section that lists the keyboard, which fits the setup note. No crash.
- **UI branches** (screenshots read): API 29 and older draw the opaque system status bar and start
  the content below it, nothing under it when scrolled; the tab indicator is the theme accent on API
  22–33 and black on API 19 and 16 (no `colorAccent` below API 21, as D-013 describes); API 22 and
  older show the button text in capitals (platform style). Edge-to-edge and light/dark status icons
  were checked on API 35 only. Dark mode was looked at on API 35 only (on API 33 it was toggled but the
  screenshot was not read; on API 29 `cmd uimode night yes` had no effect; not tried below).
- **Oracles.** API 29 and newer: the typed text read from `uiautomator dump` (Settings search field)
  and the app's Test tab agree. API 22 and older: only the Test tab (no search-field intent; the stock
  Settings has none).

### Emulator procedure notes (for repeating the runs)

- `sdkmanager` and `avdmanager` are `.bat` files: quote package ids that contain `;` (`"system-images;
  android-34;google_apis_playstore;x86_64"`), or `cmd.exe` splits them. Set `JAVA_HOME` and
  `ANDROID_HOME` in the same command.
- Boot headless: `emulator -avd <name> -no-window -no-snapshot -no-audio -gpu swiftshader_indirect`.
  Check the AVD has `hw.keyboard=yes`. Shut down with `adb emu kill`, then confirm no `emulator` or
  `qemu-system*` process is left.
- On older images, write scratch files under `/data/local/tmp` (`/sdcard` is not writable on API 22),
  and call `input swipe` with four arguments (it takes no duration argument on API 16: the extra
  argument fails silently and the list does not scroll). `monkey` prints about 1 MB; start the app with
  `am start -n io.github.aegiraexx.icelandickeyboard/.MainActivity` instead.
- Each new manual layout choice on API 34+ is permanent for that keyboard and subtype, so use a fresh
  AVD for any automatic-selection test. Before API 34 the choice is a toggle and can be undone.
