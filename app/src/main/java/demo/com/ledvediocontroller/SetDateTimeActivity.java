package demo.com.ledvediocontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import demo.com.ledvediocontroller.fragments.DatePickerDialogFragment;
import demo.com.ledvediocontroller.fragments.TimePickerDialogFragment;

public class SetDateTimeActivity extends AppCompatActivity {

    DatePickerDialogFragment datePickerDialogFragment;
    TimePickerDialogFragment timePickerDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date_time);
        findViewById(R.id.setting_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datePickerDialogFragment == null){
                    datePickerDialogFragment = new DatePickerDialogFragment();
                    datePickerDialogFragment.show(getSupportFragmentManager(),"date");
                }
            }
        });

        findViewById(R.id.setting_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timePickerDialogFragment == null){
                    timePickerDialogFragment = new TimePickerDialogFragment();
                    timePickerDialogFragment.show(getSupportFragmentManager(),"time");
                }
            }
        });
    }
}
