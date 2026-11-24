# CLAUDE.md

Agent operating instructions for this repo. Keep this file short: it covers how to work here.
What the project is lives in `CONTEXT.md`, and where it stands lives in `HANDOFF.md`.

## Start of every session

1. Read `CONTEXT.md` for the problem, root cause and chosen approach.
2. Read `HANDOFF.md` for current state, next steps and open questions.
3. Read only the `docs/` files relevant to the task at hand (index below).

## End of every session (mandatory)

- Update `HANDOFF.md`: current state, next steps, open questions, and one line in the session log.
  It is **gitignored** (local-only, D-004). If it is missing, recreate it from `CONTEXT.md`,
  `docs/decisions.md` and `git log`.
- Commit the tracked doc changes. Never push without explicit user approval.
- Record any new non-trivial decision in `docs/decisions.md` (append-only, numbered).
- Put new research and findings in `docs/research/`, not in the root files.
- Correct any doc that turned out wrong. Don't leave stale statements behind.

## Doc rules

- Root files stay lean: `CLAUDE.md` holds rules, `CONTEXT.md` holds stable facts, `HANDOFF.md` holds volatile state.
- Mark every claim as **verified** (with source or test) or **unverified**. Never promote a guess to a fact.
- Quote AOSP source verbatim when it matters, with the URL and the date it was read.
- Dates use the `DD.MM.YYYY` format.

## Environment

The project is worked on from more than one machine and OS (currently Linux and Windows). This file
stays machine-agnostic.

- Detect the OS and shell from the session environment. Don't assume one. Use that shell's syntax and
  path separators.
- Machine-specific facts (OS, shell, installed tools, SDK paths, what was verified) go in the
  **"This machine"** section of `HANDOFF.md`, never here or in other tracked files. If that section
  describes a different OS than the current session, the file came from elsewhere: re-verify and
  rewrite the section.
- Build through the Gradle wrapper (`./gradlew` or `gradlew.bat`), not a system Gradle. Machine paths
  belong in the gitignored `local.properties`.
- Line endings: LF everywhere except `*.bat`/`*.cmd`/`*.ps1` (CRLF). Enforced by `.gitattributes` and
  `.editorconfig`.
- `gradlew` must be committed as executable (mode `100755`). Windows does not track this. After adding
  it from Windows, run `git update-index --chmod=+x gradlew`.

## Engineering conventions

- Kotlin + Gradle (Kotlin DSL), minimal dependencies. The core deliverable is data (a `.kcm` file plus XML), not code.
- Derive the layout from the AOSP Icelandic KCM (Apache 2.0) and keep its license header and attribution.
- The only behavioral change from AOSP should be the intended fix. Diff against the upstream file and justify every changed line in `docs/research/`.
- No root, no accessibility service, no IME unless a decision in `docs/decisions.md` says otherwise.

## Docs index

| File | Purpose |
|---|---|
| `docs/research/root-cause.md` | Verified AOSP source of the bug, Unicode details |
| `docs/research/fix-approaches.md` | Options for a sideloadable fix, with trade-offs |
| `docs/research/kcm-diff.md` | Line-by-line diff of our KCM against AOSP, with justification |
| `docs/research/testing.md` | Test results per device, emulator test procedure, findings |
| `docs/research/upstream.md` | Plan for reporting and patching the bug in AOSP |
| `docs/research/publishing.md` | Prior art check, F-Droid and Google Play notes, application ID |
| `docs/decisions.md` | Numbered decision log |
