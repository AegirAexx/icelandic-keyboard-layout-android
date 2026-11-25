# Roadmap

Open tasks, tracked in git so they carry between machines (D-014). `HANDOFF.md` holds per-machine
session state; this file holds the shared task list. Mark a task done with the date and the PR or
commit, then remove it once nothing refers to it.

**Next up:** R-2 (upstream, started early because the path to a public fix is slow) and R-3 (emulators). R-3
needs no physical device and removes the main unknown before the first release (R-5).

Done 25.11.2026, no longer tasks:
- Prior-art check (the former R-1): no equivalent app, no Gerrit change and no Issue Tracker report
  found; AOSP `main` still has the bug and Icelandic is the only outlier among its 50 KCMs. Findings
  in `research/publishing.md` §0 and `research/upstream.md` §1.
- KCM audit (the former R-8): no second Ð/ð-style error; three AltGr differences from Windows left
  as they are, and chasing them (ÍST 125, macOS) is out of scope (D-016). `research/kcm-audit.md`.

## R-2 · Report the bug upstream and submit the patch

- File a new bug on the Issue Tracker (no existing report found), then submit the four-line KCM patch
  to AOSP Gerrit (`research/upstream.md`).
- The patch is the Ð/ð fix only (D-015, D-016).
- Gerrit needs the user's personal Google account and an individual contributor agreement.
- **Target branch is `android17-release`** today (the `android-latest-release` manifest), not
  `aosp-main`, which is read-only. The bug is present there. Re-read the manifest's default revision
  right before uploading: it moves with each source drop, and a Q4 2026 drop may be near.
- Google reviews and cherry-picks accepted changes into its internal tree. The public repo gets them
  only with a Q2 or Q4 source drop, and there is no notification of acceptance
  (`research/upstream.md` §3 and §4). Expect a long timeline and no quick public merge. How long
  review takes is not documented.
- Open points before the patch: the Gerrit setup (the guide shows `repo`; the FAQ permits plain Git,
  which is **unverified** in practice), where to file (Android Public Tracker, component "Framework"),
  and signing the Individual CLA online with a personal account. Whether an employer could have a
  claim is for the user's own contract to answer; it hasn't been researched.
- Start early: the path from a proposal to a public fix is slow.

## R-3 · Emulator tests below Android 16

Only API 36 (two devices) and API 37 (emulator) have run. On emulators, without the app and then
with it (procedure: `research/testing.md`):

- **API 34 and 35:** does Gboard's `is-IS` subtype auto-select our layout (D-012)? The README promises
  this for Android 14+.
- **API 33 and lower (e.g. 33, 30, 26, 21, 16):** the layout is listed and selectable, the key types
  U+00F0 / U+00D0, and the app's setup text matches the settings screens (no per-language entries
  before API 34). Check that the receiver works on a first install without launching the app.
- **App UI branches from D-013:** the pre-API 30 insets path and the pre-API 23 and pre-API 21 color
  branches.
- Update the README Status table and `research/testing.md` with the results.

## R-4 · Setup text for MagicOS, and an OxygenOS doc note

- `setup_note` (EN and IS) says to look for "Physical keyboard" in the keyboard or language settings.
  On MagicOS it is under System & updates → "Looking for other settings?", and only while a keyboard
  is connected (`research/testing.md`). Reword it. New Icelandic wording needs the user's
  proofreading.
- Add to `research/testing.md`: OxygenOS offers no Icelandic system language, so on OnePlus the app can
  be Icelandic (per-app language) while the Settings menus stay English.

## R-5 · Release signing and v0.1.0

- Release keystore outside the repo. The user decides where it lives and how it is backed up: a lost
  key means existing installs can't be updated.
- Signed build, GitHub Release with the APK, CHANGELOG entry.
- After R-3, so the first release makes no untested claims.
- Decide the distribution channel first: GitHub, F-Droid and Google Play sign differently, so a user
  can't switch channel without uninstalling.
- Installs of the debug build must be uninstalled first (different signature), which resets manual
  layout choices.

## R-6 · Samsung (One UI) device test

- Needs a borrowed Samsung device. One UI is the most common Android skin, and Samsung Keyboard's
  subtype tags are unknown (does it auto-select our layout?).

## R-7 · Lower priority

- TalkBack: announce the two tabs as tabs, via a platform `AccessibilityDelegate` (D-013).
- Check Gboard's layout provider on the OnePlus's Gboard version for a second Icelandic layout.
