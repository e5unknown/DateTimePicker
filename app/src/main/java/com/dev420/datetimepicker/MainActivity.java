package com.dev420.datetimepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.os.Handler;
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

    private ArrayList<String> days;
    private ArrayList<String> months;
    private ArrayList<String> years;
    private ArrayList<String> hours;
    private ArrayList<String> minutes;

    private CardView buttonCancel;
    private CardView buttonSave;

    //костыли или реализация щелчков
    private int currentMinPosition;
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
        int currentMinute = calendar.get(Calendar.MINUTE);
        rvMinute.scrollToPosition(currentMinute);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        rvHour.scrollToPosition(currentHour);
        int currentYear = calendar.get(Calendar.YEAR);
        //скролл сделал на случай, если понадобится DateTimePicker с прошлыми годами
        int startYearInArray = 2015;
        rvYear.scrollToPosition(currentYear - startYearInArray);
        yearAdapter.changeItemAppearance(currentYear - startYearInArray);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        rvMonth.scrollToPosition(currentMonth - 1);
        monthAdapter.changeItemAppearance(currentMonth - 1);
        generateDaysArray(currentMonth, currentYear);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        rvDay.scrollToPosition(currentDay - 1);
        dayAdapter.changeItemAppearance(currentDay - 1);
        tvDate.setText(String.format(Locale.getDefault(), "%02d.%02d.%d", currentDay, currentMonth, currentYear));
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute));
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
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);
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
        SnapHelper daySH = new LinearSnapHelper();
        SnapHelper monthSH = new LinearSnapHelper();
        SnapHelper yearSH = new LinearSnapHelper();
        SnapHelper hourSH = new LinearSnapHelper();
        SnapHelper minuteSH = new LinearSnapHelper();
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
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder builder = new StringBuilder();
                builder.append("Saved: ")
                        .append(hours.get(hourLM.findFirstCompletelyVisibleItemPosition() + 2))
                        .append(":")
                        .append(minutes.get(minuteLM.findFirstCompletelyVisibleItemPosition() + 2))
                        .append(" ")
                        .append(days.get(dayLM.findFirstCompletelyVisibleItemPosition() + 2))
                        .append(".")
                        .append(months.get(monthLM.findFirstCompletelyVisibleItemPosition() + 2))
                        .append(".")
                        .append(years.get(yearLM.findFirstCompletelyVisibleItemPosition() + 2));
                Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDateAndTimeData() {
        months = new ArrayList<>(Arrays.asList("", "", "янв", "фев", "март", "апр", "май", "июнь", "июль", "авг", "сен", "окт", "нояб", "дек", "", ""));
        years = new ArrayList<>(Arrays.asList("", ""));
        hours = new ArrayList<>(Arrays.asList("", ""));
        minutes = new ArrayList<>(Arrays.asList("", ""));

        for (int i = 2015; i <= 2050; i++) {
            years.add(Integer.toString(i));
        }
        years.add("");
        years.add("");
        for (int i = 0; i <= 23; i++) {
            hours.add(String.format(Locale.getDefault(), "%02d", i));
        }
        hours.add("");
        hours.add("");
        for (int i = 0; i <= 59; i++) {
            minutes.add(String.format(Locale.getDefault(), "%02d", i));
        }
        minutes.add("");
        minutes.add("");
        monthAdapter.setData(months);
        yearAdapter.setData(years);
        hourAdapter.setData(hours);
        minuteAdapter.setData(minutes);
        generateDaysArray(0, 2020);
        //init костыли
        currentMinPosition = 0;
        currentHourPosition = 0;
        currentDayPosition = 0;
        currentMonthPosition = 0;
        currentYearPosition = 0;
    }

    public void resetDaysArray() {
        //к выдаче по месяцам не добавляем 2, т.к. при firstVisible == 0 будет выделен январь
        int monthPosition = monthLM.findFirstCompletelyVisibleItemPosition();
        //к выдаче по годам добавим 2, чтобы учесть 2 пустых элемента в массиве и получить верный год
        int yearPosition = yearLM.findFirstCompletelyVisibleItemPosition() + 2;
        int year = Integer.parseInt(years.get(yearPosition));
        generateDaysArray(monthPosition, year);
    }

    public void generateDaysArray(int monthPosition, int year) {
        int daysCount;
        switch (monthPosition) {
            case 1:
                if ((year) % 4 == 0) {
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
        days = new ArrayList<>(Arrays.asList("", ""));
        for (int i = 1; i <= daysCount; i++) {
            days.add(String.format(Locale.getDefault(), "%02d", i));
        }
        days.add("");
        days.add("");
        dayAdapter.setData(days);
    }


    private void setRVScrollListeners() {
        rvDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = dayLM.findFirstCompletelyVisibleItemPosition();
                if (currentDayPosition != firstVisible){
                    currentDayPosition = firstVisible;
                    dayAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
        });

        rvMonth.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //обновляем количество дней при изменении месяца
                resetDaysArray();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = monthLM.findFirstCompletelyVisibleItemPosition();
                if (currentMonthPosition != firstVisible){
                    currentMonthPosition = firstVisible;
                    monthAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
        });

        rvYear.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //обновляем количество дней при изменении года
                resetDaysArray();

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = yearLM.findFirstCompletelyVisibleItemPosition();
                if (currentYearPosition != firstVisible){
                    currentYearPosition = firstVisible;
                    yearAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
        });

        rvHour.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = hourLM.findFirstCompletelyVisibleItemPosition();
                if (currentHourPosition != firstVisible){
                    currentHourPosition = firstVisible;
                    hourAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
        });


        rvMinute.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = minuteLM.findFirstCompletelyVisibleItemPosition();
                if (currentMinPosition != firstVisible){
                    currentMinPosition = firstVisible;
                    minuteAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                }
            }
        });
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
