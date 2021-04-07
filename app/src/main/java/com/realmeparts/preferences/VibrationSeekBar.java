/*
 * Copyright (C) 2021 HyperTeam
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
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.slider.Slider;

public class VibrationSeekBar extends Preference
        implements Slider.OnSliderTouchListener {

    private Slider mSlider;

    public VibrationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.discrete_slider);
    }

    @Override
    public void onStartTrackingTouch(@NonNull Slider slider) {
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        String Vib_LevelValue = Utils.readLine("/sys/class/leds/vibrator/level");
        mSlider = (Slider) view.findViewById(R.id.discrete_slider);
        mSlider.setValue(Integer.parseInt(Vib_LevelValue));
        mSlider.addOnSliderTouchListener(this);
    }

    @Override
    public void onStopTrackingTouch(@NonNull Slider slider) {
        try {
            int SeekBar_Value = Math.round(slider.getValue());
            Utils.writeValue("/sys/class/leds/vibrator/level", String.valueOf(SeekBar_Value));
            Utils.Vibrate(getContext(), SeekBar_Value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
