package activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataParser extends AsyncTask<Void, Void, Boolean> {
    static String longitude;
    static String latitude;
    static JSONArray jsonArr;
    Context c;
    static String jsonData;
    static String tag;
    ListView lv;

    ProgressDialog pd;

    static ArrayList<String> spacecrafts = new ArrayList<>();
    static ArrayList<String> locations = new ArrayList<>();

    public DataParser(Context c, String jsonData, ListView lv) {
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd = new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Pasring..Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        pd.dismiss();
        if (result) {
            ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_list_item_1, spacecrafts);
            lv.setAdapter(adapter);


        }
    }

    private Boolean parseData() {
        try {
            jsonArr = new JSONArray(jsonData);
            JSONObject jo;

            spacecrafts.clear();
            for (int i = 0; i < jsonArr.length(); i++) {

                jo = jsonArr.getJSONObject(i);

                String name = jo.getString("Name")+" ";
                name += jo.getString("rating")+ " ";
                String latt = jo.getString("Latitude");
                String lonn = jo.getString("Longitude");

                GPSTracker gps = new GPSTracker (c);
                double latitude22 = gps.getLatitude();
                double longitude22= gps.getLongitude();
                double endLatitude= Double.parseDouble(latt);
                double endLongitude= Double.parseDouble(lonn);

                float[] results = new float[1];
                Location.distanceBetween(latitude22, longitude22, endLatitude, endLongitude, results);
                float distance = results[0]/1000;
                if(distance<10){
                    String dist= String.valueOf(distance);
                    dist="0"+dist.substring(0,8);
                    name += dist;
                    spacecrafts.add(name);
                }
                else {
                    String dist = String.valueOf(distance);
                    name += dist;
                    spacecrafts.add(name);
                }

            }
            Collections.sort(spacecrafts, new Comparator<String>() {
                public int compare(String v1, String v2) {
                    return v1.substring(23).compareTo(v2.substring(23));
                }
            });

            return true;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}