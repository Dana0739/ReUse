package com.example.playground1.activities.accountActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playground1.R;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.PreferencesUtils;
import com.google.android.material.snackbar.Snackbar;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText password;
    Button loginButton;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
        name = findViewById(R.id.logInName);
        password = findViewById(R.id.logInPassword);
        loginButton = findViewById(R.id.logInButton);
        backButton = findViewById(R.id.logInBackButton);
        loginButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logInButton:
                if (checkFields()) {
                    int id = DBUtils.logIn(this, name.getText().toString(), password.getText().toString());
                    if (id > 0) {
                        PreferencesUtils.saveId(this, String.valueOf(id));
                        this.finish();
                    } else {
                        Snackbar.make(view, "Incorrect login or password!",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "Please provide login and password!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.logInBackButton:
                this.finish();
                break;
            default:
                break;
        }
    }

    private boolean hasNotEmptyText(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private boolean checkFields() {
        return hasNotEmptyText(name) && hasNotEmptyText(password);
    }
}
