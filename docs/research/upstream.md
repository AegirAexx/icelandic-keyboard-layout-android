# Upstream: reporting and fixing in AOSP

**Status:** prior-art check done (25.11.2026); five Issue Tracker results still to be opened. No bug
filed, no patch yet.

## 1. Check for existing reports

- Search https://issuetracker.google.com for "Icelandic keyboard", "eth", "U+00F0", "keyboard_layout_icelandic".
  Done by the user, logged in, on 25.11.2026 (the tracker has no public query API). See the Issue
  Tracker findings below.
- Search AOSP Gerrit (https://android-review.googlesource.com) for changes touching
  `keyboard_layout_icelandic.kcm`.
- Record findings (links, dates, status) here.

### Issue Tracker (searched by the user, 25.11.2026; seven screenshots)

- **No result title describes this bug** (physical keyboard, Icelandic layout, `đ` instead of `ð`).
  Searches and result counts: `Đ` 49, `U+0111` "many", `U+00F0` 34, `eth keyboard` 16,
  `keyboard_layout_icelandic` 0, `icelandic layout` 21, `icelandic keyboard` 19. Status filters were
  not recorded. Matching is fuzzy: `U+0111` returned mostly unrelated issues, so an empty or irrelevant
  list is not proof of absence. This is judged from titles; no issue body was read.
- **To open and read (titles only seen, bodies unread).** The Chromium-badged rows are Chromium, not
  Android. These carry no badge, so they may be Android or something else; the component is unknown:
  - 144652982, "Include missing ð anð Ð", Feature Request, Obsolete, 29.06.2021. Matched in three searches.
  - 65338182, "Emulator doesn't support typing @ for icelandic keyboards", Bug, Obsolete, 08.11.2022.
    Relevant to our emulator test procedure.
  - 183408437, "Special characters not working with external keyboards while using TalkBack.", Bug,
    Duplicate, 20.03.2023. Matched both `icelandic` searches, so the body mentions Icelandic.
  - 264112020, "update", Bug, New, 22.06.2023. Matched both `icelandic` searches, body unknown.
  - 516075789, "Đ", Bug, Infeasible, 25.05.2026. Matched `Đ`.
- **Seen, judged unrelated by title (Chromium badge or other product):** 41005728 "FR: Icelandic
  keyboard" (2013), 40307983 "Icelandic as an input method Chromebook" (2013), 40624456 "[more
  locales] Need to support Icelandic locale", 40843551 "Impossible to type accented characters on
  Icelandic keyboard into password field on Mac OS", 40093987 "Security: Idn-spoof with using U+00F0".
  The two 2013 Chromebook issues concern ChromeOS input methods.
- Record the result of reading the five issues here, and the issue ID and date if a bug is filed.

### Findings (25.11.2026)

- **AOSP `main` still has the bug (verified 25.11.2026 10:47, local time).** `platform/frameworks/base`
  `refs/heads/main` was at commit `1cdfff555f4a21f71ccc978290e2e212e2f8b168` when read.
  `packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm` at that commit
  (`https://android.googlesource.com/platform/frameworks/base/+/1cdfff555f4a21f71ccc978290e2e212e2f8b168/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`)
  has `LEFT_BRACKET` as `Đ` / `đ` on all four lines (`label`, `base`, `shift, capslock`,
  `shift+capslock`).
- **The public `main` may lag behind Google's development (observed, cause unverified).** That commit's
  committer date is 26.03.2025, about eight months before the date it was read. Find out how the public
  branch is updated now and how external Gerrit changes reach it before counting on a quick merge (R-2).
- **Icelandic is the only outlier (verified).** Scanned all 50 `.kcm` files in that directory for
  U+0110, U+0111, U+00D0, U+00F0 and the literal letters. `Đ/đ` on a base key appears only in the
  Croatian/Slovenian and Serbian/Montenegrin Latin layouts, where it is correct, and in Icelandic, where
  it is wrong. Danish, Finnish, Norwegian, Swedish, Hungarian and Slovak have `đ` on AltGr (`ralt`). English
  (US) International has `ð` on AltGr, correctly. There is no Faroese layout in AOSP.
  **Not checked:** whether the Nordic AltGr `đ` is right for each of those layouts. That is outside this bug.
- **Gerrit (REST query, 25.11.2026): found no change aimed at the file.** `file:keyboard_layout_icelandic.kcm`
  returned two changes in `platform/frameworks/base`: 3327151 (merged, "Merge empty history for
  sparse-…") and 2413895 (abandoned, "Use minimumInteractiveComponentSize() instead of custom modifier").
  Both list the file only among tens of thousands of files, so they are unrelated. `message:icelandic`
  and `message:eth` combined with `keyboard`, in that project, returned nothing. This is only as strong
  as those queries: a subject that says "Ð", "đ" or "layout" would be missed. The REST API shows public
  changes only. The Gerrit history page for the file needs a sign-in (403), so no file log was read.
- **Five GitHub copies, none corrected (verified 25.11.2026, reread by a second reviewer).** Each has an
  Icelandic KCM with `LEFT_BRACKET` = `Đ`/`đ`:
  `LineageOS/android_frameworks_base`, `ris58h/exkeymo`, `OctoNezd/OculusLayouts`,
  `mzp/TweakedKeyboardLayout` and `aaron-gh/accessible-android-emulator` (`aae_icelandic.kcm`). Three
  of them are older snapshots (no `shift+capslock` line). They are copies of AOSP, not independent
  evidence, and five copies don't show that no one has corrected the file anywhere. They show that ROMs
  built on AOSP inherit the bug.
- **Samsung's on-screen keyboard has a related problem (not about this bug).** A Samsung Community
  thread (https://eu.community.samsung.com/t5/other-galaxy-s-series/error-in-samsung-s-danish-and-faroese-keyboards/m-p/4868300,
  21.02.2022, S21 Ultra, One UI 4.0) says Samsung's on-screen Danish and Faroese keyboards give `đ`
  where `ð` belongs when long-pressing `d`, and that the Icelandic one offers the correct `ð` but also
  lists `đ` among its long-press characters. It is about the on-screen keyboard, not physical keyboards
  or KCMs, so it says nothing about R-6.

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
