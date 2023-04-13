package com.example.gpt;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    // creating variables on below line.
    TextView responseTV;
    TextView questionTV;
    TextInputEditText queryEdt;

    String url = "https://api.openai.com/v1/completions";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing variables on below line.
        responseTV = findViewById(R.id.idTVResponse);
        questionTV = findViewById(R.id.idTVQuestion);
        queryEdt = findViewById(R.id.idEdtQuery);

        // adding editor action listener for edit text on below line.
        queryEdt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
                responseTV.setText("Please wait..");
                // validating text
                if (queryEdt.getText().toString().length() > 0) {
                    // calling get response to get the response.
                    getResponse(queryEdt.getText().toString());
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    private void getResponse(String query) {
        // setting text on for question on below line.
        questionTV.setText(query);
        queryEdt.setText("");
        // creating a queue for request queue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // creating a json object on below line.
        JSONObject jsonObject = new JSONObject();
        // adding params to json object.
        try {
            jsonObject.put("model", "text-davinci-003");
            jsonObject.put("prompt", query);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 100);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // on below line making json object request.
        JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonObject,
                response -> {
                    // on below line getting response message and setting it to text view.
                    try {
                        String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                        responseTV.setText(responseMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                // adding on error listener
                error -> Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error)
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                // adding headers on below line.
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer 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");
                return params;
            }
        };

        // Set a retry policy for the request
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue
        queue.add(postRequest);
    }
}
