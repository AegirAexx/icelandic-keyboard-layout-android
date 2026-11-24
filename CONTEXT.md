# CONTEXT.md

Stable facts about the project. Volatile state belongs in `HANDOFF.md`.

## Problem

When a physical (USB/Bluetooth) keyboard is attached to an Android device and the physical keyboard
layout is set to **Icelandic**, the key right of `P` produces the wrong letter:

| Expected (Icelandic eth) | Actually produced (D with stroke, "crossed D") |
|---|---|
| `Ð` U+00D0 LATIN CAPITAL LETTER ETH | `Đ` U+0110 LATIN CAPITAL LETTER D WITH STROKE |
| `ð` U+00F0 LATIN SMALL LETTER ETH | `đ` U+0111 LATIN SMALL LETTER D WITH STROKE |

The uppercase forms look identical, so the bug hides there. The lowercase forms look clearly different.
Both cases are wrong at the code point level. That breaks search, sorting, spell checking and any text
processing, even where the glyph looks right.

**Scope (verified by the user, 24.11.2026):** only physical keyboards are affected, both wired and
Bluetooth, on several real keyboards. On-screen keyboards (Gboard, Samsung Keyboard, FUTO) produce the
correct `ð`. They don't use the KCM. That likely explains why the bug has survived since 2012: few
Android users attach physical keyboards.

**Motivating case:** a young Icelandic writer on an Android tablet with a keyboard, put off from writing
by a visibly wrong `ð`. The target users are ordinary people, not developers. Installation and setup
must be simple and well documented.

## Root cause (verified 24.11.2026)

A data error in AOSP, not a device or OEM quirk. The Icelandic key character map
`frameworks/base/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm` maps `LEFT_BRACKET`
to `Đ` / `đ` instead of `Ð` / `ð`. Details and verbatim source are in
`docs/research/root-cause.md`.

## Chosen approach (see `docs/decisions.md`)

1. **Workaround:** a tiny sideloadable APK that ships a corrected Icelandic layout through Android's
   public "keyboard layout provider" mechanism (a `BroadcastReceiver` for
   `android.hardware.input.action.QUERY_KEYBOARD_LAYOUTS` plus a `.kcm` resource). It needs no root,
   replaces no IME, and works with Gboard, Samsung Keyboard and others. The user selects the layout under
   the physical keyboard settings.
2. **Real fix:** report the bug on Google's Issue Tracker and submit a patch to AOSP Gerrit. The patch is
   a four-line change in one file. See `docs/research/upstream.md`.

## Constraints and goals

- Sideload first. Then publish (F-Droid, possibly Google Play) if no equivalent app exists. See
  `docs/research/publishing.md`.
- License: Apache-2.0 (D-003). The repo is not yet pushed to any remote.
- Support as many Android versions as practical. Layout selection UI changed in Android 14, and OEM
  skins (e.g. Samsung One UI) differ, so test on real devices.
- Keep everything else identical to AOSP's Icelandic layout unless a separate, documented bug is found.
- AOSP source is Apache 2.0. Keep the license header and attribution in derived files.

## Glossary

- **KCM**: Key Character Map. A text file mapping Android key codes plus modifiers to characters.
  Layout overlays use `type OVERLAY`.
- **Keyboard layout provider**: an app exposing KCM files to `InputManagerService` through receiver
  metadata `android.hardware.input.metadata.KEYBOARD_LAYOUTS`.
- **Eth (Ð/ð)**: Icelandic/Faroese/Old English letter. **D with stroke (Đ/đ)**: Croatian, Vietnamese,
  Sámi letter. They are different letters that happen to share an uppercase glyph.
