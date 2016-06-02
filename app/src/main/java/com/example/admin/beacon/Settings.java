package com.example.admin.beacon;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Settings extends Activity {
    public static ListView contactsList;
    Data data;
    android.widget.ArrayAdapter adapter;
    public static EditText massageInput;
    EditText alerInput;
    Button save;
    TextView massege, alertmassegetv;
    LinearLayout ll;
    RadioGroup range, sound;
    //debug
    static TextView distanceTv;
    private Handler customHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        data = Data.getinstance();


//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        long color = Long.parseLong("0B7792", 16);
//        window.setStatusBarColor(Color.GRAY);
        ll = (LinearLayout) findViewById(R.id.ll);
        changingfont(ll);
        massege = (TextView) findViewById(R.id.smsmassege);
        // massege.setText(Data.SMSMESSAGE + data.smsMassege);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString redSpannable = new SpannableString(Data.SMSMESSAGE);
        redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, Data.SMSMESSAGE.length(), 0);
        builder.append(redSpannable);
        SpannableString whiteSpannable = new SpannableString(data.smsMassege);
        whiteSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, data.smsMassege.length(), 0);
        builder.append(whiteSpannable);
        massege.setText(builder, TextView.BufferType.SPANNABLE);

        alertmassegetv = (TextView) findViewById(R.id.alertmassege);
        //alertmassegetv.setText(Data.ALERTMESSAGE + data.alertMassege);
        builder = new SpannableStringBuilder();
        redSpannable = new SpannableString(Data.ALERTMESSAGE);
        redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, Data.ALERTMESSAGE.length(), 0);
        builder.append(redSpannable);
        whiteSpannable = new SpannableString(data.alertMassege);
        whiteSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, data.alertMassege.length(), 0);
        builder.append(whiteSpannable);
        alertmassegetv.setText(builder, TextView.BufferType.SPANNABLE);

        alerInput = (EditText) findViewById(R.id.alertinput);
        alerInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    data.alertMassege = alerInput.getText().toString();
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    SpannableString redSpannable = new SpannableString(Data.ALERTMESSAGE);
                    redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, Data.ALERTMESSAGE.length(), 0);
                    builder.append(redSpannable);
                    SpannableString whiteSpannable = new SpannableString(data.alertMassege);
                    whiteSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, data.alertMassege.length(), 0);
                    builder.append(whiteSpannable);
                    alertmassegetv.setText(builder, TextView.BufferType.SPANNABLE);

                    // alertmassegetv.setText(Data.ALERTMESSAGE + alerInput.getText().toString());

                    SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Data.ALERT, alerInput.getText().toString());
                    editor.commit();

                    alerInput.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });


        massageInput = (EditText) findViewById(R.id.massegeinput);
        massageInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //massege.setText(Data.SMSMESSAGE + massageInput.getText());
                    data.smsMassege = massageInput.getText().toString();
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    SpannableString redSpannable = new SpannableString(Data.SMSMESSAGE);
                    redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, Data.SMSMESSAGE.length(), 0);
                    builder.append(redSpannable);
                    SpannableString whiteSpannable = new SpannableString(data.smsMassege);
                    whiteSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, data.smsMassege.length(), 0);
                    builder.append(whiteSpannable);
                    massege.setText(builder, TextView.BufferType.SPANNABLE);

                    SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Data.SMS, massageInput.getText().toString());
                    editor.commit();
                    massageInput.setText("");
                    //  massageInput.setVisibility(View.INVISIBLE);

                    return true;
                }
                return false;
            }
        });

