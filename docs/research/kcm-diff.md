# KCM diff against upstream

**Status:** verified 24.11.2026. The project's KCM was generated from the upstream file by a script
that asserts each replaced block occurs exactly once. The packaged APK resource is byte-identical to
`app/src/main/res/raw/keyboard_layout_icelandic.kcm` (checked with `cmp` on the release APK).

## Upstream

- Path: `frameworks/base/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`
- Read 24.11.2026 from
  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
  (`?format=TEXT`, base64-decoded)
- sha256 `09314f2c7db3900ddb36f90164eba10d185678f9f73b41f2bb81b53bdda3c8d9`, 362 lines, LF, ASCII only.

To re-check: fetch the file again and run `diff -u upstream.kcm app/src/main/res/raw/keyboard_layout_icelandic.kcm`.
The output must be exactly the diff below.

## Diff (verbatim `diff -u`, file names shortened)

```diff
--- upstream/keyboard_layout_icelandic.kcm
+++ app/src/main/res/raw/keyboard_layout_icelandic.kcm
@@ -15,6 +15,10 @@
 #
 # Icelandic keyboard layout.
 #
+# Modified 2026 by the Icelandic Keyboard Layout Fix for Android project:
+# LEFT_BRACKET maps to eth (U+00D0 / U+00F0) instead of D with stroke
+# (U+0110 / U+0111). No other changes.
+#
 
 type OVERLAY
 
@@ -184,10 +188,10 @@
 }
 
 key LEFT_BRACKET {
-    label:                              'Đ'
-    base:                               'đ'
-    shift, capslock:                    'Đ'
-    shift+capslock:                     'đ'
+    label:                              'Ð'
+    base:                               'ð'
+    shift, capslock:                    'Ð'
+    shift+capslock:                     'ð'
 }
 
 key RIGHT_BRACKET {
```

## Justification per changed line

| Line(s) | Change | Why |
|---|---|---|
| 18 to 21 (added) | Comment: modification notice | Apache-2.0 §4(b): "You must cause any modified files to carry prominent notices stating that You changed the files." Comment only; the KCM parser skips `#` lines. The original copyright header is kept unchanged. |
| `label` | `Đ` → `Ð` | Key cap label shown in the settings preview: Ð (eth), not Đ. |
| `base` | `đ` → `ð` | Unshifted key types ð. This is the visible bug. |
| `shift, capslock` | `Đ` → `Ð` | Shift or Caps Lock types Ð. |
| `shift+capslock` | `đ` → `ð` | Shift with Caps Lock on types ð, mirroring upstream's lowercase-on-both rule. |

No other line differs. Behavior outside `LEFT_BRACKET` is identical to AOSP.

## Layout registration

`app/src/main/res/xml/keyboard_layouts.xml` copies AOSP's Icelandic entry: same `android:name`,
`keyboardLocale="is-Latn"` and `keyboardLayoutType="qwerty"`. Only the label differs ("Icelandic
(corrected Ð)", Icelandic "Íslenskt (leiðrétt Ð)") so users can tell it apart from the stock entry, which
stays in the list. The `name` only has to be unique within this app's receiver: the system identifies a
layout by package, receiver and name together (**unverified** from source; consistent with the emulator
showing both entries).
