package com.example.admin.beacon;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    com.michaldrabik.tapbarmenulib.TapBarMenu t;
    tr.xip.markview.MarkView circle;
    BluetoothAdapter mBluetoothAdapter;
    TextView bttv;
    SwitchButton btswitch;
    Context context;
    Data data;
    ImageButton photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        data = Data.getinstance();
        startService(new Intent(getBaseContext(), BeaconDetectService.class));
        checkingPermissions();
        setContentView(R.layout.activity_main);
//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//        window.setStatusBarColor(Color.GRAY);

        //buttons
        bttv = (TextView) findViewById(R.id.bttv);
        Typeface tf = Typeface.createFromAsset(MyApp.getAppContext().getAssets(),
                "fonts/opensans_regular.ttf");
        bttv.setTypeface(tf);
        photo = (ImageButton) findViewById(R.id.photo);
        if (data.appPhoto != null) {
            Bitmap bitmap = Data.getRoundedShape(BitmapFactory.decodeFile(data.appPhoto));

            if (bitmap != null) {
                photo.setImageBitmap(bitmap);

            }
        }
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, Data.RESULT_LOAD_IMAGE);
            }
        });

        //switch
        btswitch = (SwitchButton) findViewById(R.id.btswitch);


        btswitch.setThumbDrawableRes(R.drawable.blue_on);
//        final float scale = MyApp.getAppContext().getResources().getDisplayMetrics().density;
//        int pixels = (int) (50 * scale + 0.5f);
//        btswitch.setHeight(pixels);
//        btswitch.setWidth(pixels);
        SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);

        boolean isbind = sharedpreferences.getBoolean(Data.SHOULDBIND, false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled() && isbind) {
            btswitch.setChecked(true);
        } else {
            btswitch.setChecked(false);
        }
        btswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bttv.setText("Connected");
                    btswitch.setThumbDrawableRes(R.drawable.blue_off);
                    mBluetoothAdapter.enable();
                    Intent intent = new Intent("blabla");
                    intent.putExtra(Data.ACTION, Data.BIND);
                    //context.sendBroadcast(intent);
                    BeaconDetectService.bindBeacon();
                    SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Data.SHOULDBIND, true);
                    editor.commit();

                } else {

                    bttv.setText("Disconnected");
                    btswitch.setThumbDrawableRes(R.drawable.blue_on);
                    Intent intent = new Intent("blabla");
                    intent.putExtra(Data.ACTION, Data.UNBIND);
                    BeaconDetectService.unBindBeacon();
                    SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(Data.SHOULDBIND, false);
                    editor.commit();
                    mBluetoothAdapter.disable();
                }
            }
        });
        if (btswitch.isChecked()) {
            bttv.setText("Connected");
            btswitch.setThumbDrawableRes(R.drawable.blue_off);
        } else {
            bttv.setText("Disconnected");
            btswitch.setThumbDrawableRes(R.drawable.blue_on);
        }
        t = (com.michaldrabik.tapbarmenulib.TapBarMenu) findViewById(R.id.tapBarMenu);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.toggle();


            }
        });
        ImageView item1 = (ImageView) findViewById(R.id.item1);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Settings.class);
                startActivity(intent);
            }
        });
        ImageView item2 = (ImageView) findViewById(R.id.item2);
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlertActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void checkingPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Data.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            ExifInterface ei = null;
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

//            try {
//                ei = new ExifInterface(picturePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            Log.d("orientation1 ", "" + orientation);
//            switch (orientation) {
//
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    Log.d("orientation2 ", "" + orientation);
//                    try {
//                      bitmap= decodeSampledBitmap(getBaseContext(),getImageUri(getBaseContext(),bitmap));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    bitmap = rotateImage(bitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    Log.d("orientation3 ", "" + orientation);
//                    bitmap = rotateImage(BitmapFactory.decodeFile(picturePath), 180);
//                    break;
//
//            }

            SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Data.APPPHOTO, picturePath);
            editor.commit();

            cursor.close();
            bitmap = Data.getRoundedShape(bitmap);

            if (bitmap != null) {
                photo.setImageBitmap(bitmap);


            }

        }


    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    private static final int MAX_HEIGHT = 1024;
    private static final int MAX_WIDTH = 1024;

    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);


        return img;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