//        save = (Button) findViewById(R.id.save);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                data.contactArray.get(data.numberOfArrayList).setMassege(massageInput.getText().toString());
//                Log.d("a", "" + data.contactArray.get(data.numberOfArrayList).getMassege());
//                massege.setText(s + massageInput.getText());
//                massageInput.setText("");
//                massageInput.setVisibility(View.INVISIBLE);
//            }
//        });

        contactsList = (ListView) findViewById(R.id.contactlist);
        adapter = new ArrayAdapter(this,
                R.layout.listview, R.id.contactphonenumber, data.contactArray);
        contactsList.setAdapter(adapter);

        range = (RadioGroup) findViewById(R.id.range);
        if (data.rangeOfAlert == 10) {
            RadioButton r = (RadioButton) findViewById(R.id.small);
            r.setChecked(true);
        } else if (data.rangeOfAlert == 20) {
            RadioButton r = (RadioButton) findViewById(R.id.medium);
            r.setChecked(true);
        } else if (data.rangeOfAlert == 30) {
            RadioButton r = (RadioButton) findViewById(R.id.large);
            r.setChecked(true);
        }
        range.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.small) {
//                    Toast.makeText(getApplicationContext(), "choice: 1",
//                            Toast.LENGTH_SHORT).show();
                    data.rangeOfAlert = 10;
                } else if (checkedId == R.id.medium) {
//                    Toast.makeText(getApplicationContext(), "choice: 2",
//                            Toast.LENGTH_SHORT).show();
                    data.rangeOfAlert = 20;
                } else if (checkedId == R.id.large) {
//                    Toast.makeText(getApplicationContext(), "choice: 3",
//                            Toast.LENGTH_SHORT).show();
                    data.rangeOfAlert = 30;
                }
                SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Data.RANGE, data.rangeOfAlert);
                editor.commit();
            }
        });

        sound = (RadioGroup) findViewById(R.id.sound);
        if (data.alertSound == ToneGenerator.TONE_CDMA_ABBR_INTERCEPT) {
            RadioButton r = (RadioButton) findViewById(R.id.soundA);
            r.setChecked(true);
        } else if (data.alertSound == ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE) {
            RadioButton r = (RadioButton) findViewById(R.id.soundB);
            r.setChecked(true);
        } else if (data.alertSound == ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD) {
            RadioButton r = (RadioButton) findViewById(R.id.soundC);
            r.setChecked(true);
        } else if (data.alertSound == ToneGenerator.TONE_CDMA_ABBR_ALERT) {
            RadioButton r = (RadioButton) findViewById(R.id.soundD);
            r.setChecked(true);
        }
        sound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                if (checkedId == R.id.soundA) {
                    data.alertSound = ToneGenerator.TONE_CDMA_ABBR_INTERCEPT;
                    toneG.startTone(data.alertSound, 1000);
                } else if (checkedId == R.id.soundB) {
                    data.alertSound = ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE;
                    toneG.startTone(data.alertSound, 1000);
                } else if (checkedId == R.id.soundC) {
                    data.alertSound = ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD;
                    toneG.startTone(data.alertSound, 1000);
                } else if (checkedId == R.id.soundD) {
                    data.alertSound = ToneGenerator.TONE_CDMA_ABBR_ALERT;
                    toneG.startTone(data.alertSound, 1000);
                }
                SharedPreferences sharedpreferences = getSharedPreferences(Data.PREFFOLDER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(Data.ALERT_SOUND, data.alertSound);
                editor.commit();
            }
        });

        distanceTv = (TextView) findViewById(R.id.distancetv);
        distanceTv.setText(String.format("%.2f", BeaconDetectService.dbm));
        customHandler.postDelayed(updateTimerThread, 100);
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            distanceTv.setText(String.format("%.3f", BeaconDetectService.dbm));
            customHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void getContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Data.CONTACT_PICKER_RESULT);
        // this.startActivity(intent);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Data.CONTACT_PICKER_RESULT:
                    Log.d("good ", "Warning: " + data.getData().toString());
                    Cursor cursor = null;
                    String phoneNumber = "";
                    String name = "";
                    // String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<String>();
                    int phoneIdx = 0;
                    int nameIdx = 0;
                    String id = null;
                    try {
                        Uri result = data.getData();
                        id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        if (cursor.moveToFirst()) {
                            while (cursor.isAfterLast() == false) {
                                phoneNumber = cursor.getString(phoneIdx);
                                name = cursor.getString(nameIdx);
                                Log.d("name is", "" + name);
                                allNumbers.add(phoneNumber);
                                cursor.moveToNext();
                            }
                        } else {
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.READ_CONTACTS)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                        Manifest.permission.READ_CONTACTS)) {

                                } else {
                                    ActivityCompat.requestPermissions(this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            1);
                                }
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                            Data data1 = Data.getinstance();
                            data1.contactArray.get(data1.numberOfArrayList).setName(name);
                            data1.contactArray.get(data1.numberOfArrayList).setId(id);
                            data1.iconsArray[data1.numberOfArrayList] = BitmapFactory.decodeStream((Settings.openPhoto(Integer.parseInt(id))));
                            for (int i = 0; i < allNumbers.size(); i++) {
                                Log.d("all numbers", "number " + 0 + " " + allNumbers.get(i).toString());

                                //data1.contactArray.add(new Person("a", allNumbers.get(i).toString(), "hi"));
                                data1.contactArray.get(data1.numberOfArrayList).setPhoneNumber(allNumbers.get(i).toString());

                                //data1.contactArray.get(data1.numberOfArrayList).setIcon(BitmapFactory.decodeStream(openPhoto(Integer.parseInt(id))));
                                //data1.saveArrayToStorage(data1.contactArray);
                                adapter.notifyDataSetChanged();

                            }
                        }


                    }
                    break;
            }

        } else {
            // gracefully handle failure
            Log.d("error", "Warning: activity result not ok");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data.saveArrayToStorage(data.contactArray);
    }

    public static InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = MyApp.getAppContext().getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    Log.d("pic", "pic");
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }


    public static void changingfont(LinearLayout ll) {
        Typeface tf = Typeface.createFromAsset(MyApp.getAppContext().getAssets(),
                "fonts/opensans_regular.ttf");
        TextView tx;
        EditText ev;
        Button bt;
        LinearLayout ll2;
        LinearLayout ll3;
        View v;
        View v2;
        int x = ll.getChildCount();
        int y = 0;
        int u = 0;
        for (int i = 0; i < x; i++) {
            v = ll.getChildAt(i);
            if (v instanceof EditText) {
                ev = (EditText) v;
                // ev.setTypeface(tf);
                ev.setTypeface(tf);
            }
            if (v instanceof TextView) {
                tx = (TextView) v;
                tx.setTypeface(tf);
            }
            if (v instanceof LinearLayout) {
                ll2 = (LinearLayout) v;
                y = ll2.getChildCount();
                for (int z = 0; z < y; z++) {
                    v = ll2.getChildAt(z);
                    if (v instanceof LinearLayout) {
                        ll3 = (LinearLayout) v;
                        u = ll3.getChildCount();
                        for (int k = 0; k < u; k++) {
                            v = ll3.getChildAt(k);
                            if (v instanceof EditText) {
                                ev = (EditText) v;
                                ev.setTypeface(tf);
                            }
                            if (v instanceof TextView) {
                                tx = (TextView) v;
                                tx.setTypeface(tf);
                            }
                            if (v instanceof Button) {
                                bt = (Button) v;
                                bt.setTypeface(tf);
                            }
                        }
                    }
                    if (v instanceof Button) {
                        bt = (Button) v;
                        bt.setTypeface(tf);
                    }
                    if (v instanceof EditText) {
                        ev = (EditText) v;
                        ev.setTypeface(tf);
                    }
                    if (v instanceof TextView) {
                        tx = (TextView) v;
                        tx.setTypeface(tf);
                    }
                }
            }
        }
    }

}
