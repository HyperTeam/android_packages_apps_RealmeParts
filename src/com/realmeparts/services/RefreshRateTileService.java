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
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

@TargetApi(24)
public class RefreshRateTileService extends TileService {
    private boolean enabled = false;
    private boolean isSmoothDisplayEnabled = false;

    private IBinder SF = ServiceManager.getService("SurfaceFlinger");

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
        enabled = sharedPrefs.getBoolean("refresh_rate_90", false) || sharedPrefs.getBoolean("refresh_rate_90Forced", false);

        /// Smooth Display Disabled in A12
        isSmoothDisplayEnabled = !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S);
        /// Smooth Display Disabled in A12

        boolean sTile = false;
        if (getQsTile().getLabel() == "Refresh Rate") {
            sTile = true;
        }

        if (sTile && enabled && isSmoothDisplayEnabled) {
            RefreshRateTile(true);
            sharedPrefs.edit().putBoolean("refresh_rate_90Forced", true).apply();
            sharedPrefs.edit().putBoolean("game_fps", false).apply();
        } else if (sTile && enabled && !isSmoothDisplayEnabled) {
           RefreshRateTile(false);
           sharedPrefs.edit().putBoolean("refresh_rate_60", true).apply();
           sharedPrefs.edit().putBoolean("refresh_rate_90", false).apply();
           sharedPrefs.edit().putBoolean("game_fps", true).apply();
        } else if (sTile && !enabled){
            RefreshRateTile(true);
            sharedPrefs.edit().putBoolean("refresh_rate_90", true).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_60", false).apply();
            sharedPrefs.edit().putBoolean("game_fps", false).apply();
        }
        else if (getQsTile().getLabel() == "Smooth Display" && enabled){
            RefreshRateTile(false);
            sharedPrefs.edit().putBoolean("refresh_rate_60", true).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_90", false).apply();
            sharedPrefs.edit().putBoolean("refresh_rate_90Forced", false).apply();
        }
        getQsTile().setLabel(GetSmoothDisplay() ? "Smooth Display" : "Refresh Rate");
        getQsTile().setIcon(Icon.createWithResource(this,
                GetSmoothDisplay() ? R.drawable.refresh_rate_90forced_icon : (enabled ? R.drawable.ic_refresh_tile_60 : R.drawable.ic_refresh_tile_90)));
        getQsTile().setState(GetSmoothDisplay() ? Tile.STATE_ACTIVE : (enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE));
        getQsTile().updateTile();
    }

    public boolean GetSmoothDisplay() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.getBoolean("refresh_rate_90Forced", false);
    }

    private void RefreshRateTile(boolean enabled) {
      Settings.System.putFloat(this.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), enabled ? 90f : 60f);
      Settings.System.putFloat(this.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), enabled ? 90f : 60f);
      setForcedRefreshRate((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ? (enabled ? 1 : 0) : (enabled ? 0 : -1));
    }

    public void setForcedRefreshRate(int value) {
        Parcel Info = Parcel.obtain();
        Info.writeInterfaceToken("android.ui.ISurfaceComposer");
        Info.writeInt(value);
        try {
            SF.transact(1035, Info, null, 0);
        } catch (RemoteException e) {
            Log.e("DeviceSettings", e.toString());
        } finally {
            Info.recycle();
        }
    }
}
