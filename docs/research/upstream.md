# Upstream: reporting and fixing in AOSP

**Status:** not started.

## 1. Check for existing reports

- Search https://issuetracker.google.com for "Icelandic keyboard", "eth", "U+00F0", "keyboard_layout_icelandic".
- Search AOSP Gerrit (https://android-review.googlesource.com) for changes touching
  `keyboard_layout_icelandic.kcm`.
- Record findings (links, dates, status) here.

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
