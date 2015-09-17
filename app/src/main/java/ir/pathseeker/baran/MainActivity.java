package ir.pathseeker.baran;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import com.loopj.android.http.*;

import org.apache.http.Header;

public class MainActivity extends Activity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private WebView browser;
    private ProgressBar myProgress;
    private ProgressDialog progressBar;
    String TAG = "baran";
    String selectImageAddress;
    private boolean call_setupmap_key = false;
    MyLocation myLocation;


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
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


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "On Create .....");
        //TODO : Amir situation BUG = when program rotated or destroyed, savedInstanceState not null but NullPointerException occure! ARG_SECTION_NUMBER is importent!!!
        savedInstanceState = null;

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        Button takepic = (Button) findViewById(R.id.takepic);

        takepic.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                selectImage();

            }

        });

        Button sendreport = (Button) findViewById(R.id.sendreport);

        sendreport.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                postImage();

            }

        });


        //loadwebview();
        setUpMap2();

    }

    private void selectImage() {

        final String selectImage_Take_Photo = this.getString(R.string.selectImage_Take_Photo);
        final String selectImage_Choose_from_Gallery = this.getString(R.string.selectImage_Choose_from_Gallery);
        final String selectImage_Cancel = this.getString(R.string.selectImage_Cancel);
        final CharSequence[] options = { selectImage_Take_Photo, selectImage_Choose_from_Gallery, selectImage_Cancel };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(this.getString(R.string.selectImage_Title));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(selectImage_Take_Photo))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp_baran_image.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    //pic = f;
                    startActivityForResult(intent, 1);


                }

                else if (options[item].equals(selectImage_Choose_from_Gallery))

                {

                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }

                else if (options[item].equals(selectImage_Cancel)) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            ImageView imageView = null;
            imageView = (ImageView)this.findViewById(R.id.problemPic);
            if (requestCode == 1) {
                //h=0;
                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp_baran_image.jpg")) {

                        f = temp;
                        File photo = new File(Environment.getExternalStorageDirectory(), "temp_baran_image.jpg");
                        //pic = photo;
                        break;

                    }

                }

                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),

                            bitmapOptions);


                    imageView.setImageBitmap(bitmap);


                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "Pictures" + File.separator + "baran";
                    //p = path;


                    File direct = new File(path);
                    if (!direct.exists()) {
                        File wallpaperDirectory = new File("/sdcard/DirName/");
                        wallpaperDirectory.mkdirs();
                    }


                    f.delete();

                    OutputStream outFile = null;
                    long filename = System.currentTimeMillis();

                    File file = new File(path, String.valueOf(filename) + ".jpg");
                    Log.e(" Image File: ",path + File.separator + file.getName());
                    selectImageAddress = path + File.separator + file.getName();
                    try {

                        outFile = new FileOutputStream(file);
                        //TODO : Set Correct Quality
                        /*
                        int width = photo.getWidth();
                        int height = photo.getHeight();
                        int newWidth =3000;
                        int newHeight =3000;
                        float scaleWidth = ((float) newWidth) / width;// calculate the scale - in this case = 0.4f
                        float scaleHeight = ((float) newHeight) / height;

                        // createa matrix for the manipulation
                        Matrix matrix = new Matrix();
                        // resize the bit map
                        matrix.postScale(scaleWidth, scaleHeight);
                        // rotate the Bitmap


                        // recreate the new Bitmap
                        Bitmap.createBitmap(photo, 0, 0,width, height, matrix, true);
                        Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, 1000, 1000);
                        */

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, outFile);
                        //pic=file;
                        outFile.flush();

                        outFile.close();

                        // For show picture in Android gallery
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE,  String.valueOf(filename));
                        values.put(MediaStore.Images.Media.DESCRIPTION, latitude + " " + longitude);
                        values.put(MediaStore.Images.Media.DATE_TAKEN, filename);
                        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
                        values.put("_data", file.getAbsolutePath());

                        ContentResolver cr = getContentResolver();
                        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

                    Uri selectedImage = data.getData();
                    // h=1;
                    //imgui = selectedImage;
                    String[] filePath = {MediaStore.Images.Media.DATA};

                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                    c.moveToFirst();

                    int columnIndex = c.getColumnIndex(filePath[0]);

                    String picturePath = c.getString(columnIndex);

                    c.close();

                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));


                    Log.w("path of image from gallery......******************.........", picturePath + "");
                  //  Log.e(" Image File: ",picturePath);
                    selectImageAddress = picturePath;
                        imageView.setImageBitmap(thumbnail);

            }
        }
    }


    public  void postImage(){

        if(!isLocationEnabled())
            return ;



        if(latitude <= 0) {

            if ( call_setupmap_key == false ) {
                setUpMap2();
                call_setupmap_key = true;
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.setUpMap_alertDialogtitle));
            String message = getResources().getString(R.string.setUpMap_alertDialogmessage);
            alertDialogBuilder.setMessage(message);
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go to a new activity of the app
                    //postImage();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            // show alert
            alertDialog.show();

            return;

        }

        final String lat = String.valueOf(latitude);
        final String lon = String.valueOf(longitude);
        EditText titleTextBox = (EditText) findViewById(R.id.editText);
        final String title =  titleTextBox.getText().toString();
        EditText descTextBox = (EditText) findViewById(R.id.editText2);
        final String desc = descTextBox.getText().toString();
        final String ImageAddress =  selectImageAddress;
        ImageView problemPic = (ImageView) findViewById(R.id.problemPic);


        if(!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.Report_without_internet), Toast.LENGTH_LONG).show();
            saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_faild_message));
            titleTextBox.setText("");
            descTextBox.setText("");
            problemPic.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            return ;
        }




        RequestParams params = new RequestParams();
        params.put("title",title);
        params.put("body",desc);
        params.put("lat",lat);
        params.put("long",lon);
        try {
            //params.put("userfile",new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Instagram/test.png"));
            params.put("userfile",new File(selectImageAddress));
        } catch (FileNotFoundException e) {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }

        progressBar = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage("در حال ارسال ...");


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
                Log.w("async statusCode", String.valueOf(statusCode));
                progressBar.hide();
                Toast.makeText(getApplicationContext(), "با موفقیت ثبت شد", Toast.LENGTH_LONG).show();
                saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_success_message));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("async", String.valueOf(statusCode));
                progressBar.hide();
                Toast.makeText(getApplicationContext(), "متاسفانه در روند ثبت با مشکل مواجه شدیم", Toast.LENGTH_LONG).show();
                saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_faild_message));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });

        titleTextBox.setText("");
        descTextBox.setText("");
        problemPic.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));

    }

    @Override
    protected void onDestroy() {
//        myProgress.destroyDrawingCache();
//        myProgress = null;
//        progressBar.dismiss();
//        progressBar = null;
//        //mWebContainer.removeAllViews();
//        browser.clearHistory();
//        browser.clearCache(true);
//        browser.loadUrl("about:blank");
//        browser.freeMemory();  //new code
//        browser.pauseTimers(); //new code
//        browser = null;
        super.onDestroy();
        Log.i(TAG, "On Destroy .....");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "On Pause .....");

        call_setupmap_key = false;
        if( myLocation != null ) {
            myLocation.StopIt();
            myLocation = null;
        }

    }

    /* (non-Javadoc)
    * @see android.app.Activity#onRestart()
    */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "On Restart .....");
        setUpMap2();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //do something
        Log.i(TAG, "On start .....");


    }

    @Override
    protected void onResume() {

        super.onResume();
        //do something
        Log.i(TAG, "On resume .....");
        //setUpMap2();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "On Stop .....");
    }


    @Override
    public void finish() {


        Log.i(TAG, "On Finish .....");
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
        //TODO :  Find a way for completly close the program
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

        super.finish();
        //System.exit(0);

    }


    public static Location loc;

    private void setUpMap2() {

            Log.w("setUpMap2: ", "setUpMap2");
            // to Find the Location
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(final Location location) {
                        if(location != null) {
                            loc = location;
                            Log.e("Latitude: ", String.valueOf(loc.getLatitude()));
                            latitude = loc.getLatitude();
                            Log.e("Longitude: ", String.valueOf(loc.getLongitude()));
                            longitude = loc.getLongitude();
                        } else {
                            latitude = 0;
                            longitude = 0;
                        }
                }
            };

            myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

    }

    double latitude,longitude;

    private void setUpMap() {

        LocationManager mlocManager=null;
        LocationListener mlocListener;
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new ir.pathseeker.baran.MyLocationListener();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
//        mlocListener.onLocationChanged();

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if(MyLocationListener.latitude>0)
            {
                Toast.makeText(getBaseContext(), "Lat : " + MyLocationListener.latitude + "\n" + " Lon : " + MyLocationListener.longitude  , Toast.LENGTH_LONG).show();
                latitude = MyLocationListener.latitude;
                longitude = MyLocationListener.longitude;
            }
            else
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Wait");
                String message = "GPS in progress, please wait.";
                alertDialogBuilder.setMessage(message);
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // go to a new activity of the app
                        setUpMap();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();

            }

        } else {
            Toast.makeText(getBaseContext(), "GPS is not turned on...", Toast.LENGTH_LONG).show();
        }


    }



    private void saveState(String lat, String lon, String title, String description, String image,String state) {

        // only save if either summary or description
        // is available

        if (description.length() == 0 && title.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ReportTable.COLUMN_title, title);
        values.put(ReportTable.COLUMN_DESCRIPTION, description);
        values.put(ReportTable.COLUMN_IMAGE, image);
        values.put(ReportTable.COLUMN_LAT, lat);
        values.put(ReportTable.COLUMN_LONG, lon);
        values.put(ReportTable.COLUMN_STATE, state);

        // New Report
        Uri ReportUri = getContentResolver().insert(MyReportContentProvider.CONTENT_URI, values);

    }




    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        String[] stringArray = getResources().getStringArray(R.array.section_titles);
        Log.i("onSectionAttached", Integer.toString(number));
        number = number - 1;
        if (number >= 1) {
            mTitle = stringArray[number - 1];
        }


        switch (number) {
            case 0:
                //Toast.makeText(getApplicationContext(), "Clicked",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                browser.loadUrl("http://baran.com");
                break;
            case 2:
                Intent i = new Intent(this, ReportsOverviewActivity.class);
                startActivity(i);
                break;
            case 3:

                //ViewGroup container = (ViewGroup) findViewById(R.id.container);
                //container.removeAllViews();
                //View chapterInflater = LayoutInflater.from(getApplication()).inflate(R.layout.fragment_main, container);
                Intent x = new Intent(this, ReportMapsActivity.class);
                startActivity(x);
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            browser.loadUrl("javascript:$('#contact-modal').modal('show');");
            return true;
        } else if (id == R.id.action_helper) {
            browser.loadUrl("javascript:introJs().setOptions({ 'nextLabel': 'بعد', 'prevLabel': 'قبل', 'skipLabel': 'خروج', 'doneLabel': 'اتمام' }).start();");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            Log.e("baran PlaceholderFragment", "ARG_SECTION_NUMBER : " + ARG_SECTION_NUMBER);
            super.onAttach(activity);
                ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
