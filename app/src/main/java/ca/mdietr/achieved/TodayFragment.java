package ca.mdietr.achieved;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


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

    private TextView txtReminderTime;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private LinearLayout defaultButtonRow;
    private LinearLayout editButtonRow;
    private ImageButton editButton;
    private ImageButton cancelEditButton;

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

        txtReminderTime = (TextView) rootView.findViewById(R.id.txt_reminder);
        txtReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        defaultButtonRow = (LinearLayout) rootView.findViewById(R.id.default_buttons);
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
            }
        });

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view,
                                  int hourOfDay, int minute) {
                updateReminderTime(hourOfDay, minute);
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

    public void switchToEditMode() {
        if(defaultButtonRow != null)
            defaultButtonRow.setVisibility(View.GONE);
        if(editButtonRow != null)
            editButtonRow.setVisibility(View.VISIBLE);
    }

    public void switchToDefaultMode() {
        if(editButtonRow != null)
            editButtonRow.setVisibility(View.GONE);
        if(defaultButtonRow != null)
            defaultButtonRow.setVisibility(View.VISIBLE);
    }

    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setOnTimeSetListener(mTimeSetListener);
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public void updateReminderTime(int hourOfDay, int minute) {
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

        txtReminderTime.setText(Integer.toString(hourOfDay)+":"+String.format("%02d",minute)+ " " + suffix);
    }



}
