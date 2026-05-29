# Biometric Bypass Module

Streamlines face unlock by skipping biometric confirmation in System UI (Android 10+)

![Android 10+](https://img.shields.io/badge/Android-10%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![libxposed API 101](https://img.shields.io/badge/libxposed-API_101-ff69b4?style=for-the-badge)
[![Release](https://img.shields.io/github/v/release/hxreborn/biometric-bypass?style=for-the-badge&logo=github)](https://github.com/hxreborn/biometric-bypass/releases/latest)
[![Build](https://img.shields.io/github/actions/workflow/status/hxreborn/biometric-bypass/android.yml?label=build&style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/hxreborn/biometric-bypass/actions/workflows/android.yml)

<p>
  <a href="https://f-droid.org/packages/eu.rafareborn.biometricbypass"><img src=".github/assets/badge_fdroid.png" height="60" alt="Get it on F-Droid" /></a>
  <a href="https://apt.izzysoft.de/packages/eu.rafareborn.biometricbypass"><img src=".github/assets/badge_izzyondroid.png" height="60" alt="Get it on IzzyOnDroid" /></a>
  <a href="../../releases"><img src=".github/assets/badge_github.png" height="60" alt="Get it on GitHub" /></a>
  <a href="http://apps.obtainium.imranr.dev/redirect.html?r=obtainium://app/%7B%22id%22%3A%22eu.rafareborn.biometricbypass%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fhxreborn%2Fbiometric-bypass%22%2C%22author%22%3A%22rafareborn%22%2C%22name%22%3A%22Biometric%20Bypass%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Afalse%7D%22%7D"><img src=".github/assets/badge_obtainium.png" height="60" alt="Get it on Obtainium" /></a>
</p>

## Overview

This Xposed module streamlines face unlock by skipping the confirmation step enforced after biometric authentication. The bypass applies system-wide, so it works across all apps, including banking or security-sensitive ones.

By default, even after your face is recognized, Android makes you tap to confirm before it lets you through, turning face unlock into a two-step chore. This module removes that step across all biometric flows, regardless of how each app is built.

## How it Works

Android 10 (API 29) added support for passive biometric flows via the [`setConfirmationRequired(false)`](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action) flag in the BiometricPrompt API. This lets apps skip the "tap to confirm" step after face unlock, but only if:

- The app explicitly sets the flag to `false`
- The biometric method is classified as Class 3 (strong) (e.g. secure face unlock on Pixel 8+)

Most apps don't set this flag, and even when they do, some components still enforce the confirmation dialog.

This module hooks `System UI` directly to remove that dialog, simulating the intended behavior system-wide no matter what the app does.

## Visual Comparison

| Default Behavior | Module Enabled |
|:---:|:---:|
| <img src="media/module_disabled.gif" width="220" alt="Face unlock requiring manual confirmation" /> | <img src="media/module_enabled.gif" width="220" alt="Face unlock with confirmation bypassed" /> |
| Face unlock with manual confirmation required | Face unlock with automatic confirmation bypass |

## Compatibility

- **Android Versions:** 10 and up (API 29+)
- **ROM Support:** AOSP-based ROMs, Pixel, and other close-to-stock systems.
  OEM ROMs (e.g. MIUI, OneUI) are not tested and probably won't work due to heavy modifications
- **App Support:** Works globally, including banking and security-sensitive apps, by applying the bypass system-wide

## Installation

1. Install [Xposed](https://github.com/LSPosed/LSPosed/releases) with API 101 support
2. Download and install the module APK
3. In your Xposed manager, enable the module and apply it to `System UI`
4. Restart `System UI` or reboot the device

## Legacy Xposed Support (Archived)

These branches are unmaintained and only exist for migration or historical reference:

- [Legacy Xposed - Java](https://github.com/hxreborn/biometric-bypass/tree/legacy-xposed-java)
- [Legacy Xposed - Kotlin](https://github.com/hxreborn/biometric-bypass/tree/legacy-xposed-kotlin)

## Risks

Bypassing confirmation reduces friction and security. If someone spoofs your face or waves your phone at you while you're asleep, they get in. Use responsibly.

## Contributing

Pull requests are welcome. [Issues](../../issues) too.

## License

MIT. Use it, fork it, misconfigure it.
