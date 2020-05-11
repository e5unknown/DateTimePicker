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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev420.datetimepicker.adapters.PickerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimePickerFragment extends Fragment {

    private RecyclerView rvHour;
    private RecyclerView rvMinute;

    private int minuteStep = 1;

    private LinearLayoutManager hourLM;
    private LinearLayoutManager minuteLM;

    private PickerAdapter hourAdapter;
    private PickerAdapter minuteAdapter;

    private SnapHelper hourSH;
    private SnapHelper minuteSH;

    private ArrayList<String> hours;
    private ArrayList<String> minutes;

    private int currentMinutePosition;
    private int currentHourPosition;

    private TimePickerCallback timeCallback;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;

    public TimePickerFragment(SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    //конструктор для создания минутного списка с назначенным шагом
    public TimePickerFragment(int minuteStep, SoundPool soundPool) {
        this.minuteStep = minuteStep;
        this.soundPool = soundPool;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_picker, container, false);
        timeCallback = (TimePickerCallback) getParentFragment();
        soundId = soundPool.load(getContext(), R.raw.snap1, 1);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        initUI(view);
        initDateAndTimeData();
        setCurrentDateAndTime();
        setRVScrollAndClickListeners();
        return view;
    }

    private void initUI(View view) {
        rvHour = view.findViewById(R.id.rvHour);
        rvMinute = view.findViewById(R.id.rvMinute);
        hourLM = new LinearLayoutManager(getContext());
        minuteLM = new LinearLayoutManager(getContext());
        rvHour.setLayoutManager(hourLM);
        rvMinute.setLayoutManager(minuteLM);
        hourAdapter = new PickerAdapter();
        minuteAdapter = new PickerAdapter();
        rvHour.setAdapter(hourAdapter);
        rvMinute.setAdapter(minuteAdapter);
        hourSH = new LinearSnapHelper();
        minuteSH = new LinearSnapHelper();
        hourSH.attachToRecyclerView(rvHour);
        minuteSH.attachToRecyclerView(rvMinute);
    }

    private void initDateAndTimeData() {
        hours = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 0; i <= 23; i++) {
            hours.add(String.format(Locale.getDefault(), "%02d", i));
        }
        hours.add("");
        hours.add("");

        minutes = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 0; i <= 59; i += minuteStep) {
            minutes.add(String.format(Locale.getDefault(), "%02d", i));
        }
        minutes.add("");
        minutes.add("");

        hourAdapter.setData(hours);
        minuteAdapter.setData(minutes);
    }

    private void setCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        currentMinutePosition = calendar.get(Calendar.MINUTE);
        rvMinute.scrollToPosition(currentMinutePosition);
        minuteAdapter.changeItemAppearance(currentMinutePosition);

        currentHourPosition = calendar.get(Calendar.HOUR_OF_DAY);
        rvHour.scrollToPosition(currentHourPosition);
        hourAdapter.changeItemAppearance(currentHourPosition);

        updateTimeInHeader();
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
        rvHour.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = hourLM.getPosition(hourSH.findSnapView(hourLM)) - 2;
                if (currentHourPosition != firstVisible) {
                    currentHourPosition = firstVisible;
                    hourAdapter.changeItemAppearance(firstVisible);
                    playSoundAndVibrate();
                    updateTimeInHeader();
                }
            }
        });


        rvMinute.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = minuteLM.getPosition(minuteSH.findSnapView(minuteLM)) - 2;
                if (currentMinutePosition != firstVisible) {
                    currentMinutePosition = firstVisible;
                    minuteAdapter.changeItemAppearance(firstVisible);
                    playSoundAndVibrate();
                    updateTimeInHeader();
                }
            }
        });

        hourAdapter.setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
            @Override
            public void onPickerClick(int position) {
                if (position > currentHourPosition + 2 && position < (hours.size() - 2)) {
                    rvHour.smoothScrollToPosition(position + 2);
                } else if (position < currentHourPosition + 2 && position > 1) {
                    rvHour.smoothScrollToPosition(position - 2);
                }
            }
        });

        minuteAdapter.setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
            @Override
            public void onPickerClick(int position) {
                if (position > currentMinutePosition + 2 && position < (minutes.size() - 2)) {
                    rvMinute.smoothScrollToPosition(position + 2);
                } else if (position < currentMinutePosition + 2 && position > 1) {
                    rvMinute.smoothScrollToPosition(position - 2);
                }
            }
        });

    }

    private String getTimeOnPicker() {
        return String.format(Locale.getDefault(), "%02d:%02d", currentHourPosition
                , currentMinutePosition);
    }

    private void updateTimeInHeader() {
        timeCallback.updateTimeFromPicker(getTimeOnPicker());
    }
}
