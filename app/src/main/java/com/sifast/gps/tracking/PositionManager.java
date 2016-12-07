package com.sifast.gps.tracking;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ghassen.ati on 25/03/2016.
 */
public class PositionManager  {
    private static final String TAG = "PositionManager";
    private Context context;

    public PositionManager(Context context){
        this.context = context;
    }

    public void addGeoLocRecord(int uniqueId, Location location) {
        Log.i(TAG, "AJOUT D'UNE POSITION AU CONTENT PROVIDER");

        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();


        values.put(GeoLocProvider.DATEANDTIME, dateFormat.format(date).toString());
        values.put(GeoLocProvider.DEVICE_ID, uniqueId);
        values.put(GeoLocProvider.LONGITUDE, location.getLongitude());
        values.put(GeoLocProvider.LATITUDE, location.getLatitude());
        values.put(GeoLocProvider.SPEED, location.getSpeed());
        values.put(GeoLocProvider.STATUS, "0");
        Uri uri = context.getContentResolver().insert(GeoLocProvider.CONTENT_URI, values);

    }

    public void changeStatus(int index) {
        int rowsUpdate = 0;
        //index = cursor.getPosition() + 1;
        ContentValues updates = new ContentValues();
        updates.put(GeoLocProvider.STATUS, "1");
        Uri currentGeoLoc = Uri.parse(ConstantUtil.URL + "/" + String.valueOf(index));
        //context.getContentResolver().update(currentGeoLoc, updates, "status LIKE '0'", null);
        rowsUpdate = context.getContentResolver().update(currentGeoLoc, updates, String.valueOf(index), null);
        Log.i("ROWS UPDATED", String.valueOf(rowsUpdate));
    }

    public void deleteRecord(int index){
        int rowDeleted = 0;

        Uri currentGeoLoc = Uri.parse(ConstantUtil.URL + "/" + String.valueOf(index));
        rowDeleted = context.getContentResolver().delete(currentGeoLoc,String.valueOf(index),null);
        Log.i(TAG,"Deleted rows: "+ String.valueOf(rowDeleted));
    }

    public void showAllRecords() {
        int count = 0;
        ContentResolver contentResolver = context.getContentResolver();
        Uri geoloc = Uri.parse(ConstantUtil.URL);

        Cursor cursor = contentResolver.query(geoloc, null, null, null, "dateandtime");


        if (cursor.moveToFirst()) {
            Log.i(TAG, String.valueOf(cursor.getPosition()));
            do {
                count++;
                Log.i(TAG, "Horodateur: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.DATEANDTIME)) +
                        ", DEVICE ID: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.DEVICE_ID)) +
                        ", LATITUDE: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.LATITUDE)) +
                        ", LONGITUDE: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.LONGITUDE)) +
                        ", STATUS: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.STATUS)) +
                        ", _ID: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider._ID)) +
                        ", SPEED: " + cursor.getString(cursor.getColumnIndex(GeoLocProvider.SPEED))
                );

            } while (cursor.moveToNext());
        }
        Log.i(TAG, "Nombre de records enregistrés: " + String.valueOf(count));

    }

    public void deleteAllRecords() {
        int deleted = context.getContentResolver().delete(GeoLocProvider.CONTENT_URI, null, null);
        Log.i(TAG, "DELETED: " + String.valueOf(deleted));
    }
}
