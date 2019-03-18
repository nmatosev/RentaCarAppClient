package com.example.neno.rentacar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

/**
 * Created by neno on 14.11.2015.
 * Shows list of rented cars in the fleet.
 */

public class RentedCarsActivity extends ActionBarActivity {
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private Gson mGson = new Gson();
    private ListView listView;
    private RentedAdapter adapter;

    private String RENTED_CARS_SCRIPT = Constants.AVAILABLE_CARS_SCRIPT_PATH + Constants.RENTED_CARS_SCRIPT_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_cars);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RentedCarDetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        httpClient.get(RENTED_CARS_SCRIPT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                DataStorage.cars = mGson.fromJson(response.toString(), Car[].class);
                adapter = new RentedAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
