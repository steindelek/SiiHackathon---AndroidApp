package pl.legalnyplener.POSTERRA;



import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private String downloaded_data;
    private boolean mode;
    Button goToLoginButton, goInButton, tryLoginButton;
    EditText login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new PobierzPlakaty().execute();

        mode = true;
        goToLoginButton = (Button) findViewById(R.id.LoginButton);
        goInButton = (Button) findViewById(R.id.GoInButton);
        tryLoginButton = (Button) findViewById(R.id.LoginUser);
        login = (EditText) findViewById(R.id.LoginEditText);
        password = (EditText) findViewById(R.id.PasswordEditText);

        RecyclerView recyclerView = findViewById(R.id.BackgroundRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MyBackgroundAdapter(this, recyclerView));
        recyclerView.getAdapter().notifyDataSetChanged();

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    public void GoInButtonClick(View view){
        if(!downloaded_data.isEmpty()){
            Intent intent = new Intent(this, PosterListActivity.class);
            intent.putExtra("data", downloaded_data);
            startActivity(intent);
        }else {
            Toast toast = Toast.makeText(this, R.string.checkConnection, Toast.LENGTH_SHORT);
            toast.show();
            new PobierzPlakaty().execute();
        }

    }

    public void LogInButtonClick(View view){
        ChangeLogin();
    }

    public class PobierzPlakaty extends AsyncTask<Void, Void, String>{
        private String LINK_URL = "http://plakat.legalnyplener.pl/SiiHackaton/pobierz_plakaty.php";;
        @Override
        protected void onPostExecute(String s) {
            downloaded_data = s;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return DownloadData(LINK_URL);
        }

        private String DownloadData(String adress) {
            String data = new String();
            try {
                URL url = new URL(adress);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String temp;
                while ((temp = bufferedReader.readLine()) != null){
                    data += temp;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    public void ChangeLogin(){
        if(mode){
            goToLoginButton.setVisibility(View.GONE);
            goInButton.setVisibility(View.GONE);
            tryLoginButton.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
            mode = false;
        }else {
            goToLoginButton.setVisibility(View.VISIBLE);
            goInButton.setVisibility(View.VISIBLE);
            tryLoginButton.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            mode = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(mode){
            super.onBackPressed();
        }else {
            ChangeLogin();
        }
    }

    public void logInUser(View view){
        if(String.valueOf(login.getText()).equals("user") & String.valueOf(password.getText()).equals("pass")){
            Intent intent = new Intent(this, PosterListActivity.class);
            intent.putExtra("data", downloaded_data);
            startActivity(intent);
            finish();
        }
    }
}
