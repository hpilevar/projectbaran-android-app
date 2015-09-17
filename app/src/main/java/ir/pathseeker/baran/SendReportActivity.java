package ir.pathseeker.baran;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;


public class SendReportActivity extends Activity {

    String TAG = "baran";
    String selectImageAddress;
    private boolean call_setupmap_key = false;
    MyLocation myLocation;
    double latitude,longitude;
    public static Location loc;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO : Amir situation BUG = when program rotated or destroyed, savedInstanceState not null but NullPointerException occure! ARG_SECTION_NUMBER is importent!!!
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
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report);

        Button takepic = (Button) findViewById(R.id.takePhotoButton);

        takepic.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                selectImage();

            }

        });

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                selectImage();

            }

        });

        Button sendreport = (Button) findViewById(R.id.sendreportButton);

        sendreport.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                sendReport();

            }

        });


        setUpMap2();

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
    protected void onDestroy() {
        super.onDestroy();
        try{
            progressBar.dismiss();
        } catch (Exception e) {
            Log.i(TAG, "progressBar exception eccure!");
        }
        Log.i(TAG, "On Destroy .....");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_report, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            ImageView imageView = null;
            imageView = (ImageView)this.findViewById(R.id.imageView);
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
                        File wallpaperDirectory = new File(path);
                        wallpaperDirectory.mkdirs();
                    }


                    f.delete();

                    OutputStream outFile = null;
                    long filename = System.currentTimeMillis();

                    File file = new File(path, String.valueOf(filename) + ".jpg");
                    Log.e(" Image File: ", path + File.separator + file.getName());
                    selectImageAddress = path + File.separator + file.getName();
                    try {

                        outFile = new FileOutputStream(file);
                        //TODO : Set Correct Quality
                        /*
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        int newWidth =600;
                        int newHeight =800;
                        float scaleWidth = ((float) newWidth) / width;// calculate the scale - in this case = 0.4f
                        float scaleHeight = ((float) newHeight) / height;

                        // createa matrix for the manipulation
                        Matrix matrix = new Matrix();
                        // resize the bit map
                        matrix.postScale(scaleWidth, scaleHeight);
                        // rotate the Bitmap


                        // recreate the new Bitmap
                        Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, newWidth, newHeight);
                        */
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if ( width > height) {
                            bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, false);
                        } else {
                            bitmap = Bitmap.createScaledBitmap(bitmap, 768, 1024, false);
                        }
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
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

                //String filename=picturePath.substring(picturePath.lastIndexOf("/")+1);
                //copyFile(picturePath,filename ,Environment.getExternalStorageDirectory().getPath());



                int width = thumbnail.getWidth();
                int height = thumbnail.getHeight();
                if ( width > height) {
                    thumbnail = Bitmap.createScaledBitmap(thumbnail, 1024, 768, false);
                } else {
                    thumbnail = Bitmap.createScaledBitmap(thumbnail, 768, 1024, false);
                }

                String path = android.os.Environment

                        .getExternalStorageDirectory()

                        + File.separator

                        + "Pictures" + File.separator + "baran";
                //p = path;


                File direct = new File(path);
                if (!direct.exists()) {
                    File wallpaperDirectory = new File(path);
                    wallpaperDirectory.mkdirs();
                }
                String tempName = "temp_baran_image.jpg";
                File file = new File(path, tempName);

                try {
                    OutputStream outFile = null;
                    outFile = new FileOutputStream(file);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                    picturePath = path + "/" + tempName;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.w("path of image from gallery......******************.........", picturePath + "");
                //  Log.e(" Image File: ",picturePath);
                selectImageAddress = picturePath;
                imageView.setImageBitmap(thumbnail);

            }
        }
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

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

    public  void sendReport(){

        if(!isLocationEnabled())
            return ;



        if(latitude <= 0) {

            if ( call_setupmap_key == false ) {
                setUpMap2();
                call_setupmap_key = true;
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.setUpMap_alertDialogtitle));
            String message = getResources().getString(R.string.setUpMap_alertDialogmessage);
            alertDialogBuilder.setMessage(message);
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go to a new activity of the app
                    //sendReport();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            // show alert
            alertDialog.show();

            return;

        }




        final String lat = String.valueOf(latitude);
        final String lon = String.valueOf(longitude);
        EditText titleTextBox = (EditText) findViewById(R.id.reportTitle);
        final String title =  titleTextBox.getText().toString();
        EditText descTextBox = (EditText) findViewById(R.id.reportDescription);
        final String desc = descTextBox.getText().toString() + "  ";
        final String ImageAddress =  selectImageAddress;
        ImageView problemPic = (ImageView) findViewById(R.id.imageView);


        if(!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Report_without_internet), Toast.LENGTH_LONG).show();
            saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_faild_message));
            titleTextBox.setText("");
            descTextBox.setText("");
            problemPic.setImageDrawable(getResources().getDrawable(R.drawable.ic_slr2));
            return ;
        }

        Log.e("title : ", title);
        Log.e("desc : ", desc);
        if( title == null || title.isEmpty() || title.length() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.SendReportTitle));
            String message = getResources().getString(R.string.setUpMap_selecttitle);
            alertDialogBuilder.setMessage(message);
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go to a new activity of the app
                    //sendReport();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            // show alert
            alertDialog.show();

            return;
        }

        if( selectImageAddress == null ) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.SendReportTitle));
            String message = getResources().getString(R.string.setUpMap_selectImageAddress);
            alertDialogBuilder.setMessage(message);
            // set positive button: Yes message
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // go to a new activity of the app
                    //sendReport();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            // show alert
            alertDialog.show();

            return;
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
            if(e.getMessage() != null) {
                Log.e("error", e.getMessage());
            }
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
                try {
                    progressBar.show();
                } catch (Exception e) {
                    Log.e("Error : ", "progressBar.show error");
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Log.w("async statusCode", String.valueOf(statusCode));
                progressBar.hide();
                //Toast.makeText(getApplicationContext(), "با موفقیت ثبت شد", Toast.LENGTH_LONG).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendReportActivity.this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.SendReportTitle));
                String message = getResources().getString(R.string.SendReportSucced);
                alertDialogBuilder.setMessage(message);
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();

                saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_success_message));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("async", String.valueOf(statusCode));
                progressBar.hide();
                //Toast.makeText(getApplicationContext(), "متاسفانه در روند ثبت با مشکل مواجه شدیم", Toast.LENGTH_LONG).show();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendReportActivity.this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.SendReportTitle));
                String message = getResources().getString(R.string.SendReportFaild);
                alertDialogBuilder.setMessage(message);
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // go to a new activity of the app
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();

                saveState(lat, lon, title, desc, ImageAddress,getResources().getString(R.string.report_send_faild_message));
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });

        titleTextBox.setText("");
        descTextBox.setText("");
        problemPic.setImageDrawable(getResources().getDrawable(R.drawable.ic_slr2));

    }



    private void selectImage() {

        final String selectImage_Take_Photo = this.getString(R.string.selectImage_Take_Photo);
        final String selectImage_Choose_from_Gallery = this.getString(R.string.selectImage_Choose_from_Gallery);
        final String selectImage_Cancel = this.getString(R.string.selectImage_Cancel);
        final CharSequence[] options = { selectImage_Take_Photo, selectImage_Choose_from_Gallery, selectImage_Cancel };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
}
