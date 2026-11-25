# Contributing

Thanks for helping. Reports from real devices are the most valuable contribution.

## Reporting a device result

Open an issue with:

- Device model, Android version, and manufacturer skin (e.g. One UI 7)
- Physical keyboard (wired/Bluetooth, physical layout)
- On-screen keyboard and its languages (e.g. Gboard with Icelandic and English)
- Whether the corrected layout appeared in settings and was selectable, and whether Android picked it
  automatically or you selected it by hand (and whether a layout had been picked for that keyboard
  before)
- The settings path to the physical keyboard screen on your device
- The **code points** typed, not just how they look. Ð and Đ look identical in uppercase.

## Reporting another wrong key

If another key in the Icelandic layout produces the wrong character, include the key position, the
expected character, and the actual character with code points.

## Code changes

- Keep changes small and focused.
- The layout must match AOSP's Icelandic layout except for documented fixes. Justify every changed line
  of the `.kcm` file.
- Line endings are enforced by `.gitattributes` and `.editorconfig` (LF, except Windows scripts).

By contributing, you agree that your contributions are licensed under the [Apache License 2.0](LICENSE).
