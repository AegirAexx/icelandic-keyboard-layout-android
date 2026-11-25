# KCM audit (R-8)

Key-by-key comparison of the Icelandic layout in `app/src/main/res/raw/keyboard_layout_icelandic.kcm`
(AOSP's file plus the Ð/ð fix, `kcm-diff.md`) against other Icelandic layouts. Done 25.11.2026 by
reading, not by typing on a device. On-device tests (`testing.md`) typed `x`, the key right of `P`
(plain and Shift) and `y`.

**Result:** no second error of the Ð/ð kind. Against Microsoft's Windows layout, every key matches by
position in its plain, Shift and Caps Lock states. Three AltGr differences exist (D1 to D3), none is
clearly an error, and nothing was changed. The upstream patch stays limited to the Ð/ð fix. This rests
on Windows and Linux layouts only: the standard itself (ÍST 125) and macOS were not read.

## Sources and their limits

| Source | Used for | Status |
|---|---|---|
| AOSP `main`, commit `1cdfff555f4a21f71ccc978290e2e212e2f8b168` (`android.googlesource.com`, read 25.11.2026) | the layout under audit | **Verified.** A second reader diffed the whole file against ours: only the header comment and the four `LEFT_BRACKET` lines differ. |
| **Windows KBDIC.DLL**, as shown by https://kbdlayout.info/kbdic/ (versions 10.0.17134.1 to 10.0.25393.1, read 25.11.2026; pages `virtualkeys` and `shiftstates`) | **Windows** reference: scan code to virtual key, characters per shift state, which keys are dead | **Verified as read.** It is Microsoft's own layout, shown by a third-party site that extracts it. Pages list scan codes, so key positions are real. |
| xkeyboard-config `symbols/is`, variants `basic` and `mac` (NetBSD mirror `ftp.riken.jp`, read 25.11.2026) | **Linux** and Apple-keyboard approximation | **Verified as read.** The mirror's version and date were not recorded and may be older than current. `basic` only overrides keys; the rest comes from `latin(type4)`, which I did not read. It adds Linux-only AltGr symbols (², ¼, ¤, „ and others). |
| ÍST 125 (the Icelandic standard) | the authoritative layout | **Not read.** Listed in Staðlaráð's web shop (https://www.stadlar.is, linked from Wikipedia's article); price and terms not checked. |
| macOS Icelandic | the third common layout | **Not compared.** No primary source. The `mac` variant above is a 2013-era approximation by xkeyboard-config, not Apple's file. |
| ReactOS `kbdic.c` | (first draft only) | **Dropped as a basis.** Its scan code table is US-style, so it puts `ð` and `ö` on the wrong physical keys. Matching by character, as the first draft did, could not catch a position error. Don't use it for positions. |

### How AOSP key names tie to scan codes (verified 25.11.2026)

- **Linux event codes** (`include/uapi/linux/input-event-codes.h`, torvalds/linux, read via `gh api`):
  `KEY_MINUS` 12, `KEY_EQUAL` 13, `KEY_LEFTBRACE` 26, `KEY_RIGHTBRACE` 27, `KEY_SEMICOLON` 39,
  `KEY_APOSTROPHE` 40, `KEY_GRAVE` 41, `KEY_BACKSLASH` 43, `KEY_COMMA` 51, `KEY_DOT` 52,
  `KEY_SLASH` 53, `KEY_102ND` 86. These are the numbers in the SC column above, read as decimal
  (0C = 12, 1A = 26, 27 = 39, 28 = 40, 29 = 41, 2B = 43, 35 = 53, 56 = 86). That the Windows set 1
  scan codes shown by kbdlayout.info use the same numbers for these keys is shown by the table itself:
  the characters sit on the same physical keys in both.
- **`Generic.kl`** (`platform/frameworks/base`, `data/keyboards/Generic.kl` at commit `1cdfff55…`)
  maps: 12 to `MINUS`, 13 to `EQUALS`, 26 `LEFT_BRACKET`, 27 `RIGHT_BRACKET`, 39 `SEMICOLON`,
  40 `APOSTROPHE`, 41 `GRAVE`, 43 `BACKSLASH`, 53 `SLASH`, and **86 to `BACKSLASH`** (the same key
  name as 43).
- **The KCM's `map key` lines override that for this layout.** `KeyCharacterMap::mapKey` consults the
  layout's own scan code table (`mKeysByScanCode`) and returns on a hit, and an overlay's table is merged
  into the base's (`frameworks/native`, `libs/input/KeyCharacterMap.cpp`, `refs/heads/main` at commit
  `4f463a6b1de9198963dc6aff74154a504ba3f8f6`). So `map key 12 EQUALS` and `map key 13 MINUS` swap the
  two keys relative to `Generic.kl`, putting `ö` on scan code 12 and `-` on 13, as on Windows. `map key 86
  PLUS` turns the extra ISO key into its own key (`PLUS`) instead of a second `BACKSLASH`, which is why
  `<` has its own entry. The code that calls `mapKey` (which falls back to the `.kl` on a miss) was not
  read.

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

## If someone gets ÍST 125

Compare AltGr on GRAVE (D1), APOSTROPHE (D2) and BACKSLASH (D3) against it, and re-check the plain and
Shift positions of the keys with Icelandic letters or symbols (`ð`, `þ`, `æ`, `ö`, `´`, `°`, `'`, `+`,
`<`). If the standard disagrees with AOSP anywhere, add the correction to the same Gerrit patch (R-2)
and to the app.
