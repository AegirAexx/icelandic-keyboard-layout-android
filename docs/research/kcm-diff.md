# KCM diff against upstream

**Status:** verified 24.11.2026 by `diff -u` against the upstream file. The packaged APK resource is
byte-identical to `app/src/main/res/raw/keyboard_layout_icelandic.kcm` (checked with `cmp` on the release
APK). Both files are ASCII only: characters are written as `\uXXXX` escapes.

## Upstream

- Path: `frameworks/base/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`
- Read 24.11.2026 from
  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm
  (`?format=TEXT`, base64-decoded)
- sha256 `09314f2c7db3900ddb36f90164eba10d185678f9f73b41f2bb81b53bdda3c8d9`, 362 lines, LF, ASCII only.

To re-check, fetch the file again and compare. The output must be exactly the diff below:

```sh
curl -sSL 'https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm?format=TEXT' \
  | base64 -d > upstream.kcm
diff -u --label upstream/keyboard_layout_icelandic.kcm --label app/src/main/res/raw/keyboard_layout_icelandic.kcm \
  upstream.kcm app/src/main/res/raw/keyboard_layout_icelandic.kcm
```

## Diff (verbatim `diff -u` output)

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
-    label:                              '\u0110'
-    base:                               '\u0111'
-    shift, capslock:                    '\u0110'
-    shift+capslock:                     '\u0111'
+    label:                              '\u00d0'
+    base:                               '\u00f0'
+    shift, capslock:                    '\u00d0'
+    shift+capslock:                     '\u00f0'
 }
 
 key RIGHT_BRACKET {
```

## Justification per changed line

| Line(s) | Change | Why |
|---|---|---|
| 18 to 21 (added) | Comment: modification notice | Apache-2.0 §4(b): "You must cause any modified files to carry prominent notices stating that You changed the files." Comment only; the KCM parser skips `#` lines. The original copyright header is kept unchanged. |
| `label` | `\u0110` → `\u00d0` | Key cap label in the settings preview: Ð (eth) instead of Đ (D with stroke). |
| `base` | `\u0111` → `\u00f0` | Unshifted key types ð instead of đ. This is the visible bug. |
| `shift, capslock` | `\u0110` → `\u00d0` | Shift or Caps Lock types Ð instead of Đ. |
| `shift+capslock` | `\u0111` → `\u00f0` | Shift with Caps Lock on types ð, mirroring upstream's lowercase-on-both rule. |

No other line differs. Behavior outside `LEFT_BRACKET` is identical to AOSP.

## Layout registration

`app/src/main/res/xml/keyboard_layouts.xml` copies AOSP's Icelandic entry (same `android:name` and
`keyboardLayoutType="qwerty"`), with two differences:

| Attribute | AOSP | Ours | Why |
|---|---|---|---|
| `android:label` | "Icelandic" | "Icelandic (corrected Ð)", Icelandic "Íslenskt (leiðrétt Ð)" | Users can tell it apart from the stock entry, which stays in the list. |
| `android:keyboardLocale` | `is-Latn` | `is-IS` (D-012) | On API 34+, wins automatic selection over the stock layout for IME subtypes tagged `is-IS` (Gboard). With equal locales the stock layout wins the tie. See `layout-selection.md`. |

The `name` only has to be unique within this app's receiver. The system identifies a layout by package,
receiver and name together: `KeyboardLayoutDescriptor.format(receiver.packageName, receiver.name, name)`
in `KeyboardLayoutManager.visitKeyboardLayoutsInPackage` (**verified**, source read 24.11.2026, see
`layout-selection.md` for the URL).
