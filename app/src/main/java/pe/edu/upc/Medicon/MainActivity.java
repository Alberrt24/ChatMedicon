package pe.edu.upc.Medicon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView messagesTextView;
    EditText inputEditText;
    Button sendButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        messagesTextView = findViewById(R.id.messagesTextView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputEditText.getText().toString();
                messagesTextView.append(Html.fromHtml("<p><b>Tu:</b> " + input + "</p>"));
                inputEditText.setText("");

                getResponse(input);
            }
        });

    }

    private void getResponse(String input) {

        String workspaceId = "6b9bb3eb-82ed-43c7-9371-10f243e2843e";
        String urlAssistant = "https://gateway.watsonplatform.net/assistant/api/v1/workspaces/"+workspaceId+"/message";
        String authentication = "-HVHFcFQkTIDOwq4kF89r0AF61A721_A4wkRWOuQXNeI";

        //creo la estructura json de input del usuario
        JSONObject inputJsonObject = new JSONObject();
        try {
            inputJsonObject.put("text",input);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("input", inputJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(urlAssistant)
                .addHeaders("Content-Type","application/json")
                .addHeaders("Authorization","Basic " + authentication)
                .addJSONObjectBody(jsonBody)
                .setPriority(Priority.HIGH)
                .setTag(getString(R.string.app_name))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String outputAssistant = "";

                        //parseo la respuesta del json
                        try {
                            String outputJsonObject = response.getJSONObject("output").getJSONArray("text").getString(0);
                            messagesTextView.append(Html.fromHtml("<p><b>Chatbot:</b> " + outputJsonObject + "</p>"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context,"Error de conexión", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
