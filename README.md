# Biometric Bypass Module

## Overview

This Xposed module bypasses the confirm button after successful face authentication, offering a seamless, hands-free experience. Designed specifically for Android 14, this module has been tested on the Pixel 8 Pro (husky) with the August build (ap2a.240805.005).

## How It Works

By default, Android requires users to manually confirm their identity even after successful face authentication, as outlined in the official [Android documentation](https://developer.android.com/training/sign-in/biometric-auth#no-explicit-user-action). While this default behavior adds an extra layer of security, it can also be inconvenient.

This module automates the confirmation process for apps using the BiometricPrompt API, making face unlock hands-free. It enhances the usability of the biometric prompt by eliminating the need for additional user actions after successful authentication.

## Installation

1. Install [LSPosed](https://github.com/mywalkb/LSPosed_mod/releases) on your Android device.
2. Download and install the Biometric Bypass Module APK.
3. Activate the module in your LSPosed or Xposed manager.
4. Restart the System UI to apply the changes.

## Compatibility (Tested on)

- **Android Version:** 14
- **Device:** Pixel 8 Pro (husky)
- **Build:** ap2a.240805.005

## Risks and Warnings

By bypassing the confirmation step after face authentication, this module can potentially reduce the security of your device. The confirm button is designed to ensure that the user intends to proceed with the authenticated action. Skipping this step may increase the risk of unintended actions being carried out on your device.

**Examples of Risks:**
- Unintended payments or purchases within apps that use biometric authentication.
- Accidental authorization of sensitive actions in apps without explicit user confirmation.

Please consider these risks carefully before using the module.

## Contributions

As I’m new to reverse engineering Android code, contributions and improvements are highly encouraged. If you have ideas for enhancements or encounter any issues, feel free to submit a pull request or open an issue.

## Disclaimer

This module modifies system behavior, which can impact the security model of your device. Use it at your own risk.

## Visual Demonstration

The following GIFs demonstrate the difference in behavior with and without the module enabled:

![Module Enabled](media/module_enabled.gif)
![Module Disabled](media/module_disabled.gif)

- **Left:** Using the module (bypass enabled)
- **Right:** Default behavior (confirmation required)
