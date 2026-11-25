# Decision log

Append-only. Format: ID, date, decision, why, consequences. Supersede rather than edit.

## D-001 · 24.11.2026 · Use a keyboard layout provider APK as the workaround

- **Decision:** ship a corrected copy of the AOSP Icelandic KCM through Android's
  `QUERY_KEYBOARD_LAYOUTS` provider mechanism. Do not use an IME, accessibility service or root.
- **Why:** it fixes the bug at the same layer where it lives (layout data), uses a public mechanism AOSP
  itself uses, needs no dangerous permissions, and keeps the user's soft keyboard. See
  `research/fix-approaches.md`.
- **Consequences:** users must manually select the layout in the physical keyboard settings. The exact
  steps vary by Android version and OEM, so document them per tested device.

## D-002 · 24.11.2026 · Pursue the upstream fix in parallel

- **Decision:** file an Issue Tracker bug and submit an AOSP patch.
- **Why:** the APK only helps devices it is installed on. The root cause is a trivial data error that
  should be fixed at the source.
- **Consequences:** track progress in `research/upstream.md`.

## D-003 · 24.11.2026 · License under Apache-2.0

- **Decision:** license the whole project under Apache-2.0, with a `NOTICE` file crediting AOSP.
- **Why:** the core asset is derived from an Apache-2.0 AOSP file. One matching license avoids mixed
  licensing. It is also F-Droid compatible and permissive enough for upstreaming.
- **Consequences:** derived files keep the AOSP copyright header. Can be revisited before the first
  public push.

## D-004 · 24.11.2026 · Keep `HANDOFF.md` out of version control

- **Decision:** `HANDOFF.md` is gitignored (user's choice).
- **Why:** it is volatile, per-session working state, not project documentation.
- **Consequences:** it has no history or backup, and fresh clones don't have it. Anything durable must
  go into `CONTEXT.md`, `docs/decisions.md` or `docs/research/`.

## D-005 · 24.11.2026 · Repository identity: one app repo on GitHub

- **Decision:** the project is a single Android app repository,
  `https://github.com/ORI-Aegir/icelandic-keyboard-layout-android`. The AOSP patch is not a separate
  project in this repo. It is submitted from a clone of `platform/frameworks/base` to AOSP Gerrit, and is
  only tracked here (`research/upstream.md`, optionally a reference diff under `upstream/`).
  - Description: "Corrected Icelandic layout for physical keyboards on Android. The built-in layout types
    Đ/đ (D with stroke) instead of Ð/ð (eth). Install, pick the layout, done. No root, no keyboard app
    replacement, no permissions."
  - Topics: `android`, `keyboard-layout`, `icelandic`, `physical-keyboard`, `kcm`, `i18n`, `f-droid`.
- **Why:** AOSP changes go through Gerrit against Google's repository, so a "monorepo for both" has no
  second project to hold. The app is the deliverable, and it stays useful after an upstream fix because
  most devices never receive that update.
- **Consequences:** store metadata goes in `fastlane/metadata/android/{en-US,is-IS}/`. The application ID
  is still open (see `research/publishing.md`).

## D-006 · 24.11.2026 · Repository moved to the personal GitHub account

- **Decision:** the repository is now `https://github.com/AegirAexx/icelandic-keyboard-layout-android`
  (ownership transferred from the work account `ORI-Aegir`). Supersedes the URL in D-005; the rest of
  D-005 stands.
- **Why:** the app is a personal project and should not depend on a work account.
- **Consequences:** the application ID candidate changes from `io.github.ori_aegir.icelandickeyboard` to
  `io.github.aegiraexx.icelandickeyboard` (still undecided, see `research/publishing.md`). The first two
  commits carry the work author email; history was not rewritten.

## D-007 · 24.11.2026 · Machine-agnostic agent instructions

- **Decision:** `CLAUDE.md` contains no OS-, shell- or machine-specific facts. The agent detects its
  environment per session, and each machine records its own setup in the "This machine" section of its
  local `HANDOFF.md`.
- **Why:** the project is worked on from a Linux laptop and a Windows laptop. A tracked file that
  describes one machine misleads the agent on the other.
- **Consequences:** builds go through the Gradle wrapper; `gradlew` must be committed as executable;
  machine paths stay in the gitignored `local.properties`. Builds on D-004: `HANDOFF.md` is per-machine.

## D-008 · 24.11.2026 · Application ID `io.github.aegiraexx.icelandickeyboard`

- **Decision:** the application ID (and Kotlin namespace) is `io.github.aegiraexx.icelandickeyboard`.
- **Why:** it is derived from the personal GitHub account that hosts the repo (D-006), costs nothing,
  and doesn't expose or depend on a privately held domain. Chosen by the user over a reverse-DNS name
  under an owned domain.
- **Consequences:** the ID can never change after publishing (Play treats a new ID as a new app; see
  `research/publishing.md`). It is tied to the GitHub username `AegirAexx`; renaming that account would
  leave the ID pointing at a name the author no longer holds, so don't rename it.

## D-009 · 24.11.2026 · Build stack

- **Decision:** single `:app` module. Gradle 9.9.2 through the wrapper (distribution sha256 pinned in
  `gradle-wrapper.properties`), AGP 9.5.2 via a version catalog, AGP's built-in Kotlin (no separate
  Kotlin plugin; it pulls `kotlin-stdlib` 2.2.10). `compileSdk` and `targetSdk` 37. Release builds use R8
  and resource shrinking. No AndroidX or other libraries.
