package com.example.app3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app3.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Messenger extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Intent in=getIntent();
        String name=in.getStringExtra("name");
        String receiveruid=in.getStringExtra("uid");
        String senderuid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        ImageView profile=findViewById(R.id.profile_image);
        Picasso.get()
                .load(in.getStringExtra("pic"))
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(profile);

        TextView txt_receivername=findViewById(R.id.txt_receivername);
        txt_receivername.setText(name);

        EditText et_mymessage=findViewById(R.id.et_mymessage);
        ImageView img_sendmessage=findViewById(R.id.img_sendmessage);
        //btn send message event/
        img_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  String mymessage=et_mymessage.getText().toString();
                  if (!mymessage.isEmpty())
                  {
                      HashMap<String,String> message=new HashMap<>();
                      message.put("msg",mymessage);
                      message.put("senderid",senderuid);

                      SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd");
                      SimpleDateFormat time=new SimpleDateFormat("hh:m aa");
                      message.put("date", date.format(new Date()));
                      message.put("time", time.format(new Date()));

           FirebaseDatabase.getInstance().getReference().child("message")
                   .child(senderuid+receiveruid).push().setValue(message);
           FirebaseDatabase.getInstance().getReference().child("message")
                   .child(receiveruid+senderuid).push().setValue(message);
          et_mymessage.setText("");
                  }
                  else
                  {
                      Toast.makeText(Messenger.this,"don't click me,without msg",Toast.LENGTH_SHORT);
                  }
            }
        });
                 ///back btn event//
        ImageView img_back1=findViewById(R.id.img_back1);
        img_back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Messenger.super.onBackPressed();}
        });
        //select all message between sender+receiver and bind in Recyclerview
        ArrayList<MessageModel> messages=new ArrayList<>();
        RecyclerView recycler_msg=findViewById(R.id.recycler_msg);
        MessageListAdapter adapter=new MessageListAdapter(this,messages,senderuid+receiveruid);
        recycler_msg.setAdapter(adapter);
        recycler_msg.setLayoutManager(new LinearLayoutManager(this));




        FirebaseDatabase.getInstance().getReference().child("message")
                .child(senderuid+receiveruid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                             for (DataSnapshot data: snapshot.getChildren())
                             {
                                                            MessageModel m=new MessageModel();
                                                            m.id=data.getKey();
                                                            m.message= data.child("msg").getValue().toString();
                                                            m.senderid=data.child("senderid").getValue().toString();
                                                            m.date= data.child("date").getValue().toString();
                                                            m.time=data.child("time").getValue().toString();

                                                            messages.add(m); 
                             }
                             adapter.notifyDataSetChanged();
                            if(messages.size()>3)
                                recycler_msg.scrollToPosition(messages.size()-1);
                             Toast.makeText(Messenger.this,messages.size()+"",Toast.LENGTH_SHORT).show();
                              
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })  ;
    }
    public void logout(View V)
    {
        FirebaseAuth.getInstance().signOut();
        Intent in=new Intent(this,Login.class);
        startActivity(in);
    }
}