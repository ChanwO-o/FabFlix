package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

	private EditText etUsername;
	private EditText etPassword;
	private TextView tvLoginMessage;
	private Button bLogin;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// upon creation, inflate and initialize the layout
		setContentView(R.layout.activity_login);
		etUsername = findViewById(R.id.etUsername);
		etPassword = findViewById(R.id.etPassword);
		tvLoginMessage = findViewById(R.id.tvLoginMessage);
		bLogin = findViewById(R.id.bLogin);
		/**
		 * In Android, localhost is the address of the device or the emulator.
		 * To connect to your machine, you need to use the below IP address
		 * **/
//		url = "http://10.0.2.2:8080/cs122b-spring20-project2-login-cart-example/api/";
		url = "https://18.209.31.65:8443/cs122b-spring20-team-131/api/";

		//assign a listener to call a function to handle the user request when clicking a button
		bLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				login();
			}
		});
	}

	public void login() {

		tvLoginMessage.setText("Trying to login");
		// Use the same network queue across our application
		final RequestQueue queue = NetworkManager.sharedManager(this).queue;
		//request type is POST
		final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				//TODO should parse the json response to redirect to appropriate functions.
				Log.d("LoginActivity.login()", "success: " + response);
				//initialize the activity(page)/destination
				Intent listPage = new Intent(LoginActivity.this, MovieListActivity.class);
				//without starting the activity/page, nothing would happen
				startActivity(listPage);
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// error
						Log.d("LoginActivity.login()", "error: " + error.toString());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Post request form data
				final Map<String, String> params = new HashMap<>();
				params.put("username", etUsername.getText().toString());
				params.put("password", etPassword.getText().toString());

				return params;
			}
		};

		// !important: queue.add is where the login request is actually sent
		queue.add(loginRequest);

	}
}
