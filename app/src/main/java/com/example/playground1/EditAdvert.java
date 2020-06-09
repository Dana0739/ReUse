package com.example.playground1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground1.model.ItemModel;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.GenericFileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditAdvert extends AppCompatActivity implements OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    File directory;

    TextView advertName;
    TextView advertDescription;
    ImageView imageView;
    Uri imageUri;
    Button takePhoto;
    Button loadPhoto;
    Button createAdvert;
    Button cancelAdvert;
    int advertId;
    ItemModel itemModel;

    private int REQUEST_TAKE_PHOTO = 1;
    private int REQUEST_CHOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDirectory();
        advertId = getIntent().getIntExtra("EXTRA_ADVERT_ID", -1);
        itemModel = DBUtils.getItem(this, advertId);
        setContentView(R.layout.activity_edit_advert);
        advertName = findViewById(R.id.editAdvertName);
        advertDescription = findViewById(R.id.editAdvertDescription);
        imageView = findViewById(R.id.editAdvertImageView);
        takePhoto = findViewById(R.id.editTakePictureAdvertButton);
        takePhoto.setOnClickListener(this);
        loadPhoto = findViewById(R.id.editAddPictureAdvertButton);
        loadPhoto.setOnClickListener(this);
        createAdvert = findViewById(R.id.editAdvertButton);
        createAdvert.setOnClickListener(this);
        cancelAdvert = findViewById(R.id.cancelEditAdvertButton);
        cancelAdvert.setOnClickListener(this);
        getAccess();
    }

    private void getAccess()  {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            setPreviousValues();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setPreviousValues();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    private void setPreviousValues() {
        advertName.setText(itemModel.getName());
        advertDescription.setText(itemModel.getDescription());
        imageView.setImageBitmap(getPictureFromUri(itemModel.getPictureUri()));
        imageUri = Uri.parse(itemModel.getPictureUri());
    }

    private Bitmap getPictureFromUri(String uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editAddPictureAdvertButton:
                dispatchChoosePictureIntent();
                break;
            case R.id.editTakePictureAdvertButton:
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.editAdvertButton:
                itemModel = prepareModelForDB();
                if (DBUtils.editItem(this, itemModel)) {
                    Toast.makeText(this, "Your advert was successfully saved!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Something went wrong! Changes in your advert was not saved!", Toast.LENGTH_LONG).show();
                }
                this.finish();
                break;
            case R.id.cancelEditAdvertButton:
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
        return itemModel.setName(advertName.getText().toString())
                .setDescription(advertDescription.getText().toString())
                .setPictureUri(imageUri.toString());
    }
}