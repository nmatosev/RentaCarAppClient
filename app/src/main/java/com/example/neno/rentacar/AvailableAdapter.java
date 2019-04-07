package com.example.neno.rentacar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by neno on 10.11.2015.
 * This class is responsible for presenting list of available cars.
 */
public class AvailableAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public AvailableAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return DataStorage.cars.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_available_car, parent, false);
        }
        ImageView thumbnailImage = (ImageView) convertView.findViewById(R.id.thumbnail_image);
        TextView licensePlate = (TextView) convertView.findViewById(R.id.license_plate);
        TextView manufacturer = (TextView) convertView.findViewById(R.id.manufacturer);
        TextView model = (TextView) convertView.findViewById(R.id.model);
        TextView fuelTankStatus = (TextView) convertView.findViewById(R.id.fuel_tank_status);
        licensePlate.setText((DataStorage.cars[position].getLicensePlate()));
        manufacturer.setText(" " + (DataStorage.cars[position].getManufacturer()));
        model.setText(Constants.SPACE + (DataStorage.cars[position].getModel()));
        fuelTankStatus.setText("Tank: " + (DataStorage.cars[position].getFuelTankStatus()) + "%");
        UrlImageViewHelper.setUrlDrawable(thumbnailImage, DataStorage.cars[position].getThumbnailImage());
        return convertView;
    }
}
