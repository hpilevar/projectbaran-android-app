package ir.pathseeker.baran;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReportMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Log.e("setUpMap" , "OK");
        Double lat = null,lon = null;
        String title = null;

        String[] projection = { ReportTable.COLUMN_title, ReportTable.COLUMN_LAT, ReportTable.COLUMN_LONG };
        //Uri ReportUri = Uri.parse(MyReportContentProvider.CONTENT_URI + "/" + 3);
        Cursor cursor = getContentResolver().query(MyReportContentProvider.CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            Log.e("Cursor count: ",  String.valueOf(cursor.getCount()));
            if(cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {


                    lat = cursor.getDouble(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LAT));
                    lon = cursor.getDouble(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LONG));
                    title = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_title));

                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("\u200e"+title));
                    cursor.moveToNext();
                }


            }
        }

        cursor.close();

        if(lat != null && lon != null) {

            LatLng MOUNTAIN_VIEW = new LatLng(lat, lon);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MOUNTAIN_VIEW)      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