- **Why:** the latest stable versions on 24.11.2026 (AGP from Google's Maven metadata, Gradle from
  services.gradle.org). AGP 9.5 requires Gradle ≥ 9.6.0 and JDK 17, and supports up to API 37
  (https://developer.android.com/build/releases/gradle-plugin, read 24.11.2026). The only code is an
  empty `BroadcastReceiver`, so no libraries are needed. R8 strips the Kotlin stdlib: the release APK is
  5.8 KB.
- **Consequences:** building needs JDK 17+ (Android Studio's bundled JBR works). Kotlin reflection
  metadata (`kotlin/**.kotlin_builtins`) is excluded from the APK. Gradle reports one deprecation
  (`Configuration.setVisible`), not from our build scripts (**unverified** that it comes from AGP).

## D-010 · 24.11.2026 · minSdk 16

- **Decision:** `minSdk = 16` (Android 4.1).
- **Why:** the keyboard layout provider mechanism (`QUERY_KEYBOARD_LAYOUTS`) was added in API 16, and the
  app uses no newer API. A lower floor costs nothing here and reaches old tablets, which fits the target
  users (CONTEXT.md). AGP 9.5.2 accepts it.
- **Consequences:** `keyboardLocale` / `keyboardLayoutType` are API 34+ and ignored on older versions
  (lint suppressed with `tools:targetApi`). Behavior below API 37 is **untested**. If a future dependency
  or AGP release needs a higher floor, raise it in a new decision.
- **Update 25.11.2026:** emulator runs on API 16, 19, 22, 29, 33, 34 and 35 passed (`research/testing.md`),
  so the floor holds and the "untested" statement above is outdated. D-017 has what is still unverified.

## D-011 · 24.11.2026 · Launcher activity with instructions and a key test

- **Decision:** add one launcher activity (`MainActivity`, plain `android.app.Activity`, no AndroidX,
  no permissions) that states the app is for USB/Bluetooth keyboards only, lists the setup steps, opens
  the physical keyboard settings, and tests the key right of P. Add an adaptive launcher icon (Ð on
  Icelandic flag blue, with a monochrome layer) and a vector fallback that AGP rasterizes for API < 21.
- **Why:** the target users are not developers (CONTEXT.md), and an app with no activity gives them no
  way to find instructions or confirm the fix. Android 10+ shows a synthetic launcher icon for apps
  without a launcher activity, but exempts apps that request no permissions, which is this app, so it
  would be invisible in the launcher. Play's minimum-functionality policy may also reject a UI-less app
  (both **unverified**). The key test reads `KeyEvent.getUnicodeChar()` from non-virtual devices only,
  with no text field, so an on-screen keyboard cannot produce a false pass.
- **Consequences:** the release APK grows from 5.8 KB to about 27 KB. Keyboard presence is detected as
  any non-virtual alphabetic keyboard, without `InputDevice.isExternal` (it would hide built-in
  keyboards that also use the selected layout, and the emulator's). Strings exist in English and
  Icelandic. The Icelandic text uses the everyday loanword "layout" (layout’ið, layout’i) instead of
  "útlit", which Icelanders don't use for this; revisit after user testing. The launcher label is
  "Ð Layout" in every language, because "Icelandic Keyboard Layout" truncates in launchers. The
  settings path wording stays generic until real devices are tested. Light system bar icons are only
  set from API 35 (edge-to-edge, transparent bars).

## D-012 · 24.11.2026 · Declare `keyboardLocale="is-IS,is-Latn"` instead of AOSP's `is-Latn`

- **Decision:** our layout's `android:keyboardLocale` is `is-IS,is-Latn`. The KCM and every other
  attribute except the label stay as in AOSP. The setup steps also tell users to add Icelandic to the
  on-screen keyboard's languages first.
- **Why:** on API 34+ the system auto-selects a layout per keyboard and IME subtype. With identical
  locales the layouts tie: third-party providers get priority 0, and the tie is broken by label in the
  system language, which the stock "Icelandic" wins in English and Icelandic. `is-IS` scores an exact
  country match (3) against stock's `is-Latn` empty-country semi-match (2.5) for Gboard's `is-IS`
  subtype, so ours is picked with no manual setup. Verified on the API 37 emulator with a clean install
  and an `is-Latn` control (`research/layout-selection.md`). `is-Latn` stays in the list because the
  scorer takes a layout's best locale: for a bare `is` subtype ours keeps AOSP's score and tie, where
  `is-IS` alone would score lower and lose even in system languages where ours wins the tie today
  (found by a blind review). Gaming the label sort order instead was rejected: it is fragile and
  would need an odd label.
- **Consequences:** a deliberate deviation from AOSP metadata (justified in `research/kcm-diff.md`).
  Never worse than `is-Latn` alone. It only helps IME subtypes tagged with country `IS`; other IMEs'
  tags are **unverified**. No effect below API 34. Manual selection stays in the instructions as the
  fallback. AOSP's own XML never lists several locales; the `,`/`|` list form is documented in the
  `keyboardLocale` attribute (`core/res/res/values/attrs.xml`) and was verified on the emulator.

## D-013 · 25.11.2026 · Two tabs ("Set up" and "Test"), with setup text from real-device testing

- **Decision:** the screen from D-011 becomes two tabs under the title: "Set up" (intro, steps,
  settings button, note) and "Test" (hint and key test result). The tab bar is two plain views with an
  accent-colored indicator, not `TabHost` (deprecated in API 30) or Material's `TabLayout`. The app
  switches to "Test" on the first return after the button opened the settings (also if the user
  went elsewhere in between, e.g. Home and back), and when the key right of P is pressed on "Set up". The selected tab survives recreation. The root no longer
  scrolls; it takes the system bar insets as padding and each tab scrolls on its own. The setup steps
  gain an OTG hint (verified on OnePlus; OPPO named as likely, **unverified**), the "Keyboard layout" path, a note that some IMEs (FUTO) are listed
  by name, a note that an automatic choice of our layout needs no change, and point to the "Test" tab.
- **Why:** the user asked for a more app-like screen; the test was squeezed at the bottom of one long
  page. Setup and test are separate jobs, and the test is what users come back for. A tab can hide
  content, so the automatic switches make sure a result is never off screen. `TabLayout` would add
  AndroidX and Material (several MB against a 27 KB APK, D-011). The old `ScrollView` root with
  `clipToPadding="false"` drew scrolled text under the status bar (found on the OnePlus 12,
  `research/testing.md`). The text changes come from the same test.
- **Consequences:** the tab indicator color is `colorAccent` on API 21+ and `textColorPrimary` below,
  resolved in code. TalkBack announces the tabs as selected or not, but not as tabs. A role description could be added
  with a platform `AccessibilityDelegate` (API 19+) if needed. New and changed Icelandic strings need the user's proofreading. Verified on the OnePlus 12
  (API 36, dark, EN and IS) and the API 37 emulator (light). *Update 25.11.2026:* the pre-API 30 insets
  path and the pre-API 23 and pre-API 21 color branches were run on emulators (API 29, 22, 19, 16) and
  render correctly (`research/testing.md`). The setup steps do not yet fit Android 13 and older (R-4).

## D-014 · 25.11.2026 · Shared task list in `docs/roadmap.md`

- **Decision:** open tasks live in a tracked `docs/roadmap.md`, numbered R-n, with a "Next up" line.
  `HANDOFF.md` keeps per-machine session state and points to it.
- **Why:** the user works from more than one machine and wants the task list to carry between them.
  `HANDOFF.md` is gitignored (D-004), and `CONTEXT.md` is for stable facts, not volatile tasks.
  GitHub Issues would also carry, but they are public and outward-facing, and a file keeps the agent's
  start-of-session reading in one place.
- **Consequences:** the roadmap must be updated and committed when tasks change (CLAUDE.md, end of
  session). It stays machine-agnostic: which machine takes which task is not recorded there.

## D-015 · 25.11.2026 · Keep the KCM audit differences as they are; the upstream patch stays Ð/ð only

- **Decision:** after the key-by-key audit (`research/kcm-audit.md`), change nothing in the layout
  beyond the Ð/ð fix. The three AltGr differences from Windows (D1: no `°` on the key under Esc; D2, D3:
  plain `^` and `` ` `` instead of dead keys) stay as AOSP has them, and the Gerrit patch (R-2) contains
  only the Ð/ð change.
- **Why:** the references disagree on these keys (Windows, xkeyboard-config's `basic` and `mac`
  variants), the standard (ÍST 125) was not read, and each would be a separate behavior change. The
  project's rule is that the only change from AOSP is the intended fix (CLAUDE.md).
- **Consequences:** revisit if ÍST 125 or a user report shows AOSP wrong on those keys. Then add the
  correction to the same Gerrit patch if it is not yet merged, and to the app as an update (R-8).

## D-016 · 25.11.2026 · Drop the ÍST 125 and macOS comparison; the AltGr differences are out of scope

- **Decision:** don't buy or read ÍST 125, and don't compare Apple's macOS layout. The three AltGr
  differences from the audit (D1 to D3, `research/kcm-audit.md`) are not pursued. Supersedes the
  "revisit if ÍST 125 ..." consequence of D-015 and closes R-8.
- **Why:** the project fixes the Ð/ð bug. The audit found no second error of that kind and the Windows
  and Linux references agree on the Ð/ð key (`research/kcm-audit.md`, comparison table). The AltGr
  differences are small, and the two references disagree on D1 (Windows `°`, xkeyboard-config `¬`).
  ÍST 125:2015 costs 10.032 kr. at Staðlaráð Íslands (https://www.stadlar.is, listing read 25.11.2026).
  The user judged chasing AltGr choices out of scope.
- **Consequences:** the Gerrit patch (R-2) and the app contain only the Ð/ð change. If a user reports
  one of the three keys as wrong, reopen it as a separate, documented behavior change.

## D-017 · 25.11.2026 · Accept real-device evidence for automatic selection on Android 14+; claim only what was verified

- **Decision:** the README says automatic selection (D-012) is verified on Android 15, 16 and 17 and
  **not verified on Android 14**. No test IME is built and no Gboard is sideloaded to close the gap.
  The `minSdk 16` floor (D-010) stays. The setup text for Android 13 and older is fixed under R-4, not
  here.
- **Why:** the R-3 emulator runs (`research/testing.md`) showed that the Gboard preload on the API 34
  image (12.4) registers no IME subtype for Icelandic, so there is no `is-IS` subtype to match against.
  The API 35 emulator (Gboard 14.2) does register one and auto-selects our layout after a plain
  install, which agrees with the two real devices on API 36. Closing the gap would need a Play Store
  sign-in, a Gboard APK from a third party, or a custom test IME: all more cost and risk than the claim
  is worth. The user chose to accept the existing evidence.
- **Consequences:** Android 14 users may have to select the layout by hand, as the instructions
  already say. Anyone with an Android 14 device and a current Gboard can close the gap with a device
  report. Before API 34 there is no per-language selection at all; on API 16–33 the layout has to be
  enabled and the built-in "Icelandic" switched off (both verified on emulators). The `setup_note`
  does not say that yet (R-4).