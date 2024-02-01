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
import android.os.SystemProperties;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

public class RefreshRateSwitch implements OnPreferenceChangeListener {

    public static int setRefreshRate;
    private final Context mContext;

    public RefreshRateSwitch(Context context) {
        mContext = context;
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), getDefaultRefreshRate()) == getDefaultRefreshRate();
    }

    public static void setPeakRefresh(Context context, boolean enabled) {
        Settings.System.putFloat(context.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), enabled ? getDefaultRefreshRate() : 60f);
        Settings.System.putFloat(context.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), enabled ? getDefaultRefreshRate() : 60f);
    }

    private static float getDefaultRefreshRate() {
        return Float.parseFloat(SystemProperties.get("persist.high.refresh.rate", "90.0"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        float customRefreshRate = getDefaultRefreshRate();

        switch ((preference == DeviceSettings.mRefreshRate90 && enabled) ? 1 : 0) {
            case 1:
                Settings.System.putFloat(mContext.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), customRefreshRate);
                Settings.System.putFloat(mContext.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), customRefreshRate);
                break;
            case 0:
                Settings.System.putFloat(mContext.getContentResolver(), "PEAK_REFRESH_RATE".toLowerCase(), 60f);
                Settings.System.putFloat(mContext.getContentResolver(), "MIN_REFRESH_RATE".toLowerCase(), 60f);
                break;
        }
        return true;
    }
}
