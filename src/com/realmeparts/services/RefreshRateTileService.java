/*
 * Copyright (C) 2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.realmeparts;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;
import android.os.Build;

@TargetApi(24)
public class RefreshRateTileService extends TileService {
    private boolean enabled = false;
    private boolean isSmoothDisplayEnabled = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPrefs.getBoolean("refresh_rate_90_device", false)) {
            getQsTile().setState(Tile.STATE_UNAVAILABLE);
            getQsTile().setLabel(getResources().getString(R.string.unsupported));
        } else {
            enabled = RefreshRateSwitch.isCurrentlyEnabled(this);
            getQsTile().setIcon(Icon.createWithResource(this,
                    GetSmoothDisplay() ? R.drawable.refresh_rate_90forced_icon : (enabled ? R.drawable.ic_refresh_tile_90 : R.drawable.ic_refresh_tile_60)));
            getQsTile().setState(GetSmoothDisplay() ? Tile.STATE_ACTIVE : (enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE));
            getQsTile().setLabel(GetSmoothDisplay() ? "Smooth Display" : "Refresh Rate");
        }
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = RefreshRateSwitch.isCurrentlyEnabled(this);
        isSmoothDisplayEnabled = DeviceSettings.mRefreshRate90Forced.isEnabled();

        boolean sTile = false;
        if (getQsTile().getLabel() == "Refresh Rate") {
            sTile = true;
        }

        if (sTile && enabled && isSmoothDisplayEnabled) {
            DeviceSettings.mRefreshRate60.setEnabled(false);
            DeviceSettings.mRefreshRate90.setEnabled(false);
            RefreshRateSwitch.setRefreshRateFinal((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? 1 : 2);
            sharedPrefs.edit().putBoolean("refresh_rate_90Forced", true).apply();
            DeviceSettings.mRefreshRate90Forced.setChecked(true);
            DeviceSettings.mGameFPS.setEnabled(false);
        } else if (sTile && enabled && !isSmoothDisplayEnabled) {
           RefreshRateSwitch.setRefreshRateFinal(0);
           sharedPrefs.edit().putBoolean("refresh_rate_60", true).apply();
           sharedPrefs.edit().putBoolean("refresh_rate_90", false).apply();
           DeviceSettings.mRefreshRate90.setChecked(false);
           DeviceSettings.mRefreshRate60.setChecked(true);
           DeviceSettings.mGameFPS.setEnabled(true);
        } else if (sTile && !enabled){
            RefreshRateSwitch.setRefreshRateFinal(1);
            sharedPrefs.edit().putBoolean("refresh_rate_90", true).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_60", false).apply();
            DeviceSettings.mRefreshRate90.setChecked(true);
            DeviceSettings.mRefreshRate60.setChecked(false);
            DeviceSettings.mGameFPS.setEnabled(false);
            /// Smooth Display Disabled in A12
            if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)){DeviceSettings.mRefreshRate90Forced.setEnabled(true);}
            /// Smooth Display Disabled in A12
        }
        else if (getQsTile().getLabel() == "Smooth Display" && enabled){
            DeviceSettings.mRefreshRate60.setEnabled(true);
            DeviceSettings.mRefreshRate90.setEnabled(true);
            DeviceSettings.mRefreshRate90Forced.setEnabled(false);
            RefreshRateSwitch.setRefreshRateFinal((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? 0 : 3);
            sharedPrefs.edit().putBoolean("refresh_rate_60", true).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_90", false).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_90Forced", false).apply();
            DeviceSettings.mRefreshRate90Forced.setChecked(false);
            DeviceSettings.mRefreshRate90.setChecked(false);
            DeviceSettings.mRefreshRate60.setChecked(true);
            DeviceSettings.mGameFPS.setEnabled(true);
        }
        getQsTile().setLabel(GetSmoothDisplay() ? "Smooth Display" : "Refresh Rate");
        getQsTile().setIcon(Icon.createWithResource(this,
                GetSmoothDisplay() ? R.drawable.refresh_rate_90forced_icon : (enabled ? R.drawable.ic_refresh_tile_60 : R.drawable.ic_refresh_tile_90)));
        getQsTile().setState(GetSmoothDisplay() ? Tile.STATE_ACTIVE : (enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE));
        getQsTile().updateTile();
    }

    public boolean GetSmoothDisplay() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.getBoolean("refresh_rate_90Forced", false) && DeviceSettings.mRefreshRate90Forced.isEnabled();
    }
}
