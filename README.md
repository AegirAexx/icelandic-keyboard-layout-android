# Icelandic Keyboard Layout Fix for Android

> **Status:** early development. No release yet.

A tiny Android app that fixes the built-in **Icelandic physical keyboard layout**. On stock Android, the
key right of `P` on a USB or Bluetooth keyboard types the Croatian/Vietnamese **Đ/đ** (U+0110/U+0111)
instead of the Icelandic **Ð/ð** (U+00D0/U+00F0).

| | Expected | Stock Android |
|---|---|---|
| Lowercase | `ð` U+00F0 | `đ` U+0111 (visibly wrong) |
| Uppercase | `Ð` U+00D0 | `Đ` U+0110 (looks identical, but is a different letter) |

On-screen keyboards (Gboard, Samsung Keyboard, FUTO, …) are **not** affected. Only physical keyboards
using Android's Icelandic layout are.

## Why it happens

The layout file in the Android Open Source Project,
[`keyboard_layout_icelandic.kcm`](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm),
maps that key to the wrong Unicode characters, and has done so since 2012. See
[docs/research/root-cause.md](docs/research/root-cause.md).

## How the fix works

The app adds a corrected copy of the Icelandic layout through Android's standard mechanism for
third-party keyboard layouts. It needs:

- no root
- no special permissions
- no replacement of your on-screen keyboard

After installing it, choose the corrected layout in your device's physical keyboard settings.

## Installation

_Coming soon._ Planned channels: GitHub Releases (APK), F-Droid, and possibly Google Play.

## Usage

Open the app. The **Set up** tab explains the setup and opens the physical keyboard settings. The
**Test** tab checks the key right of `P` and shows whether it types `ð` (fixed) or `đ` (stock layout).

If your on-screen keyboard is Gboard with Icelandic enabled, Android 14 and later usually pick the
corrected layout automatically for a keyboard you have not set up before (verified on Android 16 and
17). It does not replace a layout you already picked by hand, for example if you chose the built-in
"Icelandic" earlier to try to fix the ð. Keyboards that report their own language may get a different
one. Check with the **Test** tab, and if it is not right, select **Icelandic (corrected Ð)** by hand.

### OnePlus (OxygenOS 16)

Verified on a OnePlus 12, Android 16.

1. Turn on OTG: open Settings, search for "OTG", and switch it on. OxygenOS may switch it off again
   after a while without a device.
2. Connect the keyboard.
3. In the app, tap **Open keyboard settings** (or Settings → search "Physical keyboard").
4. Tap **Keyboard layout**. It lists your on-screen keyboards, each with its entries: under "Gboard
   layout" one per language (e.g. "Íslenska (Ísland)"), under "FUTO Keyboard layout" a single
   "FUTO Keyboard".
5. Open each entry you use and select **Icelandic (corrected Ð)**.

### Honor (MagicOS 10)

Verified on an Honor MagicPad 2, Android 16, with USB and Bluetooth keyboards.

1. Connect the keyboard. With a USB keyboard, MagicOS may ask whether to enable PC mode. You can
   cancel.
2. In the app, tap **Open keyboard settings**. By hand: Settings → System & updates → at the bottom,
   under "Looking for other settings?", **Physical keyboard**. That link only shows while a keyboard is
   connected.
3. Tap your keyboard. It is listed by its name, with something like "Gboard, Icelandic" underneath.
   The next screen lists one entry per language of your on-screen keyboard.
4. Open each language you use and select **Icelandic (corrected Ð)**.

Other manufacturers and Android versions will be added as they are tested.

## Building from source

Requires the Android SDK (API 37) and JDK 17 or newer. Android Studio ships both.

```sh
./gradlew assembleDebug     # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # unsigned; signing is not set up yet
```

On Windows, use `gradlew.bat`. Point Gradle at the SDK with `ANDROID_HOME` or `sdk.dir` in
`local.properties`.

## Upstream

The real fix belongs in Android itself. Progress on reporting and patching it is tracked in
[docs/research/upstream.md](docs/research/upstream.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[Apache License 2.0](LICENSE). The keyboard layout is derived from the Android Open Source Project.
See [NOTICE](NOTICE).
