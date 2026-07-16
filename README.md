# CameraX High Speed FPS & Slow Motion Sample

A specialized Android sample application demonstrating how to implement high-speed video capture (120/240 FPS) and slow-motion effects using the **CameraX 1.6+ APIs**.

## Features

- **Dynamic Capability Discovery**: Automatically queries device hardware using `Recorder.getHighSpeedVideoCapabilities` to find valid high-speed resolution and FPS combinations.
- **High-Speed Engine**: Implements the latest `HighSpeedVideoSessionConfig` for efficient high-frame-rate capture.
- **Dual Recording Modes**:
    - **High-Frame-Rate (HFR)**: Records at the native high FPS (e.g., 240fps) for player-side control.
    - **Baked-in Slow Motion**: Uses `setSlowMotionEnabled(true)` to re-encode the video at 30 FPS, making the slow-motion effect permanent in the file.
- **Adaptive 16:9 Preview**: A strictly constrained 16:9 preview (`9:16` in portrait) to ensure accurate framing and parity with the recorded output.
- **Ergonomic Orientation Tracking**: The record button dynamically reposition itself to stay centered on the short edge (where the physical USB port is) across all orientations.
- **Automated Media Management**: Saves high-speed recordings to `DCIM/CameraXHighSpeed` using the `MediaStore` API.

## Technical Implementation (CameraX 1.6+)

### 1. Unified Session Binding
High-speed video capture requires a specialized capture session. This app demonstrates the transition from individual `UseCase` binding to the unified `HighSpeedVideoSessionConfig` model required by modern CameraX releases:

```kotlin
// Define the high-speed session
val sessionConfig = HighSpeedVideoSessionConfig.Builder(videoCapture)
    .setPreview(preview)
    .setFrameRateRange(Range(240, 240))
    .setSlowMotionEnabled(true)
    .build()

// Bind the session config as a single atomic unit
cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, sessionConfig)
```

### 2. Capabilities & Constraints
The implementation accounts for the following high-speed constraints:
- **Shared Resolution**: In high-speed mode, `Preview` and `VideoCapture` must share the same internal resolution.
- **Experimental Opt-in**: Uses `@file:OptIn(ExperimentalHighSpeedVideo::class, ExperimentalSessionConfig::class)` to access the latest high-speed engine features.

## Project Architecture

- **`camera.HighSpeedCameraManager`**: The core "backend" that manages hardware discovery, session state, and the `Recording` lifecycle.
- **`ui.HighSpeedViewModel`**: A thin layer that manages UI-specific state and delegates actions to the manager.
- **`ui.CameraScreen`**: A pure Jetpack Compose layer handling layout, orientation math, and user interaction.

## Requirements

- **Minimum SDK**: 35
- **Target/Compile SDK**: 37
- **Hardware**: A physical device supporting `CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES` with values $\ge$ 120 (e.g., Pixel 7/8/9 series).
- **Dependencies**: `androidx.camera:camera-video`, `camera-view`, `camera-lifecycle` (Version 1.6.1+).

## Getting Started

1. Deploy the app to a compatible physical device.
2. Tap the **Gear Icon** (Top-Right) to choose your target quality and FPS.
3. Toggle the **Slow Motion Effect** switch if you want "ready-to-play" slow motion.
4. Press the **Record Button** centered at the physical bottom of the device.
5. View your results in the device Gallery under the `CameraXHighSpeed` folder.
