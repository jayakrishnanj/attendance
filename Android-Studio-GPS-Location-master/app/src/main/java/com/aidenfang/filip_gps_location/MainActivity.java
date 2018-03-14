package com.aidenfang.filip_gps_location;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        //Define a listener that responds to location updates


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textView.append("\n" + location.getLatitude() + " " + location.getLongitude());
                postLocationData(location);
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        configureButton();

    }

    private void postLocationData(Location location) {
        if(location != null){
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://www.mocky.io/v2/5aa92bc23200002a2d165b87";
            JSONObject data = new JSONObject();
            try {
                data.put("lat",location.getLatitude());
                data.put("lon",location.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,data,new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject resp) {
                    if(resp != null){
                        Util.showAlert(MainActivity.this,"Response - 200",resp.toString());
                    }else{
                        Util.showAlert(MainActivity.this,"Response - 200","Empty");
                    }
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Util.showAlert(MainActivity.this,String.valueOf(volleyError.networkResponse.statusCode),volleyError.getMessage());
                }
            });
            queue.add(request);
        }
    }

    //@Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configureButton();
                break;
            default:
                break;
        }
    }


    private void configureButton() {

        //first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }


        button.setOnClickListener(myListener);
    }

    private OnClickListener myListener = new OnClickListener() {
        public void onClick (View view) {

            //ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
            //progressDialog.setMessage("Finding location ...");
            //progressDialog.setIndeterminate(true);
            //progressDialog.setCancelable(false);
            //progressDialog.show();

            // 5000 millisec, 5 meters
            locationManager.requestLocationUpdates("gps", 500, 5, locationListener);
        }
    };
}
