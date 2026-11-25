# KCM audit (former R-8)

Key-by-key comparison of the Icelandic layout in `app/src/main/res/raw/keyboard_layout_icelandic.kcm`
(AOSP's file plus the Ð/ð fix, `kcm-diff.md`) against other Icelandic layouts. Done 25.11.2026 by
reading, not by typing on a device. On-device tests (`testing.md`) typed `x`, the key right of `P`
(plain and Shift) and `y`.

**Result:** no second error of the Ð/ð kind. Against Microsoft's Windows layout, every key matches by
position in its plain, Shift and Caps Lock states. Three AltGr differences exist (D1 to D3), none is
clearly an error, and nothing was changed. The upstream patch stays limited to the Ð/ð fix. This rests
on Windows and Linux layouts only: the standard itself (ÍST 125) and macOS were not read, and
chasing them is out of scope (D-016).

## Sources and their limits

| Source | Used for | Status |
|---|---|---|
| AOSP `main`, commit `1cdfff555f4a21f71ccc978290e2e212e2f8b168` (`android.googlesource.com`, read 25.11.2026) | the layout under audit | **Verified.** A second reader diffed the whole file against ours: only the header comment and the four `LEFT_BRACKET` lines differ. |
| **Windows KBDIC.DLL**, as shown by https://kbdlayout.info/kbdic/ (versions 10.0.17134.1 to 10.0.25393.1, read 25.11.2026; pages `virtualkeys` and `shiftstates`) | **Windows** reference: scan code to virtual key, characters per shift state, which keys are dead | **Verified as read.** It is Microsoft's own layout, shown by a third-party site that extracts it. Pages list scan codes, so key positions are real. |
| xkeyboard-config `symbols/is`, variants `basic` and `mac` (NetBSD mirror `ftp.riken.jp`, read 25.11.2026) | **Linux** and Apple-keyboard approximation | **Verified as read.** The mirror's version and date were not recorded and may be older than current. `basic` only overrides keys; the rest comes from `latin(type4)`, which I did not read. It adds Linux-only AltGr symbols (², ¼, ¤, „ and others). |
| ÍST 125 (the Icelandic standard) | the authoritative layout | **Not read, out of scope (D-016).** ÍST 125:2015 is listed in Staðlaráð Íslands' web shop for 10.032 kr. (https://www.stadlar.is, read 25.11.2026). Its scope covers the character arrangement on the 48 alphanumeric keys (ISO/IEC 9995-1 and -2); whether it covers AltGr was not seen. Terms on quoting it were not checked. |
| macOS Icelandic | the third common layout | **Not compared.** No primary source. The `mac` variant above is a 2013-era approximation by xkeyboard-config, not Apple's file. |
| ReactOS `kbdic.c` | (first draft only) | **Dropped as a basis.** Its scan code table is US-style, so it puts `ð` and `ö` on the wrong physical keys. Matching by character, as the first draft did, could not catch a position error. Don't use it for positions. |

### How AOSP key names tie to scan codes (verified 25.11.2026, except where noted)

- **Linux event codes** (https://github.com/torvalds/linux/blob/master/include/uapi/linux/input-event-codes.h,
  `master` read 25.11.2026): `KEY_MINUS` 12, `KEY_EQUAL` 13, `KEY_LEFTBRACE` 26, `KEY_RIGHTBRACE` 27, `KEY_SEMICOLON` 39,
  `KEY_APOSTROPHE` 40, `KEY_GRAVE` 41, `KEY_BACKSLASH` 43, `KEY_COMMA` 51, `KEY_DOT` 52,
  `KEY_SLASH` 53, `KEY_102ND` 86. These are the numbers in the SC column above, read as decimal
  (0C = 12, 1A = 26, 27 = 39, 28 = 40, 29 = 41, 2B = 43, 35 = 53, 56 = 86). The equality of Linux
  event codes and Windows set 1 scan codes for these keys is the long-standing PC/AT convention
  (Linux event codes below 0x59 reproduce the set 1 codes). **Not re-verified here**, and not
  independent of the table: the SC column was matched to AOSP names through these numbers.
- **`Generic.kl`** (`platform/frameworks/base`, `data/keyboards/Generic.kl` at commit `1cdfff55…`)
  maps: 12 to `MINUS`, 13 to `EQUALS`, 26 `LEFT_BRACKET`, 27 `RIGHT_BRACKET`, 39 `SEMICOLON`,
  40 `APOSTROPHE`, 41 `GRAVE`, 43 `BACKSLASH`, 53 `SLASH`, and **86 to `BACKSLASH`** (the same key
  name as 43).
- **The KCM's `map key` lines override that for this layout.** `KeyCharacterMap::mapKey` consults the
  layout's own scan code table (`mKeysByScanCode`) and returns on a hit, and an overlay's table is merged
  into the base's (`platform/frameworks/native`, `libs/input/KeyCharacterMap.cpp` and
  `services/inputflinger/reader/EventHub.cpp` at commit `4f463a6b1de9198963dc6aff74154a504ba3f8f6`,
  `main`, read 25.11.2026; for example
  https://android.googlesource.com/platform/frameworks/native/+/4f463a6b1de9198963dc6aff74154a504ba3f8f6/libs/input/KeyCharacterMap.cpp).
  `EventHub::mapKey` asks the device's KCM first and only on a miss falls back to the `.kl`. So `map key 12 EQUALS` and `map key 13 MINUS` swap the
  two keys relative to `Generic.kl`, putting `ö` on scan code 12 and `-` on 13, as on Windows. `map key 86
  PLUS` turns the extra ISO key into its own key (`PLUS`) instead of a second `BACKSLASH`, which is why
  `<` has its own entry.

## Comparison

Windows column: scan code (SC), then characters from KBDIC as listed on kbdlayout.info. "Dead" marks a
dead key there.

| AOSP key | SC | AOSP | Windows | Match |
|---|---|---|---|---|
| GRAVE | 29 | dead ring above (U+030A); Shift dead diaeresis (U+0308); AltGr none | dead `°`; Shift dead `¨`; AltGr plain `°` (U+00B0) | base, Shift: yes. AltGr: **no, D1** |
| 1 2 3 4 | 02 to 05 | `1 !`, `2 "`, `3 #`, `4 $` | same | yes |
| 5 | 06 | `5 %`, AltGr `€` | same | yes |
| 6 | 07 | `6 &` | same | yes |
| 7 8 9 0 | 08 to 0B | `/ ( ) =`; AltGr `{ [ ] }` | same | yes |
| EQUALS (`ö`, remapped from 12) | 0C | `ö Ö`, AltGr `\`, Caps Lock swaps | same | yes |
| MINUS (remapped from 13) | 0D | `- _` | same | yes |
| Q | 10 | `q Q`, AltGr `@` | same | yes |
| E | 12 | `e E`, AltGr `€` | same | yes |
| M | 32 | AltGr `µ` (U+00B5) | same | yes |
| other letters | | `a`..`z`, Caps Lock swaps | same | yes |
| LEFT_BRACKET (`ð`) | 1A | `ð Ð` (our fix), Caps Lock swaps | `ð Ð` (U+00F0 / U+00D0), Caps Lock swaps | **yes, after our fix** (stock AOSP: no) |
| RIGHT_BRACKET | 1B | `' ?`, AltGr `~` | same | yes |
| SEMICOLON (`æ`) | 27 | `æ Æ`, Caps Lock swaps | same | yes |
| APOSTROPHE (`´`) | 28 | dead acute (U+0301); Shift `'`; AltGr plain `^` | dead `´`; Shift `'`; AltGr **dead** `^` | base, Shift: yes. AltGr: **D2** |
| BACKSLASH (`+`) | 2B | `+ *`; AltGr plain `` ` `` | `+ *`; AltGr **dead** `` ` `` | base, Shift: yes. AltGr: **D3** |
| PLUS (extra ISO key) | 56 | `< >`, AltGr `\|` | same | yes |
| SLASH (`þ`) | 35 | `þ Þ`, Caps Lock swaps | same | yes |
| COMMA, PERIOD | 33, 34 | `, ;` and `. :` | same | yes |

## Differences

- **D1 · AltGr on the key under Esc.** Windows types a plain `°` (U+00B0). xkeyboard-config `basic`
  types `¬` (U+00AC), its `mac` variant has `°` on the base level. AOSP has nothing. The Linux layout
  disagrees with Windows, so the intended character is unknown without ÍST 125. Left as is.
- **D2 · AltGr on `´` types a plain `^`** in AOSP. Windows has a **dead** circumflex there (verified).
  xkeyboard-config `basic` also makes it dead circumflex, while its `mac` variant types a plain `^`,
  as AOSP does. Left as is.
- **D3 · AltGr on `+` types a plain `` ` ``** in AOSP. Windows has a **dead** grave there (verified).
  xkeyboard-config types a plain grave in both `basic` and `mac`, as AOSP does. Left as is.
- **D4 · Shift on `´` (Linux only).** xkeyboard-config `basic` and `mac` make Shift+`´` a dead acute.
  AOSP and Windows type `'`. Windows is the reference, so no action.

D2 and D3 are the kind of simplification AOSP layouts make (plain characters in place of dead keys on
AltGr), and the references don't agree. Each would be a separate behavior change with its own
decision. CLAUDE.md: the only change from AOSP is the intended fix.

## Not covered

- Dead-key composition (what `´`, `°` and `¨` produce with each letter), and how Android composes dead
  keys. Not read for either platform.
- Ctrl, Alt, Meta and Fn states, number pad, function and navigation keys. Not in this file.
- ISO versus ANSI hardware. The KCM maps scan codes, so a US-ANSI keyboard has no `<` key.
- AltGr `{ [ ] }`, `@`, `€`, `µ` and `\` against xkeyboard-config: they come from `latin(type4)` and the
  `mac` variant, only partly read. They were checked against Windows only.

## Out of scope (D-016)

The standard (ÍST 125:2015, 10.032 kr. at https://www.stadlar.is, listing read 25.11.2026) and Apple's
macOS layout were deliberately not obtained or compared. If a user reports AOSP wrong on GRAVE (D1),
APOSTROPHE (D2) or BACKSLASH (D3), a future check could compare them against the standard, and any
correction would be a separate, documented behavior change.
