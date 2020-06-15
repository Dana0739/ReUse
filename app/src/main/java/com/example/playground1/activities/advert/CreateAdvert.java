package com.example.playground1.activities.advert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground1.R;
import com.example.playground1.model.ItemModel;
import com.example.playground1.model.UserModel;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.GenericFileProvider;
import com.example.playground1.utils.PreferencesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CreateAdvert extends AppCompatActivity implements OnClickListener {

    File directory;

    TextView advertName;
    TextView advertDescription;
    ImageView imageView;
    Uri imageUri;
    Button takePhoto;
    Button loadPhoto;
    Button createAdvert;
    Button cancelAdvert;

    private int REQUEST_TAKE_PHOTO = 1;
    private int REQUEST_CHOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDirectory();
        setContentView(R.layout.activity_create_advert);
        advertName = findViewById(R.id.createAdvertName);
        advertDescription = findViewById(R.id.createAdvertDescription);
        imageView = findViewById(R.id.createAdvertImageView);
        takePhoto = findViewById(R.id.takePictureAdvertButton);
        takePhoto.setOnClickListener(this);
        loadPhoto = findViewById(R.id.addPictureAdvertButton);
        loadPhoto.setOnClickListener(this);
        createAdvert = findViewById(R.id.createAdvertButton);
        createAdvert.setOnClickListener(this);
        cancelAdvert = findViewById(R.id.cancelAdvertButton);
        cancelAdvert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPictureAdvertButton:
                dispatchChoosePictureIntent();
                break;
            case R.id.takePictureAdvertButton:
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.createAdvertButton:
                ItemModel itemModel = prepareModelForDB();
                if (DBUtils.addItem(this, itemModel)) {
                    Toast.makeText(this, "Your advert was successfully saved!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Something went wrong! Your advert was not saved!", Toast.LENGTH_LONG).show();
                }
                this.finish();
                break;
            case R.id.cancelAdvertButton:
                this.finish();
                break;
            default:
                break;
        }
    }

    private void dispatchChoosePictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (photoPickerIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, REQUEST_CHOSE_PHOTO);
        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = generateFileUri();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private Uri generateFileUri() throws IOException {
        File file = File.createTempFile("ReUse_" + System.currentTimeMillis(), ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        return GenericFileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".utils.GenericFileProvider", file);
    }

    private void createDirectory() {
        directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ReUse");
        if (!directory.exists()) directory.mkdirs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                switch (requestCode) {
                    case 2:
                        imageUri = data.getData();
                    case 1:
                        setImageView();
                        break;
                    default:
                        Toast.makeText(this, "Unknown request", Toast.LENGTH_LONG).show();
                        break;
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void setImageView() throws FileNotFoundException {
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(selectedImage);
    }

    ItemModel prepareModelForDB() {
        EditText name = findViewById(R.id.createAdvertName);
        EditText description = findViewById(R.id.createAdvertDescription);
        int ownerId = Integer.parseInt(PreferencesUtils.loadId(this));
        UserModel owner = new UserModel().setId(ownerId);
        return new ItemModel().setName(name.getText().toString())
                .setDescription(description.getText().toString())
                .setOwner(owner)
                .setPictureUri(imageUri.toString());
    }
}