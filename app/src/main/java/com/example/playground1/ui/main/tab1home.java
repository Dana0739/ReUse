package com.example.playground1.ui.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.playground1.activities.advert.EditAdvert;
import com.example.playground1.model.ItemModel;
import com.example.playground1.R;
import com.example.playground1.utils.DBUtils;
import com.example.playground1.utils.PreferencesUtils;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class tab1home extends Fragment implements OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab1home, null);
    }

    public void onStart() {
        super.onStart();
        getAccess();
    }

    private void getAccess()  {
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this.getActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            addAdvertsOnMainPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addAdvertsOnMainPage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    private boolean isLoggedIn() {
        return !PreferencesUtils.loadId(this.getActivity()).isEmpty();
    }

    private void cleanLayout(LinearLayout linLayout) {
        if (linLayout.getChildCount() > 0) {
            linLayout.removeAllViews();
        }
    }

    public void addAdvertsOnMainPage() {
        LinearLayout linLayout = this.getActivity().findViewById(R.id.advertsLayout);
        LayoutInflater inflater = getLayoutInflater();
        cleanLayout(linLayout);
        if (isLoggedIn()) {
            int currentAccountId = Integer.parseInt(PreferencesUtils.loadId(this.getActivity()));
            ArrayList<ItemModel> currentTakenItems =
                    DBUtils.getCurrentTakenItems(this.getActivity(), currentAccountId);
            ArrayList<ItemModel> currentAccountItems =
                    DBUtils.getCurrentAccountItems(this.getActivity(), currentAccountId);
            ArrayList<ItemModel> freeOtherItems =
                    DBUtils.getOtherFreeItems(this.getActivity(), currentAccountId);
            addCurrentTakenItemsInLayout(linLayout, inflater, currentTakenItems);
            addCurrentAccountItemsInLayout(linLayout, inflater, currentAccountItems);
            addFreeItemsInLayout(linLayout, inflater, freeOtherItems, true);
        } else {
            ArrayList<ItemModel> freeItems = DBUtils.getAllFreeItems(this.getActivity());
            addFreeItemsInLayout(linLayout, inflater, freeItems, false);
        }
    }

    private Bitmap getPictureFromUri(String uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), Uri.parse(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addCurrentAccountItemsInLayout(LinearLayout linLayout, LayoutInflater inflater,
                                      ArrayList<ItemModel> adverts) {
        for (int i = 0; i < adverts.size(); i++) {
            View item = inflater.inflate(R.layout.current_account_item, linLayout, false);
            ImageView itemImage = item.findViewById(R.id.currentAccountImageView);
            itemImage.setImageBitmap(getPictureFromUri(adverts.get(i).getPictureUri()));
            TextView itemName = item.findViewById(R.id.currentAccountAdvertName);
            itemName.setText(adverts.get(i).getName());
            TextView itemDescription = item.findViewById(R.id.currentAccountAdvertDescription);
            itemDescription.setText(adverts.get(i).getDescription());
            if (adverts.get(i).getTakenBy().getName() != null) {
                TextView whoWantsToTake = item.findViewById(R.id.currentAvvountWhoWnantsAdvert);
                whoWantsToTake.setText(String.format("This advert wants to take user: %s",
                        adverts.get(i).getTakenBy().getName()));
            }
            Button editButton = item.findViewById(R.id.currentAccountAdvertEditButton);
            editButton.setContentDescription(String.valueOf(adverts.get(i).getId()));
            editButton.setOnClickListener(this);
            Button deleteButton = item.findViewById(R.id.currentAccountAdvertDeleteButton);
            deleteButton.setContentDescription(String.valueOf(adverts.get(i).getId()));
            deleteButton.setOnClickListener(this);
            linLayout.addView(item);
        }
    }

    private void addCurrentTakenItemsInLayout(LinearLayout linLayout, LayoutInflater inflater,
                                                ArrayList<ItemModel> adverts) {
        for (int i = 0; i < adverts.size(); i++) {
            View item = inflater.inflate(R.layout.taken_item, linLayout, false);
            ImageView itemImage = item.findViewById(R.id.takeAdvertImageView);
            itemImage.setImageBitmap(getPictureFromUri(adverts.get(i).getPictureUri()));
            TextView itemName = item.findViewById(R.id.takeAdvertName);
            itemName.setText(adverts.get(i).getName());
            TextView itemDescription = item.findViewById(R.id.takeAdvertDescription);
            itemDescription.setText(adverts.get(i).getDescription());
            TextView itemOwner = item.findViewById(R.id.takeAdvertOwner);
            itemOwner.setText(String.format("Owner: %s", adverts.get(i).getOwner().getName()));
            TextView itemAddress = item.findViewById(R.id.takeAdvertOwnerAddress);
            itemAddress.setText(String.format("Address: %s", adverts.get(i).getOwner().getAddress()));
            TextView ownerPhone = item.findViewById(R.id.takeAdvertOwnerPhone);
            ownerPhone.setText(String.format("Phone: %s", adverts.get(i).getOwner().getPhone()));
            TextView ownerEmail = item.findViewById(R.id.takeAdvertOwnerEmail);
            ownerEmail.setText(String.format("Email: %s", adverts.get(i).getOwner().getEmail()));
            Button discardButton = item.findViewById(R.id.takeAdvertDiscardButton);
            discardButton.setContentDescription(String.valueOf(adverts.get(i).getId()));
            discardButton.setOnClickListener(this);
            linLayout.addView(item);
        }
    }

    private void addFreeItemsInLayout(LinearLayout linLayout, LayoutInflater inflater,
                                      ArrayList<ItemModel> adverts, boolean loggedIn) {
        for (int i = 0; i < adverts.size(); i++) {
            View item = inflater.inflate(R.layout.free_item, linLayout, false);
            ImageView itemImage = item.findViewById(R.id.freeAdvertImageView);
            itemImage.setImageBitmap(getPictureFromUri(adverts.get(i).getPictureUri()));
            TextView itemName = item.findViewById(R.id.freeAdvertName);
            itemName.setText(adverts.get(i).getName());
            TextView itemDescription = item.findViewById(R.id.freeAdvertDescription);
            itemDescription.setText(adverts.get(i).getDescription());
            TextView itemOwner = item.findViewById(R.id.freeAdvertOwner);
            itemOwner.setText(String.format("Owner: %s", adverts.get(i).getOwner().getName()));
            TextView itemAddress = item.findViewById(R.id.freeAdvertAddress);
            itemAddress.setText(String.format("Address: %s", adverts.get(i).getOwner().getAddress()));
            TextView ownerPhone = item.findViewById(R.id.freeAdvertOwnerPhone);
            ownerPhone.setText(String.format("Phone: %s", adverts.get(i).getOwner().getPhone()));
            TextView ownerEmail = item.findViewById(R.id.freeAdvertOwnerEmail);
            ownerEmail.setText(String.format("Email: %s", adverts.get(i).getOwner().getEmail()));
            Button takeButton = item.findViewById(R.id.freeAdvertTakeButton);
            if (loggedIn) {
                takeButton.setContentDescription(String.valueOf(adverts.get(i).getId()));
                takeButton.setOnClickListener(this);
            } else {
                takeButton.setVisibility(View.GONE);
            }
            linLayout.addView(item);
        }
    }

    @Override
    public void onClick(View view) {
        int advertId = Integer.parseInt(view.getContentDescription().toString());
        switch (view.getId()) {
            case R.id.freeAdvertTakeButton:
                int userId = Integer.parseInt(PreferencesUtils.loadId(this.getActivity()));
                if (DBUtils.setTakenByIdToItem(this.getActivity(), advertId, userId)) {
                    reloadCurrentFragment();
                } else {
                    Snackbar.make(view, "Something went wrong! Please, try again!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.currentAccountAdvertEditButton:
                Intent intent = new Intent(this.getActivity(), EditAdvert.class);
                intent.putExtra("EXTRA_ADVERT_ID", advertId);
                startActivity(intent);
                break;
            case R.id.currentAccountAdvertDeleteButton:
                if (DBUtils.deleteItem(this.getActivity(), advertId)) {
                    reloadCurrentFragment();
                } else {
                    Snackbar.make(view, "Something went wrong! Please, try again!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.takeAdvertDiscardButton:
                if (DBUtils.discardTakenByIdToItem(this.getActivity(), advertId)) {
                    reloadCurrentFragment();
                } else {
                    Snackbar.make(view, "Something went wrong! Please, try again!",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            default:
                break;
        }
    }

    private void reloadCurrentFragment() {
        this.getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}