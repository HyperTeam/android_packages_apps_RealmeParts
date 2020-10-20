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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;

public class SmartChargingService extends Service {

    private static boolean Debug = false;

    public static String cool_down = "/sys/class/power_supply/battery/cool_down";

    public static String mmi_charging_enable = "/sys/class/power_supply/battery/mmi_charging_enable";

    public static String battery_capacity = "/sys/class/power_supply/battery/capacity";

    public static String battery_temperature = "/sys/class/power_supply/battery/temp";

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter batteryInfo = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryInfo, batteryInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBatteryInfo);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BroadcastReceiver mBatteryInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pluggedIN = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            float battTemp = ((float) Integer.parseInt(Utils.readLine(battery_temperature))) / 10;
            int battCap = Integer.parseInt(Utils.readLine(battery_capacity));
            int coolDown = Integer.parseInt(Utils.readLine(cool_down));
            int chargingLimit = Integer.parseInt(Utils.readLine(mmi_charging_enable));
            final boolean pluggedAC = pluggedIN == BatteryManager.BATTERY_PLUGGED_AC;
            final boolean pluggedUSB = pluggedIN == BatteryManager.BATTERY_PLUGGED_USB;
            if (pluggedAC || pluggedUSB) {
                if (Debug) Log.d("DeviceSettings", "USB plugged: " + pluggedUSB + "\n" + "AC plugged: " + pluggedAC + "\n" + "Battery Temperature: " + battTemp + "\n" + "Battery Capacity in %: " + battCap + "\n" + "Selected Charging Limit: " + SeekBarPreference.getProgress());
                // Apply cool down if battery temperature goes above 39.5C
                if (battTemp >= 39.5 && coolDown != 2) {
                    Utils.writeValue(cool_down, "2");
                    Log.d("DeviceSettings", "Battery Temperature - " + battTemp + "\n" + "Battery Capacity in % - " + battCap + "\n" + "Writing " + coolDown + " to " + mmi_charging_enable);
                // Do not apply cool down if temperature is 37C & below
                } else if (battTemp <= 37 && coolDown == 2 && coolDown != 0) {
                    Utils.writeValue(cool_down, "0");
                    Log.d("DeviceSettings", "Battery Temperature - " + battTemp + "\n" + "Battery Capacity in % - " + battCap + "\n" + "Writing " + coolDown + " to " + mmi_charging_enable);
                // Limit charging based on user preferred battery charging percentage
                } else if (SeekBarPreference.getProgress() == battCap && chargingLimit != 0) {
                    Utils.writeValue(cool_down, "0");
                    Utils.writeValue(mmi_charging_enable, "0");
                    Log.d("DeviceSettings", "Battery Capacity is at " + battCap + "%, " + "stopped charging");
                } else if (SeekBarPreference.getProgress() > battCap && chargingLimit != 1 && chargingLimit == 0) {
                    Utils.writeValue(mmi_charging_enable, "1");
                    Log.d("DeviceSettings", "Charging...");
                }
            }
        }
    };
}
