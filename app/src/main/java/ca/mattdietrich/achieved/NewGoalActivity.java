package ca.mattdietrich.achieved;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ca.mattdietrich.achieved.database.DatabaseAccessObject;
import ca.mattdietrich.achieved.model.Goal;
import ca.mattdietrich.achieved.model.Reminder;
import ca.mattdietrich.achieved.notification.NotificationIntentService;

public class NewGoalActivity extends AppCompatActivity {

    private EditText newGoalText;
    private EditText newRewardText;
    private TextView reminderTimeText;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private SwitchCompat reminderSwitch;
    private ImageButton cancelButton;
    private ImageButton confirmButton;

    private int mHourOfDay;
    private int mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);

        newGoalText = (EditText) findViewById(R.id.txt_new_goal);
        newGoalText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isGoalBlank(newGoalText.getText().toString()))
                    newGoalText.setError("Goal cannot be blank");
                else
                    newGoalText.setError(null);
            }
        });
        newRewardText = (EditText) findViewById(R.id.txt_new_goal_reward);

        mHourOfDay = 0;
        mMinute = 0;

        reminderTimeText = (TextView) findViewById(R.id.txt_new_goal_reminder);
        reminderTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view,
                                  int hourOfDay, int minute) {
                updateReminderTime(hourOfDay, minute);
                reminderSwitch.setChecked(true);
            }
        };

        reminderSwitch = (SwitchCompat) findViewById(R.id.switch_new_goal_reminder);

        cancelButton = (ImageButton) findViewById(R.id.btn_cancel_new_goal);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelWithoutSaving();
            }
        });

        confirmButton = (ImageButton) findViewById(R.id.btn_confirm_new_goal);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGoalBlank(newGoalText.getText().toString()))
                    newGoalText.setError("Goal cannot be blank");
                else
                    confirmNewGoal();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_goal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setOnTimeSetListener(mTimeSetListener);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void updateReminderTime(int hourOfDay, int minute) {
        // Update the saved time values (so we can easily set an alarm later)
        mHourOfDay = hourOfDay;
        mMinute = minute;

        // Convert 24-hour time to 12-hour time
        String suffix;
        if(hourOfDay < 12)
            suffix = "AM";
        else {
            suffix = "PM";
            hourOfDay = hourOfDay % 12;
        }
        if (hourOfDay == 0)
            hourOfDay = 12;

        reminderTimeText.setText(Integer.toString(hourOfDay) + ":" + String.format("%02d", minute) + " " + suffix);
    }

    private void cancelWithoutSaving() {
        finish();
    }

    private void confirmNewGoal() {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        Goal newGoal = db.createGoal(newGoalText.getText().toString(), today, newRewardText.getText().toString());

        Toast.makeText(getApplicationContext(), "Today's goal: " + newGoal.getText(), Toast.LENGTH_SHORT).show();

        confirmReminder(newGoal);

        this.finish();
    }

    private void confirmReminder(Goal goal) {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(goal.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDay);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);
        Date reminderTime = calendar.getTime();

        Reminder newReminder = db.createReminder(reminderTime, reminderSwitch.isChecked(), goal.getId());

        if (reminderSwitch.isChecked()) {
            Toast.makeText(getApplicationContext(), "Today's reminder: " + reminderTimeText.getText(), Toast.LENGTH_SHORT).show();

            // Set the notification alarm
            Intent serviceIntent = NotificationIntentService.createIntentReminderNotification(getApplicationContext());
            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            //Log.d("Current Time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
            //Log.d("Alarm Time", String.valueOf(calendar.getTimeInMillis()));
        }

    }

    private boolean isGoalBlank(String goal) {
        if (goal == null)
            return true;
        return goal.trim().equalsIgnoreCase("");
    }
}
