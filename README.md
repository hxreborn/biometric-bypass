# Biometric Bypass Module

**Streamlines face unlock by skipping biometric confirmation in System UI (Android 10+)**

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dfcf1c46fa474099a3aef3f5b3dfe8c3)](https://app.codacy.com/gh/hxreborn/biometric-bypass)
![GitHub stars](https://img.shields.io/github/stars/hxreborn/biometric-bypass?style=social)
![GitHub downloads](https://img.shields.io/github/downloads/hxreborn/biometric-bypass/total?label=GitHub%20downloads)
![Xposed repo downloads](https://img.shields.io/github/downloads/Xposed-Modules-Repo/eu.rafareborn.biometricbypass/total?label=Xposed%20repo%20downloads)
![GitHub Release (latest by date)](https://img.shields.io/github/v/release/hxreborn/biometric-bypass)

<p>
  <a href="https://f-droid.org/packages/eu.rafareborn.biometricbypass"><img src=".github/assets/badge_fdroid.png" height="60" alt="Get it on F-Droid" /></a>
  <a href="https://apt.izzysoft.de/packages/eu.rafareborn.biometricbypass"><img src=".github/assets/badge_izzyondroid.png" height="60" alt="Get it on IzzyOnDroid" /></a>
  <a href="../../releases"><img src=".github/assets/badge_github.png" height="60" alt="Get it on GitHub" /></a>
  <a href="http://apps.obtainium.imranr.dev/redirect.html?r=obtainium://app/%7B%22id%22%3A%22eu.rafareborn.biometricbypass%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Fhxreborn%2Fbiometric-bypass%22%2C%22author%22%3A%22rafareborn%22%2C%22name%22%3A%22Biometric%20Bypass%22%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Afalse%7D%22%7D"><img src=".github/assets/badge_obtainium.png" height="60" alt="Get it on Obtainium" /></a>
</p>

## Overview

This LSPosed module streamlines face unlock by skipping the confirmation step enforced after biometric authentication. It applies the bypass system-wide — so it works across **all apps**, including banking or security-sensitive ones.

Android introduced the [`setConfirmationRequired(false)`](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action) flag in Android 10 to support passive authentication flows (e.g., face unlock without requiring a tap). Since most apps don’t disable confirmation explicitly, Android defaults to requiring a manual tap, turning face unlock into a two-step chore.

This module ensures the confirmation step is skipped across all biometric flows, regardless of app implementation.

---

## How it Works

Android 10 (API 29) added support for passive biometric flows via the `setConfirmationRequired(false)` flag in the BiometricPrompt API. This allows apps to skip the "tap to confirm" step after face unlock — **but only** if:

- The app explicitly sets `setConfirmationRequired(false)`
- The biometric method is classified as **Class 3 (strong)** (e.g., secure face unlock on Pixel 8+)

Most apps don’t set this flag, and even when they do, some components still enforce the confirmation dialog.

This module hooks System UI directly to eliminate that dialog, simulating the intended behavior system-wide, no matter what the app does.

---

## Visual Comparison

<p align="center">
    <img src="media/module_disabled.gif" width="200" alt="Face unlock requiring manual confirmation">
    <br/>
    <strong>Default Behavior: Face unlock with manual confirmation required.</strong>
    <br/><br/>
    <img src="media/module_enabled.gif" width="200" alt="Face unlock with confirmation bypassed">
    <br/>
    <strong>Module Enabled: Face unlock with automatic confirmation bypass.</strong>
</p>

---

## Compatibility

- **Android Versions:** 10 and up (API 29+)
- **ROM Support:** AOSP-based ROMs, Pixel, and other close-to-stock systems  
  OEM ROMs (e.g. MIUI, OneUI) are **not tested and probably won't work** due to heavy modifications
- **App Support:** Works globally, including banking and security-sensitive apps by applying the bypass system-wide

---

## Installation

1. Install [LSPosed](https://github.com/LSPosed/LSPosed) with API 101 support (may not be publicly available yet)
2. Download and install the module APK
3. In LSPosed, enable the module and apply it to **System UI**
4. Restart System UI or reboot the device

---

## Legacy Xposed Support (Archived)

These branches are unmaintained and only exist for migration or historical reference:

- [Legacy Xposed - Java](https://github.com/hxreborn/biometric-bypass/tree/legacy-xposed-java)
- [Legacy Xposed - Kotlin](https://github.com/hxreborn/biometric-bypass/tree/legacy-xposed-kotlin)

---

## Risks

Bypassing confirmation reduces friction and security. If someone spoofs your face or waves your phone at you while you’re asleep, they get in. Use responsibly.

---

## Contributing

Pull requests are welcome. Issues too. 

---

## License

MIT. Use it, fork it, misconfigure it.  
If your cat unlocks your phone and sends crypto to North Korea, that’s on you.
