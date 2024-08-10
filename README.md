# Biometric Bypass Module

## Overview

This Xposed module is a simple yet effective tool that bypasses the confirm button after successful face authentication, providing a seamless experience. The module is specifically designed for Android 14 and has been only tested on the Pixel 8 Pro (husky) using the August build (ap2a.240805.005).

## How It Works

Android's default behavior, as described in the official [Android documentation](https://developer.android.com/training/sign-in/biometric-auth#no-explicit-user-action), typically requires users to manually confirm their identity even after successful face authentication. This is the default behavior, and the app developer needs to override it. While this additional confirmation provides an extra layer of security, it can also be cumbersome for users seeking a seamless experience.

This module streamlines the process by automatically bypassing the confirmation step for apps using the BiometricPrompt API, making face unlock truly hands-free. It enhances the usability of the biometric prompt and reinforces its purpose by eliminating the need for additional user actions after successful authentication.

## Risks Involved

Bypassing the confirmation step removes a layer of security, which could lead to potential risks:

1. **Unintentional Actions**: Without the confirm button, actions such as financial transactions, app logins, or payments could be completed without the user’s explicit consent, just by scanning the face.

2. **Unauthorized Access**: If someone else gains access to your device while it’s unlocked, they could trigger sensitive actions that would normally require an additional confirmation.

3. **Reduced User Awareness**: Users might become less aware of what actions are being taken on their behalf, leading to unintended consequences.

### Examples of Risky Scenarios

- **Banking Apps**: An app could execute a transaction or approve a payment without the user’s explicit consent, relying solely on biometric authentication.

- **Shopping Apps**: A purchase could be completed immediately after face recognition, without giving the user a final chance to review the transaction details.

- **Sensitive App Logins**: Apps that contain sensitive data, such as password managers, could grant access without confirming the user's intention.

## Installation

1. Install [LSPosed](https://github.com/mywalkb/LSPosed_mod/releases) on your Android device.
2. Download and install the Biometric Bypass Module APK.
3. Activate the module in your LSPosed or Xposed manager.
4. Restart the System UI to apply the changes.

## Compatibility (Tested on)

- **Android Version**: 14
- **Device**: Pixel 8 Pro (husky)
- **Build**: ap2a.240805.005

## Contributions

I’m completely new to reverse engineering Android code, so contributions and improvements are always welcome. If you have ideas for enhancements, or if you encounter any issues, feel free to submit a pull request or open an issue.

## Disclaimer

This module modifies system behavior, which can impact the security model of your device. Use it at your own risk and understand that bypassing the confirm button can have significant security implications.
