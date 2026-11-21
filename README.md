# FCLDriverPlugin — PanVK (Mali)

A [FoldCraftLauncher](https://github.com/FCL-Team/FoldCraftLauncher) (FCL) driver plugin that bundles **PanVK**, Mesa's open-source Vulkan driver for **Arm Mali** GPUs, packaged as an installable APK. After installing, select it in FCL under *Version Settings → Driver*.

## Bundled driver

- **Driver:** PanVK Mali (Bifrost & Valhall, kbase) — **r10**, tag [v1.6.0](https://github.com/JimVulkan/mali-panvk/releases/tag/v1.6.0) of [JimVulkan/mali-panvk](https://github.com/JimVulkan/mali-panvk)
- **Base:** Mesa 27.0.0-devel (commit `58f2c55`), Vulkan 1.3.363
- **Library:** `libvulkan_panfrost.so` (arm64-v8a only)
- **Package:** `mali-panvk-27.0.0-devel-r10.zip`, sha256 `7e97db4d79d81ec9461d1e0676b62a865561cb56e21c151693aa25ffbe749f1c`

PanVK runs on the **stock Arm kbase kernel driver** (`/dev/mali0`) that production Mali devices already ship: **no root and no custom kernel required**. Upstream Mesa panvk only supports the mainline panfrost/panthor kernel drivers; the kbase port is what makes it usable on retail phones and tablets.

### Supported GPUs

| Architecture | GPUs | Vulkan |
|---|---|---|
| Bifrost | Mali-G31, G51, G52, G71, G72, G76 | 1.4 (1.3 on the G71 and others with fewer than 8 colour attachments) |
| Valhall, first generation | Mali-G57, G68, G77, G78 | 1.4 |
| Valhall / 5th gen with the command stream frontend | Mali-G610, G710, G615, G715, G720, Immortalis-G925, Mali-G1-Ultra | 1.4 (G610, G710, G715 untested) |

Tested by upstream on: Galaxy S10e (Mali-G76, Exynos 9820) and Galaxy Tab S10 Ultra (Immortalis-G720, Dimensity 9300+).

### Requirements

- Android 12 (API 31) or newer, 64-bit device.
- A device with a supported Mali GPU (the plugin declares `mali`; FCL will fall back to the system driver on other GPUs).
- An FCL build with generic custom driver support (`driver_lib` / `driver_gpu`; older builds that only bundle Turnip ignore these keys).

### Known limitations (from upstream)

- No depth bounds test and no 64-bit floats in shaders (Mali has no hardware for either).
- Direct3D 12 (via DXVK/vkd3d-style layers) tops out at feature level 11.1; unbounded (bindless) descriptor ranges do not start on Bifrost.
- Pipeline statistics queries do not count indirect draws or fragment shader invocations.
- No mesh shaders (no Bifrost or Valhall GPU has them in hardware).
- On devices with 4 GB RAM or less, give Minecraft about 600 MB of Java heap.

## Plugin contract

FCL identifies driver plugins through manifest metadata:

| metaData | Description | Value in this plugin |
|---|---|---|
| `fclPlugin` | Must be `true` | `true` |
| `driver` | Display name in the launcher | `PanVK 27.0.0 r10` |
| `driver_lib` | Driver library file name, must exist in the APK's jniLibs | `libvulkan_panfrost.so` |
| `driver_gpu` | GPU family the driver targets: `adreno` / `mali` / `any` | `mali` |

All values are configured via `manifestPlaceholders` in `app/build.gradle.kts`.

> **Note:** Mesa's panvk ICD library is named **`libvulkan_panfrost.so`**, not `libvulkan_panvk.so`.

## Building the plugin

1. The driver library is already in place at `app/src/main/jniLibs/arm64-v8a/libvulkan_panfrost.so`. To use a different build, replace it there.
2. (Optional) Adjust the display name, package suffix, and placeholders in `app/build.gradle.kts`.
3. Build:

   ```bash
   ./gradlew assembleRelease
   ```

4. Install the generated APK (`app/release/` / `app/build/outputs/apk/`), then pick the driver in FCL under *Version Settings → Driver*.

## How FCL loads the driver

- FCL passes the plugin's extracted native library directory as `DRIVER_PATH` to the game process; a linker hook intercepts the Vulkan loader's `android_dlopen_ext` and loads the library named by `driver_lib` from that directory.
- The custom driver path is only engaged when the device's GPU family matches `driver_gpu`; otherwise the system driver is used.
- The pre-launch Vulkan check evaluates per-MC-version support from the actually enumerated version/extensions/features; if driver initialization fails, FCL falls back to the system driver and says so.

## Adapting this template to other drivers

To package a different Vulkan driver with this template, edit `app/build.gradle.kts`:

```kotlin
resValue("string","app_name","<Driver name>")          // app name
applicationIdSuffix = ".<unique-suffix>"                // unique per driver so multiple plugins can coexist

manifestPlaceholders["driver"] = "<Driver name>"        // display name in the launcher
manifestPlaceholders["driverLib"] = "<driver .so>"      // must exist under src/main/jniLibs/<abi>/
manifestPlaceholders["driverGpu"] = "adreno|mali|any"   // target GPU family
```

## References

- PanVK driver builds (this bundle): [JimVulkan/mali-panvk](https://github.com/JimVulkan/mali-panvk) — [v1.6.0 / r10 release notes](https://github.com/JimVulkan/mali-panvk/releases/tag/v1.6.0)
- Other kbase-port panvk builds: [zenithblue-oss/panvk-kbase-android](https://github.com/zenithblue-oss/panvk-kbase-android), [FristOneRR/FristOneRR-Panvk-Driver](https://github.com/FristOneRR/FristOneRR-Panvk-Driver)
- Turnip (Adreno) driver builds: [K11MCH1/AdrenoToolsDrivers](https://github.com/K11MCH1/AdrenoToolsDrivers)
- Related FCL issues: [#1614](https://github.com/FCL-Team/FoldCraftLauncher/issues/1614), [#1937](https://github.com/FCL-Team/FoldCraftLauncher/issues/1937)

## License

The bundled `libvulkan_panfrost.so` is built from Mesa (MIT) with kbase-port changes by JimVulkan; its full third-party notice is shipped in [`app/src/main/assets/NOTICE.txt`](app/src/main/assets/NOTICE.txt).
