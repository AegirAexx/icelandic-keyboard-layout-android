# KCM audit (R-8)

Key-by-key comparison of the Icelandic layout in `app/src/main/res/raw/keyboard_layout_icelandic.kcm`
(AOSP's file plus the Ð/ð fix, `kcm-diff.md`) against other Icelandic layouts. Done 25.11.2026 by
reading, not by typing on a device. On-device tests (`testing.md`) only exercised the key right of `P`,
`[` and `{`.

**Result:** no second error of the Ð/ð kind. Every letter, digit and punctuation key matches the
Windows reference in its plain and Shift states, including Caps Lock behavior on the letter keys. Three
AltGr differences exist and none is clearly an error (see "Differences"). No change to the KCM or to the
upstream patch is proposed. The upstream patch stays limited to the Ð/ð fix.

## Sources and their limits

| Source | Used for | Status |
|---|---|---|
| AOSP `main` `keyboard_layout_icelandic.kcm` (`android.googlesource.com`, read 25.11.2026) | the layout under audit | **verified**, identical to ours except the fix (`kcm-diff.md`) |
| ReactOS `dll/keyboard/kbdic/kbdic.c` (https://github.com/reactos/reactos/blob/master/dll/keyboard/kbdic/kbdic.c, read 25.11.2026) | **Windows** reference (KBDIC character tables, per virtual key) | **Unverified against Microsoft's own KBDIC.** ReactOS reimplements Windows keyboard layouts. I did not read its dead-key tables, so what each dead key composes is unknown. |
| xkeyboard-config `symbols/is`, variant `basic` (NetBSD mirror `ftp.riken.jp`, read 25.11.2026) | **Linux** reference | **Verified as read.** It only overrides keys; the rest comes from `latin(type4)`, which I did not read. It adds Linux-only AltGr symbols (², ¼, ¤, „ and others) that neither Windows nor AOSP have. |
| ÍST 125 (the Icelandic standard) | the authoritative layout | **Not read.** Sold by Staðlaráð (https://www.stadlar.is). Needs a purchased copy. |
| macOS Icelandic | the third common layout | **Not compared.** No usable primary source. xkeyboard-config's `mac_legacy` variant is a legacy copy and differs from the PC layout in many AltGr positions on purpose. |
| `kbdlayout.info`, `gitlab.freedesktop.org` | alternative references | **Not used.** Both serve an anti-bot challenge to automated clients. I didn't work around it. |

The Windows reference is a PC layout reimplementation, so a difference from it is a prompt to look
closer, not proof of an AOSP error.

## Comparison

Key names are Android's. The KCM's scan code remaps (`map key 12 EQUALS`, `13 MINUS`, `86 PLUS`) put
`ö` right of `0`, `-` right of `ö` and `<` left of `Z`. That is **inferred** from the character
assignments agreeing with the Windows table, not tested: only the key right of `P` was typed on devices.

| Key | AOSP (ours) | Windows (ReactOS KBDIC) | Match |
|---|---|---|---|
| GRAVE | dead ring above (U+030A); Shift dead diaeresis (U+0308) | dead; dead; **AltGr `°` (U+00B0)** | base, Shift: yes. AltGr: **no, see D1** |
| 1 2 3 4 | `1 !`, `2 "`, `3 #`, `4 $` | same | yes |
| 5 | `5 %`, AltGr `€` | same | yes |
| 6 | `6 &` | same | yes |
| 7 8 9 0 | `/ ( ) =`; AltGr `{ [ ] }` | same | yes |
| EQUALS (`ö`) | `ö Ö`, AltGr `\` | same, CapsLock applies | yes |
| MINUS | `- _` | same | yes |
| Q | `q Q`, AltGr `@` | same | yes |
| E | `e E`, AltGr `€` | same | yes |
| W R T Y U I O P, A S D F G H J K L, Z X C V B N | letters, CapsLock | same | yes |
| M | AltGr `µ` (U+00B5) | same | yes |
| LEFT_BRACKET (`ð`) | `ð Ð` (the fix) | `ð Ð` (U+00F0 / U+00D0), CapsLock applies | **yes, after our fix** (stock AOSP: no) |
| RIGHT_BRACKET | `' ?`, AltGr `~` | same | yes |
| SEMICOLON (`æ`) | `æ Æ` | same | yes |
| APOSTROPHE (`´`) | dead acute (U+0301); Shift `'`; AltGr **`^`** | dead; `'`; AltGr **dead** | base, Shift: yes. AltGr: **see D2** |
| BACKSLASH (`+`) | `+ *`; AltGr **`` ` ``** | `+ *`; AltGr **dead** | base, Shift: yes. AltGr: **see D3** |
| PLUS (`<`) | `< >`, AltGr `\|` | same | yes |
| SLASH (`þ`) | `þ Þ` | same | yes |
| COMMA, PERIOD | `, ;` and `. :` | same | yes |

## Differences

- **D1 · AltGr on the key under Esc.** Windows types `°` (U+00B0). xkeyboard-config types `¬`
  (U+00AC, `notsign`). AOSP has nothing. The two references disagree, so the intended character is
  unknown without ÍST 125. Missing characters beat wrong ones. Left as is.
- **D2 · AltGr on `´` types a plain `^`** in AOSP. Windows has a dead key at that position. I did not
  read what it composes, so a dead circumflex is **unverified**. xkeyboard-config makes AltGr+`´` a dead
  circumflex too, but puts a plain `^` on AltGr+`æ`. Left as is.
- **D3 · AltGr on `+` types a plain `` ` ``** in AOSP. Windows has a dead key there (composition
  **unverified**). xkeyboard-config types a plain `` ` ``, like AOSP. Left as is.

D2 and D3 are the kind of simplification AOSP layouts make (plain characters in place of dead keys on
AltGr), and the references don't agree. Nothing here justifies diverging from AOSP, and each would be a
separate behavior change with its own decision (CLAUDE.md: the only change from AOSP is the intended fix).

## Not covered

- Dead-key composition (what `´`, `°` and `¨` produce with each letter). Android handles it with its
  own dead-key table, outside the KCM.
- Modifier states beyond what the KCM defines (Ctrl, Alt, Meta, Fn). These come from the generic
  `Generic.kcm` rules, not this file.
- Number-pad, function and navigation keys. Not in the file.
- ISO versus ANSI hardware. The KCM maps scan codes, so a US-ANSI keyboard has no `<` key.

## If someone buys ÍST 125

Compare AltGr on GRAVE (D1), APOSTROPHE (D2) and BACKSLASH (D3) against it, and re-check the plain and
Shift positions of the nine special keys (`ð`, `þ`, `æ`, `ö`, `´`, `°`, `'`, `+`, `<`). If the standard
disagrees with AOSP anywhere, add the correction to the same Gerrit patch (R-2) and to the app.
