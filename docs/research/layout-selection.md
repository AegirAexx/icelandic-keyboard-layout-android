# Automatic layout selection on Android 14+ (API 34+)

How Android picks a physical keyboard layout when the user hasn't chosen one, and what that means for
this app. Source read on 24.11.2026 from `refs/heads/main` of `platform/frameworks/base`. As served
that day, `main` was at commit `1cdfff555f4a21f71ccc978290e2e212e2f8b168` (committed 26.03.2025), so the
quotes can be checked against that commit even after `main` moves. Emulator tests on API 37 the same
day.

Sources:
- `KeyboardLayoutManager.java`:
  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/services/core/java/com/android/server/input/KeyboardLayoutManager.java
- `KeyboardLayout.java`:
  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/core/java/android/hardware/input/KeyboardLayout.java
- AOSP provider manifest and Icelandic label:
  https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/packages/InputDevices/AndroidManifest.xml,
  `packages/InputDevices/res/values-is/strings.xml`

## Summary

1. **The selection is per keyboard and per IME subtype** (verified, source and emulator). A layout
   picked under "English (US)" does not apply when the IME is on an Icelandic subtype.
2. **With an Icelandic IME subtype, automatic selection picks the stock (broken) layout over ours**
   (verified, source and emulator). Users must select ours by hand under every language they use.
3. **Adding `is-IS` to our `keyboardLocale` makes ours win** for Gboard's `is-IS` subtype (verified on
   the emulator, clean install, with a control; confirmed on a OnePlus 12, API 36, with a keyboard new to
   the phone). Applied in D-012 as `is-IS,is-Latn`. It does not help IMEs whose subtype has no language
   tag, such as FUTO (verified on the same phone).
4. **Gboard switches its subtype to match the focused app's locale** (verified on the emulator). This
   explains the earlier per-app locale finding in `testing.md`.
5. **The auto-selection cache survives an app update** (verified, source and emulator). It is cleared
   only when the set of installed layout descriptors changes.

## How selection works (verified, source)

The stored user choice is keyed by keyboard plus IME subtype (`LayoutKey`):

```java
        public String toString() {
            if (mImeInfo == null) {
                return mKeyboardIdentifier.toString();
            }
            Objects.requireNonNull(mImeInfo.mImeSubtypeHandle, "subtypeHandle must not be null");
            return "layoutDescriptor:" + mKeyboardIdentifier + ",userId:" + mImeInfo.mUserId
                    + ",subtypeHandle:" + mImeInfo.mImeSubtypeHandle.toStringHandle();
        }
```

`getKeyboardLayoutForInputDeviceInternal` returns the user's choice for that key if one exists,
otherwise an auto-selected layout (cached per key). The auto-selection order, from the Javadoc of
`getDefaultKeyboardLayoutBasedOnImeInfo`:

```java
     *     <li> Layout selection Algorithm:
     *     <ul>
     *         <li> Choose product specific layout(KCM file with matching vendor ID and product
     *         ID) </li>
     *         <li> If none, then find layout based on PK layout info (based on country code
     *         provided by the HID descriptor of the keyboard) </li>
     *         <li> If none, then find layout based on IME layout info associated with the IME
     *         subtype </li>
     *         <li> If none, return null (Generic.kcm is the default) </li>
```

Language tag scoring, in `getMatchingLayoutForProvidedLanguageTag`:

```java
                float layoutScore = 1; // If language matches then score +1
                if (l.getCountry().equals(locale.getCountry())) {
                    layoutScore += 1; // If country matches then score +1
                } else if (TextUtils.isEmpty(l.getCountry())) {
                    layoutScore += 0.5; // Consider empty country as semi-match
                }
                if (l.getVariant().equals(locale.getVariant())) {
                    layoutScore += 1; // If variant matches then score +1
                } else if (TextUtils.isEmpty(l.getVariant())) {
                    layoutScore += 0.5; // Consider empty variant as semi-match
                }
                if (layoutScore > bestMatchingLayoutScore) {
                    bestMatchingLayoutScore = layoutScore;
                    bestMatchingLayout = layout.getDescriptor();
```

