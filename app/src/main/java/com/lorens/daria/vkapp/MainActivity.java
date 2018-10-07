package com.lorens.daria.vkapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.*;
import com.vk.sdk.api.model.*;
import com.vk.sdk.util.VKUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private String[] scope = new String[]{VKScope.FRIENDS, VKScope.MESSAGES, VKScope.WALL};
    private ListView listView;
    private Button showMessage;
    private VKList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
//
//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        Log.d(TAG, "onCreate: " + Arrays.toString(fingerprints));
        VKSdk.login(this, scope);

        showMessage = (Button) findViewById(R.id.showMessage);
        showMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                        VKList<VKApiDialog> list = getMessagesResponse.items;

                        ArrayList<String> messages = new ArrayList<>();
                        ArrayList<String> users = new ArrayList<>();

                        for (VKApiDialog msg : list){
                            users.add(String.valueOf(MainActivity.this.list.getById(msg.message.user_id)));
                            messages.add(msg.message.body);
                        }

                        listView.setAdapter(new CustomAdapter(MainActivity.this, users, messages, list,0));
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {

                VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name"));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        list = (VKList) response.parsedModel;

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_expandable_list_item_1, list);

                        listView.setAdapter(arrayAdapter);
                    }
                });
                Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
