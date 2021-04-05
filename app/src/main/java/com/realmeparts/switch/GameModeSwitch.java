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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;

public class GameModeSwitch implements OnPreferenceChangeListener {
    public static final int GameMode_Notification_Channel_ID = 0x11011;
    private static final String FILE = "/proc/touchpanel/game_switch_enable";
    private static final boolean GameMode = false;
    private static Context mContext;
    private static NotificationManager mNotificationManager;
    private static int userSelectedDndMode;

    public GameModeSwitch(Context context) {
        mContext = context;
        userSelectedDndMode = mContext.getSystemService(NotificationManager.class).getCurrentInterruptionFilter();
    }

    public static String getFile() {
        if (Utils.fileWritable(FILE)) {
            return FILE;
        }
        return null;
    }

    public static boolean isSupported() {
        return Utils.fileWritable(getFile());
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Utils.getFileValueAsBoolean(getFile(), false);
    }

    public static boolean checkNotificationPolicy(Context context) {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager.isNotificationPolicyAccessGranted();
    }

    public static void GameModeDND() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (!checkNotificationPolicy(mContext)) {
            //Launch Do Not Disturb Access settings
            Intent DNDAccess = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(DNDAccess);
        } else if (isCurrentlyEnabled(mContext)) {
            userSelectedDndMode = mContext.getSystemService(NotificationManager.class).getCurrentInterruptionFilter();
            if (sharedPreferences.getBoolean("dnd", false)) activateDND();
            AppNotification.Send(mContext, GameMode_Notification_Channel_ID, mContext.getString(R.string.game_mode_title), mContext.getString(R.string.game_mode_notif_content));
            ShowToast();
        } else if (!isCurrentlyEnabled(mContext)) {
            if (sharedPreferences.getBoolean("dnd", false))
                mNotificationManager.setInterruptionFilter(userSelectedDndMode);
            AppNotification.Cancel(mContext, GameMode_Notification_Channel_ID);
            ShowToast();
        }
    }

    public static void activateDND() {
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        mNotificationManager.setNotificationPolicy(
                new NotificationManager.Policy(NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA, 0, 0));
    }

    public static void ShowToast() {
        if (isCurrentlyEnabled(mContext)) {
            Toast.makeText(mContext, "GameMode is activated. ", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mContext, "GameMode is deactivated. ", Toast.LENGTH_SHORT).show();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Utils.writeValue(getFile(), enabled ? "1" : "0");
        Utils.writeValue(DeviceSettings.TP_LIMIT_ENABLE, enabled ? "0" : "1");
        Utils.writeValue(DeviceSettings.TP_DIRECTION, enabled ? "1" : "0");
        SystemProperties.set("perf_profile", enabled ? "1" : "0");
        GameModeDND();
        return true;
    }
}
