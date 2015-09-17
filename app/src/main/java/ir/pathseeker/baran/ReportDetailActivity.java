package ir.pathseeker.baran;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class ReportDetailActivity extends Activity {

    private Uri reportUri;
    String path = android.os.Environment

            .getExternalStorageDirectory()

            + File.separator

            + "Pictures" + File.separator + "baran";

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
        setContentView(R.layout.activity_report_detail);


        // check from the saved Instance
        /*
        reportUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(MyreportContentProvider.CONTENT_ITEM_TYPE);
        */
        Bundle extras = getIntent().getExtras();
        // Or passed from the other activity
        if (extras != null) {
            reportUri = extras
                    .getParcelable(MyReportContentProvider.CONTENT_ITEM_TYPE);

            fillData(reportUri);
        }

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



    private void fillData(Uri uri) {
        String[] projection = { ReportTable.COLUMN_ID, ReportTable.COLUMN_title, ReportTable.COLUMN_DESCRIPTION, ReportTable.COLUMN_IMAGE, ReportTable.COLUMN_LAT, ReportTable.COLUMN_LONG };
        final Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String DESCRIPTION = cursor.getString(cursor
                    .getColumnIndexOrThrow(ReportTable.COLUMN_DESCRIPTION));

            final String reportId = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_ID));
            final String lat = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LAT));
            final String lon = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LONG));
            final String title =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_title));
            final String desc = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_DESCRIPTION));
            final String ImageAddress =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_IMAGE));

            TextView EditreportTitle = (TextView) findViewById(R.id.EditreportTitle);
            EditreportTitle.setText(title);

            TextView EditreportDescription = (TextView) findViewById(R.id.EditreportDescription);
            EditreportDescription.setText(desc);

            ImageView ImageView = (ImageView)  findViewById(R.id.EditimageView);


            Bitmap bitmap;
            //BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(ImageAddress);
            ImageView.setImageBitmap(bitmap);


            // always close the cursor
            cursor.close();

            Button EditDeleteButton = (Button) findViewById(R.id.EditDeleteButton);
            EditDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {

                    getContentResolver().delete(MyReportContentProvider.CONTENT_URI ,ReportTable.COLUMN_ID + " = " + reportId , null);
                    finish();

                }

            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_detail, menu);
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
}
