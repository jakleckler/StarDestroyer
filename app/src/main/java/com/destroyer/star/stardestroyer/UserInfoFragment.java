package com.destroyer.star.stardestroyer;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    protected static final String PASSWORD = "password";
    protected static final String USERNAME = "username";

    private EditText passTextView;
    private EditText userTextView;  //Member variable for text view
    private OnFragmentInteractionListener mListener;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserInfoFragment.
     */
    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_user_info, container, false);
        userTextView = (EditText) v.findViewById(R.id.username);
        passTextView = (EditText) v.findViewById(R.id.password);

        if (savedInstanceState != null) {
            userTextView.setText(savedInstanceState.getString(USERNAME));
            passTextView.setText(savedInstanceState.getString(PASSWORD));
        } else {
            userTextView.setText("");
            passTextView.setText("");
        }

        Button button = (Button) v.findViewById(R.id.login_user_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginUser();
            }
        });

        return v;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Save the entered info
        savedInstanceState.putString(USERNAME, userTextView.getText().toString());
        savedInstanceState.putString(PASSWORD, passTextView.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Called when the user clicks the Send button
     */
    public void loginUser() {
        Intent intent;
        intent = new Intent(getActivity(), DisplayUserActivity.class);

        String username = userTextView.getText().toString();
        String password = passTextView.getText().toString();
        intent.putExtra(USERNAME, username);
        intent.putExtra(PASSWORD , password);
        startActivity(intent);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
