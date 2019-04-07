package com.example.neno.rentacar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

/**
 * Shows list of available cars in the fleet.
 */
public class AvailableCarsActivity extends ActionBarActivity {

    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private Gson mGson = new Gson();
    private ListView listView;
    private AvailableAdapter adapter;
    private String GET_AVAILABLE_CARS_SCRIPT = Constants.SERVER_IP_ADDRESS + Constants.AVAILABLE_CARS_SCRIPT_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_cars);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AvailableCarDetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        httpClient.get(GET_AVAILABLE_CARS_SCRIPT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                DataStorage.cars = mGson.fromJson(response.toString(), Car[].class);
                Log.d("Http reponse", "Gson converted");
                adapter = new AvailableAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(getApplicationContext(), "Error: Unable to reach server.", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


