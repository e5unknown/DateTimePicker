package com.dev420.datetimepicker.adapters;

import android.media.SoundPool;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dev420.datetimepicker.fragments.DatePickerFragment;
import com.dev420.datetimepicker.fragments.TimePickerFragment;

public class PickerViewPagerAdapter extends FragmentPagerAdapter {

    SoundPool soundPool;

    public PickerViewPagerAdapter(@NonNull FragmentManager fm, int behavior, SoundPool soundPool) {
        super(fm, behavior);
        this.soundPool = soundPool;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new DatePickerFragment(soundPool);
        } else {
            return new TimePickerFragment(soundPool);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}