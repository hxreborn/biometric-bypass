# Biometric Bypass Module

## Overview

This Xposed module bypasses the confirm button after successful face authentication, offering a seamless, hands-free experience. Designed specifically for Android 14, this module has been tested on the Pixel 8 Pro (husky) with the August build (ap2a.240805.005).

## How It Works

By default, Android requires users to manually confirm their identity even after successful face authentication, as outlined in the official [Android documentation](https://developer.android.com/training/sign-in/biometric-auth#no-explicit-user-action). This is the default behavior, and the app developer needs to override it. While this additional confirmation provides an extra layer of security, it can also be inconvenient.

This module automates the confirmation process for apps using the BiometricPrompt API, making face unlock hands-free. It enhances the usability of the biometric prompt by eliminating the need for additional user actions after successful authentication.

## Visual Example

Below are visual examples of the module in action versus the default behavior:

<p align="center">
    <img src="media/module_enabled.gif" width="200" alt="Module Enabled">
    <img src="media/module_disabled.gif" width="200" alt="Default Behavior">
</p>

- **Left:** Face unlock using the Biometric Bypass module (confirmation automatically bypassed).
- **Right:** Default behavior without the module (confirmation required).

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

As I’m completely new to reverse engineering Android code, contributions and improvements are highly encouraged. If you have ideas for enhancements or encounter any issues, feel free to submit a pull request or open an issue.

## Disclaimer

This module modifies system behavior, which can impact the security model of your device. Use it at your own risk.
