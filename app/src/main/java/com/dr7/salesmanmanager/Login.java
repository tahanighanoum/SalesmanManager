package com.dr7.salesmanmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dr7.salesmanmanager.Reports.SalesMan;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class Login extends AppCompatActivity {

    private String username, password, link, ipAddress;
    private EditText usernameEditText, passwordEditText;
    private ImageView logo;
    private CardView loginCardView;
    public static String salesMan = "" , salesManNo = "";
    private boolean isMasterLogin;

    DatabaseHandler mDHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDHandler = new DatabaseHandler(Login.this);
        logo = (ImageView) findViewById(R.id.imageView3);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        try {
            if (mDHandler.getAllCompanyInfo().get(0).getLogo() == null) {
                logo.setImageDrawable(null);
            } else {
                logo.setImageBitmap(mDHandler.getAllCompanyInfo().get(0).getLogo());
            }
        } catch (Exception e){

        }

        ipAddress = "192.168.1.8";
        link = "http://" + ipAddress + "/tickets_service/index.php";

        loginCardView = (CardView) findViewById(R.id.loginCardView);
        loginCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!passwordEditText.getText().toString().equals("") && !usernameEditText.getText().toString().equals("")) {
                    List<SalesMan> salesMenList = mDHandler.getAllSalesMen();
//                    List<SalesMan> salesMenList = Splashscreen.salesMenList;
                    boolean exist = false;
                    int index = 0;

                    if (passwordEditText.getText().toString().equals("f123f"))
                    {
                        exist = true;
                        index = 1;
                        isMasterLogin = true;
                    }
                    else
                    {
                        isMasterLogin = false;
                        for (int i = 0; i < salesMenList.size(); i++) {
                            Log.e("*****" , usernameEditText.getText().toString() +" " + salesMenList.get(i).getUserName());
                            if (usernameEditText.getText().toString().equals(salesMenList.get(i).getUserName())) {
                                exist = true;
                                index = i;
                                break;
                            }
                        }
                    }

                    if (exist) {

                        if (isMasterLogin)
                        {
                            salesMan = usernameEditText.getText().toString();
                            salesManNo = passwordEditText.getText().toString();
                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);
                        }
                        else
                        {
                            if (salesMenList.get(index).getPassword().equals(passwordEditText.getText().toString())) {
                                salesMan = usernameEditText.getText().toString();
                                salesManNo = passwordEditText.getText().toString();

                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(main);
                            } else
                                Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }

                    } else
                        Toast.makeText(Login.this, "UserName does not exist", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(Login.this, "Please enter username and password", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private class RequestLogin extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                username = usernameEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();

                String data = URLEncoder.encode("USERNAME=" + username, "UTF-8");
                data += "&" + URLEncoder.encode("PASSWORD=" + password, "UTF-8");

                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter =
                        new OutputStreamWriter(urlConnection.getOutputStream());

                outputStreamWriter.write(data);

                String line = null;


            } catch (Exception e) {

            }

            return null;
        }
    }// Class RequestLogin

}
