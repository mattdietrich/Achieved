package ca.mdietr.achieved;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ca.mdietr.achieved.achievements.AchievementsCursorAdapter;
import ca.mdietr.achieved.database.DatabaseAccessObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AchievementsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AchievementsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AchievementsFragment extends Fragment {

    private ListView listView;

    private TextView txtNoAchievementsTitle;
    private TextView txtNoAchievementsMessage;

    private View[] noAchievementsViews;

    private AchievementsCursorAdapter cursorAdapter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AchievementsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AchievementsFragment newInstance(int navDrawerSectionNumber) {
        AchievementsFragment fragment = new AchievementsFragment();
        Bundle args = new Bundle();
        args.putInt (MainActivity.ARG_NAV_DRAWER_SECTION_NUMBER, navDrawerSectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AchievementsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_achievements, container, false);

        listView = (ListView) rootView.findViewById(R.id.lv_achievements);

        txtNoAchievementsTitle = (TextView) rootView.findViewById(R.id.no_achievements_title);
        txtNoAchievementsMessage = (TextView) rootView.findViewById(R.id.no_achievements_message);

        noAchievementsViews = new View[] {txtNoAchievementsTitle, txtNoAchievementsMessage};

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
        Cursor cursor = getAchievementsCursor();
        if (cursor == null || cursor.getCount() <= 0 )
            setViewListVisibility(noAchievementsViews, View.VISIBLE); // Show the "No Achievements" message
        else
            setViewListVisibility(noAchievementsViews, View.GONE); // Hide the "No Achievements" message
        if (cursorAdapter == null) {
            cursorAdapter = new AchievementsCursorAdapter(getActivity(), cursor);
            listView.setAdapter(cursorAdapter);
        } else {
            cursorAdapter.changeCursor(cursor);
        }
    }

    private void setViewListVisibility(View[] viewList, int visibility) {
        for (View v: viewList) {
            v.setVisibility(visibility);
        }
    }

    private Cursor getAchievementsCursor() {
        DatabaseAccessObject db = DatabaseAccessObject.getInstance(getActivity().getApplicationContext());
        return db.getAchievementsCursor();
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
