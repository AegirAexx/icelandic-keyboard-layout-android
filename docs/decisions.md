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
