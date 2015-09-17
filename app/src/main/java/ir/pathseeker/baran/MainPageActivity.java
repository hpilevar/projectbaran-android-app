package ir.pathseeker.baran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;


public class MainPageActivity extends Activity {

    private static final int STATIC_INTEGER_VALUE = 1;
    private int listItemCount = 0;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.WIRELESS_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_WIRELESS_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
            return false;
        } else
            return true;
    }

    private boolean isLocationEnabled() {
        //Checl location servise is on
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(this.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                View v = tryInflate(name, context, attrs);
                if (v instanceof TextView) {
                    setTypeFace((TextView) v);
                }
                return v;
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        ImageButton sendreport = (ImageButton) findViewById(R.id.report);
        sendreport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                // Add the buttons
                builder.setPositiveButton(R.string.city_issue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if(isLocationEnabled()) {
                            Intent i = new Intent(getApplicationContext(), SendReportActivity.class);
                            startActivityForResult(i, STATIC_INTEGER_VALUE);
                        }
                    }
                });

                builder.setNegativeButton(R.string.mobile_issue, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                            Intent i = new Intent(getApplicationContext(), SendMobileIssue.class);
                            startActivityForResult(i, STATIC_INTEGER_VALUE);
                    }
                });
                // Set other dialog properties


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });

        fillData();

        ImageButton viewlocation = (ImageButton) findViewById(R.id.location);
        viewlocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    //TODO : if list is empty then this intent must do not show
                    if (listItemCount == 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainPageActivity.this);
                        dialog.setMessage(getResources().getString(R.string.Zero_listItemCount));
                        dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                // TODO Auto-generated method stub

                            }
                        });
                        dialog.show();
                        return ;
                    }
                    Intent i = new Intent(getApplicationContext(), ReportMapsActivity.class);
                    startActivity(i);


            }

        });

        ImageButton viewinternet = (ImageButton) findViewById(R.id.internet);
        viewinternet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isNetworkConnected()) {
                    Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                    startActivity(i);
                }
            }

        });

