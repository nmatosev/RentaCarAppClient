package com.example.neno.rentacar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by neno on 14.11.2015..
 */
public class RentedCarDetailsActivity extends Activity {

    String key;
    int value;
    private static final String TRANSFER_CAR_TO_AVAILABLE_SCRIPT = Constants.SERVER_IP_ADDRESS + Constants.TRANSFER_CAR_TO_AVAILABLE_SCRIPT_PATH;

    private static final String TAG_SUCCESS = "success";
    private static final String REG = "licensePlate";

    private static final String TAG_NAME = "manufacturer";  //poveznica sa arg u php
    private static final String TAG_MODEL = "model";
    private static final String TAG_STETE = "stete";
    private static final String TAG_KM = "kilometraza";
    private static final String TAG_TANK = "fuelTankStatus";
    private static final String TAG_IMG = "thumbnailImage";
    private static final String TAG_KAT = "category";
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    public static SeekBar seekBar;
    private static TextView fuelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_car_details);
        TextView modelTextView = (TextView) findViewById(R.id.model);
        TextView manufacturerTextView = (TextView) findViewById(R.id.manufacturer);
        TextView licencsePlateTextView = (TextView) findViewById(R.id.license_plate);
        TextView fuelTankStatusTextView = (TextView) findViewById(R.id.fuel_tank_status);
        TextView categoryTextView = (TextView) findViewById(R.id.category);
        TextView mileageTextView = (TextView) findViewById(R.id.mileage);
        ImageView imageView = (ImageView) findViewById(R.id.splash_image);
        Button getBackToPreviousActivityButton = (Button) findViewById(R.id.transfer_to_available);
        Button newDamageButton = (Button) findViewById(R.id.new_damage);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("position");
            manufacturerTextView.setText("   " + (DataStorage.cars[position].getManufacturer()));
            modelTextView.setText(Constants.EMPTY_STRING + (DataStorage.cars[position].getModel()));
            licencsePlateTextView.setText((DataStorage.cars[position].getLicensePlate()));
            mileageTextView.setText("Izdan na:" + (DataStorage.cars[position].getMileage()) + " mileage");
            fuelTankStatusTextView.setText("Tank izdan na: " + (DataStorage.cars[position].getFuelTankStatus()) + "%");
            categoryTextView.setText("Kategorija: " + (DataStorage.cars[position].getCategory()));
            UrlImageViewHelper.setUrlDrawable(imageView, DataStorage.cars[position].getThumbnailImage());
        }

        setSeekBar();
        final Preferences prefs = new Preferences(this);
        key = "poz";
        value = extras.getInt("position");
        prefs.setInt(key, value);

        getBackToPreviousActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new tranfserCarToAvailable().execute();
            }
        });

        newDamageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DamagesActivity.class);
                intent.putExtra("position", value);
                startActivity(intent);
            }
        });
    }

    public void setSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        fuelTextView = (TextView) findViewById(R.id.fuel);
        fuelTextView.setText("Novo stanje tanka " + seekBar.getProgress() + "/" + seekBar.getMax());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Integer seekBarProggresValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProggresValue = progress;
                fuelTextView.setText("Novo stanje tanka " + progress + "/" + RentedCarDetailsActivity.seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                fuelTextView.setText("Novo stanje tanka " + seekBarProggresValue + "/" + RentedCarDetailsActivity.seekBar.getMax());
            }
        });
    }

    class tranfserCarToAvailable extends AsyncTask<String, String, String> {
        EditText newMileageEditText = (EditText) findViewById(R.id.new_mileage);
        String fuelTankStatus = String.valueOf(seekBar.getProgress());
        String newMileage = newMileageEditText.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RentedCarDetailsActivity.this);
            pDialog.setMessage("Spremam ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(REG, (DataStorage.cars[value].getLicensePlate())));
            params.add(new BasicNameValuePair(TAG_NAME, (DataStorage.cars[value].getManufacturer())));
            params.add(new BasicNameValuePair(TAG_MODEL, (DataStorage.cars[value].getModel())));
            params.add(new BasicNameValuePair(TAG_KM, newMileage));
            params.add(new BasicNameValuePair(TAG_STETE, (DataStorage.cars[value].getDamages())));
            params.add(new BasicNameValuePair(TAG_TANK, fuelTankStatus));
            params.add(new BasicNameValuePair(TAG_IMG, (DataStorage.cars[value].getThumbnailImage())));
            params.add(new BasicNameValuePair(TAG_KAT, (DataStorage.cars[value].getCategory())));
            // check json success tag
            JSONObject json = jsonParser.makeHttpRequest(TRANSFER_CAR_TO_AVAILABLE_SCRIPT,
                    "POST", params);
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Intent i = getIntent();
                    setResult(100, i);
                    Log.d("POST request", "Successful POST request.");
                    finish();
                } else {
                    Log.d("POST request", "POST request failed.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            Integer newMileage = Integer.valueOf(newMileageEditText.getText().toString());
            Integer oldMileage = Integer.valueOf((DataStorage.cars[value].getMileage()).toString());
            Integer mileageDifference;
            Integer fuelTankDifference;

            if (newMileage < 1000000) {
                mileageDifference = calculateDifference(newMileage, oldMileage);
                fuelTankDifference = calculateDifference(Integer.valueOf(fuelTankStatus), Integer.valueOf((DataStorage.cars[value].getFuelTankStatus()).toString()));
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
                if (mileageDifference > 0) {
                    Toast.makeText(getApplicationContext(), "Prijeđeni kilometri: " + mileageDifference.toString() + " mileage." +
                            " Razlika tanka: " + fuelTankDifference.toString() + " %", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Upozorenje: Nova kilometraža manja od stare." +
                            " Razlika tanka: " + fuelTankDifference.toString() + "%", Toast.LENGTH_LONG).show();
                }
            } else {
                fuelTankDifference = calculateDifference(Integer.valueOf(fuelTankStatus), Integer.valueOf((DataStorage.cars[value].getFuelTankStatus()).toString()));
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Upozorenje: Nova kilometraža prevelika." +
                        " Razlika tanka: " + fuelTankDifference.toString() + "%", Toast.LENGTH_LONG).show();
            }
        }

        private int calculateDifference(int newValue, int oldValue) {
            int difference = newValue - oldValue;
            return difference;
        }
    }
}
