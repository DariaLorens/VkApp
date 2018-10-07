package com.lorens.daria.vkapp;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<String> users, messages;
    private Context context;
    private VKList<VKApiDialog> list;
    private Messages msgs;
    int flag;

    public CustomAdapter(Context context, ArrayList<String> users, ArrayList<String> messages, VKList<VKApiDialog> list,int flag) {
        this.users = users;
        this.messages = messages;
        this.context = context;
        this.list = list;
        this.flag = flag;
    }

    public CustomAdapter(Context context, Messages messages,int flag) {
        this.context = context;
        this.msgs = messages;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        if(flag ==0)
            return users.size();
        else
            return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.style_list_view, null);


        setData.user_name = (TextView) view.findViewById(R.id.user_name);
        setData.msg = (TextView) view.findViewById(R.id.msg);

        if(flag==0){
            setData.user_name.setText(users.get(position));
            setData.msg.setText(messages.get(position));
        }else{
            Message message = msgs.getMessage(position);
            setData.user_name.setText(String.valueOf(message.getUser_name()));
            setData.msg.setText(String.valueOf(message.getMsg()));
            if(message.isOut()){
                setData.user_name.setGravity(Gravity.RIGHT);
                setData.user_name.setText("Вы:");
                setData.msg.setGravity(Gravity.RIGHT);
            }else{
                setData.user_name.setGravity(Gravity.LEFT);

                setData.msg.setGravity(Gravity.LEFT);
            }
        }



        if(list != null)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> inList = new ArrayList<>();
                    final ArrayList<String> outList = new ArrayList<>();
                    final int id = list.get(position).message.user_id;
                    msgs = new Messages();

                    final VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id ));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            try {
                                JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                                VKApiMessage [] msg = new VKApiMessage[array.length()];
                                for (int i = 0; i < array.length(); i++ ){
                                    VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
                                    msg[i] = mes;
                                }
                                for (VKApiMessage mess : msg){
                                    if (mess.out){
                                        outList.add(mess.body);
                                    } else {
                                        inList.add(mess.body);
                                    }
                                    Message message = new Message();
                                    message.setMsg(mess.body);
                                    message.setUser_id(mess.user_id);
                                    message.setAt(mess.date);
                                    message.setOut(mess.out);
                                    message.setUser_name(users.get(position));
                                    msgs.addMessage(message);
                                }
                                msgs.setup();
                                context.startActivity(new Intent(context, SendMessage.class)
                                        .putExtra("id",id)
                                        .putExtra("messages", msgs));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });

        return view;
    }

    public class SetData{
        TextView user_name, msg;
    }
}
