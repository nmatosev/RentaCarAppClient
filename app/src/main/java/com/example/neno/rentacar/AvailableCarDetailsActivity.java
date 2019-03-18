package com.example.neno.rentacar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by neno on 12.11.2015..
 */
public class AvailableCarDetailsActivity extends Activity {
    String key;
    int value;

    private static final String TRANSFER_CAR_TO_RENTED_SCRIPT = Constants.SERVER_IP_ADDRESS + "/checkInCheckOut/transferToRented.php";
    private static final String TAG_SUCCESS = "success";
    private static final String REG = "licensePlate";
    private static final String TAG_NAME = "manufacturer";  //poveznica sa arg u php skripti
    private static final String TAG_MODEL = "model";
    private static final String TAG_STETE = "stete";
    private static final String TAG_KM = "kilometraza";
    private static final String TAG_TANK = "fuelTankStatus";
    private static final String TAG_IMG = "thumbnailImage";
    private static final String TAG_KAT = "category";
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_car_details);
        TextView modelTextView = (TextView) findViewById(R.id.model);
        TextView manufacturerTextView = (TextView) findViewById(R.id.manufacturer);
        TextView licensePlateTextView = (TextView) findViewById(R.id.license_plate);
        TextView fuelTankStatusTextView = (TextView) findViewById(R.id.fuel_tank_status);
        TextView categoryTextView = (TextView) findViewById(R.id.category);
        TextView mileageTextView = (TextView) findViewById(R.id.mileage);
        Button sendToRentedButton = (Button) findViewById(R.id.transfer_to_rented);
        Button damagesButton = (Button) findViewById(R.id.stete);
        ImageView imageView = (ImageView) findViewById(R.id.splash_image);
        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            int position = extras.getInt("position");
            manufacturerTextView.setText("   " + (DataStorage.cars[position].getManufacturer()));
            modelTextView.setText(Constants.EMPTY_STRING + (DataStorage.cars[position].getModel()));
            licensePlateTextView.setText((DataStorage.cars[position].getLicensePlate()).toString());
            fuelTankStatusTextView.setText("Stanje tanka: " + (DataStorage.cars[position].getFuelTankStatus()) + "%");
            mileageTextView.setText((DataStorage.cars[position].getMileage()).toString() + " mileage");
            categoryTextView.setText("Kategorija: " + (DataStorage.cars[position].getCategory()));
            UrlImageViewHelper.setUrlDrawable(imageView, DataStorage.cars[position].getThumbnailImage());
        }

        final Preferences preferences = new Preferences(this);
        key = "poz";
        value = extras.getInt("position");
        preferences.setInt(key, value);
        String val = String.valueOf(value);

        sendToRentedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = extras.getInt("position");
                new transferCarToRented().execute();
            }
        });

        damagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DamagesActivity.class);
                intent.putExtra("position", value);
                startActivity(intent);
            }
        });
    }

    class transferCarToRented extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AvailableCarDetailsActivity.this);
            pDialog.setMessage("Saving ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(REG, (DataStorage.cars[value].getLicensePlate()).toString()));
            params.add(new BasicNameValuePair(TAG_NAME, (DataStorage.cars[value].getManufacturer()).toString()));
            params.add(new BasicNameValuePair(TAG_MODEL, (DataStorage.cars[value].getModel()).toString()));
            params.add(new BasicNameValuePair(TAG_KM, (DataStorage.cars[value].getMileage()).toString()));
            params.add(new BasicNameValuePair(TAG_STETE, (DataStorage.cars[value].getDamages()).toString()));
            params.add(new BasicNameValuePair(TAG_TANK, (DataStorage.cars[value].getFuelTankStatus()).toString()));
            params.add(new BasicNameValuePair(TAG_IMG, (DataStorage.cars[value].getThumbnailImage()).toString()));
            params.add(new BasicNameValuePair(TAG_KAT, (DataStorage.cars[value].getCategory()).toString()));
            JSONObject json = jsonParser.makeHttpRequest(TRANSFER_CAR_TO_RENTED_SCRIPT, "POST", params);
            // check json success tag
            try {
                Log.d("POST request", "Trying to send post request...");
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
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            pDialog.dismiss();
        }
    }
}


