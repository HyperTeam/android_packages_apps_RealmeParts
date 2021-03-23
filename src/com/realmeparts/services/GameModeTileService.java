/*
 * Copyright (C) 2020 The LineageOS Project
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
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

@TargetApi(24)
public class GameModeTileService extends TileService {
    private boolean enabled = false;
    private Context mContext;
    private NotificationManager mNotificationManager;

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
        enabled = GameModeSwitch.isCurrentlyEnabled(this);
        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        enabled = GameModeSwitch.isCurrentlyEnabled(this);
        if (!enabled) {
            AppNotification.Send(this, GameModeSwitch.GameMode_Notification_Channel_ID, this.getString(R.string.game_mode_title), this.getString(R.string.game_mode_notif_content));
        } else AppNotification.Cancel(this, GameModeSwitch.GameMode_Notification_Channel_ID);
        Utils.writeValue(GameModeSwitch.getFile(), enabled ? "0" : "1");
        Utils.writeValue(DeviceSettings.TP_LIMIT_ENABLE, enabled ? "1" : "0");
        Utils.writeValue(DeviceSettings.TP_DIRECTION, enabled ? "0" : "1");
        SystemProperties.set("perf_profile", enabled ? "0" : "1");
        if (sharedPrefs.getBoolean("dnd", false)) GameModeTileDND();
        sharedPrefs.edit().putBoolean(DeviceSettings.KEY_GAME_SWITCH, !enabled).commit();
        getQsTile().setState(enabled ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    private void GameModeTileDND() {
        switch ((GameModeSwitch.isCurrentlyEnabled(this)) ? 1 : 0) {
            case 1:
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                mNotificationManager.setNotificationPolicy(
                        new NotificationManager.Policy(NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA, 0, 0));
                break;
            case 0:
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                break;
        }
    }
}
