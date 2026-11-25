# Roadmap

Open tasks, tracked in git so they carry between machines (D-014). `HANDOFF.md` holds per-machine
session state; this file holds the shared task list. Mark a task done with the date and the PR or
commit, then remove it once nothing refers to it.

**Next up:** R-2 (upstream, started early because Gerrit review is slow) and R-3 (emulators). R-3
needs no physical device and removes the main unknown before the first release (R-5). R-8 waits only
on ÍST 125.

Prior-art check (the former R-1), done 25.11.2026: no equivalent app, no Gerrit change and no Issue
Tracker report found; AOSP `main` still has the bug and Icelandic is the only outlier among its 50
KCMs. Findings in `research/publishing.md` §0 and `research/upstream.md` §1.

## R-2 · Report the bug upstream and submit the patch

- File a new bug on the Issue Tracker (no existing report found), then submit the four-line KCM patch
  to AOSP Gerrit (`research/upstream.md`).
- If ÍST 125 (R-8) turns up more corrections, one patch should carry them all, but don't hold the bug
  report for it.
- Gerrit needs the user's personal Google account and an individual contributor agreement.
- **Target branch is `android17-release`** (the `android-latest-release` manifest), not `aosp-main`,
  which is read-only. The bug is present there. Google reviews and cherry-picks accepted changes into
  its internal tree, and the public repo gets them only with a Q2 or Q4 source drop
  (`research/upstream.md` §3 and §4). Expect a long timeline and no quick public merge.
- Open points before the patch: the Gerrit/`repo` setup (the docs describe `repo` only; plain Git is
  **unverified**), where to file (Android Public Tracker, component "Framework"), and signing the
  Individual CLA with a personal Google account.
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
- After R-3, so the first release makes no untested claims. R-8 is done apart from ÍST 125; the
  release only needs the user's decision on that, not its outcome. If ÍST 125 later disagrees with
  AOSP, correcting the layout is a behavior change shipped as an update. Decide the distribution channel
  first: GitHub, F-Droid and Google Play sign differently, so a user can't switch channel without
  uninstalling. Installs of the debug build must be
  uninstalled first (different signature), which resets manual layout choices.

## R-6 · Samsung (One UI) device test

- Needs a borrowed Samsung device. One UI is the most common Android skin, and Samsung Keyboard's
  subtype tags are unknown (does it auto-select our layout?).

## R-7 · Lower priority

- TalkBack: announce the two tabs as tabs, via a platform `AccessibilityDelegate` (D-013).
- Check Gboard's layout provider on the OnePlus's Gboard version for a second Icelandic layout.

## R-8 · Key-by-key audit of the Icelandic KCM (done 25.11.2026 except ÍST 125)

- **Done:** compared against Microsoft's KBDIC (via kbdlayout.info) and Linux (xkeyboard-config). No
  second Ð/ð-style error; three AltGr differences against Windows (D1 to D3), none clearly wrong.
  Result: `research/kcm-audit.md`.
- **Left:** the standard itself, ÍST 125 (sold by Staðlaráð), and Apple's macOS layout, were not read.
  Only ÍST 125 can settle D1 to D3. If it disagrees with AOSP anywhere, add the correction to the Gerrit patch
  (R-2) and the app. Optional: the user decides whether to buy it.
- Remove this entry once ÍST 125 is checked or the user decides not to.
