package com.isucorp.acmecompanytest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.helpers.ToastHelper;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

@Info("Created by Ivan Faez Cobo on 23/5/2021")
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{
    // region CONSTANTS

    private static final String EXTRA_ADDRESS = "45730659aa594209aae4bc3d9b1459b5";

    // endregion

    // region PRIVATE VARIABLES

    private GoogleMap m_map;

    // endregion

    // region CACHING VARIABLES

    private MapView m_mapView;
    private WebView m_webView;
    private EditText m_edtAddress;

    // endregion

    // region FACTORY METHODS

    public static void start(Activity fromActivity, String address)
    {
        Intent intent = new Intent(fromActivity, MapsActivity.class);
        if (!TextUtils.isEmpty(address))
            intent.putExtra(EXTRA_ADDRESS, address);

        fromActivity.startActivity(intent);
    }

    // endregion

    // region OVERRIDES

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // init webview
        m_webView = findViewById(R.id.web_view);
        m_webView.setWebViewClient(new WebViewClient());
        m_webView.getSettings().setJavaScriptEnabled(true);

        initCachingVariables();
        initExtras();

        // go now to the address
        goToAddressInWebViewMap();

        // init map
        /*m_mapView = findViewById(R.id.map_view);
        m_mapView.onCreate(savedInstanceState);
        m_mapView.getMapAsync(this);*/

        // show go back btn in the toolbar and set the close icon
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i)
    {
        if (i.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(i);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        m_map = googleMap;
        MapsInitializer.initialize(getApplicationContext());

        goToAddressInMap();

        // show lat/long on click
        m_map.setOnMapClickListener(
                latLng -> ToastHelper.show((int) latLng.latitude + ", " + (int) latLng.longitude)
        );
    }

    @Override
    public void onResume()
    {
        //m_mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //m_mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //m_mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        //m_mapView.onLowMemory();
    }

    // endregion

    // region PRIVATE METHODS

    private void initCachingVariables()
    {
        m_edtAddress = findViewById(R.id.edt_map_address);

        // init btn go
        findViewById(R.id.btn_go).setOnClickListener(v ->
        {
            // if the address is empty show error
            String address = m_edtAddress.getText().toString();
            if (TextUtils.isEmpty(address))
                m_edtAddress.setError(getString(R.string.error_msg_can_not_be_empty));
            else
                goToAddressInMap();

            // hide the keyboard
            InputMethodManager imm = (InputMethodManager) MapsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(m_edtAddress.getWindowToken(), 0);
        });
    }

    private void initExtras()
    {
        // set address extra to the edit text
        String addressExtra = getIntent().getStringExtra(EXTRA_ADDRESS);
        m_edtAddress.setText(addressExtra);
    }

    private void goToAddressInMap()
    {
        if (m_map == null)
        {
            ToastHelper.show("Map not ready");
            return;
        }

        String address = m_edtAddress.getText().toString();
        if (TextUtils.isEmpty(address))
            return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try
        {
            addresses = geocoder.getFromLocationName(address, 5);
        }
        catch (Exception e)
        {
            Log.e("Maps", "Error", e);
            ToastHelper.show("Error getting addresses for '" + address + "'");
            return;
        }

        if (addresses.size() > 0)
        {
            Address addr = addresses.get(0);

            LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());
            m_map.addMarker(new MarkerOptions().position(latLng).title("Marker in " + address));
            m_map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            m_mapView.onResume();
        }
        else
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Google Map");
            adb.setMessage("The place '" + address + "' was not found");
            adb.setPositiveButton("OK", null);
            adb.show();
        }
    }

    private void goToAddressInWebViewMap()
    {
        String url = "https://www.google.com/maps/"; // default for empty

        String address = m_edtAddress.getText().toString();
        if (!TextUtils.isEmpty(address))
        {
            try
            {
                url += "search/" + URLEncoder.encode(address, "utf8");
            }
            catch (Exception ignored)
            {
            }
        }

        m_webView.loadUrl(url);
    }

    // endregion
}