The comparison is a strict `>`, so on a tie the first layout in sort order wins. The list is sorted
by `KeyboardLayout.compareTo`:

```java
    public int compareTo(KeyboardLayout another) {
        // Note that these arguments are intentionally flipped since you want higher priority
        // keyboards to be listed before lower priority keyboards.
        int result = Integer.compare(another.mPriority, mPriority);
        if (result == 0) {
            result = Integer.compare(mLayoutType.mValue, another.mLayoutType.mValue);
        }
        if (result == 0) {
            result = mLabel.compareToIgnoreCase(another.mLabel);
        }
        if (result == 0) {
            result = mCollection.compareToIgnoreCase(another.mCollection);
        }
        return result;
    }
```

Priority is only honored for system apps. Third-party providers get 0:

```java
        int priority;
        if ((receiver.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            priority = requestedPriority;
        } else {
            priority = 0;
        }
```

AOSP's `InputDeviceReceiver` sets no `android:priority` in its intent filter, so the stock layouts
also have priority 0. Both Icelandic layouts are `qwerty`. The tie is broken by label, resolved in the
system language. With an English or Icelandic system language the stock layout wins: "Icelandic" sorts
before "Icelandic (corrected Ð)", and AOSP's Icelandic label "Íslenskt" sorts before "Íslenskt
(leiðrétt Ð)". **In other system languages ours can win the tie.** This app only has English and
Icelandic labels, so elsewhere it shows "Icelandic (corrected Ð)" against AOSP's translation. For
example, in German "Icelandic …" sorts before "Isländisch" (`c` < `s`). Verified from the sort code
and the AOSP translations (blind review, 24.11.2026); not tested on a device.

## Scores for Gboard's Icelandic subtype

Gboard's Icelandic subtype has `mSubtypeLocale=is_IS`, `mSubtypeLanguageTag=is-IS` and no physical
keyboard hint (from `ime list -a`, emulator). The IME step therefore matches against `is-IS`:

| Layout | `keyboardLocale` | Language | Country | Variant | Score |
|---|---|---|---|---|---|
| Stock (AOSP) | `is-Latn` | 1 | 0.5 (empty) | 1 (both empty) | 2.5 |
| Ours today | `is-Latn` | 1 | 0.5 | 1 | 2.5, tie, stock wins on label |
| Ours with `is-IS` only | `is-IS` | 1 | 1 | 1 | **3, ours wins** |
| Ours with `is-IS,is-Latn` (D-012) | best of both | | | | **3, ours wins** |

For a subtype tagged only `is` (no country), `is-IS` alone would score 2 against stock's 3, so stock
would win even in the system languages where ours used to win the tie: a regression. Keeping `is-Latn`
next to it avoids that: the scorer keeps a layout's best score across its locales, so ours scores 3 via
`is-Latn` and the tie is broken by label as before. `is-IS,is-Latn` is therefore never worse than
`is-Latn` alone. Which tags Samsung Keyboard and SwiftKey use is **unverified**. FUTO Keyboard (Play
Store build) uses none: it exposes a single subtype with an empty locale and no language tag, so
auto-selection has nothing to match and gives the generic layout (verified on a OnePlus 12, API 36,
24.11.2026; see `testing.md`). FUTO users must select our layout by hand.

The hardware step runs before the IME step: a keyboard that reports its own language tag
(`InputDevice.getKeyboardLanguageTag()`, from the HID country code or the IDC file) is matched on
that tag first. Whether real USB/Bluetooth keyboards sold in Iceland report one is **unverified**. The
HID country code list may have no Iceland entry (**unverified**), and most keyboards report
0 (not supported).

## Emulator results (API 37, 24.11.2026)

Gboard with English (US) and Íslenska (Ísland) enabled. The "Automatic: …" lines are what Settings →
Physical keyboard → device shows per language when the user has chosen nothing.

