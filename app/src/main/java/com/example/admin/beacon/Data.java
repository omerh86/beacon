package com.example.admin.beacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ToneGenerator;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Admin on 1/28/2016.
 */
public class Data {
    public static final String ACTION = "action";
    public static final String ARRAY = "array";
    public static final String BIND = "bind";
    public static final String UNBIND = "unbind";
    public static final String PREFFOLDER = "prefFolder";
    public static final String SHOULDBIND = "shouldbind";
    public static final String ALERT = "alert";
    public static final String SMS = "sms";
    public static final String RANGE = "range";
    public static final String ALERT_SOUND = "alertsound";
    public static final String GPS_DATA = "gpsdata";
    public static final String APPPHOTO = "appphoto";
    public static final String SMSMESSAGE = "SMS message:  \n";
    public static final String ALERTMESSAGE = "Alert message:  \n";
    public static final int CONTACT_PICKER_RESULT = 1001;
    public static final int RESULT_LOAD_IMAGE = 1111;

    private static Data data;
    public ArrayList<Person> contactArray;
    public Bitmap[] iconsArray;
    public int numberOfArrayList;
    public double lat;
    public double lon;
    public int alertSound = ToneGenerator.TONE_CDMA_ABBR_INTERCEPT;

    public int rangeOfAlert = 10;
    public String alertMassege = "";
    public String smsMassege = "";
    public String appPhoto;

    private Data() {
        data = this;
        iconsArray = new Bitmap[5];
        contactArray = loadArrayFromStorage();
        this.loadStorageValues();


        if (contactArray == null) {
            contactArray = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                contactArray.add(new Person());
            }
            Log.d("array", "is null");
        } else {
            generateIcons();
        }


    }


    public static Data getinstance() {
        if (data == null) {
            data = new Data();
            return data;
        }
        return data;

    }

    public void saveArrayToStorage(ArrayList array) {
        try {

            FileOutputStream fileOut =
                    MyApp.getAppContext().openFileOutput(Data.ARRAY, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(array);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public ArrayList loadArrayFromStorage() {
        ArrayList array;
        try {
            FileInputStream filein =
                    MyApp.getAppContext().openFileInput(Data.ARRAY);
            ObjectInputStream in = new ObjectInputStream(filein);
            array = (ArrayList<Person>) in.readObject();
            in.close();
            filein.close();
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Log.d("array", "" + array.size());
        return array;

    }

    public void loadStorageValues() {
        SharedPreferences sharedpreferences = MyApp.getAppContext().getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
        this.alertSound = sharedpreferences.getInt(Data.ALERT_SOUND,ToneGenerator.TONE_CDMA_ABBR_INTERCEPT );
        this.alertMassege = sharedpreferences.getString(Data.ALERT, "");
        this.smsMassege = sharedpreferences.getString(Data.SMS, "");
        this.rangeOfAlert = sharedpreferences.getInt(Data.RANGE, 10);
        this.appPhoto = sharedpreferences.getString(Data.APPPHOTO, null);

    }

    public void generateIcons() {
        for (int i = 0; i < contactArray.size(); i++) {
            iconsArray[i] = BitmapFactory.decodeStream((Settings.openPhoto(Integer.parseInt(contactArray.get(i).getId()))));
        }
    }

//    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
//
//        final float scale = MyApp.getAppContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (160 * scale + 0.5f);
//        int targetWidth = pixels;
//        int targetHeight = pixels;
//        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
//                targetHeight, Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(targetBitmap);
//        Path path = new Path();
//        path.addCircle(((float) targetWidth - 5) / 2,
//                ((float) targetHeight - 5) / 2,
//                (Math.min(((float) targetWidth),
//                        ((float) targetHeight)) / 2),
//                Path.Direction.CCW);
//
//        canvas.clipPath(path);
//        Bitmap sourceBitmap = scaleBitmapImage;
//        canvas.drawBitmap(sourceBitmap,
//                new Rect(0, 0, sourceBitmap.getWidth(),
//                        sourceBitmap.getHeight()),
//                new Rect(0, 0, targetWidth, targetHeight), null);
//        return targetBitmap;
//    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        final float scale = MyApp.getAppContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (140 * scale + 0.5f);
        int targetWidth = pixels;
        int targetHeight = pixels;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth) / 2,
                ((float) targetHeight) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

}