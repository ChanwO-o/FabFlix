package com.parkchanwoo.fabflixmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kusu.loadingbutton.LoadingButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

	private EditText etUsername, etPassword;
	private TextView tvLoginMessage;
	private LoadingButton bLogin;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// upon creation, inflate and initialize the layout
		setContentView(R.layout.activity_login);
		goFullScreen();

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
				hideKeyboard();
				goFullScreen();
				bLogin.showLoading();
				login();
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		goFullScreen();
	}

	public void login() {
		// Use the same network queue across our application
		final RequestQueue queue = NetworkManager.sharedManager(this).queue;
		//request type is POST
		final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("fabflixandroid", "login response: " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = (String) jsonObject.get("status");
					if (status.equals("fail")) {
						Log.d("fabflixandroid", "login failed!");
						bLogin.hideLoading();
						tvLoginMessage.setText("Login failed!");
						etUsername.setError("Invalid credentials");
						etPassword.setError("Invalid credentials");
					}
					else {
						bLogin.hideLoading();
						Log.d("fabflixandroid", "login success!");
						//initialize the activity(page)/destination
						Intent listPage = new Intent(LoginActivity.this, MainPageActivity.class);
						//without starting the activity/page, nothing would happen
						startActivity(listPage);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("fabflixandroid", "error: " + error.toString());
						tvLoginMessage.setText("Login Timeout: Please try again");
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// Post request form data
				final Map<String, String> params = new HashMap<>();
				params.put("email", etUsername.getText().toString());
				params.put("password", etPassword.getText().toString());

				return params;
			}
		};

		loginRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// !important: queue.add is where the login request is actually sent
		queue.add(loginRequest);
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(this);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void goFullScreen() {
		int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		getWindow().getDecorView().setSystemUiVisibility(uiOptions);
	}
}
