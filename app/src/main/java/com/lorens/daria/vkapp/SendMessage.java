package com.lorens.daria.vkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SendMessage extends Activity {
    //    ArrayList<String> inList = new ArrayList<>();
//    ArrayList<String> outList = new ArrayList<>();
    private Messages messages;
    int id = 0;

    EditText text;
    Button send;
    ListView listView;
    CustomAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        id = getIntent().getIntExtra("id", 0);
        messages = (Messages) getIntent().getSerializableExtra("messages");

        text = (EditText) findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.listMsg);

        adapter = new CustomAdapter(this, messages,1);
        listView.setAdapter(adapter);
        scrollMyListViewToBottom();

        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, id,
                        VKApiConst.MESSAGE, text.getText().toString()));

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        text.setText("");
                        refreshMessages();
                    }
                });
            }
        });
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void refreshMessages() {
        messages.clear();
        final VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id ));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                    VKApiMessage[] msg = new VKApiMessage[array.length()];
                    for (int i = 0; i < array.length(); i++ ){
                        VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
                        msg[i] = mes;
                    }
                    for (VKApiMessage mess : msg){
                        Message message = new Message();
                        message.setMsg(mess.body);
                        message.setUser_id(mess.user_id);
                        message.setAt(mess.date);
                        message.setOut(mess.out);
                        messages.addMessage(message);
                    }
                    messages.setup();
                    adapter.notifyDataSetChanged();
//                    listView.setAdapter(adapter);
//                    listView.notify();
                    listView.setSelection(adapter.getCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}