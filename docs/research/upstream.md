# Upstream: reporting and fixing in AOSP

**Status:** prior-art check done (25.11.2026): no existing report or fix found. No bug filed, no patch
yet.

## 1. Check for existing reports

- Search https://issuetracker.google.com. Done by the user, logged in, on 25.11.2026 (the tracker has no
  public query API) with `icelandic keyboard`, `icelandic layout`, `eth keyboard`, `keyboard_layout_icelandic`,
  `U+00F0`, `U+0111` and `Đ`. See the Issue Tracker findings below.
- Search AOSP Gerrit (https://android-review.googlesource.com) for changes touching
  `keyboard_layout_icelandic.kcm`.
- Record findings (links, dates, status) here.

### Issue Tracker (searched by the user, 25.11.2026; seven screenshots)

- **No result title clearly describes this bug** (physical keyboard, Icelandic layout, `đ` instead of `ð`).
  Searches and result counts: `Đ` 49, `U+0111` "many", `U+00F0` 34, `eth keyboard` 16,
  `keyboard_layout_icelandic` 0, `icelandic layout` 21, `icelandic keyboard` 19. Status filters were
  not recorded. Matching is fuzzy: `U+0111` returned mostly unrelated issues, so an empty or irrelevant
  list is not proof of absence. This is judged from titles; no issue body was read.
- **Opened and read by the user, 25.11.2026: none of the five is relevant** (user-reported; no notes
  or screenshots kept, so their components and contents are not recorded here). I only saw their
  titles. Rows with a Chromium badge are probably Chromium issues, not Android (inferred from the
  badge). These five carried no badge:
  - 144652982, "Include missing ð anð Ð", Feature Request, Obsolete, 29.06.2021. Matched in three searches.
  - 65338182, "Emulator doesn't support typing @ for icelandic keyboards", Bug, Obsolete, 08.11.2022.
    Relevant to our emulator test procedure.
  - 183408437, "Special characters not working with external keyboards while using TalkBack.", Bug,
    Duplicate, 20.03.2023. Matched both `icelandic` searches, so the body probably mentions Icelandic.
  - 264112020, "update", Bug, New, 22.06.2023. Matched both `icelandic` searches; body unknown.
  - 516075789, "Đ", Bug, Infeasible, 25.05.2026. Matched `Đ`.
- **Seen, judged unrelated by title (Chromium badge or other product):** 41005728 "FR: Icelandic
  keyboard" (2013), 40307983 "Icelandic as an input method Chromebook" (2013), 40624456 "[more
  locales] Need to support Icelandic locale", 40843551 "Impossible to type accented characters on
  Icelandic keyboard into password field on Mac OS", 40093987 "Security: Idn-spoof with using U+00F0".
  The two 2013 Chromebook issues concern ChromeOS input methods.
- **Conclusion:** no existing Issue Tracker report found, so R-2 files a new bug. Limits: the
  tracker search is fuzzy, only public issues are visible, and the status filters were not recorded.
  Record the issue ID and date here when the bug is filed.

### Findings (25.11.2026)

- **AOSP `main` still has the bug (verified 25.11.2026 10:47, local time).** `platform/frameworks/base`
  `refs/heads/main` was at commit `1cdfff555f4a21f71ccc978290e2e212e2f8b168` when read.
  `packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm` at that commit
  (`https://android.googlesource.com/platform/frameworks/base/+/1cdfff555f4a21f71ccc978290e2e212e2f8b168/packages/InputDevices/res/raw/keyboard_layout_icelandic.kcm`)
  has `LEFT_BRACKET` as `Đ` / `đ` on all four lines (`label`, `base`, `shift, capslock`,
  `shift+capslock`).
- **The public `main` has not moved since March 2025; the bug is on the release branches (verified
  25.11.2026).** The `main` commit above is dated 26.03.2025, and `platform/frameworks/native` `main`
  (`4f463a6b1de9198963dc6aff74154a504ba3f8f6`) is dated 25.03.2025. The dates fit §4: the FAQ says
  `aosp-main` "is a read-only branch as of March 27, 2025". That the googlesource `main` ref is the
  branch the docs call `aosp-main` is an **inference** from the matching dates (there is no
  `refs/heads/aosp-main` on googlesource for `frameworks/base`; not checked in the manifest). The same
  file still has the bug on `android17-release` (`94b4c163b7dfe5ce3607f7bb8456f9573f7de57d`,
  committer date 21.05.2026) and `android16-release` (`99b01a65cc4c104933788b3143285ab6bae65827`,
  07.05.2025): `LEFT_BRACKET` is `Đ` / `đ`.
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

- Requires a signed Contributor License Agreement. The licenses page
  (https://source.android.com/docs/setup/contribute/licenses, read 25.11.2026) says individual
  contributors, "those making contributions only on their own behalf", sign the Individual CLA, which
  "you can execute ... online through the code review tool". The Corporate CLA lets a company authorize
  its designated employees, and "doesn't remove the need for a developer to sign their own Individual
  Contributor License Agreement". The agreement "doesn't change your rights to use your own
  contributions for any other purpose". This is a personal project, so the plan is the Individual CLA
  signed with a personal account. Whether an employer could have a claim on work done on a personal
  project is a question for the contributor's own employment contract (**not researched**).
- Signing in to Gerrit presumably needs a Google account (**unverified**: the submit-patches page
  doesn't say so).
- Change: four lines in `keyboard_layout_icelandic.kcm` (`Đ` → `Ð`, `đ` → `ð`).
- **Target branch: the `android-latest-release` manifest's branch, currently `android17-release`** (§4).
  Not `aosp-main`, which is read-only. **Re-read the manifest's default revision right before
  uploading** (https://android.googlesource.com/platform/manifest/+/refs/heads/android-latest-release/default.xml):
  Google moves it with each source drop, and a Q4 2026 drop may be near (the last was Q2 2026).
- Put the Issue Tracker bug ID in the commit message as `Bug: <id>`. This is the usual Android
  convention, **unsourced** here: the submit-patches page doesn't mention it.
- Guide: https://source.android.com/docs/setup/contribute/submit-patches
- How to get the code, per the docs: `repo init --partial-clone --no-use-superproject -b android-latest-release
  -u https://android.googlesource.com/platform/manifest`, then `repo sync`, `repo start`, commit,
  `repo upload` (the upload prompt shows the remote branch, e.g. `android17-release`). The guide shows
  the `repo` route. The FAQ also permits plain Git ("when using Git directly": sync to and propose
  against the manifest's default revision branch). A full `repo sync` is very large, so a partial
  `git clone --filter=blob:none` of `platform/frameworks/base` on that branch, then a push to
  `refs/for/<branch>`, would be the lighter route. That is general Gerrit practice and is
  **unverified** for AOSP: Gerrit authentication, the `Change-Id` commit hook, and whether this
  route works in practice.
- Every change needs review by the code owners (`find-owners`, "SUGGEST OWNERS" in Gerrit).

## 4. How external changes reach AOSP now (read 25.11.2026)

Quotes are verbatim from https://source.android.com/docs/setup/contribute/submit-patches,
https://source.android.com/docs/setup/contribute/release-lifecycle (the page says it was last updated
15.04.2025, so before the 2026 Q2/Q4 policy),
https://source.android.com/docs/setup/about/faqs#android-latest-release (last updated 17.06.2026) and
https://source.android.com/docs/whatsnew/site-updates. The inferences are marked as such.

- **Q2/Q4 publication, from 2026.** The site banner: "Effective in 2026, to align with our trunk stable
  development model and ensure platform stability for the ecosystem, we will publish source code to AOSP
  in Q2 and Q4. For building and contributing to AOSP, use android-latest-release. The
  android-latest-release manifest branch will always reference the most recent release pushed to AOSP."
- **`aosp-main` is read-only.** FAQ: "Why can't I submit to aosp-main? You can't submit to aosp-main
  because that branch is now read-only." and "You should propose new changes to android-latest-release
  (when using Repo) or to the default revision branch specified in the android-latest-release manifest
  (when using Git directly). ... The default revision branch is not android-latest-release."
- **The current branch is `android17-release`.** Site updates: "Starting March 27, 2025, the latest
  release branch will always be referenced by the new android-latest-release manifest ... The
  android-latest-release manifest is set to the latest AOSP release branch, android17-release." The
  manifest file itself (`platform/manifest`, `android-latest-release`, `default.xml`) has
  `revision="android17-release"` (checked by the second reviewer).
- **What happens to a proposal (release lifecycle page):** "External contributors can download and
  modify the latest release branch for their device, as well as propose code changes for the next
  version of AOSP on this branch." "Google reviews and, if accepted, cherrypicks proposed changes on
  the latest release branch into Google's internal development branch. ... Periodically, an internal
  release branch is created from the internal development branch. ... At some point, Google pushes code
  from the internal release branch to the latest release branch ... to create a read-only copy of a
  release branch on the public AOSP host."
- **FAQ on acceptance (verified, 17.06.2026 page):** "After you upload a proposed change, Google will
  review it and, if accepted, will cherrypick it into the internal Gerrit." "Accepted and cherrypicked
  changes appear in a future push to a release branch on the Android host and can be synced with repo
  using android-latest-release. There isn't a notification mechanism for when a proposed change is
  accepted or rejected."
- **Consequence for this project (inference):** an accepted fix is cherry-picked into Google's internal
  tree and reaches the public repository only with a later source drop (Q2 or Q4), and reaches devices
  only through OEM updates. The Gerrit change may never show as an ordinary merge, and there is no
  notice of acceptance, so "not yet merged" (D-015, R-8) isn't observable: read it as "not yet
  accepted". Expect a long timeline and no quick public confirmation. The sideloadable app (D-001)
  remains the only fix users can get soon.
- **Bugs:** https://source.android.com/docs/setup/contribute/report-bugs says to search open and closed
  bugs, star an existing one, and otherwise file a new one in a component under the **Android Public
  Tracker**. The platform list includes "Framework" (and "Text", "System", "Accessibility", among
  others). Whether an "Input" component exists was not checked, so file under "Framework" unless the
  tracker offers a better one. The page also says: "The amount of detail that you provide, including log
  files and even a patch set, helps Google address your issue."
- **Not found:** how long Google takes to review external changes, and how many external changes get
  accepted. The docs don't say.

## Log

(append dated entries: bug filed, patch uploaded, review status)
