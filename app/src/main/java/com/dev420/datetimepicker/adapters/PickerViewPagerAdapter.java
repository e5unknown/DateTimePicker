package com.dev420.datetimepicker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dev420.datetimepicker.fragments.DatePickerFragment;
import com.dev420.datetimepicker.fragments.TimePickerFragment;

public class PickerViewPagerAdapter extends FragmentPagerAdapter {

    public PickerViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new DatePickerFragment();
        } else {
            return new TimePickerFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}