# Upstream: reporting and fixing in AOSP

**Status:** prior-art check done except the Issue Tracker (25.11.2026). No bug filed, no patch yet.

## 1. Check for existing reports

- Search https://issuetracker.google.com for "Icelandic keyboard", "eth", "U+00F0", "keyboard_layout_icelandic".
  **Not done:** the tracker is a JavaScript app with no public query API, and web search returned no
  issuetracker.google.com results at all (which proves nothing). Needs a manual search by the user,
  logged in.
- Search AOSP Gerrit (https://android-review.googlesource.com) for changes touching
  `keyboard_layout_icelandic.kcm`.
- Record findings (links, dates, status) here.

### Findings (25.11.2026)

- **AOSP `main` still has the bug (verified 25.11.2026 10:47).** Read
  `https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`:
  `LEFT_BRACKET` is still `Đ` / `đ`.
- **Icelandic is the only outlier (verified).** Scanned all 50 `.kcm` files in that directory for
  U+0110, U+0111, U+00D0, U+00F0 and the literal letters. `Đ/đ` on a base key appears only in the
  Croatian/Slovenian and Serbian/Montenegrin Latin layouts, where it is correct, and in Icelandic, where
  it is wrong. Danish, Finnish, Norwegian, Swedish, Hungarian and Slovak have `đ` on AltGr (`ralt`). English
  (US) International has `ð` on AltGr, correctly. There is no Faroese layout in AOSP.
  **Not checked:** whether the Nordic AltGr `đ` is right for each of those layouts. That is outside this bug.
- **Gerrit (REST query, 25.11.2026): no change targets the file or mentions it.** `file:keyboard_layout_icelandic.kcm`
  returned two unrelated changes (3327151, a merge of empty history; 2413895, an abandoned Compose change).
  `message:icelandic` and `message:eth` combined with `keyboard` in `platform/frameworks/base` returned
  nothing. The Gerrit history page for the file needs a sign-in (403), so a direct file log was not read.
- **Copies of the bug in the wild.** Every GitHub copy of the file I checked carries the same
  `Đ`/`đ`: LineageOS `android_frameworks_base`, `ris58h/exkeymo`, `OctoNezd/OculusLayouts`,
  `mzp/TweakedKeyboardLayout`, `aaron-gh/accessible-android-emulator` (`aae_icelandic.kcm`). Nobody had
  corrected it. Downstream ROMs inherit the bug.
- **Samsung (unverified, search snippet only).** A Samsung community thread
  (https://eu.community.samsung.com/t5/other-galaxy-s-series/error-in-samsung-s-danish-and-faroese-keyboards/m-p/4868300/highlight/true)
  reports Samsung's Danish and Faroese keyboards typing the stroked letters instead of eth. The poster
  says Icelandic types eth correctly there. I did not open the page, so whether it is about physical or
  on-screen layouts, and its date, are unknown. Relevant to R-6: Samsung ships its own layouts, so
  Samsung devices may not have this bug at all.

## 2. File a bug (Issue Tracker, Android Public Tracker → Framework / Input)

Include:
- Repro: attach a physical keyboard, choose the Icelandic layout, press the key right of `P`, and check
  the code point of the output.
- Expected U+00F0/U+00D0, actual U+0111/U+0110.
- Exact file and line (see `root-cause.md`) and the proposed diff.
- Impact: every Icelandic physical keyboard user on Android since 2012. Lowercase is visibly wrong.
  Uppercase is invisibly wrong, which breaks search and spell check.

## 3. Submit a patch (AOSP Gerrit)

- Requires a Google account plus a signed Contributor License Agreement.
- Change: four lines in `keyboard_layout_icelandic.kcm` (`Đ` → `Ð`, `đ` → `ð`).
- Reference the Issue Tracker bug ID in the commit message (`Bug: <id>`).
- Guide: https://source.android.com/docs/setup/contribute/submit-patches

## Log

(append dated entries: bug filed, patch uploaded, review status)
