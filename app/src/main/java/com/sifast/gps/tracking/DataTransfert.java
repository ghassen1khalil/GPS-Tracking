package com.sifast.gps.tracking;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghassen.ati on 25/03/2016.
 */
public class DataTransfert {
    private static final String TAG = "DataTransfert";
    private Context context;
    private PositionManager positionManager;

    public DataTransfert(Context context, PositionManager positionManager) {
        this.context = context;
        this.positionManager = positionManager;
    }

    public void sendGeoLocToServer(final String serverUrl) {
        positionManager = new PositionManager(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri geoloc = Uri.parse(ConstantUtil.URL);
                ContentResolver contentResolver = context.getContentResolver();
                //Cursor cursor = contentResolver.query(geoloc, null, "status LIKE '0' ", null, "dateandtime");
                Cursor cursor = contentResolver.query(geoloc, null, null, null, "dateandtime");

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://" + serverUrl + "/GPS-Tracking-WebApp/rest/positionNotification/getGeoLocFromDevice");

                if (cursor.moveToFirst()) {
                    do {
                        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair("HORODATEUR", cursor.getString(cursor.getColumnIndex(GeoLocProvider.DATEANDTIME))));
                        pairs.add(new BasicNameValuePair("DEVICE_ID", cursor.getString(cursor.getColumnIndex(GeoLocProvider.DEVICE_ID))));
                        pairs.add(new BasicNameValuePair("LATITUDE", cursor.getString(cursor.getColumnIndex(GeoLocProvider.LATITUDE))));
                        pairs.add(new BasicNameValuePair("LONGITUDE", cursor.getString(cursor.getColumnIndex(GeoLocProvider.LONGITUDE))));
                        pairs.add(new BasicNameValuePair("SPEED", cursor.getString(cursor.getColumnIndex(GeoLocProvider.SPEED))));
                        try {
                            post.setEntity(new UrlEncodedFormEntity(pairs));
                            HttpResponse response = client.execute(post);
                            StatusLine statusLine = response.getStatusLine();
                            Log.i(TAG, "SERVER RESPONSE " + String.valueOf(statusLine.getStatusCode()));
                            if (statusLine.getStatusCode() == ConstantUtil.SERVER_RESPONSE) {

                                //modifier la STATUS à 1
                                positionManager.changeStatus(Integer.parseInt(cursor.getString(cursor.getColumnIndex(GeoLocProvider._ID))));
                                //suppression de l'élément envoyé
                                positionManager.deleteRecord(Integer.parseInt(cursor.getString(cursor.getColumnIndex(GeoLocProvider._ID))));

                            }

                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, e.toString());
                        } catch (ClientProtocolException e) {
                            Log.e(TAG, e.toString());
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }


                    } while (cursor.moveToNext());
                }

            }
        }).start();
    }
}
