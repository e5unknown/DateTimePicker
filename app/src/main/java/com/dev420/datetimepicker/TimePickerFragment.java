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
import com.dev420.datetimepicker.callbacks.TimePickerCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimePickerFragment extends Fragment {

    private final int HOUR = 0;
    private final int MINUTE = 1;

    private RecyclerView[] rv = new RecyclerView[2];
    private LinearLayoutManager[] layoutManagers = new LinearLayoutManager[2];
    private PickerAdapter[] pickerAdapters = new PickerAdapter[2];
    private SnapHelper[] snapHelpers = new SnapHelper[2];
    private int[] pickerPosition = new int[2];

    private TimePickerCallback timeCallback;
    private SoundPool soundPool;
    private int soundId;
    private Vibrator vibrator;

    private ArrayList[] timeData = new ArrayList[2];
    private ArrayList<String> hours;
    private ArrayList<String> minutes;

    private int minuteStep = 1;

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
        initViews(view);
        createTimeArrays();
        setCurrentDateAndTime();
        setRVScrollAndClickListeners();
        return view;
    }

    private void initViews(View view) {
        rv[HOUR] = view.findViewById(R.id.rvHour);
        rv[MINUTE] = view.findViewById(R.id.rvMinute);
        for (int i = 0; i<=1; i++){
            layoutManagers[i] = new LinearLayoutManager(getContext());
            rv[i].setLayoutManager(layoutManagers[i]);
            pickerAdapters[i] = new PickerAdapter();
            rv[i].setAdapter(pickerAdapters[i]);
            snapHelpers[i] = new LinearSnapHelper();
            snapHelpers[i].attachToRecyclerView(rv[i]);
        }
    }

    private void createTimeArrays() {
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

        pickerAdapters[HOUR].setData(hours);
        pickerAdapters[MINUTE].setData(minutes);
    }

    private void setCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        pickerPosition[MINUTE] = calendar.get(Calendar.MINUTE);
        rv[MINUTE].scrollToPosition(pickerPosition[MINUTE]);
        pickerAdapters[MINUTE].changeItemAppearance(pickerPosition[MINUTE]);

        pickerPosition[HOUR] = calendar.get(Calendar.HOUR_OF_DAY);
        rv[HOUR].scrollToPosition(pickerPosition[HOUR]);
        pickerAdapters[HOUR].changeItemAppearance(pickerPosition[HOUR]);

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
        timeData[HOUR] = hours;
        timeData[MINUTE] = minutes;

        for (int i = 0; i <= 1; i++) {
            final int finalI = i;
            rv[finalI].addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisible = layoutManagers[finalI].getPosition(snapHelpers[finalI].findSnapView(layoutManagers[finalI])) - 2;
                    if (pickerPosition[finalI] != firstVisible) {
                        pickerPosition[finalI] = firstVisible;
                        pickerAdapters[finalI].changeItemAppearance(firstVisible);
                        playSoundAndVibrate();
                        updateTimeInHeader();
                    }
                }
            });

            pickerAdapters[finalI].setOnPickerClickListener(new PickerAdapter.OnPickerClickListener() {
                @Override
                public void onPickerClick(int position) {
                    if (position > pickerPosition[finalI] + 2 && position < (timeData[finalI].size() - 2)) {
                        rv[finalI].smoothScrollToPosition(position + 2);
                    } else if (position < pickerPosition[finalI] + 2 && position > 1) {
                        rv[finalI].smoothScrollToPosition(position - 2);
                    }
                }
            });

        }

    }

    private String getTimeOnPicker() {
        return String.format(Locale.getDefault(), "%02d:%02d", pickerPosition[HOUR]
                , pickerPosition[MINUTE]);
    }

    private void updateTimeInHeader() {
        timeCallback.updateTimeFromPicker(getTimeOnPicker());
    }
}
