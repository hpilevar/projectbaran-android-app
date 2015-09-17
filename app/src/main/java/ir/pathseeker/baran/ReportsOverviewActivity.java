package ir.pathseeker.baran;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

import ir.pathseeker.baran.MyReportContentProvider;
import ir.pathseeker.baran.ReportTable;

/**
 * Created by farid on 4/13/15.
 */
public class ReportsOverviewActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_list);
        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
    }

    // create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.listmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        switch (item.getItemId()) {
            case R.id.insert:
                createReport();
                return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(MyReportContentProvider.CONTENT_URI + "/"
                        + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createReport() {
        //Intent i = new Intent(this, ReportDetailActivity.class);
        //startActivity(i);

    }

    // Opens the second activity if an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        /*
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReportDetailActivity.class);
        Uri ReportUri = Uri.parse(MyReportContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MyReportContentProvider.CONTENT_ITEM_TYPE, ReportUri);

        startActivity(i);
        */

        //Toast.makeText(getApplicationContext(), "id : " + id, Toast.LENGTH_LONG).show();
        Uri ReportUri = Uri.parse(MyReportContentProvider.CONTENT_URI + "/" + id);
        resendReport(ReportUri);
    }

    private void resendReport(final Uri uri) {
        String[] projection = { ReportTable.COLUMN_title, ReportTable.COLUMN_DESCRIPTION, ReportTable.COLUMN_IMAGE, ReportTable.COLUMN_LAT, ReportTable.COLUMN_LONG };
                final Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String DESCRIPTION = cursor.getString(cursor
                    .getColumnIndexOrThrow(ReportTable.COLUMN_DESCRIPTION));

            Toast.makeText(getBaseContext(), DESCRIPTION, Toast.LENGTH_LONG).show();




            final String lat = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LAT));
            final String lon = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_LONG));
            final String title =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_title));
            final String desc = cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_DESCRIPTION));
            final String ImageAddress =  cursor.getString(cursor.getColumnIndexOrThrow(ReportTable.COLUMN_IMAGE));


            RequestParams params = new RequestParams();
            params.put("title",title);
            params.put("body",desc);
            params.put("lat",lat);
            params.put("long",lon);
            try {
                //params.put("userfile",new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/Instagram/test.png"));
                params.put("userfile",new File(ImageAddress));
            } catch (FileNotFoundException e) {
                Log.e("error", e.getMessage());
                e.printStackTrace();
            }

            final ProgressDialog progressBar = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
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
                    Log.w("async", "success!!!!");
                    Log.w("async statusCode", String.valueOf(statusCode));
                    progressBar.hide();
                    Toast.makeText(getApplicationContext(), "با موفقیت ثبت شد", Toast.LENGTH_LONG).show();

                    ContentValues values = new ContentValues();
                    values.put(ReportTable.COLUMN_STATE, getResources().getString(R.string.report_send_success_message));
                    getContentResolver().update(uri, values, null, null);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.w("async", "KHAAAAAAAAAAAAAAAAAAAR!");
                    Log.e("async", String.valueOf(statusCode));
                    progressBar.hide();
                    Toast.makeText(getApplicationContext(), "متاسفانه در روند ثبت با مشکل مواجه شدیم", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }

            });













            // always close the cursor
            cursor.close();


        }
    }


    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { ReportTable.COLUMN_title, ReportTable.COLUMN_IMAGE, ReportTable.COLUMN_STATE};
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.title, R.id.image, R.id.state};
        //Cursor cursor = getContentResolver().query(ReportTable.TABLE_REPORT, null, null, null,null, null, ReportTable.COLUMN_ID + " Desc");
        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.report_row, null, from,
                to, 0);

        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ReportTable.COLUMN_ID, ReportTable.COLUMN_LAT, ReportTable.COLUMN_LONG, ReportTable.COLUMN_title, ReportTable.COLUMN_DESCRIPTION,ReportTable.COLUMN_IMAGE,ReportTable.COLUMN_STATE };
        CursorLoader cursorLoader = new CursorLoader(this,
                MyReportContentProvider.CONTENT_URI, projection, null, null, ReportTable.COLUMN_ID + " Desc");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }


}
