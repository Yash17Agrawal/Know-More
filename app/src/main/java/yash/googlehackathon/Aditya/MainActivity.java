package yash.googlehackathon.Aditya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import yash.googlehackathon.Chat;
import yash.googlehackathon.R;
import yash.googlehackathon.SignInActivity;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,NavigationView.OnNavigationItemSelectedListener{

   public static EditText search_q;
    PrefManager prefManager;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    Button write,read,chat;
    String username;
    SharedPreferences pref;
    TextView tv1,tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager=new PrefManager(this);
        search_q=(EditText)findViewById(R.id.search_q);
        String s=search_q.getText().toString();
        prefManager.set_search(s);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username=pref.getString("USER_DISPLAY_NAME", "Default");
        Log.e("username", username);
        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v=navigationView.getHeaderView(0);
        View v2=navigationView.getHeaderView(1);
        tv1=(TextView)v.findViewById(R.id.name);
        //tv2=(TextView)v2.findViewById(R.id.email);
        tv1.setText(username);
        //tv2.setText(pref.getString("USERNAME","default"));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void search_clicked(View view)
    {
        Log.e("button", "button");

        new HttpAsyncTask().execute();

    }
    public static  String POST(){
        InputStream inputStream = null;
        String result = "";
        try {

            Log.e("clicked","clicked");

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost("http://192.168.43.19:5000/article");

            String json = "";

            // 3. build jsonObject
            String search=search_q.getText().toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("movie", search);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        String var="";
        @Override
        protected String doInBackground(String... urls) {



            var= POST();
            prefManager.set_matter(var);
            return var;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(),var, Toast.LENGTH_LONG).show();
            Intent i=new Intent(MainActivity.this,Search_answers.class);
            startActivity(i);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.aboutus) {
            // Handle the camera action
        } else if (id == R.id.chatroom) {
            Intent i=new Intent(MainActivity.this,Chat.class);
            startActivity(i);


        } else if (id == R.id.home) {
                Intent i =new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else if(id== R.id.logoutnav)
        {
            SharedPreferences yash= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            mAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            // updateUI(null);

                            Intent i = new Intent(getApplicationContext(),SignInActivity.class);
                            SharedPreferences yash= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor ed=yash.edit();
                            ed.putBoolean("signedin", false);
                            ed.putString("ID","");
                            ed.putString("USER_DISPLAY_NAME","");
                            ed.putString("USERNAME","");
                            ed.putString("mode",null);
                            ed.commit();
                            startActivity(i);
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
