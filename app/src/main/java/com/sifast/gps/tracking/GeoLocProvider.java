package com.sifast.gps.tracking;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by ghassen.ati on 18/03/2016.
 */
public class GeoLocProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.sifast.gps.tracking.GeoLocProvider";
    static final String URL = "content://"+PROVIDER_NAME+"/geoloc";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String DATEANDTIME = "dateandtime";
    static final String DEVICE_ID = "deviceid";
    static final String LONGITUDE = "longitude";
    static final String LATITUDE = "latitude";
    static final String SPEED = "speed";
    static final String STATUS = "status";

    private static HashMap<String,String> GEOLOC_PROJECTION_MAP;

    static final int GEOLOC = 1;
    static final int GEOLOC_ID = 2;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "geoloc", GEOLOC);
        uriMatcher.addURI(PROVIDER_NAME, "geoloc/#", GEOLOC_ID);
    }

    //Base de données
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "GeoLoc";
    static final String TABLE_NAME = "geolocstore";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " dateandtime TEXT NOT NULL, " +
                    " status TEXT, " +
                    " deviceid INTEGER NOT NULL, " +
                    " longitude INTEGER NOT NULL, " +
                    " latitude INTEGER NOT NULL," +
                    " speed INTEGER NOT NULL);";

    //Classe Helper
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db==null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case GEOLOC:
                queryBuilder.setProjectionMap(GEOLOC_PROJECTION_MAP);
                break;
            case GEOLOC_ID:
                queryBuilder.appendWhere(_ID+"="+uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("URI Inconnue" + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            sortOrder = DEVICE_ID;
        }
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case GEOLOC:
                return "vnd.android.cursor.dir/vnd.gps.tracking.geoloc";
            case GEOLOC_ID:
                return "vnd.android.cursor.item/vnd.gps.tracking.geoloc";
            default:
                throw new IllegalArgumentException("URI Inconnue" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //Ajout d'un enregistrement
        long rowID = db.insert(TABLE_NAME,"",values);

        //Si l'ajout est effectué avec succès
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("Echec lors de l'ajout d'un enregistrement"+uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case GEOLOC:
                count = db.delete(TABLE_NAME,null,selectionArgs);
                break;
            case GEOLOC_ID:
                //String id = uri.getPathSegments().get(1);
                count  = db.delete(TABLE_NAME,_ID + " = " + selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI Inconnue "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case GEOLOC:
                count = db.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case GEOLOC_ID:
                count = db.update(TABLE_NAME,values, _ID + " = " + selection , selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI Inconnue"+ uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
