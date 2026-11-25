# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Project documentation, license, and repository setup.
- Gradle project with the corrected Icelandic layout (`Icelandic (corrected Ð)`), registered through
  Android's keyboard layout provider mechanism. Verified on an Android 17 emulator.
- Instructions screen (English and Icelandic) with a button to the physical keyboard settings and a
  key test that shows whether the key right of P types ð or đ.
- App icon.

### Changed
- The corrected layout declares the locales `is-IS,is-Latn` (AOSP: `is-Latn`), so Android 14+ selects
  it automatically for Gboard's Icelandic language instead of the broken stock layout.
- Setup steps: add Icelandic to the on-screen keyboard's languages first.
- The app has two tabs, "Set up" and "Test", and switches to "Test" when you come back from the
  settings or press the key. Text no longer scrolls under the status bar.
- Setup steps: OTG hint for OnePlus/OPPO, the "Keyboard layout" path, and keyboards such as FUTO that
  are listed by name. Verified on a OnePlus 12 (Android 16).
