# Root cause: AOSP Icelandic KCM maps Đ/đ instead of Ð/ð

**Status:** verified 24.11.2026 by reading the raw file from the AOSP GitHub mirror (`main` branch).

## Source

- File: `frameworks/base/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`
- Read from: https://raw.githubusercontent.com/aosp-mirror/platform_frameworks_base/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
- Canonical: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
- License: Apache 2.0, "Copyright (C) 2012 The Android Open Source Project". The bug has likely been
  there since the file was added in 2012 (Android 4.1). Confirm with `git log` / blame on that path.

## The faulty block (verbatim)

```
key LEFT_BRACKET {
    label:                              'Đ'
    base:                               'đ'
    shift, capslock:                    'Đ'
    shift+capslock:                     'đ'
}
```

## The fix

```
key LEFT_BRACKET {
    label:                              'Ð'
    base:                               'ð'
    shift, capslock:                    'Ð'
    shift+capslock:                     'ð'
}
```

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
- `APOSTROPHE` base `́` (combining acute, which acts as a dead key, giving á é í ó ú ý), shift `'`.
- `GRAVE` base `̊` (ring above), shift `̈` (diaeresis).
- `RIGHT_BRACKET` → `'` / `?`, `BACKSLASH` → `+` / `*`.
- Not yet compared key-by-key against the Windows/macOS Icelandic layouts. That is an optional follow-up.
