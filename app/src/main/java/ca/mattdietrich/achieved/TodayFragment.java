package ca.mattdietrich.achieved;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ca.mattdietrich.achieved.database.DatabaseAccessObject;
import ca.mattdietrich.achieved.model.Goal;
import ca.mattdietrich.achieved.model.Reminder;
import ca.mattdietrich.achieved.notification.NotificationIntentService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    // UI Controls
    private View cardInclude;
    private EditText txtGoal;
    private TextView txtReminderLabel;
    private TextView txtReminderTime;
    private SwitchCompat reminderSwitch;
    private TextView txtRewardLabel;
    private EditText txtReward;
    private TextView txtStatusLabel;
    private TextView txtStatus;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private LinearLayout defaultButtonRow;
    private LinearLayout editButtonRow;
    private ImageButton achieveButton;
    private ImageButton addButton;
    private ImageButton editButton;
    private ImageButton cancelEditButton;
    private ImageButton confirmEditButton;

    private TextView txtNoGoalTitle;
    private TextView txtNoGoalMessage;

    private View[] formViews;
    private View[] noGoalViews;

    private Goal todaysGoal;
    private Reminder todaysReminder;

    private boolean isGoalEditable = false;

    private int mReminderHourOfDay;
    private int mReminderMinute;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(int navDrawerSectionNumber) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putInt (MainActivity.ARG_NAV_DRAWER_SECTION_NUMBER, navDrawerSectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TodayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        cardInclude = rootView.findViewById(R.id.include);
        txtReminderLabel = (TextView) rootView.findViewById(R.id.txt_reminder_label);
        txtRewardLabel = (TextView) rootView.findViewById(R.id.txt_reward_label);
        txtStatusLabel = (TextView) rootView.findViewById(R.id.txt_status_label);

        txtGoal = (EditText) rootView.findViewById(R.id.card_view_goal_name);
        txtGoal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (todaysGoal != null && isGoalBlank(txtGoal.getText().toString()))
                    txtGoal.setError("Goal cannot be blank");
                else
                    txtGoal.setError(null);
            }
        });

        txtReminderTime = (TextView) rootView.findViewById(R.id.txt_reminder);
        txtReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGoalEditable)
                    showTimePickerDialog(v);
            }
        });

        reminderSwitch = (SwitchCompat) rootView.findViewById(R.id.switch_reminder);

        txtReward = (EditText) rootView.findViewById(R.id.txt_reward);

        txtStatus = (TextView) rootView.findViewById(R.id.txt_status);

        defaultButtonRow = (LinearLayout) rootView.findViewById(R.id.default_buttons);

        achieveButton = (ImageButton) rootView.findViewById(R.id.btn_achieve_it);
        achieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                achieveGoal();
            }
        });

        addButton = (ImageButton) rootView.findViewById(R.id.btn_new_goal);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewGoalActivity.class);
                startActivity(intent);
            }
        });

        editButtonRow = (LinearLayout) rootView.findViewById(R.id.edit_buttons);

        editButton = (ImageButton) rootView.findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEditMode();
            }
        });

        cancelEditButton = (ImageButton) rootView.findViewById(R.id.btn_cancel_edit);
        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToDefaultMode();
                refresh();
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled changes", Toast.LENGTH_SHORT).show();
            }
        });

        confirmEditButton = (ImageButton) rootView.findViewById(R.id.btn_confirm_edit);
        confirmEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGoalBlank(txtGoal.getText().toString()))
                    txtGoal.setError("Goal cannot be blank");
                else {
                    saveEditedGoalAndReminder();
                    switchToDefaultMode();
                    refresh();
                }
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                updateReminderTime(hourOfDay, minute);
                reminderSwitch.setChecked(true);
            }
        };

        formViews = new View[] {cardInclude, txtReminderLabel, txtReminderTime, reminderSwitch, txtRewardLabel, txtReward, txtStatusLabel, txtStatus};

        txtNoGoalTitle = (TextView) rootView.findViewById(R.id.no_goal_title);
        txtNoGoalMessage = (TextView) rootView.findViewById(R.id.no_goal_message);

        noGoalViews = new View[] {txtNoGoalTitle, txtNoGoalMessage};

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_NAV_DRAWER_SECTION_NUMBER));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO - Implement pull down refresh
        refresh();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void saveEditedGoalAndReminder() {
        if (todaysGoal == null)
            return;

        // Save the edited goal in the database
        todaysGoal.setText(txtGoal.getText().toString());
        todaysGoal.setReward(txtReward.getText().toString());
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getActivity().getApplicationContext());
        db.updateGoal(todaysGoal);

        // Save the edited reminder in the database
        if (todaysReminder != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(todaysGoal.getDate());
            calendar.set(Calendar.HOUR_OF_DAY, mReminderHourOfDay);
            calendar.set(Calendar.MINUTE, mReminderMinute);
            calendar.set(Calendar.SECOND, 0);
            Date reminderTime = calendar.getTime();

            todaysReminder.setEnabled(reminderSwitch.isChecked());
            todaysReminder.setDateTime(reminderTime);
            db.updateReminder(todaysReminder);

            cancelReminderAlarm(); // Cancel the old alarm
            if (todaysReminder.isEnabled()) // Set a new one, if necessary
                setReminderAlarm();
        }

        refresh();

        Toast.makeText(getActivity().getApplicationContext(), "Saved changes", Toast.LENGTH_SHORT).show();
    }

    public void achieveGoal() {
        if (todaysGoal == null || todaysGoal.isAchieved())
            return;

        // Update Goal achieved flag in Database

        todaysGoal.setAchieved(true);
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getActivity().getApplicationContext());
        db.updateGoal(todaysGoal);

        refresh();

        // Show Congratulations + Reward Dialog

        String rewardMessage = "I knew you could do it!";
        if (todaysGoal.getReward() != null && !todaysGoal.getReward().equals(""))
            rewardMessage = "Reward yourself:\n" + todaysGoal.getReward();


        Toast.makeText(getActivity().getApplicationContext(), "Goal Achieved!", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Goal Achieved!")
                .setMessage(rewardMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void switchToEditMode() {
        if(defaultButtonRow != null)
            defaultButtonRow.setVisibility(View.GONE);
        if(editButtonRow != null)
            editButtonRow.setVisibility(View.VISIBLE);

        txtGoal.setEnabled(true);
        reminderSwitch.setEnabled(true);
        txtReward.setEnabled(true);
        isGoalEditable = true;
    }

    public void switchToDefaultMode() {
        if(editButtonRow != null)
            editButtonRow.setVisibility(View.GONE);
        if(defaultButtonRow != null)
            defaultButtonRow.setVisibility(View.VISIBLE);

        txtGoal.setEnabled(false);
        txtGoal.setTextColor(Color.BLACK);
        reminderSwitch.setEnabled(false);
        txtReward.setEnabled(false);
        txtReward.setTextColor(Color.BLACK);
        isGoalEditable = false;
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setOnTimeSetListener(mTimeSetListener);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public void updateReminderTime(int hourOfDay, int minute) {
        // Update the saved time values (so we can easily set an alarm later)
        mReminderHourOfDay = hourOfDay;
        mReminderMinute = minute;

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

        txtReminderTime.setText(Integer.toString(hourOfDay) + ":" + String.format("%02d", minute) + " " + suffix);
    }

    public void refresh() {
        // TODO - Show Refresh Animation
        todaysGoal = getTodaysGoal();
        todaysReminder = getReminder(todaysGoal);
        updateUIWithGoal(todaysGoal);
        updateUIWithReminder(todaysReminder);
        if (todaysGoal == null) {
            setViewListVisibility(formViews, View.GONE); // Hide the form fields
            setViewListVisibility(noGoalViews, View.VISIBLE); // Show the "No Goal" message

            editButton.setEnabled(false);
            editButton.setVisibility(View.INVISIBLE);
            addButton.setEnabled(true);
            addButton.setVisibility(View.VISIBLE);
        }
        else {
            setViewListVisibility(formViews, View.VISIBLE); // Show the form fields
            setViewListVisibility(noGoalViews, View.GONE); // Hide the "No Goal" message

            editButton.setEnabled(true);
            editButton.setVisibility(View.VISIBLE);
            addButton.setEnabled(false);
            addButton.setVisibility(View.INVISIBLE);
        }
        // TODO - Hide Refresh Animation
    }

    private void setViewListVisibility(View[] viewList, int visibility) {
        for (View v: viewList) {
            v.setVisibility(visibility);
        }
    }

    private void setReminderAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysReminder.getDateTime());

        Intent serviceIntent = NotificationIntentService.createIntentReminderNotification(getActivity().getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelReminderAlarm() {
        Intent serviceIntent = NotificationIntentService.createIntentReminderNotification(getActivity().getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private Goal getTodaysGoal() {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getActivity().getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Goal g = db.getGoal(today);
        
        return g;
    }

    private Reminder getReminder(Goal goal) {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getActivity().getApplicationContext());

        Reminder r = db.getReminder(goal);

        return r;
    }

    private void updateUIWithGoal(Goal goal) {
        if (goal == null) {
            // TODO - No Goal message
            txtGoal.setText("");
            txtReward.setText("");
            txtStatus.setText("");

            return;
        }
        String status = "In Progress";
        if (goal.isAchieved())
            status = "Achieved!";

        txtGoal.setText(goal.getText());
        txtReward.setText(goal.getReward());
        txtStatus.setText(status);
    }

    private void updateUIWithReminder(Reminder reminder) {
        if (reminder == null || !reminder.isEnabled()) {
            // TODO - No Reminder message
            reminderSwitch.setChecked(false);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(reminder.getDateTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        updateReminderTime(hours, minutes);
        reminderSwitch.setChecked(true);
    }

    private boolean isGoalBlank(String goal) {
        if (goal == null)
            return true;
        return goal.trim().equalsIgnoreCase("");
    }
}
