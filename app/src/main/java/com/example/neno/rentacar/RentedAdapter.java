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
 * Created by neno on 10.11.2015..
 */
public class RentedAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    public RentedAdapter(Context context) {
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_rented_car, parent, false);
        }
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_image);
        TextView reg = (TextView) convertView.findViewById(R.id.license_plate);
        TextView marka = (TextView) convertView.findViewById(R.id.manufacturer);
        TextView model = (TextView) convertView.findViewById(R.id.model);
        TextView stanjeTanka = (TextView) convertView.findViewById(R.id.fuel_tank_status);
        reg.setText((DataStorage.cars[position].getLicensePlate()));
        marka.setText(" " + (DataStorage.cars[position].getManufacturer())); //podatak obavezno u string inače puca APP
        model.setText(" " + (DataStorage.cars[position].getModel())); //pristupanje datstorageu na poziciju objekta
        stanjeTanka.setText("Tank: " + (DataStorage.cars[position].getFuelTankStatus()) + "%");
        UrlImageViewHelper.setUrlDrawable(thumbnail, DataStorage.cars[position].getThumbnailImage());
        return convertView;
    }
}
