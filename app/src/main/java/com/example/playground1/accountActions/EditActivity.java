package com.example.playground1.accountActions;

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

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name;
    EditText address;
    EditText phone;
    EditText email;
    EditText password;
    EditText password2;
    Button editButton;
    Button backButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_activity);
        name = findViewById(R.id.editName);
        address = findViewById(R.id.editAddress);
        phone = findViewById(R.id.editPhone);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
        password2 = findViewById(R.id.editPassword2);
        editButton = findViewById(R.id.editButton);
        backButton = findViewById(R.id.editBackButton);
        deleteButton = findViewById(R.id.deleteUserButton);
        editButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        initialize();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editButton:
                if (checkFields()) {
                    if (checkPasswords()) {
                        if (checkName()) {
                            UserModel user = new UserModel()
                                    .setId(Integer.parseInt(PreferencesUtils.loadId(this)))
                                    .setName(name.getText().toString())
                                    .setAddress(address.getText().toString())
                                    .setPhone(phone.getText().toString())
                                    .setEmail(email.getText().toString())
                                    .setPassword(password.getText().toString());
                            if (DBUtils.editUser(this, user)) {
                                Snackbar.make(view, "Your user data was successfully updated!",
                                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                this.finish();
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
            case R.id.deleteUserButton:
                if (DBUtils.deleteUser(this, Integer.parseInt(PreferencesUtils.loadId(this)))) {
                    PreferencesUtils.initializeEmpty(this);
                    Snackbar.make(view, "Your account was successfully deleted!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    this.finish();
                } else {
                    Snackbar.make(view, "Something went wrong, please try again!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.editBackButton:
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

    private boolean checkName() {
        return DBUtils.checkUserNameExists(this, name.getText().toString());
    }

    private void initialize() {
        UserModel userModel = DBUtils.getUser(this, Integer.parseInt(PreferencesUtils.loadId(this)));
        name.setText(userModel.getName());
        address.setText(userModel.getAddress());
        phone.setText(userModel.getPhone());
        email.setText(userModel.getEmail());
        password.setText(userModel.getPassword());
        password2.setText(userModel.getPassword());
    }
}