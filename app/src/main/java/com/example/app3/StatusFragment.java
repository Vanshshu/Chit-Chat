package com.example.app3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app3.model.StausModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class StatusFragment extends Fragment {


    public StatusFragment (){
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_status, container, false);
        ImageView Img_status=v.findViewById(R.id.img_statuscamera);
        Img_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in,10);
            }
        });

//get all status from database and bind into recyclerview
        ArrayList<StausModel> status=new ArrayList<>();
        RecyclerView recycler_status=v.findViewById(R.id.recycler_status);
        StatusAdapte adapte=new StatusAdapte(getContext(),status,getActivity().getSupportFragmentManager());

        recycler_status.setAdapter(adapte);
        recycler_status.setLayoutManager(new LinearLayoutManager(getContext()));
       //get data of ststus abd bind into array
        FirebaseDatabase.getInstance().getReference().child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                status.clear();
                for (DataSnapshot data: snapshot.getChildren()){
                    StausModel s=new StausModel();
                    s.id=data.getKey();
                    s.name=data.child("name").getValue(String.class);
                    s.date=data.child("date").getValue(String.class);
                    ArrayList<String> urls=new ArrayList<>();
                    for (DataSnapshot pic:data.child("images").getChildren())
                    {
                        urls.add(pic.child("url").getValue(String.class));
                    }
                    s.statusurls=urls;
                    status.add(s);
                }
                adapte.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
Toast.makeText(getContext(),"Erroe occured", Toast.LENGTH_LONG);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10)
        {
            Random r=new Random();
            String Filename= FirebaseAuth.getInstance().getCurrentUser().getUid()+""+r.ints(100000);
            FirebaseStorage.getInstance().getReference().child("status").child(Filename).putFile(data.getData())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //now the ststus uploading
                                    FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String name=snapshot.child("name").getValue(String.class);
                                                    HashMap<String,Object> data=new HashMap<>();
                                                    data.put("name",name);
                                                    SimpleDateFormat date=new SimpleDateFormat("hh:mm aa");
                                                    data.put("date",date.format(new Date()));
                                                    FirebaseDatabase.getInstance().getReference().child("status")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(data);
                                                    HashMap<String,Object> images=new HashMap<>();
                                                    images.put("url",uri.toString());
                                                    FirebaseDatabase.getInstance().getReference().child("status")
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child("images").push().updateChildren(images);
                                                    Toast.makeText(getContext(),"status updated",Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                 Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            });
                        }
                    });


        }
    }
}