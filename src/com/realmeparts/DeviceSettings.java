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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.Display.Mode;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;

import com.realmeparts.RadioButtonPreference;
import com.realmeparts.SeekBarPreference;
import com.realmeparts.SecureSettingListPreference;

import java.io.IOException;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceSettings extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String ProductName = Utils.ProductName();
 
    private static final String KEY_CATEGORY_CHARGING = "charging";
    private static final String KEY_CATEGORY_GRAPHICS = "graphics";
    public static final String KEY_SRGB_SWITCH = "srgb";
    public static final String KEY_HBM_SWITCH = "hbm";
    public static final String KEY_DC_SWITCH = "dc";
    public static final String KEY_OTG_SWITCH = "otg";
    public static final String KEY_GAME_SWITCH = "game";
    public static final String KEY_CHARGING_SWITCH = "smart_charging";
    public static final String KEY_CHARGING_SPEED = "charging_speed";
    public static final String KEY_RESET_STATS = "reset_stats";
    public static final String KEY_DND_SWITCH = "dnd";

    public static final String KEY_CABC = "cabc";
    public static final String CABC_SYSTEM_PROPERTY = "persist.cabc_profile";

    private static final String KEY_CATEGORY_REFRESH_RATE = "refresh_rate";

    public static final String KEY_FPS_INFO = "fps_info";

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";

    public static final String TP_LIMIT_ENABLE = "/proc/touchpanel/oppo_tp_limit_enable";
    public static final String TP_DIRECTION = "/proc/touchpanel/oppo_tp_direction";

    private boolean CABC_DeviceMatched;
    private boolean DC_DeviceMatched;
    private boolean HBM_DeviceMatched;
    private boolean sRGB_DeviceMatched;

    private static TwoStatePreference mDCModeSwitch;
    private static TwoStatePreference mSRGBModeSwitch;
    private static TwoStatePreference mHBMModeSwitch;
    private static TwoStatePreference mOTGModeSwitch;
    private static TwoStatePreference mGameModeSwitch;
    public static TwoStatePreference mDNDSwitch;
    private static TwoStatePreference mSmartChargingSwitch;
    public static SecureSettingListPreference mChargingSpeed;
    private SecureSettingListPreference mCABC;
    public static TwoStatePreference mResetStats;
    public static TwoStatePreference mRefreshRate90Forced;
    private static SwitchPreference mFpsInfo;
    private static NotificationManager mNotificationManager;

    public static RadioButtonPreference mRefreshRate90;
    public static RadioButtonPreference mRefreshRate60;

    public static SeekBarPreference mSeekBarPreference;

    public static PreferenceCategory mPreferenceCategory;

    public static DisplayManager mDisplayManager;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        prefs.edit().putString("ProductName", ProductName).commit();

        addPreferencesFromResource(R.xml.main);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        mDCModeSwitch = (TwoStatePreference) findPreference(KEY_DC_SWITCH);
        mDCModeSwitch.setEnabled(DCModeSwitch.isSupported());
        mDCModeSwitch.setChecked(DCModeSwitch.isCurrentlyEnabled(this.getContext()));
        mDCModeSwitch.setOnPreferenceChangeListener(new DCModeSwitch());

        mSRGBModeSwitch = (TwoStatePreference) findPreference(KEY_SRGB_SWITCH);
        mSRGBModeSwitch.setEnabled(SRGBModeSwitch.isSupported());
        mSRGBModeSwitch.setChecked(SRGBModeSwitch.isCurrentlyEnabled(this.getContext()));
        mSRGBModeSwitch.setOnPreferenceChangeListener(new SRGBModeSwitch());

        mHBMModeSwitch = (TwoStatePreference) findPreference(KEY_HBM_SWITCH);
        mHBMModeSwitch.setEnabled(HBMModeSwitch.isSupported());
        mHBMModeSwitch.setChecked(HBMModeSwitch.isCurrentlyEnabled(this.getContext()));
        mHBMModeSwitch.setOnPreferenceChangeListener(new HBMModeSwitch(getContext()));

        mOTGModeSwitch = (TwoStatePreference) findPreference(KEY_OTG_SWITCH);
        mOTGModeSwitch.setEnabled(OTGModeSwitch.isSupported());
        mOTGModeSwitch.setChecked(OTGModeSwitch.isCurrentlyEnabled(this.getContext()));
        mOTGModeSwitch.setOnPreferenceChangeListener(new OTGModeSwitch());

        mGameModeSwitch = (TwoStatePreference) findPreference(KEY_GAME_SWITCH);
        mGameModeSwitch.setEnabled(GameModeSwitch.isSupported());
        mGameModeSwitch.setChecked(GameModeSwitch.isCurrentlyEnabled(this.getContext()));
        mGameModeSwitch.setOnPreferenceChangeListener(new GameModeSwitch(getContext()));

        mDNDSwitch = (TwoStatePreference) findPreference(KEY_DND_SWITCH);
        mDNDSwitch.setChecked(prefs.getBoolean(KEY_DND_SWITCH, false));
        mDNDSwitch.setOnPreferenceChangeListener(this);

        mSmartChargingSwitch = (TwoStatePreference) findPreference(KEY_CHARGING_SWITCH);
        mSmartChargingSwitch.setChecked(prefs.getBoolean(KEY_CHARGING_SWITCH, false));
        mSmartChargingSwitch.setOnPreferenceChangeListener(new SmartChargingSwitch(getContext()));

        mChargingSpeed = (SecureSettingListPreference) findPreference(KEY_CHARGING_SPEED);
        mChargingSpeed.setEnabled(mSmartChargingSwitch.isChecked());
        mChargingSpeed.setOnPreferenceChangeListener(this);

        mResetStats = (TwoStatePreference) findPreference(KEY_RESET_STATS);
        mResetStats.setChecked(prefs.getBoolean(KEY_RESET_STATS, false));
        mResetStats.setEnabled(mSmartChargingSwitch.isChecked());
        mResetStats.setOnPreferenceChangeListener(this);

        mSeekBarPreference = (SeekBarPreference) findPreference("seek_bar");
        mSeekBarPreference.setEnabled(mSmartChargingSwitch.isChecked());
        SeekBarPreference.mProgress = prefs.getInt("seek_bar", 95);

        mRefreshRate90Forced = (TwoStatePreference) findPreference("refresh_rate_90Forced");
        mRefreshRate90Forced.setChecked(prefs.getBoolean("refresh_rate_90Forced", false));
        mRefreshRate90Forced.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

        mRefreshRate90 = (RadioButtonPreference) findPreference("refresh_rate_90");
        mRefreshRate90.setChecked(RefreshRateSwitch.isCurrentlyEnabled(this.getContext()));
        mRefreshRate90.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

        mRefreshRate60 = (RadioButtonPreference) findPreference("refresh_rate_60");
        mRefreshRate60.setChecked(!RefreshRateSwitch.isCurrentlyEnabled(this.getContext()));
        mRefreshRate60.setOnPreferenceChangeListener(new RefreshRateSwitch(getContext()));

        mFpsInfo = (SwitchPreference) findPreference(KEY_FPS_INFO);
        mFpsInfo.setChecked(prefs.getBoolean(KEY_FPS_INFO, false));
        mFpsInfo.setOnPreferenceChangeListener(this);

        mCABC = (SecureSettingListPreference) findPreference(KEY_CABC);
        mCABC.setValue(Utils.getStringProp(CABC_SYSTEM_PROPERTY, "0"));
        mCABC.setSummary(mCABC.getEntry());
        mCABC.setOnPreferenceChangeListener(this);

        // Few checks to enable/disable options when activity is launched
        if ((prefs.getBoolean("refresh_rate_90", false) && prefs.getBoolean("refresh_rate_90Forced", false))) {
            mRefreshRate60.setEnabled(false);
            mRefreshRate90.setEnabled(false);
        }  else if ((prefs.getBoolean("refresh_rate_60", false))) {
            mRefreshRate90Forced.setEnabled(false);
        }

        isCoolDownAvailable();
        DisplayRefreshRateModes();
        try {
            ParseJson();
        } catch (Exception e) {
            Log.d("DeviceSettings", e.toString());
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
       if(preference == mRefreshRate90) {
                mRefreshRate60.setChecked(false);
                mRefreshRate90.setChecked(true);
                mRefreshRate90Forced.setEnabled(true);
                return true;
        } else if (preference == mRefreshRate60) {
                mRefreshRate60.setChecked(true);
                mRefreshRate90.setChecked(false);
                mRefreshRate90Forced.setEnabled(false);
                return true;
        }
            return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFpsInfo) {
            boolean enabled = (Boolean) newValue;
            Intent fpsinfo = new Intent(this.getContext(), com.realmeparts.FPSInfoService.class);
            if (enabled) {
                this.getContext().startService(fpsinfo);
            } else {
                this.getContext().stopService(fpsinfo);
            }
        }

        if (preference == mChargingSpeed) {
            mChargingSpeed.setValue((String) newValue);
            mChargingSpeed.setSummary(mChargingSpeed.getEntry());
        }

        if (preference == mCABC) {
            mCABC.setValue((String) newValue);
            mCABC.setSummary(mCABC.getEntry());
            Utils.setStringProp(CABC_SYSTEM_PROPERTY, (String) newValue);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Remove Smart Charging preference category if cool_down node is unavailable
    private void isCoolDownAvailable() {
        if (!Utils.fileWritable(SmartChargingService.cool_down)) {
            mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_CHARGING);
            getPreferenceScreen().removePreference(mPreferenceCategory);
        }
    }

    // Remove display refresh rate modes category if display doesn't support 90hz
    private void DisplayRefreshRateModes() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String refreshRate = new String();
        mDisplayManager = (DisplayManager) this.getContext().getSystemService(Context.DISPLAY_SERVICE);
        Display.Mode[] DisplayModes = mDisplayManager.getDisplay(Display.DEFAULT_DISPLAY).getSupportedModes();
        for (Display.Mode mDisplayMode : DisplayModes) {
            DecimalFormat df = new DecimalFormat("0.##");
            refreshRate += String.valueOf(df.format(mDisplayMode.getRefreshRate()))+"Hz, ";
        }
        Log.d("DeviceSettings", "Device supports "+refreshRate+"refresh rate modes");

        if(!refreshRate.contains("90")) {
            prefs.edit().putBoolean("refresh_rate_90_device", false).commit();
            mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_REFRESH_RATE);
            getPreferenceScreen().removePreference(mPreferenceCategory);
        } else prefs.edit().putBoolean("refresh_rate_90_device", true).commit();
    }

    private void ParseJson() throws IOException, JSONException {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mPreferenceCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_GRAPHICS);
        String features_json = Utils.InputStreamToString(getResources().openRawResource(R.raw.realmeParts_features));
        JSONObject jsonOB = new JSONObject(features_json);

        JSONArray CABC = jsonOB.getJSONArray(KEY_CABC);
        for (int i = 0; i < CABC.length(); i++) {
            if (ProductName.toUpperCase().contains(CABC.getString(i))) {
                {
                    CABC_DeviceMatched = true;
                }
            }
        }

        JSONArray DC = jsonOB.getJSONArray(KEY_DC_SWITCH);
        for (int i = 0; i < DC.length(); i++) {
            if (ProductName.toUpperCase().contains(DC.getString(i))) {
                {
                    DC_DeviceMatched = true;
                }
            }
        }

        JSONArray HBM = jsonOB.getJSONArray(KEY_HBM_SWITCH);
        for (int i = 0; i < HBM.length(); i++) {
            if (ProductName.toUpperCase().contains(HBM.getString(i))) {
                {
                    HBM_DeviceMatched = true;
                }
            }
        }

        JSONArray sRGB = jsonOB.getJSONArray(KEY_SRGB_SWITCH);
        for (int i = 0; i < sRGB.length(); i++) {
            if (ProductName.toUpperCase().contains(DC.getString(i))) {
                {
                    sRGB_DeviceMatched = true;
                }
            }
        }

        // Remove CABC preference if device is unsupported
        if (!CABC_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_CABC));
            prefs.edit().putBoolean("CABC_DeviceMatched", false).commit();
        } else prefs.edit().putBoolean("CABC_DeviceMatched", true).commit();

        // Remove DC-Dimming preference if device is unsupported
        if (!DC_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_DC_SWITCH));
            prefs.edit().putBoolean("DC_DeviceMatched", false).commit();
        } else prefs.edit().putBoolean("DC_DeviceMatched", true).commit();

        // Remove HBM preference if device is unsupported
        if (!HBM_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_HBM_SWITCH));
            prefs.edit().putBoolean("HBM_DeviceMatched", false).commit();
        } else prefs.edit().putBoolean("HBM_DeviceMatched", true).commit();

        // Remove sRGB preference if device is unsupported
        if (!sRGB_DeviceMatched) {
            mPreferenceCategory.removePreference(findPreference(KEY_SRGB_SWITCH));
            prefs.edit().putBoolean("sRGB_DeviceMatched", false).commit();
        } else prefs.edit().putBoolean("sRGB_DeviceMatched", true).commit();
    }
}
