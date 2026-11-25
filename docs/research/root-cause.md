# Root cause: AOSP Icelandic KCM maps Đ/đ instead of Ð/ð

**Status:** verified 24.11.2026 by reading the raw file from the AOSP GitHub mirror (`main` branch).
Re-read 24.11.2026 from googlesource (`?format=TEXT`): byte-identical to the mirror, sha256
`09314f2c7db3900ddb36f90164eba10d185678f9f73b41f2bb81b53bdda3c8d9`, 362 lines.
**Reproduced** 24.11.2026 on an Android 17 (API 37) emulator: the stock "Icelandic" layout types
U+0111 / U+0110 (see `testing.md`).

## Source

- File: `frameworks/base/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`
- Read from: https://raw.githubusercontent.com/aosp-mirror/platform_frameworks_base/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
- Canonical: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
- License: Apache 2.0, "Copyright (C) 2012 The Android Open Source Project". The bug has likely been
  there since the file was added in 2012 (Android 4.1). Confirm with `git log` / blame on that path (**unverified**: googlesource log pages now require sign-in;
  a local clone is needed).

## The faulty block (verbatim, lines 186 to 191)

The file writes non-ASCII characters as `\uXXXX` escapes. (An earlier version of this doc showed the
block with literal characters. That was a rendering, not the source.)

```
key LEFT_BRACKET {
    label:                              '\u0110'
    base:                               '\u0111'
    shift, capslock:                    '\u0110'
    shift+capslock:                     '\u0111'
}
```

## The fix

```
key LEFT_BRACKET {
    label:                              '\u00d0'
    base:                               '\u00f0'
    shift, capslock:                    '\u00d0'
    shift+capslock:                     '\u00f0'
}
```

Lowercase hex matches the file's existing style (e.g. `'\u00c6'` for Æ). The full diff of this
project's copy is in `kcm-diff.md`.

## Unicode reference

| Char | Code point | Name | Used in |
|---|---|---|---|
| Ð | U+00D0 | LATIN CAPITAL LETTER ETH | Icelandic, Faroese, Old English |
| ð | U+00F0 | LATIN SMALL LETTER ETH | Icelandic, Faroese, Old English |
| Đ | U+0110 | LATIN CAPITAL LETTER D WITH STROKE | Croatian, Vietnamese, Sámi |
| đ | U+0111 | LATIN SMALL LETTER D WITH STROKE | Croatian, Vietnamese, Sámi |

There is also U+0189 (Ɖ, African D), a third look-alike capital. It is irrelevant here but worth knowing
when testing by eye. **Always verify code points, not glyphs.**

## Layout registration (verbatim, `res/xml/keyboard_layouts.xml`)

```xml
<keyboard-layout
    android:name="keyboard_layout_icelandic"
    android:label="@string/keyboard_layout_icelandic"
    android:keyboardLayout="@raw/keyboard_layout_icelandic"
    android:keyboardLocale="is-Latn"
    android:keyboardLayoutType="qwerty" />
```

## Rest of the file (spot-checked, not fully audited)

- Correct: `SEMICOLON` → Æ/æ, `SLASH` → Þ/þ, `EQUALS` → Ö/ö.
- `APOSTROPHE`: label `\u00b4` (acute accent), base `\u0301` (combining acute, which acts as a dead
  key, giving á é í ó ú ý), shift `\'`, ralt `^`.
- `GRAVE`: label `\u02da` (ring above, spacing), base `\u030a` (combining ring above), shift `\u0308`
  (combining diaeresis).
- `RIGHT_BRACKET` → `'` / `?`, `BACKSLASH` → `+` / `*`.
- Compared key-by-key against Windows (Microsoft's KBDIC) and xkeyboard-config on 25.11.2026: no second
  Ð/ð-style error, three AltGr differences left as they are. See `kcm-audit.md`. Not compared: ÍST 125
  (the standard) and Apple's macOS layout.
