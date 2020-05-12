package com.dev420.datetimepicker.fragments;

import android.content.Context;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev420.datetimepicker.R;
import com.dev420.datetimepicker.adapters.PickerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DatePickerFragment extends Fragment {

    private final int DAY = 0;
    private final int MONTH = 1;
    private final int YEAR = 2;

    private RecyclerView[] rv = new RecyclerView[3];
    private LinearLayoutManager[] layoutManagers = new LinearLayoutManager[3];
    private PickerAdapter[] pickerAdapters = new PickerAdapter[3];
    private SnapHelper[] snapHelpers = new SnapHelper[3];
    private int[] pickerPosition = new int[3];

    private DatePickerCallback dateCallback;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;

    private ArrayList[] dateData = new ArrayList[3];
    private ArrayList<String> days;
    private ArrayList<String> months;
    private ArrayList<String> years;

    int firstYearInArray = 2015;

    public DatePickerFragment(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public interface DatePickerCallback {
        public void updateDateFromPicker(String dateDDMMYYYY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        dateCallback = (DatePickerCallback) getParentFragment();
        soundId = soundPool.load(getContext(), R.raw.snap1, 1);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        initViews(view);
        createDateArrays();
        setCurrentDateAndTime();
        setRVScrollAndClickListeners();
        return view;
    }

    private void initViews(View view) {
        rv[DAY] = view.findViewById(R.id.rvDay);
        rv[MONTH] = view.findViewById(R.id.rvMonth);
        rv[YEAR] = view.findViewById(R.id.rvYear);
        for (int i = 0; i<=2; i++){
            layoutManagers[i] = new LinearLayoutManager(getContext());
            rv[i].setLayoutManager(layoutManagers[i]);
            pickerAdapters[i] = new PickerAdapter();
            rv[i].setAdapter(pickerAdapters[i]);
            snapHelpers[i] = new LinearSnapHelper();
            snapHelpers[i].attachToRecyclerView(rv[i]);
        }
    }

    private void createDateArrays() {
        days = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 1; i <= 31; i++) {
            days.add(String.format(Locale.getDefault(), "%02d", i));
        }
        days.add("");
        days.add("");

        months = new ArrayList<>(Arrays.asList("", "", "янв", "фев", "март", "апр", "май", "июнь",
                "июль", "авг", "сен", "окт", "нояб", "дек", "", ""));

        years = new ArrayList<>(Arrays.asList("", ""));
        for (int i = firstYearInArray; i <= 2050; i++) {
            years.add(Integer.toString(i));
        }
        years.add("");
        years.add("");

        pickerAdapters[DAY].setData(days);
        pickerAdapters[MONTH].setData(months);
        pickerAdapters[YEAR].setData(years);
    }

    private void resetDaysCount() {
        int daysCount;
        switch (pickerPosition[MONTH]) {
            case 1:
                if ((pickerPosition[YEAR]+ firstYearInArray) % 4 == 0) {
                    daysCount = 29;
                } else daysCount = 28;
                break;
            case 3:
            case 5:
            case 8:
            case 10:
                daysCount = 30;
                break;
            default:
                daysCount = 31;
                break;
        }
        if ((days.size() - 4) < daysCount) {
            ArrayList<String> newDays = new ArrayList<>();
            for (int i = days.size() - 3; i <= daysCount; i++) {
                newDays.add(Integer.toString(i));
            }
            days.addAll(days.size() - 2, newDays);
            pickerAdapters[DAY].notifyDataSetChanged();
        } else if ((days.size() - 4) > daysCount) {
            int startIndex = daysCount + 2;
            int lastIndex = days.size() - 2;
            int count = lastIndex - startIndex;
            days.subList(startIndex, lastIndex).clear();
            pickerAdapters[DAY].notifyItemRangeRemoved(startIndex, count);
        }
        if (pickerPosition[DAY] + 1 > daysCount) {
            pickerPosition[DAY] = pickerPosition[DAY] - (pickerPosition[DAY] + 1 - daysCount);
        }
        pickerAdapters[DAY].changeItemAppearance(pickerPosition[DAY]);
    }

    private void setCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        pickerPosition[YEAR] = calendar.get(Calendar.YEAR) - firstYearInArray;
        rv[YEAR].scrollToPosition(pickerPosition[YEAR]);
        pickerAdapters[YEAR].changeItemAppearance(pickerPosition[YEAR]);

        pickerPosition[MONTH] = calendar.get(Calendar.MONTH);
        rv[MONTH].scrollToPosition(pickerPosition[MONTH]);
        pickerAdapters[MONTH].changeItemAppearance(pickerPosition[MONTH]);

        pickerPosition[DAY] = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        rv[DAY].scrollToPosition(pickerPosition[DAY]);
        resetDaysCount();

        updateDateInHeader();
    }

    private void playSoundAndVibrate() {
        soundPool.play(soundId, 0.3f, 0.3f, 1, 0, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(25);
        }
    }

    private void setRVScrollAndClickListeners() {
        dateData[DAY] = days;
        dateData[MONTH] = months;
        dateData[YEAR] = years;

        for (int i = 0; i <=2; i++){
            final int finalI = i;
            rv[i].addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisible = layoutManagers[finalI].getPosition(snapHelpers[finalI].findSnapView(layoutManagers[finalI])) - 2;
                    if (pickerPosition[finalI] != firstVisible) {
                        pickerPosition[finalI] = firstVisible;
                        pickerAdapters[finalI].changeItemAppearance(firstVisible);
                        playSoundAndVibrate();
                        updateDateInHeader();
                    }
                }
            });

            if (finalI != 0){
                rv[i].addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            resetDaysCount();
                        }
                    }
                });
            }

            //докручивает число в центр при клике на не выделенную строчку
            pickerAdapters[finalI].setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
                @Override
                public void onPickerClick(int position) {
                    if (position > pickerPosition[finalI] + 2 && position < (dateData[finalI].size() - 2)) {
                        rv[finalI].smoothScrollToPosition(position + 2);
                    } else if (position < pickerPosition[finalI] + 2 && position > 1) {
                        rv[finalI].smoothScrollToPosition(position - 2);
                    }
                }
            });

        }
    }

    private String getDateOnPicker(){
        return String.format(Locale.getDefault(), "%02d.%02d.%02d", pickerPosition[DAY] + 1
                , pickerPosition[MONTH] + 1, pickerPosition[YEAR] + firstYearInArray);
    }

    private void updateDateInHeader() {
        dateCallback.updateDateFromPicker(getDateOnPicker());
    }

}
