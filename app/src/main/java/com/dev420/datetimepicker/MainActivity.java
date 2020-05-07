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

    private ArrayList<String> days;
    private ArrayList<String> months;
    private ArrayList<String> years;
    private ArrayList<String> hours;
    private ArrayList<String> minutes;

    int startYearInArray;

    //проверка изменения позиции для аккуратной реализация щелчков
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
        int currentMinute = calendar.get(Calendar.MINUTE);
        rvMinute.scrollToPosition(currentMinute);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        rvHour.scrollToPosition(currentHour);
        int currentYear = calendar.get(Calendar.YEAR);
        rvYear.scrollToPosition(currentYear - startYearInArray);
        yearAdapter.changeItemAppearance(currentYear - startYearInArray);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        rvMonth.scrollToPosition(currentMonth - 1);
        monthAdapter.changeItemAppearance(currentMonth - 1);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        rvDay.scrollToPosition(currentDay);
        Log.i(TAG, "dayPos, currentDay: " + currentDay);
        resetDaysArray(currentMonth - 1, currentYear, currentDay);
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
        //Получение выбранной даты/времени по клику на кнопку "Сохранить"
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String builder = "Saved: " +
                        hours.get(hourLM.findFirstCompletelyVisibleItemPosition() + 2) +
                        ":" +
                        minutes.get(minuteLM.findFirstCompletelyVisibleItemPosition() + 2) +
                        " " +
                        days.get(dayLM.findFirstCompletelyVisibleItemPosition() + 2) +
                        "." +
                        months.get(monthLM.findFirstCompletelyVisibleItemPosition() + 2) +
                        "." +
                        years.get(yearLM.findFirstCompletelyVisibleItemPosition() + 2);
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

    public void checkCurrentMonthYearAndResetDaysArray() {
        //к выдаче по месяцам не добавляем 2, т.к. при firstVisible == 0 будет выделен январь
        int monthPosition = monthLM.findFirstCompletelyVisibleItemPosition();
        //к выдаче по годам добавим 2, чтобы учесть 2 пустых элемента в массиве и получить верный год
        int yearPosition = yearLM.findFirstCompletelyVisibleItemPosition();
        View daySnapView = daySH.findSnapView(dayLM);
        int dayPosition = dayLM.getPosition(daySnapView);
        Log.i(TAG, "dayPos: " + dayPosition);
        resetDaysArray(monthPosition, yearPosition + startYearInArray, dayPosition - 2);
    }

    public void resetDaysArray(int currentMonth, int currentYear, int dayPosition) {
        Log.i("WTF", "currentMonth " + currentMonth);
        Log.i("WTF", "currentYear " + currentYear);
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
            Log.i("WTF+", days.size() + " " + daysCount + " " + newDays.toString());
            dayAdapter.notifyItemRangeInserted(days.size() - 2, newDays.size());
        } else if ((days.size() - 4) > daysCount) {
            int startIndex = daysCount + 2;
            int lastIndex = days.size() - 2;
            int count = lastIndex - startIndex;
            days.subList(startIndex, lastIndex).clear();
            Log.i("WTF-", days.size() + " " + daysCount + " " + startIndex + " " + lastIndex + " " + count);
            dayAdapter.notifyItemRangeRemoved(startIndex, count);
        }
        Log.i(TAG, dayPosition+ " " + daysCount);
        if (dayPosition + 1 > daysCount){
            dayPosition = dayPosition - (dayPosition + 1 - daysCount);
            Log.i(TAG, "new dayPos:"+dayPosition);
        }
        dayAdapter.changeItemAppearance(dayPosition);
    }

    private void setRVScrollListeners() {
        //init костыли, чтобы он не отправлял по несколько раз в секунду обновление адаптера
        currentMinutePosition = 0;
        currentHourPosition = 0;
        currentDayPosition = 0;
        currentMonthPosition = 0;
        currentYearPosition = 0;
        rvDay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i(TAG, "IDLE");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = dayLM.findFirstCompletelyVisibleItemPosition();
                if (currentDayPosition != firstVisible) {
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkCurrentMonthYearAndResetDaysArray();
                    Log.i(TAG, "IDLE month");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = monthLM.findFirstCompletelyVisibleItemPosition();
                if (currentMonthPosition != firstVisible) {
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkCurrentMonthYearAndResetDaysArray();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = yearLM.findFirstCompletelyVisibleItemPosition();
                if (currentYearPosition != firstVisible) {
                    currentYearPosition = firstVisible;
                    yearAdapter.changeItemAppearance(firstVisible);
                    recyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                    checkCurrentMonthYearAndResetDaysArray();
                }
            }
        });

        rvHour.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisible = hourLM.findFirstCompletelyVisibleItemPosition();
                if (currentHourPosition != firstVisible) {
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
                if (currentMinutePosition != firstVisible) {
                    currentMinutePosition = firstVisible;
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
