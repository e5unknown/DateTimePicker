package com.dev420.datetimepicker;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private RecyclerView rvDay;
    private RecyclerView rvMonth;
    private RecyclerView rvYear;

    private LinearLayoutManager dayLM;
    private LinearLayoutManager monthLM;
    private LinearLayoutManager yearLM;

    private PickerAdapter dayAdapter;
    private PickerAdapter monthAdapter;
    private PickerAdapter yearAdapter;

    private SnapHelper daySH;
    private SnapHelper monthSH;
    private SnapHelper yearSH;

    private ArrayList<String> days;
    private ArrayList<String> months;
    private ArrayList<String> years;

    int startYearInArray;

    private int currentDayPosition;
    private int currentMonthPosition;
    private int currentYearPosition;
    private int[] pickerPosition;

    private DatePickerCallback dateCallback;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;

    public DatePickerFragment(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        // минимальный год установлен 2015. можно переделать и создавать изначально массив начиная с текущего года
        startYearInArray = 2015;
        dateCallback = (DatePickerCallback) getParentFragment();
        soundId = soundPool.load(getContext(), R.raw.snap1, 1);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        initUI(view);
        initDateAndTimeData();
        setCurrentDateAndTime();
        setRVScrollAndClickListeners();
        return view;
    }

    private void initUI(View view) {
        rvDay = view.findViewById(R.id.rvDay);
        rvMonth = view.findViewById(R.id.rvMonth);
        rvYear = view.findViewById(R.id.rvYear);
        dayLM = new LinearLayoutManager(getContext());
        monthLM = new LinearLayoutManager(getContext());
        yearLM = new LinearLayoutManager(getContext());
        rvDay.setLayoutManager(dayLM);
        rvMonth.setLayoutManager(monthLM);
        rvYear.setLayoutManager(yearLM);
        dayAdapter = new PickerAdapter();
        monthAdapter = new PickerAdapter();
        yearAdapter = new PickerAdapter();
        rvDay.setAdapter(dayAdapter);
        rvMonth.setAdapter(monthAdapter);
        rvYear.setAdapter(yearAdapter);
        daySH = new LinearSnapHelper();
        monthSH = new LinearSnapHelper();
        yearSH = new LinearSnapHelper();
        daySH.attachToRecyclerView(rvDay);
        monthSH.attachToRecyclerView(rvMonth);
        yearSH.attachToRecyclerView(rvYear);
    }

    private void initDateAndTimeData() {
        days = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 1; i <= 31; i++) {
            days.add(String.format(Locale.getDefault(), "%02d", i));
        }
        days.add("");
        days.add("");

        months = new ArrayList<>(Arrays.asList("", "", "янв", "фев", "март", "апр", "май", "июнь",
                "июль", "авг", "сен", "окт", "нояб", "дек", "", ""));

        years = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 2015; i <= 2050; i++) {
            years.add(Integer.toString(i));
        }
        years.add("");
        years.add("");

        dayAdapter.setData(days);
        monthAdapter.setData(months);
        yearAdapter.setData(years);
    }

    private void resetDaysCount() {
        Log.i("WTF", pickerPosition[0]+"");
        Log.i("WTF", pickerPosition[1]+"");
        Log.i("WTF", pickerPosition[2]+"");
        int daysCount;
        switch (pickerPosition[MONTH]) {
            case 1:
                if ((pickerPosition[YEAR]+startYearInArray) % 4 == 0) {
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
            dayAdapter.notifyDataSetChanged();
        } else if ((days.size() - 4) > daysCount) {
            int startIndex = daysCount + 2;
            int lastIndex = days.size() - 2;
            int count = lastIndex - startIndex;
            days.subList(startIndex, lastIndex).clear();
            dayAdapter.notifyItemRangeRemoved(startIndex, count);
        }
        if (pickerPosition[DAY] + 1 > daysCount) {
            pickerPosition[DAY] = pickerPosition[DAY] - (pickerPosition[DAY] + 1 - daysCount);
        }
        dayAdapter.changeItemAppearance(pickerPosition[DAY]);
    }

    private void setCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        pickerPosition = new int[3];

        pickerPosition[YEAR] = calendar.get(Calendar.YEAR) - startYearInArray;
        rvYear.scrollToPosition(pickerPosition[YEAR]);
        yearAdapter.changeItemAppearance(pickerPosition[YEAR]);

        pickerPosition[MONTH] = calendar.get(Calendar.MONTH);
        rvMonth.scrollToPosition(pickerPosition[MONTH]);
        monthAdapter.changeItemAppearance(pickerPosition[MONTH]);

        pickerPosition[DAY] = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        rvDay.scrollToPosition(pickerPosition[DAY]);
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

        rvDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = dayLM.getPosition(daySH.findSnapView(dayLM)) - 2;
                if (pickerPosition[DAY] != firstVisible) {
                    pickerPosition[DAY] = firstVisible;
                    dayAdapter.changeItemAppearance(firstVisible);
                    playSoundAndVibrate();
                    updateDateInHeader();
                }
            }
        });

        rvMonth.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resetDaysCount();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = monthLM.getPosition(monthSH.findSnapView(monthLM)) - 2;
                if (pickerPosition[MONTH] != firstVisible) {
                    pickerPosition[MONTH] = firstVisible;
                    monthAdapter.changeItemAppearance(firstVisible);
                    playSoundAndVibrate();
                    updateDateInHeader();
                }
            }
        });

        rvYear.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resetDaysCount();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = yearLM.getPosition(yearSH.findSnapView(yearLM)) - 2;
                if (pickerPosition[YEAR] != firstVisible) {
                    pickerPosition[YEAR] = firstVisible;
                    yearAdapter.changeItemAppearance(firstVisible);
                    playSoundAndVibrate();
                    updateDateInHeader();
                }
            }
        });

        //докручивает число в центр при клике на не выделенную строчку
        dayAdapter.setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
            @Override
            public void onPickerClick(int position) {
                if (position > pickerPosition[DAY] + 2 && position < (days.size() - 2)) {
                    rvDay.smoothScrollToPosition(position + 2);
                } else if (position < pickerPosition[DAY] + 2 && position > 1) {
                    rvDay.smoothScrollToPosition(position - 2);
                }
            }
        });

        monthAdapter.setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
            @Override
            public void onPickerClick(int position) {
                if (position > pickerPosition[MONTH] + 2 && position < (months.size() - 2)) {
                    rvMonth.smoothScrollToPosition(position + 2);
                } else if (position < pickerPosition[MONTH] + 2 && position > 1) {
                    rvMonth.smoothScrollToPosition(position - 2);
                }
            }
        });

        yearAdapter.setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
            @Override
            public void onPickerClick(int position) {
                if (position > pickerPosition[YEAR] + 2 && position < (years.size() - 2)) {
                    rvYear.smoothScrollToPosition(position + 2);
                } else if (position < pickerPosition[MONTH] + 2 && position > 1) {
                    rvYear.smoothScrollToPosition(position - 2);
                }
            }
        });

    }

    private String getDateOnPicker(){
        return String.format(Locale.getDefault(), "%02d.%02d.%02d", pickerPosition[DAY] + 1
                , pickerPosition[MONTH] + 1, pickerPosition[YEAR] + startYearInArray);
    }

    private void updateDateInHeader() {
        dateCallback.updateDateFromPicker(getDateOnPicker());
    }

}
