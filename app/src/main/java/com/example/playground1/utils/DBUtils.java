package com.example.playground1.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.playground1.DBHelper;
import com.example.playground1.model.ItemModel;
import com.example.playground1.model.UserModel;

import java.util.ArrayList;

public class DBUtils {

    public static boolean addItem(Context context, ItemModel itemModel) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("name", itemModel.getName());
        cv.put("description", itemModel.getDescription());
        cv.put("ownerId", itemModel.getOwner().getId());
        cv.put("pictureUri", itemModel.getPictureUri());
        long res = db.insert("item", null, cv);
        db.close();
        dbHelper.close();
        return res > 0;
    }

    public static boolean addUser(Context context, UserModel user) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("name", user.getName());
        cv.put("address", user.getAddress());
        cv.put("phone", user.getPhone());
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        long res = db.insert("user", null, cv);
        db.close();
        dbHelper.close();
        return res > 0;
    }

    public static boolean setTakenByIdToItem(Context context, int itemId, int userId) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("takenById", String.valueOf(userId));
        int updCount = db.update("item", cv, "id = ?",
                new String[] {String.valueOf(itemId)});
        db.close();
        dbHelper.close();
        return updCount > 0;
    }

    public static boolean discardTakenByIdToItem(Context context, int itemId) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("takenById", "");
        int updCount = db.update("item", cv, "id = ?",
                new String[] {String.valueOf(itemId)});
        db.close();
        dbHelper.close();
        return updCount > 0;
    }

    public static boolean editItem(Context context, ItemModel itemModel) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("name", itemModel.getName());
        cv.put("description", itemModel.getDescription());
        cv.put("pictureUri", itemModel.getPictureUri());
        int updCount = db.update("item", cv, "id = ?",
                new String[] {String.valueOf(itemModel.getId())});
        db.close();
        dbHelper.close();
        return updCount > 0;
    }

    public static boolean editUser(Context context, UserModel user) {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("name", user.getName());
        cv.put("address", user.getAddress());
        cv.put("phone", user.getPhone());
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        int updCount = db.update("user", cv, "id = ?",
                new String[] {String.valueOf(user.getId())});
        db.close();
        dbHelper.close();
        return updCount > 0;
    }

    public static boolean deleteItem(Context context, int id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delCount = db.delete("item", "id = " + id, null);
        db.close();
        dbHelper.close();
        return delCount > 0;
    }

    public static boolean deleteUser(Context context, int id) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delCount = db.delete("user", "id = " + id, null);
        db.close();
        dbHelper.close();
        return delCount > 0;
    }

    public static int logIn(Context context, String name, String password) {
        String[] columns = new String[] {"id"};
        String selection = "name = ? and password = ?";
        String[] selectionArgs = new String[] {name, password};
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("user", columns, selection, selectionArgs, null, null, null);
        int id = -1;
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            id = c.getInt(idColIndex);
        }
        c.close();
        db.close();
        dbHelper.close();
        return id;
    }

    public static boolean checkUserNameExists(Context context, String name) {
        String[] columns = new String[] {"name"};
        String selection = "name = ?";
        String[] selectionArgs = new String[] {name};
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("user", columns, selection, selectionArgs, null, null, null);
        boolean result = c.moveToFirst();
        c.close();
        db.close();
        dbHelper.close();
        return result;
    }

    public static ArrayList<ItemModel> getOtherFreeItems(Context context, int userId) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "ownerId != ? and (takenById like '' or takenById = 0 or takenById is null)";
        String[] selectionArgs = new String[] {String.valueOf(userId)};
        Cursor c = db.query("item", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            do {
                ItemModel item = new ItemModel().setName(c.getString(nameColIndex))
                        .setId(c.getInt(idColIndex))
                        .setDescription(c.getString(descriptionColIndex))
                        .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                        .setPictureUri(c.getString(pictureUriColIndex));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();
        return items;
    }

    public static ArrayList<ItemModel> getCurrentTakenItems(Context context, int userId) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "takenById = ?";
        String[] selectionArgs = new String[] {String.valueOf(userId)};
        Cursor c = db.query("item", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            do {
                ItemModel item = new ItemModel().setName(c.getString(nameColIndex))
                        .setId(c.getInt(idColIndex))
                        .setDescription(c.getString(descriptionColIndex))
                        .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                        .setPictureUri(c.getString(pictureUriColIndex));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();
        return items;
    }

    public static ArrayList<ItemModel> getCurrentAccountItems(Context context, int userId) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "ownerId = ?";
        String[] selectionArgs = new String[] {String.valueOf(userId)};
        Cursor c = db.query("item", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            int takenByIdColIndex = c.getColumnIndex("takenById");
            do {
                ItemModel item = new ItemModel().setName(c.getString(nameColIndex))
                        .setId(c.getInt(idColIndex))
                        .setDescription(c.getString(descriptionColIndex))
                        .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                        .setPictureUri(c.getString(pictureUriColIndex))
                        .setTakenBy(getUser(context, c.getInt(takenByIdColIndex)));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();
        return items;
    }

    public static ArrayList<ItemModel> getAllFreeItems(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "takenById like '' or takenById = 0 or takenById is null";
        Cursor c = db.query("item", null, selection, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            do {
                ItemModel item = new ItemModel().setName(c.getString(nameColIndex))
                        .setId(c.getInt(idColIndex))
                        .setDescription(c.getString(descriptionColIndex))
                        .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                        .setPictureUri(c.getString(pictureUriColIndex));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();
        return items;
    }

    public static ArrayList<ItemModel> getAllItems(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("item", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            int takenByIdColIndex = c.getColumnIndex("takenById");
            do {
                ItemModel item = new ItemModel().setName(c.getString(nameColIndex))
                        .setId(c.getInt(idColIndex))
                        .setDescription(c.getString(descriptionColIndex))
                        .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                        .setPictureUri(c.getString(pictureUriColIndex))
                        .setTakenBy(getUser(context, c.getInt(takenByIdColIndex)));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        dbHelper.close();
        return items;
    }

    public static UserModel getUser(Context context, int id) {
        DBHelper dbHelper = new DBHelper(context);
        UserModel userModel = new UserModel();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[] {String.valueOf(id)};
        Cursor c = db.query("user", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int addressColIndex = c.getColumnIndex("address");
            int phoneColIndex = c.getColumnIndex("phone");
            int emailColIndex = c.getColumnIndex("email");
            int passwordColIndex = c.getColumnIndex("password");
            userModel.setId(c.getInt(idColIndex))
                    .setName(c.getString(nameColIndex))
                    .setAddress(c.getString(addressColIndex))
                    .setPhone(c.getString(phoneColIndex))
                    .setEmail(c.getString(emailColIndex))
                    .setPassword(c.getString(passwordColIndex));
        }
        c.close();
        db.close();
        dbHelper.close();
        return userModel;
    }

    public static ItemModel getItem(Context context, int id) {
        DBHelper dbHelper = new DBHelper(context);
        ItemModel itemModel = new ItemModel();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[] {String.valueOf(id)};
        Cursor c = db.query("item", null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int descriptionColIndex = c.getColumnIndex("description");
            int ownerIdColIndex = c.getColumnIndex("ownerId");
            int pictureUriColIndex = c.getColumnIndex("pictureUri");
            int takenByIdColIndex = c.getColumnIndex("takenById");
            itemModel.setId(c.getInt(idColIndex))
                    .setName(c.getString(nameColIndex))
                    .setDescription(c.getString(descriptionColIndex))
                    .setOwner(getUser(context, c.getInt(ownerIdColIndex)))
                    .setPictureUri(c.getString(pictureUriColIndex))
                    .setTakenBy(getUser(context, c.getInt(takenByIdColIndex)));
        }
        c.close();
        db.close();
        dbHelper.close();
        return itemModel;
    }
}
