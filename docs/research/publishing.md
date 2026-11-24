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

To decide before the first build. The ID can't change after publishing. It should be a reverse-DNS name
the author controls, or e.g. `io.github.<username>.<app>` if publishing from GitHub.

With the GitHub account `ORI-Aegir` (D-005), the GitHub-derived candidate is
`io.github.ori_aegir.icelandickeyboard`. Hyphens aren't allowed in ID segments, so `-` becomes `_`
(**unverified**: based on the Android `applicationId` naming rules, not checked this session). The ID
would be tied to that account permanently, so confirm it is the account that should own the app.
