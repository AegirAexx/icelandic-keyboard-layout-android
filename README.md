# Icelandic Keyboard Layout Fix for Android

> **Status:** works on everything tested so far: two real devices (Android 16) and emulators from
> Android 4.1 to 17. Not released yet. See [Status](#status).

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
maps that key to the wrong Unicode characters, probably since the file was added in 2012. See
[docs/research/root-cause.md](docs/research/root-cause.md).

## How the fix works

The app adds a corrected copy of the Icelandic layout, **Icelandic (corrected Ð)**, through Android's
standard mechanism for third-party keyboard layouts. It needs:

- no root
- no special permissions
- no replacement of your on-screen keyboard

Every other key works exactly as in Android's own Icelandic layout.

On Android 14 and later, with Gboard and Icelandic among its languages, Android usually picks the
corrected layout by itself, unless you already picked a layout for that keyboard by hand (tested on
Android 15, 16 and 17; Android 14 is not verified). Otherwise you select it once in the physical
keyboard settings. The app walks you through it and has a test that shows whether the fix is active.

## Status

Tested with the debug build:

| Device | Android | Keyboards | Result |
|---|---|---|---|
| OnePlus 12, OxygenOS 16 | 16 | USB | Works. Picked automatically under Gboard Icelandic; selected by hand under FUTO. |
| Honor MagicPad 2, MagicOS 10 | 16 | USB, Bluetooth | Works. Picked automatically for a new USB keyboard; selected by hand for a Bluetooth keyboard that had been set up before. |
| Emulator | 17 | built-in | Works. Picked automatically under Gboard Icelandic. |
| Emulator | 15 | built-in | Works. Picked automatically under Gboard Icelandic. |
| Emulator | 14 | built-in | Works when selected by hand. Automatic selection could not be tested (the emulator's Gboard does not register an Icelandic subtype with Android, so there is nothing to match). |
| Emulators | 13, 10, 5.1, 4.4, 4.1 | built-in | Works when selected by hand (see [Android 13 and older](#android-13-and-older)). Android has no automatic choice there. |

Not tested yet: automatic selection on Android 14, Samsung and other manufacturers, automatic
selection with a Bluetooth keyboard, and MagicOS PC mode. Device reports are very welcome, see
[Contributing](#contributing).

Not done yet: a signed release, F-Droid and Google Play, and reporting the bug to Android itself.
Details per device are in [docs/research/testing.md](docs/research/testing.md).

## Installation

There is no release yet. Planned channels: GitHub Releases (APK), then F-Droid and possibly Google
Play if no equivalent app exists.
Until then, you can [build it from source](#building-from-source).

## Usage

Open the app. The **Set up** tab explains the setup and opens the physical keyboard settings. The
**Test** tab checks the key right of `P` and shows whether it types `ð` (fixed) or `đ` (stock layout).

On Android 14 and later, first add Icelandic to the languages of your on-screen keyboard (for example
in Gboard's settings). Without it, there is no Icelandic entry to select the layout under, and Android
can't pick it automatically. Android 13 and older have no per-language entries: see
[Android 13 and older](#android-13-and-older).

On Android 14 and later, the layout is stored **per keyboard and per language** of your on-screen
keyboard. If you use more than one language (for example Icelandic and English in Gboard), select the
corrected layout under each one you type Icelandic in. Gboard can switch to Icelandic by itself in apps
set to Icelandic, so select it under Icelandic even if you mostly type in English.

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

### Android 13 and older

Verified on emulators running Android 13, 10, 5.1, 4.4 and 4.1, with the stock Android settings. The
versions in between are expected to behave the same but were not run. Phones from other manufacturers
may word the menus differently.

1. Connect the keyboard.
2. In the app, tap **Open keyboard settings**. On Android 10 and 13 it opens the "Physical keyboard"
   screen. On Android 5.1 and older it opens "Language & input", where "Physical keyboard" is a section
   further down.
3. Tap your keyboard, then **Set up keyboard layouts**.
4. Switch on **Icelandic (corrected Ð)** and switch **off** the built-in **Icelandic**. If both are on,
   Android keeps using the built-in one and the key still types `đ`.

Other manufacturers will be added as they are tested.

## Known limitations

- **A layout you picked earlier stays.** If you chose the built-in "Icelandic" before, for example to
  try to fix the ð, Android keeps that choice. Select **Icelandic (corrected Ð)** by hand.
- **Automatic selection needs Android 14+ and an Icelandic language in the on-screen keyboard.**
  Verified with Gboard on Android 15, 16 and 17. Not verified on Android 14: the Gboard that the
  emulator ships does not register an Icelandic subtype with Android. Keyboards that switch languages
  internally, such as FUTO, show up by name instead of a language, and the layout must be selected by
  hand. Samsung Keyboard and SwiftKey are untested.
- **On Android 13 and older the built-in "Icelandic" must be switched off.** Android has no automatic
  choice there. With both layouts on, the built-in one stays active (see
  [Android 13 and older](#android-13-and-older)).
- **Keyboards that report their own language** may get a different layout automatically. None of the
  real keyboards tested did.
- **No "back to automatic" option** on the OnePlus tested (OxygenOS 16). Once a layout is picked by
  hand for a keyboard, it can only be changed to another layout.

## Building from source

Requires the Android SDK (API 37) and JDK 17 or newer. Android Studio provides both.

```sh
./gradlew assembleDebug     # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease   # unsigned; signing is not set up yet
```

On Windows, use `gradlew.bat`. Point Gradle at the SDK with `ANDROID_HOME` or `sdk.dir` in
`local.properties`.

## Upstream

The real fix belongs in Android itself: a four-line change in one file. Reporting it on Google's Issue
Tracker and submitting the patch to AOSP has not started yet. The plan is in
[docs/research/upstream.md](docs/research/upstream.md).

## Contributing

Reports from real devices help most, especially from Samsung and other manufacturers and from older
Android versions. See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[Apache License 2.0](LICENSE). The keyboard layout is derived from the Android Open Source Project.
See [NOTICE](NOTICE).
