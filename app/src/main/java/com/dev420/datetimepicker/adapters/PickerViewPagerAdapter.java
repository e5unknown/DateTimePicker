package com.dev420.datetimepicker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dev420.datetimepicker.fragments.DatePickerFragment;
import com.dev420.datetimepicker.fragments.TimePickerFragment;

public class PickerViewPagerAdapter extends FragmentPagerAdapter {

    private Boolean blockDatePicker;
    private int minuteStep;

    public PickerViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setParameters(boolean blockDatePicker, int minuteStep) {
        this.blockDatePicker = blockDatePicker;
        this.minuteStep = minuteStep;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return DatePickerFragment.newInstance(blockDatePicker);
        } else {
            return TimePickerFragment.newInstance(minuteStep);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}