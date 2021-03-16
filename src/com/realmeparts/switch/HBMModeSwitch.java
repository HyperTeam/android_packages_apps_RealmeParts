/*
* Copyright (C) 2016 The OmniROM Project
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

import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;
import android.os.UserHandle;
import android.util.Log;

import com.realmeparts.DeviceSettings;

public class HBMModeSwitch implements OnPreferenceChangeListener {

    private static Context mContext;

    public HBMModeSwitch(Context context) {
        mContext = context;
    }

    private static final String FILE = "/sys/kernel/oppo_display/hbm";

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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        Utils.writeValue(getFile(), enabled ? "1" : "0");
        TriggerService(enabled);
        return true;
    }

    public static void TriggerService(boolean enabled){
        Intent HBMIntent = new Intent(mContext, com.realmeparts.HBMService.class);
        if (enabled) {
            mContext.startServiceAsUser(HBMIntent, UserHandle.CURRENT);
            Log.d("DeviceSettings", "Starting HBM Service");
        } else if (!enabled && HBMIntent != null) {
            mContext.stopServiceAsUser(HBMIntent, UserHandle.CURRENT);
            Log.d("DeviceSettings", "Stopping HBM Service");
        }
    }
}
