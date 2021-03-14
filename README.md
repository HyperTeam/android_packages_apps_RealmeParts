## FEATURES 
### Smart Charging: 

> Due to SuperVooc charging at high speed the battery/device gets heated eventually, to mitigate this problem we have implemented cool down based on battery temperature & also helps in stopping the charging based on limit set by user including a toggle which can reset battery stats (optional). Smart Charging also allows user to control the charging speed, for now users can choose between 1.8A | 15W & default speed.
### Game Mode: 
>Boost touch sampling rate, on device side you can also set performance tweaks (optional), Do-Not-Disturb (DND) toggle is avaliable with notification support which lets user notify that Game Mode is enabled.

>TODO: Enable Game Mode automatically based on user selected game packages.
### Ambient Display Gestures:
>Which includes Always on Display (AOD), Pulse notifications on pick up, Hand wave gesture etc.,
### Screen Refresh Rate Mode
>User can choose 60Hz or 90Hz, also Smooth Display toggle which can force display to 90Hz.

>TODO: Add Per-App refresh rate setting
### Misc Settings:
>USB-OTG

> FPS Overlay
### DC-Dimming & sRGB Mode
>Currently DC-Dim & sRGB is restricted to realme X2 & X2 Pro since there is no proper way to confirm which realme device has DC-Dim & sRGB for real, so we have added device based checks for it. Check https://github.com/HyperTeam/packages_apps_RealmeParts/blob/master/res/raw/realmeParts_features.json
to know which devices support these both features. You can always send pull request or even open issue if you would like to have these in your devices.  

### CABC Mode
>https://twitter.com/realmeIndia/status/1120227947573665792 (Enabled for realme 3 Pro & 5 Pro, copy cabc init script)

## Setting up Realme Parts

    git clone https://github.com/HyperTeam/packages_apps_RealmeParts.git packages/apps/RealmeParts
### Build the package or include parts makefile
```
# Parts
$(call inherit-product, packages/apps/RealmeParts/parts.mk)
PRODUCT_COPY_FILES += \
    packages/apps/RealmeParts/init/cabc.rc:$(TARGET_COPY_OUT_VENDOR)/etc/init/cabc.rc
```
## Overview
<img align="left" width="400" height="800" src="/RealmeParts.gif">
