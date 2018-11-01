package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;


import static activity.DataParser.jsonArr;
import static activity.DataParser.jsonData;
import static activity.DataParser.spacecrafts;

public class MainActivity extends Activity {

    private TextView txtName;
    static ListView lv;
    private TextView txtEmail;
    private Button btnLogout;
    private Button rating;
    private Button location;
    private SQLiteHandler db;
    private SessionManager session;
    final static String urlAddress="http://192.168.1.11/android_login_api/landmarks.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtName = (TextView) findViewById(R.id.name);
        lv= (ListView) findViewById(R.id.lv) ;
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        rating=(Button) findViewById(R.id.rating);
        location=(Button) findViewById(R.id.location);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());


        // session manager
        session = new SessionManager(getApplicationContext());
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(spacecrafts, new Comparator<String>() {
                    public int compare(String v1, String v2) {
                        return v1.substring(19,23).compareTo(v2.substring(19,23));
                    }
                });
                lv.invalidateViews();

            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MapsActivity.class);

                startActivity(i);

                finish();

            }
        });




        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jo;
                try {
                    jsonArr= new JSONArray(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    for (int i = 0; i < jsonArr.length(); i++) {


                        jo = jsonArr.getJSONObject(i);
                        if(jo.getString("Name").equals(spacecrafts.get(position).substring(0,spacecrafts.get(position).length()-14))){

                            DataParser.latitude=jo.getString("Latitude");
                            DataParser.longitude=jo.getString("Longitude");
                            DataParser.tag=jo.getString("Name");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        ArrayAdapter adapter=new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,spacecrafts);
        lv.setAdapter(adapter);
        new Downloader(MainActivity.this,urlAddress,lv).execute();



    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }




}