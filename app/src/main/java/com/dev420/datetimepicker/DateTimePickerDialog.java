package com.dev420.datetimepicker;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev420.datetimepicker.adapters.PickerViewPagerAdapter;

public class DateTimePickerDialog extends DialogFragment implements TimePickerCallback, DatePickerCallback {

    private LinearLayout llDate;
    private LinearLayout llTime;
    private ImageView ivDate;
    private ImageView ivTime;
    private TextView tvDate;
    private TextView tvTime;

    private SoundPool sounds;

    private ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Dialog_Alert);
        createSoundPool();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_time_picker_dialog, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        PickerViewPagerAdapter pagerAdapter = new PickerViewPagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, sounds);
        viewPager = view.findViewById(R.id.viewPagerDateTimePicker);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    clickDateHeader();
                } else{
                    clickTimeHeader();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initUI(view);
        return view;
    }

    private void createSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    private void initUI(View view) {
        llDate = view.findViewById(R.id.llDate);
        llTime = view.findViewById(R.id.llTime);
        ivDate = view.findViewById(R.id.ivDate);
        ivTime = view.findViewById(R.id.ivTime);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
        CardView buttonCancel = view.findViewById(R.id.buttonCancel);
        CardView buttonSave = view.findViewById(R.id.buttonSave);
        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDateHeader();
            }
        });
        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickTimeHeader();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            dismiss();
            }
        });
        //Получение выбранной даты/времени по клику на кнопку "Сохранить"
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "Time: " + tvTime.getText().toString() +", Date: "+ tvDate.getText().toString();
                Toast.makeText(getContext(), result , Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void clickDateHeader() {
        viewPager.setCurrentItem(0, true);
        llDate.setBackgroundResource(R.drawable.bottom_border);
        llTime.setBackgroundResource(0);
        ivDate.setColorFilter(getResources().getColor(R.color.colorPrimary));
        tvDate.setTextColor(getResources().getColor(R.color.colorPrimary));
        ivTime.setColorFilter(getResources().getColor(R.color.textColorPrimary));
        tvTime.setTextColor(getResources().getColor(R.color.textColorPrimary));
    }

    private void clickTimeHeader() {
        viewPager.setCurrentItem(1,true);
        llTime.setBackgroundResource(R.drawable.bottom_border);
        llDate.setBackgroundResource(0);
        ivTime.setColorFilter(getResources().getColor(R.color.colorPrimary));
        tvTime.setTextColor(getResources().getColor(R.color.colorPrimary));
        ivDate.setColorFilter(getResources().getColor(R.color.textColorPrimary));
        tvDate.setTextColor(getResources().getColor(R.color.textColorPrimary));
    }

    @Override
    public void updateTimeFromPicker(String timeHHMM) {
        tvTime.setText(timeHHMM);
    }

    @Override
    public void updateDateFromPicker(String dateDDMMYYYY) {
        tvDate.setText(dateDDMMYYYY);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        sounds.release();
        sounds = null;
    }
}