| Build installed (clean install) | AT Translated Set 2 keyboard, Íslenska (Ísland) |
|---|---|
| None (stock only) | Automatic: Icelandic |
| Ours, `keyboardLocale="is-Latn"` (current) | Automatic: Icelandic (stock wins) |
| Ours, `keyboardLocale="is-IS"` (scratch build) | **Automatic: Icelandic (corrected Ð)** |
| Ours, `keyboardLocale="is-IS,is-Latn"` (D-012) | **Automatic: Icelandic (corrected Ð)** |

Under English (US) all three show "Automatic: English (US)".

The emulator's other keyboard, `qwerty2` (the one the console types through), shows "Automatic:
English (US)" under Íslenska. That fits the hardware step: qwerty2 likely reports an English language
tag. This is **unverified**, because `/vendor/usr/idc/qwerty2.idc` is not readable without root. So
qwerty2 cannot demonstrate auto-selection by typing. Its manual selections can: with "Icelandic
(corrected Ð)" selected under both languages, the key test passed with either subtype active.

### Gboard follows the app's locale (verified, emulator)

`dumpsys input_method` `mCurrentSubtype` prints `InputMethodSubtype@<hash>`, and `hashCode()` is the
subtype hash, so it identifies the subtype.

- Icelandic **not** enabled in Gboard, app locale `is-IS`: Gboard switched to a temporary `is-IS`
  subtype (`-1179739310`, not in the enabled list). Settings offers no language entry for it, so the
  user cannot select a layout for it. The key typed `[` (qwerty2 falls back to its hardware tag).
- Icelandic enabled: app locale `is-IS` switches to the enabled Icelandic subtype (`-420501798`).
  App locale cleared switches back to English (US) (`1594443099`). With our layout selected under both,
  both passed.

So a user whose apps run in Icelandic must have Icelandic enabled in Gboard, then select our layout
under it.

### Auto-selection cache (verified, source and emulator)

`updateKeyboardLayouts()` clears the cache only when the set of layout descriptors changes:

```java
        // If available layouts don't change, there is no need to reload layouts.
        if (mAvailableLayouts.equals(availableKeyboardLayouts)) {
            return;
        }
```

An in-place update (`adb install -r`, or a store update) keeps our descriptor, so a changed
`keyboardLocale` does not take effect for layouts already auto-selected until the cache is rebuilt.
A clean install, an uninstall of any provider, or a reboot rebuilds it (the reboot case is
**unverified**, but the cache is in memory only). Uninstalling also removes stored user selections of
our layout (`removeUninstalledKeyboardLayouts`).

## Real device result (OnePlus 12, OxygenOS 16, API 36, 24.11.2026)

D-012 holds on real hardware. A Ducky USB keyboard that had never been connected to the phone, and that
reports no language of its own, showed "Automatic: Icelandic (corrected Ð)" under Gboard Íslenska
(Ísland) with no manual selection, and its key right of P typed U+00F0 / U+00D0. Under FUTO the same
keyboard showed "Default" (see above). Details in `testing.md`.

## Implications

- **User instructions:** selecting the layout under each language stays in the setup steps as the
  fallback for IMEs and Android versions where auto-selection doesn't pick ours. The steps now also
  tell users to add Icelandic to the on-screen keyboard's languages first, so it appears in the list.
- **`keyboardLocale="is-IS,is-Latn"` (D-012, applied):** for Gboard users with Icelandic enabled on
  API 34+, the fix applies with no manual selection. Trade-offs: it deviates from AOSP's `is-Latn`; it
  only helps subtypes tagged with country `IS`; and it changes nothing below API 34, where
  `keyboardLocale` is ignored.
- **Cache caveat for future releases:** no version has been released yet (v0.1.0 is unreleased,
  CHANGELOG `[Unreleased]`), so no install has a stale cache entry. A later release that changes the
  locales would only take effect on existing installs once the cache is rebuilt (see above).
