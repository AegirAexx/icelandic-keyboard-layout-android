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
