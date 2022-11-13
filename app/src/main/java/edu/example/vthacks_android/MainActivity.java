package edu.example.vthacks_android;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText inputPhone;
    private TimePicker timePicker;
    private Button addBtn;
    private Button delBtn;
    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @SuppressLint("HandlerLeak")
    private final Handler uiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            Toast toast = null;
            switch (msg.what) {
                case 0:
                    toast = Toast.makeText(getApplicationContext(), "Successfully Set up!", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 1:
                    toast = Toast.makeText(getApplicationContext(), "You have set up. Please add reminder again after deleting the record.", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 2:
                    toast = Toast.makeText(getApplicationContext(), "Fail to add reminder", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 3:
                    toast = Toast.makeText(getApplicationContext(), "Successfully delete", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                case 4:
                    toast = Toast.makeText(getApplicationContext(), "Fail to delete", Toast.LENGTH_SHORT);
                    toast.show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        inputPhone = findViewById(R.id.input_phone);
        timePicker = findViewById(R.id.time_picker);
        addBtn = findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                String phone = inputPhone.getText().toString();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                Log.println(Log.DEBUG, "demo", "test");
                try {
                    sendRequest(phone, hour, minute);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        delBtn = findViewById(R.id.del_btn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = inputPhone.getText().toString();
                Log.println(Log.DEBUG, "demo", "test");
                try {
                    sendDelete(phone);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendRequest(String phonenumber, int hour, int minute) throws IOException {
        String url = "http://10.0.2.2:8080/messageInfo";
//        String url = "https://posthere.io/506a-4281-8bd4";
        Gson gson = new Gson();

        String json = "{\"hour\":" + hour + ",\"minute\":" + minute + ",\"phoneNumber\":\"" + phonenumber + "\"}";
        RequestBody formBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();

                    Log.d("demo", "onResponse:" + body);
                    if (body.contains("200")) {
                        msg.what = 0;
                        uiHandler.sendMessage(msg);
                    } else if (body.contains("401")) {
                        msg.what = 1;
                        uiHandler.sendMessage(msg);
                    }

                } else {
                    msg.what = 2;
                    uiHandler.sendMessage(msg);
                }
            }
        });
    }

    private void sendDelete(String phoneNumber) throws IOException {
        String url = "http://10.0.2.2:8080/messageInfo/del";
//        String url = "https://posthere.io/506a-4281-8bd4";

        String json = "{\"phoneNumber\":\"" + phoneNumber + "\"}";
        RequestBody formBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Message msg = new Message();
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                    Log.d("demo", "onResponse:" + body);
                    if (body.contains("200")) {
                        msg.what = 3;
                        uiHandler.sendMessage(msg);
                    } else if (body.contains("401")) {
                        msg.what = 4;
                        uiHandler.sendMessage(msg);
                    }
                } else {
                    msg.what = 4;
                    uiHandler.sendMessage(msg);
                }
            }
        });
    }
}