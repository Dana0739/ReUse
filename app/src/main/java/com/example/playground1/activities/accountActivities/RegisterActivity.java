package com.example.playground1.activities.accountActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.playground1.R;
import com.example.playground1.model.UserModel;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.PreferencesUtils;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText address;
    EditText phone;
    EditText email;
    EditText password;
    EditText password2;
    Button registerButton;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        name = findViewById(R.id.registerName);
        address = findViewById(R.id.registerAddress);
        phone = findViewById(R.id.registerPhone);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        password2 = findViewById(R.id.registerPassword2);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.registerBackButton);
        registerButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerButton:
                if (checkFields()) {
                    if (checkPasswords()) {
                        if (!checkNameExists()) {
                            UserModel user = new UserModel()
                                    .setName(name.getText().toString())
                                    .setAddress(address.getText().toString())
                                    .setPhone(phone.getText().toString())
                                    .setEmail(email.getText().toString())
                                    .setPassword(password.getText().toString());
                            if (DBUtils.addUser(this, user)) {
                                int id = DBUtils.logIn(this, user.getName(), user.getPassword());
                                if (id > 0) {
                                    PreferencesUtils.saveId(this, String.valueOf(id));
                                    this.finish();
                                } else {
                                    Snackbar.make(view, "Something went wrong, please try log in!",
                                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            } else {
                                Snackbar.make(view, "Something went wrong, please try again!",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        } else {
                            Snackbar.make(view, "This account name already exists!",
                                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    } else {
                        Snackbar.make(view, "Passwords do not match!",
                                Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(view, "All fields are necessary!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.registerBackButton:
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
        return hasNotEmptyText(name) && hasNotEmptyText(address) && hasNotEmptyText(phone)
                && hasNotEmptyText(email) && hasNotEmptyText(password) && hasNotEmptyText(password2);
    }

    private boolean checkPasswords() {
        return password.getText().toString().equals(password2.getText().toString());
    }

    private boolean checkNameExists() {
        return DBUtils.checkUserNameExists(this, name.getText().toString());
    }
}