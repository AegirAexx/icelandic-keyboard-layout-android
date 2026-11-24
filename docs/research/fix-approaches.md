# Fix approaches

Ways to get correct Ð/ð from a physical keyboard on unmodified (non-rooted) Android devices.

## 1. Keyboard layout provider APK (chosen, D-001)

Android lets any installed app contribute physical keyboard layouts. `InputManagerService` queries
receivers for `android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS` and reads their
`android.hardware.input.metadata.KEYBOARD_LAYOUTS` metadata. That metadata points to an XML resource
listing `.kcm` files. AOSP's own layouts ship this way (the `InputDevices` package).

Shape (as built in `app/src/main/AndroidManifest.xml`; verified on an API 37 emulator, see `testing.md`):

```xml
<!-- AndroidManifest.xml -->
<receiver android:name=".LayoutsReceiver" android:exported="true"
          android:label="@string/keyboard_layouts_label">
    <intent-filter>
        <action android:name="android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS" />
    </intent-filter>
    <meta-data android:name="android.hardware.input.metadata.KEYBOARD_LAYOUTS"
               android:resource="@xml/keyboard_layouts" />
</receiver>
```

The receiver class can be empty. The system reads metadata and never sends a broadcast. The "stopped
app" state after install does not matter: the layout was listed without the app ever being launched
(verified on API 37 only, `testing.md`).

- Pros: no root, no IME replacement, no accessibility permission. Works with any soft keyboard app. The
  deliverable is a few KB of data.
- Cons: the user has to select the layout manually per keyboard (and on Android 14+ per IME language).
  OEM settings UIs vary.
- To verify: whether Android 14+ `keyboardLocale` / `keyboardLayoutType` attributes cause auto-selection,
  and which layout wins when two claim `is-Latn`. Also Samsung One UI behavior.
- Prior art (unverified details): ExKeyMo, a web tool that generates custom-layout APKs this way.

## 2. Custom IME that remaps hardware keys

An input method can intercept `onKeyDown` and commit `ð` instead of `đ`.

- Rejected as the primary path: it replaces the user's soft keyboard (Gboard etc.), which is a large
  scope increase and a privacy-sensitive app category.

## 3. Accessibility service key filtering

`FLAG_REQUEST_FILTER_KEY_EVENTS` can see key events, but it cannot cleanly substitute committed text.

- Rejected: hacky, privacy-sensitive permission, fragile.

## 4. Root / system overlay replacing the AOSP KCM

- Rejected: needs root or a custom ROM, so it doesn't fit "sideload onto various devices".

## 5. Upstream fix in AOSP

The real fix. It still takes years to reach devices and never reaches unsupported ones, so the
workaround APK is needed anyway. See `upstream.md`.
