# Publishing

**Status:** not started. Mostly **unverified** notes. Check current policies before acting on them.

## 0. Prior art check (do first)

The plan is to publish only if no equivalent exists. Search:
- F-Droid (https://f-droid.org) and Google Play for "Icelandic keyboard layout", "physical keyboard
  layout", "KCM"
- GitHub for `keyboard_layout_icelandic` and `QUERY_KEYBOARD_LAYOUTS`
- Generic layout tools such as ExKeyMo (generates custom layout APKs). If an equivalent exists, the user
  may still prefer a dedicated, maintained Icelandic app.

Record findings here with dates.

## F-Droid (unverified)

- Requires a FOSS license (Apache-2.0 is fine) and builds from source on F-Droid's build server.
- No proprietary dependencies (no Google Play Services, Firebase, or proprietary analytics). This app
  needs none.
- Store metadata can live in the repo as Fastlane structure (`fastlane/metadata/android/<locale>/`).
  Plan for `en-US` and `is-IS`.
- Submission: merge request to `fdroiddata` on GitLab. Reproducible builds are optional but valued.

## Google Play (unverified)

- Developer account, one-time fee. New personal accounts reportedly must run a closed test with a minimum
  number of testers for a minimum period before production access. Check the current numbers.
- Must meet the current `targetSdk` requirement.
- Needs a privacy policy URL. Simple here: the app collects no data.

## Application ID

**Decided (D-008, 24.11.2026):** `io.github.aegiraexx.icelandickeyboard`.

Naming rules, **verified** against https://developer.android.com/build/configure-app-module
(read 24.11.2026), quoted verbatim:

> - It must have at least two segments (one or more dots).
> - Each segment must start with a letter.
> - All characters must be alphanumeric or an underscore \[a-zA-Z0-9_\].

> Once you publish your app, you should never change the application ID. If you change the
> application ID, Google Play Store treats the upload as a completely different app.

The chosen ID satisfies these rules: three segments after `io`, each starts with a letter, only
lowercase letters. Uppercase is technically allowed; the lowercase form of the GitHub username
`AegirAexx` follows the reverse-DNS convention (GitHub usernames are case-insensitive, **unverified**
here but widely documented).

Alternatives considered: `io.github.ori_aegir.icelandickeyboard` (dropped with the move off the work
account, D-006) and a reverse-DNS name under a domain the author owns (rejected, see D-008).