/*
        ImageButton infobutton = (ImageButton) findViewById(R.id.infobutton);
        infobutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    Intent i = new Intent(getApplicationContext(), infoActivity.class);
                    startActivity(i);

            }

        });
        ImageButton exitbutton = (ImageButton) findViewById(R.id.exitbutton);
        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                // Add the buttons
                builder.setPositiveButton(R.string.exit_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        System.exit(0);
                    }
                });
                builder.setNegativeButton(R.string.exit_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
*/

        ImageButton sync_logo = ( ImageButton ) findViewById(R.id.sync_logo);
        sync_logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                syncing();
            }

        });


        TextView sync_text = ( TextView ) findViewById(R.id.sync_text);
        sync_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                syncing();
            }

        });







    }

    private View tryInflate(String name, Context context, AttributeSet attrs) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = null;
        try {
            v = li.createView(name, null, attrs);
        } catch (Exception e) {
            try {
                v = li.createView("android.widget." + name, null, attrs);
            } catch (Exception e1) {
            }
        }
        return v;
    }

    private void setTypeFace(TextView tv) {
        tv.setTypeface(FontUtils.getFonts(this, "wyekan.ttf"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult"," OK ");
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (STATIC_INTEGER_VALUE) : {
                Log.e("onActivityResult",String.valueOf(STATIC_INTEGER_VALUE));
                fillData();
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
            // Add the buttons
            builder.setPositiveButton(R.string.exit_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    System.exit(0);
                }
            });
            builder.setNegativeButton(R.string.exit_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // Set other dialog properties


            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

            return true;

        } else if (id == R.id.action_info) {
            //TODO : Action
            Intent i = new Intent(getApplicationContext(), infoActivity.class);
            startActivity(i);
        }



        return super.onOptionsItemSelected(item);
    }

    // private Cursor cursor;
    private SimpleCursorAdapter adapter;

    private void fillData() {
        Log.e("fillData","Run");
        String[] projection = { ReportTable.COLUMN_ID, ReportTable.COLUMN_title, ReportTable.COLUMN_LAT, ReportTable.COLUMN_LONG, ReportTable.COLUMN_STATE };
        final Cursor cursor = getContentResolver().query(MyReportContentProvider.CONTENT_URI, projection, null, null, ReportTable.COLUMN_ID + " DESC ");
        // The desired columns to be bound
        String[] fromFieldNames = {ReportTable.COLUMN_title, ReportTable.COLUMN_STATE};
        // the XML defined views which the data will be bound to
        int[] toViewsID = {R.id.title, R.id.state};
        adapter = new SimpleCursorAdapter(this, R.layout.report_row, cursor, fromFieldNames, toViewsID, 0);
        Log.e("listView Count : ", String.valueOf(cursor.getCount()));
        listItemCount = cursor.getCount();
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        Intent i = new Intent(MainPageActivity.this, ReportDetailActivity.class);
                        Uri ReportUri = Uri.parse(MyReportContentProvider.CONTENT_URI + "/" + id);
                        i.putExtra(MyReportContentProvider.CONTENT_ITEM_TYPE, ReportUri);

                        //startActivity(i);
                        startActivityForResult(i, STATIC_INTEGER_VALUE);
                        //Take action here.
                    }
                }
        );


        SyncCheck();
    }


    private void SyncCheck() {
            Log.e("SyncCheck","Run");
            //Checking for Sync
            String searchQuery = ReportTable.COLUMN_STATE + " = '" + getResources().getString(R.string.report_send_faild_message) + "' ";
            Cursor cursor = getContentResolver().query(MyReportContentProvider.CONTENT_URI, null, searchQuery, null, null);
            if (cursor != null) {
                ImageButton sync_logo = ( ImageButton ) findViewById(R.id.sync_logo);
                TextView sync_text = ( TextView ) findViewById(R.id.sync_text);
                LinearLayout syncSection = (LinearLayout) findViewById(R.id.syncSection);

                if( cursor.getCount() > 0 ) {
                    syncSection.setVisibility(View.VISIBLE);
                    sync_logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_sync_problems));
                    sync_text.setText(getResources().getString(R.string.syncTextStr));
                } else {
                    syncSection.setVisibility(View.GONE);
                    sync_logo.setImageDrawable(getResources().getDrawable(R.drawable.ic_synchronization));
                    sync_text.setText("");
                }
            }
        cursor.close();
    }

    private void syncing() {
        Log.e("syncing","Run");
        if (!isNetworkConnected())
            return ;
        //Sometimes it lasts in love but sometimes it hurts instead
        //syncing
        String searchQuery = ReportTable.COLUMN_STATE + " = '" + getResources().getString(R.string.report_send_faild_message) + "' ";
        Cursor cursor = getContentResolver().query(MyReportContentProvider.CONTENT_URI, null, searchQuery, null, null);
        if (cursor != null) {
            Log.e("Cursor count: ",  String.valueOf(cursor.getCount()));
            int cursorCount = cursor.getCount();
            if( cursorCount == 0 ) {
                Toast.makeText(getApplicationContext(), R.string.syncOk, Toast.LENGTH_LONG).show();
                return ;
            }
            if(cursor.moveToFirst()) {
                for (int i = 0; i < cursorCount; i++) {

                    final String id = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
                    final String lat = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LAT));
                    final String lon = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LONG));
                    final String title =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_title));
                    final String desc = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_DESCRIPTION));
                    final String ImageAddress =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_IMAGE));
                    final String updateQuery = ReportTable.COLUMN_ID + " = '" + id + "' ";

                    Log.e("title : ", title);


                    RequestParams params = new RequestParams();
                    params.put("title",title);
                    params.put("body",desc);
                    params.put("lat",lat);
                    params.put("long",lon);
                    try {
                        //params.put("userfile",new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Instagram/test.png"));
                        params.put("userfile",new File(ImageAddress));
                    } catch (FileNotFoundException e) {
                        //Log.e("error", e.getMessage());
                        e.printStackTrace();
                    }

                    final ProgressDialog progressBar = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
                    progressBar.setMessage("در حال ارسال مورد "  + (i+1) + " از"   + cursorCount);


                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(45000);
                    client.post("http://www.projectbaran.ir/mobile/send_report.php", params, new AsyncHttpResponseHandler() {


                        @Override
                        public void onStart() {
                            // called before request is started
                            Log.w("async", "onStart!");
                            progressBar.show();

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            // called when response HTTP status is "200 OK"
                            Log.w("async", "success!!!!");
                            Log.w("async statusCode", String.valueOf(statusCode));
                            progressBar.hide();
                            Toast.makeText(getApplicationContext(), "با موفقیت ارسال شد", Toast.LENGTH_LONG).show();

                            ContentValues values = new ContentValues();
                            values.put(ReportTable.COLUMN_STATE, getResources().getString(R.string.report_send_success_message));
                            getContentResolver().update(MyReportContentProvider.CONTENT_URI, values, updateQuery, null);
                            fillData();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.w("async", "KHAAAAAAAAAAAAAAAAAAAR!");
                            Log.e("async", String.valueOf(statusCode));
                            progressBar.hide();
                            Toast.makeText(getApplicationContext(), "متاسفانه در روند ارسال  با مشکل مواجه شدیم", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                        }

                    });


                    cursor.moveToNext();
                }


            }



        }

        cursor.close();



    }


}
