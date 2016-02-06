package ca.mdietr.achieved;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import ca.mdietr.achieved.notification.NotificationIntentService;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private TextView txtSettingsReminderTime;
    private SwitchCompat settingsReminderSwitch;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(int navDrawerSectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt (MainActivity.ARG_NAV_DRAWER_SECTION_NUMBER, navDrawerSectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        txtSettingsReminderTime =  (TextView) rootView.findViewById(R.id.txt_settings_reminder);

        txtSettingsReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });


        settingsReminderSwitch =  (SwitchCompat) rootView.findViewById(R.id.switch_settings_reminder);
        settingsReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSharedPreferences(isChecked);
                if (isChecked)
                    setReminderAlarm();
                else
                    cancelReminderAlarm();
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                updateReminderTime(hourOfDay, minute);
                updateSharedPreferences(hourOfDay, minute);

                // If reminder is already set, cancel the old one and replace with new one
                // Else, set the reminder switch (and later set the new alarm)
                if (settingsReminderSwitch.isChecked()) {
                    cancelReminderAlarm();
                    setReminderAlarm();
                } else
                    settingsReminderSwitch.setChecked(true);
            }
        };
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

        refresh();
    }

    private void refresh() {
        SharedPreferences spref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_name), Activity.MODE_PRIVATE);

        // Set the switch
        if (spref.getBoolean(getString(R.string.shared_preferences_set_goal_enabled),false))
            settingsReminderSwitch.setChecked(true);
        else
            settingsReminderSwitch.setChecked(false);

        // Set time UI
        int hour = spref.getInt(getString(R.string.shared_preferences_set_goal_hour), 0);
        int minute = spref.getInt(getString(R.string.shared_preferences_set_goal_minute), 0);
        updateReminderTime(hour, minute);
    }

    private void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setOnTimeSetListener(mTimeSetListener);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    private void updateReminderTime(int hourOfDay, int minute) {
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

        txtSettingsReminderTime.setText(Integer.toString(hourOfDay) + ":" + String.format("%02d", minute) + " " + suffix);
    }

    private void setReminderAlarm() {
        SharedPreferences spref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_name), Activity.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, spref.getInt(getString(R.string.shared_preferences_set_goal_hour), 0));
        calendar.set(Calendar.MINUTE, spref.getInt(getString(R.string.shared_preferences_set_goal_minute), 0));
        calendar.set(Calendar.SECOND, 0);
        long alarmTime = calendar.getTimeInMillis();

        if (currentTime > alarmTime)
            calendar.add(Calendar.DAY_OF_YEAR, 1); // Add a day if we're already past the alarm time

        Intent serviceIntent = NotificationIntentService.createIntentSetGoalNotification(getActivity().getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelReminderAlarm() {
        Intent serviceIntent = NotificationIntentService.createIntentSetGoalNotification(getActivity().getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(getActivity().getApplicationContext(), 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    private void updateSharedPreferences(boolean isEnabled) {
        SharedPreferences spref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_name), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putBoolean(getString(R.string.shared_preferences_set_goal_enabled), isEnabled);
        editor.commit();
    }

    private void updateSharedPreferences(int hour, int minute) {
        SharedPreferences spref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences_name), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt(getString(R.string.shared_preferences_set_goal_hour), hour);
        editor.putInt(getString(R.string.shared_preferences_set_goal_minute), minute);
        editor.commit();
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

}
