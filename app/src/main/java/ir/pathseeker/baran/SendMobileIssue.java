package ir.pathseeker.baran;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class SendMobileIssue extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mobile_issue);


        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation location = (GsmCellLocation) tel.getCellLocation();
        int lac = location.getLac();
        int cid = location.getCid();
        int psc = location.getPsc();

// Type of the network
        int phoneTypeInt = tel.getPhoneType();
        String phoneType = null;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;
        JSONArray cellList = new JSONArray();
        JSONObject cellObj = new JSONObject();
        JSONObject cellObj2 = new JSONObject();

        try {

            if (phoneType != null) {
                //params.put("radioType", phoneType);


                Log.e("LAC", String.valueOf(lac));
                cellObj.put("LAC", String.valueOf(lac));

                Log.e("CID",String.valueOf(cid));
                cellObj.put("CID", String.valueOf(cid));

                Log.e("PSC",String.valueOf(psc));
                cellObj.put("PSC", String.valueOf(psc));

                Log.e("phoneType",phoneType);
                cellObj.put("phoneType", phoneType);

                Log.e("IMEI",tel.getDeviceId());
                cellObj.put("IMEI", tel.getDeviceId());

                Log.e("Country",tel.getNetworkCountryIso());
                cellObj.put("Country", tel.getNetworkCountryIso());

                Log.e("Oprator id",tel.getNetworkOperator());
                cellObj.put("Oprator id", tel.getNetworkOperator());

                String networkOperator = tel.getNetworkOperator();

                if (networkOperator != null) {
                    int mcc = Integer.parseInt(networkOperator.substring(0, 3));
                    int mnc = Integer.parseInt(networkOperator.substring(3));

                    Log.e("mcc",String.valueOf(mcc));
                    cellObj.put("mcc", String.valueOf(mcc));

                    Log.e("mnc",String.valueOf(mnc));
                    cellObj.put("mnc", String.valueOf(mnc));
                }



                Log.e("NetworkOperatorName",tel.getNetworkOperatorName());
                cellObj.put("NetworkOperatorName", tel.getNetworkOperatorName());

                Log.e("SimOperatorName",tel.getSimOperatorName());
                cellObj.put("SimOperatorName", tel.getSimOperatorName());
                cellObj2.put("mainInformation", cellObj);
                //cellList.put(cellObj2);
            }
        } catch (Exception e) {}

/*
 * The below code doesn't work I think.
 */
        TextView sendmobileissue_text = (TextView) findViewById(R.id.sendmobileissue_text);
        List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
        for (int i = 0; i < neighCells.size(); i++) {
            try {
                JSONObject cellObj3 = new JSONObject();

                NeighboringCellInfo thisCell = neighCells.get(i);
                cellObj3.put("cellId", thisCell.getCid());
                cellObj3.put("psc", thisCell.getPsc());
                cellObj3.put("type", thisCell.getNetworkType());
                cellObj3.put("RSI", thisCell.getRssi());
                /*
                Returns received signal strength or UNKNOWN_RSSI if unknown For GSM, it is in "asu" ranging from 0 to 31 (dBm = -113 + 2*asu) 0 means "-113 dBm or less" and 31 means "-51 dBm or greater" For UMTS, it is the Level index of CPICH RSCP defined in TS 25.125
                 */


                cellList.put(cellObj3);

            } catch (Exception e) {}
        }

        try {
            cellObj2.put("NeighboringCellInfo", cellList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (cellList.length() > 0) {
            try {
                //params.put("cellTowers", cellList);
                Log.e("cellTowers","retriving...");
                Log.e("cellTowers",String.valueOf(cellObj2));

                //sendmobileissue_text.setText(String.valueOf(cellObj2));
            } catch (Exception e) {}

        }
        sendmobileissue_text.setText(String.valueOf(cellObj2));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_mobile_issue, menu);
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
