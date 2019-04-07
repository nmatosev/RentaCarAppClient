package com.example.neno.rentacar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by neno on 20.11.2015.
 * This class is responsible for downloading and uploading images to server.
 */
public class DamagesActivity extends Activity {

    ImageView newDamagePicture;
    Button takePictureButton;
    Button uploadButton;
    Button checkOldPicturesButton;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String REG = "licensePlate";

    EditText damageDescription;
    int value;

    static final int CAM_REQUEST = 1;
    private static final String UPLOAD_IMAGE_SCRIPT = Constants.SERVER_IP_ADDRESS + Constants.UPLOAD_IMAGE_SCRIPT_PATH;
    private static final String DOWNLOAD_IMAGE_SCRIPT = Constants.SERVER_IP_ADDRESS + Constants.DOWNLOAD_IMAGE_SCRIPT_PATH;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damages);
        newDamagePicture = (ImageView) findViewById(R.id.new_damage);
        takePictureButton = (Button) findViewById(R.id.take_a_picture);
        checkOldPicturesButton = (Button) findViewById(R.id.check_old_damages);
        uploadButton = (Button) findViewById(R.id.up);
        damageDescription = (EditText) findViewById(R.id.new_damage_description);
        TextView modelTextView = (TextView) findViewById(R.id.model);
        TextView manufacturerTextView = (TextView) findViewById(R.id.manufacturer);
        TextView licensePlateTextView = (TextView) findViewById(R.id.license_plate);

        final Bundle extras = getIntent().getExtras();
        value = extras.getInt("position");
        licensePlateTextView.setText((DataStorage.cars[value].getLicensePlate()));
        manufacturerTextView.setText("   " + (DataStorage.cars[value].getManufacturer()));
        modelTextView.setText(Constants.SPACE + (DataStorage.cars[value].getModel()));

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera, CAM_REQUEST);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {  //UPLOADAJ
            @Override
            public void onClick(View v) {
                if (newDamagePicture.getDrawable() != null) {
                    Bitmap bigImage = ((BitmapDrawable) newDamagePicture.getDrawable()).getBitmap();
                    Bitmap image = scaleDown(bigImage, 150, true);
                    new UploadImage(image, damageDescription.getText().toString()).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Slika nije postavljena!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkOldPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadImageFromServer().execute();
            }
        });
    }

    private File getFile() {
        File folder = new File("sdcard/camera_app");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image.jpg");
        return image_file;
    }

    /**
     * Resizes picure. Reduces file size but preserves photo quality.
     *
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return newBitamp
     * resized BitMap instance
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = "sdcard/camera_app/cam_image.jpg";
        newDamagePicture.setImageDrawable(Drawable.createFromPath(path)); //POSTAVI SA PATHA U imageview
    }

    /**
     * Starts picture upload to server in 3 phases:
     * - onPreExecute - shows dialog window.
     * - doInBackground - Adds encoded image in BasicNameValuePair instance. This instance is posted to server and UPLOADED_IMAGE script makes sure that encoded
     * image is successfully saved in DB.
     * - onPostExecute - Triggers toast message.
     */
    private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DamagesActivity.this);
            pDialog.setMessage("Uploading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);//bitmap komprimiraj sa kvalitetom 100% u BYTEARRAY
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);//BYTEARRAY kodiraj u String
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image", encodedImage));
            dataToSend.add(new BasicNameValuePair("name", name));
            dataToSend.add(new BasicNameValuePair("licensePlate", (DataStorage.cars[value].getLicensePlate())));

            JSONObject json = jsonParser.makeHttpRequest(UPLOAD_IMAGE_SCRIPT, "POST", dataToSend);
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Intent i = getIntent();
                    setResult(100, i);
                    Log.d("response", "200");
                    finish();
                } else {
                    Log.d("Error", "NOK");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            Toast.makeText(getApplicationContext(), "Slika je uploadana", Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }
    }

    /**
     * Starts picture download from server in 3 phases:
     * - onPreExecute - shows dialog window
     * - doInBackground - Sends license plate attribute to server in order to check if there are som pictures of old damages. Coded images are downloaded and saved to images set
     * <p/>
     * - onPostExecute - Receives wrapper instance that contains image set. Images from set are converted from BASE64 code to byte array.
     * Byte array is converted to picture.
     */
    private class DownloadImageFromServer extends AsyncTask<String, String, DownloadImageFromServer.Wrapper> {
        public class Wrapper {
            public HashSet<String> imagesSet = new HashSet<String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DamagesActivity.this);
            pDialog.setMessage("Pretraga baze podataka...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected Wrapper doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(REG, (DataStorage.cars[value].getLicensePlate())));
            // check json success tag
            JSONObject json = jsonParser.makeHttpRequest(DOWNLOAD_IMAGE_SCRIPT, "POST", params);
            Wrapper w = new Wrapper();
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Integer j = 0;
                    int i = 0;
                    for (j = 0; j < json.length(); j++) {
                        if (json.has(j.toString())) {
                            w.imagesSet.add(json.getString(j.toString()));
                            i++;
                        }
                    }
                    w.imagesSet.add(json.getString("0"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return w;
        }

        protected void onPostExecute(Wrapper wrapper) {
            pDialog.dismiss();
            if (!wrapper.imagesSet.isEmpty()) {
                Iterator<String> imagesSetIterator = wrapper.imagesSet.iterator();
                LinearLayout layout = (LinearLayout) findViewById(R.id.damages_layout);
                while (imagesSetIterator.hasNext()) {
                    String picture = imagesSetIterator.next();
                    byte[] decodedString = Base64.decode(picture, Base64.DEFAULT); //primi base 64 string,dekodiraj u bajtove
                    Log.d("decodedstring", decodedString.toString());
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);//bajtovi => slika
                    ImageView image = new ImageView(DamagesActivity.this);
                    image.setLayoutParams(new android.view.ViewGroup.LayoutParams(500, 500));
                    image.setMaxHeight(500);
                    image.setMaxWidth(500);
                    image.setPadding(100, 1, 1, 1);
                    image.setImageBitmap(decodedByte);
                    // Adds the view to the layout
                    layout.addView(image);
                }
            } else {
                Toast.makeText(DamagesActivity.this, "Ovo vozilo nema zabilježenih šteta.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private HttpParams getHttpRequestParams() {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 40);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 40);
        return httpRequestParams;
    }
}
