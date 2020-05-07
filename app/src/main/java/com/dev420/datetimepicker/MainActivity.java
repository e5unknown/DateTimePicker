package com.dev420.datetimepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private LinearLayout llDate;
    private LinearLayout llTime;
    private ImageView ivDate;
    private ImageView ivTime;
    private TextView tvDate;
    private TextView tvTime;
    private RecyclerView rvDay;
    private RecyclerView rvMonth;
    private RecyclerView rvYear;
    private RecyclerView rvHour;
    private RecyclerView rvMinute;

    private LinearLayoutManager dayLM;
    private LinearLayoutManager monthLM;
    private LinearLayoutManager yearLM;
    private LinearLayoutManager hourLM;
    private LinearLayoutManager minuteLM;

    private PickerAdapter dayAdapter;
    private PickerAdapter monthAdapter;
    private PickerAdapter yearAdapter;
    private PickerAdapter hourAdapter;
    private PickerAdapter minuteAdapter;

    private SnapHelper daySH;
    private SnapHelper monthSH;
    private SnapHelper yearSH;
    private SnapHelper hourSH;
    private SnapHelper minuteSH;

    private ArrayList<String> days;
    private ArrayList<String> months;
    private ArrayList<String> years;
    private ArrayList<String> hours;
    private ArrayList<String> minutes;

    int startYearInArray;

    private int currentMinutePosition;
    private int currentHourPosition;
    private int currentDayPosition;
    private int currentMonthPosition;
    private int currentYearPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initDateAndTimeData();
        startYearInArray = 2015;
        setCurrentDateAndTime();
        Handler handler = new Handler();
        //Костыли, чтобы при прокручивании пикера до текущего времени не производилась куча щелчков
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRVScrollListeners();
            }
        }, 500);
    }

    // минимальный год установлен 2015. можно переделать и создавать изначально массив начиная с текущего года
    private void setCurrentDateAndTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        currentMinutePosition = calendar.get(Calendar.MINUTE);
        rvMinute.scrollToPosition(currentMinutePosition);
        minuteAdapter.changeItemAppearance(currentMinutePosition);
        currentHourPosition = calendar.get(Calendar.HOUR_OF_DAY);
        rvHour.scrollToPosition(currentHourPosition);
        hourAdapter.changeItemAppearance(currentHourPosition);
        currentYearPosition = calendar.get(Calendar.YEAR)- startYearInArray;
        rvYear.scrollToPosition(currentYearPosition);
        yearAdapter.changeItemAppearance(currentYearPosition);
        currentMonthPosition = calendar.get(Calendar.MONTH);
        rvMonth.scrollToPosition(currentMonthPosition);
        monthAdapter.changeItemAppearance(currentMonthPosition);
        currentDayPosition = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        rvDay.scrollToPosition(currentDayPosition);
        resetDaysArray(currentMonthPosition, currentYearPosition, currentDayPosition);
        updateDateInHeader();
        updateTimeInHeader();
    }

    private void initUI() {
        llDate = findViewById(R.id.llDate);
        llTime = findViewById(R.id.llTime);
        ivDate = findViewById(R.id.ivDate);
        ivTime = findViewById(R.id.ivTime);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        rvDay = findViewById(R.id.rvDay);
        rvMonth = findViewById(R.id.rvMonth);
        rvYear = findViewById(R.id.rvYear);
        rvHour = findViewById(R.id.rvHour);
        rvMinute = findViewById(R.id.rvMinute);
        CardView buttonCancel = findViewById(R.id.buttonCancel);
        CardView buttonSave = findViewById(R.id.buttonSave);
        dayLM = new LinearLayoutManager(this);
        monthLM = new LinearLayoutManager(this);
        yearLM = new LinearLayoutManager(this);
        hourLM = new LinearLayoutManager(this);
        minuteLM = new LinearLayoutManager(this);
        rvDay.setLayoutManager(dayLM);
        rvMonth.setLayoutManager(monthLM);
        rvYear.setLayoutManager(yearLM);
        rvHour.setLayoutManager(hourLM);
        rvMinute.setLayoutManager(minuteLM);
        dayAdapter = new PickerAdapter();
        monthAdapter = new PickerAdapter();
        yearAdapter = new PickerAdapter();
        hourAdapter = new PickerAdapter();
        minuteAdapter = new PickerAdapter();
        rvDay.setAdapter(dayAdapter);
        rvMonth.setAdapter(monthAdapter);
        rvYear.setAdapter(yearAdapter);
        rvHour.setAdapter(hourAdapter);
        rvMinute.setAdapter(minuteAdapter);
        daySH = new LinearSnapHelper();
        monthSH = new LinearSnapHelper();
        yearSH = new LinearSnapHelper();
        hourSH = new LinearSnapHelper();
        minuteSH = new LinearSnapHelper();
        daySH.attachToRecyclerView(rvDay);
        monthSH.attachToRecyclerView(rvMonth);
        yearSH.attachToRecyclerView(rvYear);
        hourSH.attachToRecyclerView(rvHour);
        minuteSH.attachToRecyclerView(rvMinute);
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Clicked Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        //Получение выбранной даты/времени по клику на кнопку "Сохранить"
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String builder = "Saved: " +
                        currentHourPosition + ":" + currentMinutePosition + " " +
                        (currentDayPosition+1) + "." + (currentMonthPosition + 1) + "." +
                        (currentYearPosition + startYearInArray);
                Toast.makeText(MainActivity.this, builder, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDateAndTimeData() {
        days = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 1; i <= 31; i++) {
            days.add(String.format(Locale.getDefault(), "%02d", i));
        }
        days.add("");
        days.add("");

        hours = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 0; i <= 23; i++) {
            hours.add(String.format(Locale.getDefault(), "%02d", i));
        }
        hours.add("");
        hours.add("");

        minutes = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 0; i <= 59; i++) {
            minutes.add(String.format(Locale.getDefault(), "%02d", i));
        }
        minutes.add("");
        minutes.add("");

        months = new ArrayList<>(Arrays.asList("", "", "янв", "фев", "март", "апр", "май", "июнь", "июль", "авг", "сен", "окт", "нояб", "дек", "", ""));

        years = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 2015; i <= 2050; i++) {
            years.add(Integer.toString(i));
        }
        years.add("");
        years.add("");

        dayAdapter.setData(days);
        monthAdapter.setData(months);
        yearAdapter.setData(years);
        hourAdapter.setData(hours);
        minuteAdapter.setData(minutes);
    }

    public void resetDaysArray(int currentMonth, int currentYear, int dayPosition) {
        int daysCount;
        switch (currentMonth) {
            case 1:
                if ((currentYear) % 4 == 0) {
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
        Log.i(TAG, dayPosition+ " " + daysCount);
        if (dayPosition + 1 > daysCount){
            dayPosition = dayPosition - (dayPosition + 1 - daysCount);
            Log.i(TAG, "new dayPos:" + dayPosition);
        }
        dayAdapter.changeItemAppearance(dayPosition);
    }

    private void setRVScrollListeners() {
        final Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        rvDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = dayLM.getPosition(daySH.findSnapView(dayLM)) - 2;
                if (currentDayPosition != firstVisible) {
                    currentDayPosition = firstVisible;
                    dayAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else{
                        v.vibrate(10);
                    }
                    updateDateInHeader();
                }
            }
        });

        rvMonth.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resetDaysArray(currentMonthPosition,
                            currentYearPosition + startYearInArray, currentDayPosition);
                    Log.i(TAG, "IDLE month");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = monthLM.getPosition(monthSH.findSnapView(monthLM)) - 2;
                if (currentMonthPosition != firstVisible) {
                    currentMonthPosition = firstVisible;
                    monthAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else{
                        v.vibrate(50);
                    }
                    updateDateInHeader();
                }
            }
        });

        rvYear.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resetDaysArray(currentMonthPosition,
                            currentYearPosition + startYearInArray, currentDayPosition);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = yearLM.getPosition(yearSH.findSnapView(yearLM)) - 2;
                if (currentYearPosition != firstVisible) {
                    currentYearPosition = firstVisible;
                    yearAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else{
                        v.vibrate(50);
                    }
                    updateDateInHeader();
                }
            }
        });

        rvHour.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = hourLM.getPosition(hourSH.findSnapView(hourLM)) - 2;
                if (currentHourPosition != firstVisible) {
                    currentHourPosition = firstVisible;
                    hourAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else{
                        v.vibrate(50);
                    }
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
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else{
                        v.vibrate(50);
                    }
                    updateTimeInHeader();
                }
            }
        });
    }

    private void updateDateInHeader(){
        tvDate.setText(String.format(Locale.getDefault(), "%02d.%02d.%02d", currentDayPosition+1,
                currentMonthPosition+1, currentYearPosition+startYearInArray));
    }

    private void updateTimeInHeader(){
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", currentHourPosition
                , currentMinutePosition));
    }

    private void showDatePicker() {
        if (rvDay.getVisibility() == View.INVISIBLE) {
            rvDay.setVisibility(View.VISIBLE);
            rvMonth.setVisibility(View.VISIBLE);
            rvYear.setVisibility(View.VISIBLE);
            rvHour.setVisibility(View.INVISIBLE);
            rvMinute.setVisibility(View.INVISIBLE);
            llDate.setBackgroundResource(R.drawable.bottom_border);
            llTime.setBackgroundResource(0);
            ivDate.setColorFilter(getResources().getColor(R.color.colorPrimary));
            tvDate.setTextColor(getResources().getColor(R.color.colorPrimary));
            ivTime.setColorFilter(getResources().getColor(R.color.textColorPrimary));
            tvTime.setTextColor(getResources().getColor(R.color.textColorPrimary));
        }
    }

    private void showTimePicker() {
        if (rvHour.getVisibility() == View.INVISIBLE) {
            rvDay.setVisibility(View.INVISIBLE);
            rvMonth.setVisibility(View.INVISIBLE);
            rvYear.setVisibility(View.INVISIBLE);
            rvHour.setVisibility(View.VISIBLE);
            rvMinute.setVisibility(View.VISIBLE);
            llTime.setBackgroundResource(R.drawable.bottom_border);
            llDate.setBackgroundResource(0);
            ivTime.setColorFilter(getResources().getColor(R.color.colorPrimary));
            tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
            ivDate.setColorFilter(getResources().getColor(R.color.textColorPrimary));
            tvDate.setTextColor(getResources().getColor(R.color.textColorPrimary));
        }
    }

    //Установка шага для minutes
    private void setMinutesStep(int step) {
        minutes.clear();
        minutes.add("");
        minutes.add("");
        for (int i = 0; i <= 59; i += step) {
            minutes.add(String.format(Locale.getDefault(), "%02d", i));
        }
        minutes.add("");
        minutes.add("");
    }
}
