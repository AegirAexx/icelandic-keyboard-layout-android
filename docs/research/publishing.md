# Publishing

**Status:** prior-art check done by web and GitHub search (25.11.2026); no store submission started.
The F-Droid and Google Play notes below are mostly **unverified**. Check current policies before acting
on them.

## 0. Prior art check (do first)

The plan is to publish only if no equivalent exists. Search:
- F-Droid (https://f-droid.org) and Google Play for "Icelandic keyboard layout", "physical keyboard
  layout", "KCM"
- GitHub for `keyboard_layout_icelandic` and `QUERY_KEYBOARD_LAYOUTS`
- Generic layout tools such as ExKeyMo (generates custom layout APKs). If an equivalent exists, the user
  may still prefer a dedicated, maintained Icelandic app.

Record findings here with dates.

### Findings (25.11.2026)

- **No equivalent app found by web search.** Searches (25.11.2026) turned up no F-Droid or Google Play
  app that ships a corrected Icelandic physical-keyboard layout. Neither store was searched directly.
  The only Icelandic hit was "Icelandic for AnySoftKeyboard"
  (`com.anysoftkeyboard.languagepack.icelandic`, Apache-2.0, 5.1.6158, 15.07.2025,
  https://f-droid.org/packages/com.anysoftkeyboard.languagepack.icelandic/). Its description says it adds
  Icelandic layouts and a dictionary to the AnySoftKeyboard app, so it is an on-screen language pack for
  another IME, not a physical layout (judged from the store text; the package was not inspected).
  Limits: web search is not an exhaustive store search. Google Play was not searched directly, and
  ExKeyMo-generated APKs would not be listed under an Icelandic name.
- **GitHub code search** (`QUERY_KEYBOARD_LAYOUTS`, 25.11.2026) found other layout-provider apps, none
  built for Icelandic:
  `calin-darie/extra-keyboard-layouts`, `ris58h/custom-keyboard-layout` and `ris58h/exkeymo`,
  `OctoNezd/OculusLayouts`, `anssih/finqwerty`, `tialaramex/BritishKeyboard`, `anisse/bepo-android`,
  `Viatorus/neo2-external-keyboard`, `alisabzevari/PersianHardwareKeyboard`, among others. They confirm
  the mechanism is established and give no reason to doubt D-001. Two of them,
  `ris58h/exkeymo` and `OctoNezd/OculusLayouts`, contain the uncorrected Icelandic KCM.
- Details of the upstream search: `upstream.md` §1.

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
