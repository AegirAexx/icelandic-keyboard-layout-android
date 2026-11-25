# Roadmap

Open tasks, tracked in git so they carry between machines (D-014). `HANDOFF.md` holds per-machine
session state; this file holds the shared task list. Mark a task done with the date and the PR or
commit, then remove it once nothing refers to it.

**Next up:** R-1's two manual searches, then R-8 (KCM audit), R-2 (upstream, started early because Gerrit
review is slow) and R-3 (emulators). R-8 and R-3 need no physical device, and R-3 removes the main
unknown before the first release (R-5).

## R-1 · Prior art and existing upstream reports (mostly done 25.11.2026)

- **Done:** web and GitHub searches found no equivalent app; the Gerrit queries found no change aimed at
  the file; AOSP `main` still has the bug and Icelandic is the only outlier among the 50 AOSP KCMs.
  Findings in `research/publishing.md` §0 and `research/upstream.md` §1.
- **Left, needs the user (logged in):** search https://issuetracker.google.com by hand for an existing
  report ("Icelandic", "eth", "U+00F0", "keyboard_layout_icelandic", "Đ"). If one exists, R-2 becomes
  "add to it". Also search Google Play directly for an equivalent app.
- Remove this entry once those two searches are logged.

## R-2 · Report the bug upstream and submit the patch

- File the bug on the Issue Tracker, then submit the four-line KCM patch to AOSP Gerrit
  (`research/upstream.md`).
- Depends on R-1's Issue Tracker search, and on R-8 if the audit finds more corrections (then one patch
  carries them all). Gerrit needs the user's personal Google account and an individual contributor
  agreement.
- Before relying on a quick merge, find out how the public AOSP `main` is updated and how external
  changes reach it. Its latest commit was dated 26.03.2025 when read (`research/upstream.md` §1).
- Start early: the upstream review takes a long time.

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
- After R-3 and R-8, so the first release makes no untested claims. Decide the distribution channel
  first: GitHub, F-Droid and Google Play sign differently, so a user can't switch channel without
  uninstalling. Installs of the debug build must be
  uninstalled first (different signature), which resets manual layout choices.

## R-6 · Samsung (One UI) device test

- Needs a borrowed Samsung device. One UI is the most common Android skin, and Samsung Keyboard's
  subtype tags are unknown (does it auto-select our layout?).

## R-7 · Lower priority

- TalkBack: announce the two tabs as tabs, via a platform `AccessibilityDelegate` (D-013).
- Check Gboard's layout provider on the OnePlus's Gboard version for a second Icelandic layout.

## R-8 · Key-by-key audit of the Icelandic KCM

- Compare every key of AOSP's `keyboard_layout_icelandic.kcm` with the Icelandic standard layout
  (ÍST 125; Windows and macOS Icelandic layouts) before filing the Gerrit patch (R-2) and before
  v0.1.0 (R-5). The project claims the Ð/ð fix is the only behavioral change, and the upstream patch
  should be complete.
- Record the result in `research/kcm-diff.md` or a new `research/kcm-audit.md`, so one patch can carry
  every correction.
