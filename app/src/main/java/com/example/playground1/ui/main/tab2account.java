package com.example.playground1.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.playground1.R;
import com.example.playground1.activities.accountActivities.EditActivity;
import com.example.playground1.activities.accountActivities.LogInActivity;
import com.example.playground1.activities.accountActivities.RegisterActivity;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.PreferencesUtils;

public class tab2account extends Fragment implements View.OnClickListener {

    private static String CURRENT_USER = "Current user: ";
    private static String NOT_LOGGED_IN = "You have not logged in";

    View rootView;
    TextView currentName;
    Button loginButton;
    Button registerButton;
    Button editButton;
    Button logoutButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab2account, null);
        currentName = rootView.findViewById(R.id.currentAccountName);
        loginButton = rootView.findViewById(R.id.accountLogInButton);
        loginButton.setOnClickListener(this);
        registerButton = rootView.findViewById(R.id.accountRegisterButton);
        registerButton.setOnClickListener(this);
        editButton = rootView.findViewById(R.id.accountEditButton);
        editButton.setOnClickListener(this);
        logoutButton = rootView.findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(this);
        return rootView;
    }

    public void onStart() {
        super.onStart();
        String id = PreferencesUtils.loadId(this.getActivity());
        if (id.isEmpty()) {
            setLoggedOut();
        } else {
            setLoggedIn(DBUtils.getUser(this.getActivity(), Integer.parseInt(id)).getName());
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.accountLogInButton:
                intent = new Intent(this.getActivity(), LogInActivity.class);
                startActivity(intent);
                break;
            case R.id.accountRegisterButton:
                intent = new Intent(this.getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.accountEditButton:
                intent = new Intent(this.getActivity(), EditActivity.class);
                startActivity(intent);
                break;
            case R.id.logOutButton:
                PreferencesUtils.initializeEmpty(this.getActivity());
                setLoggedOut();
                reloadActivity();
                break;
            default:
                break;
        }
    }

    private void setLoggedOut() {
        currentName.setText(NOT_LOGGED_IN);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
    }

    private void setLoggedIn(String name) {
        currentName.setText(CURRENT_USER + name);
        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    private void reloadActivity() {
        this.getActivity().recreate();
    }
}