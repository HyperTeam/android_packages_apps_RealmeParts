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
### DC-Dimming
### sRGB Mode
## Setting up Realme Parts

    git clone https://github.com/HyperTeam/packages_apps_RealmeParts.git packages/apps/RealmeParts
### Build the package
```
# Parts
PRODUCT_PACKAGES += \
	RealmeParts
```
## Overview
<img align="left" width="400" height="800" src="/RealmeParts.gif">
