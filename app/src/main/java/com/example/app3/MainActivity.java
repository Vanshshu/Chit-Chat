  package com.example.app3;


  import androidx.annotation.NonNull;
  import androidx.appcompat.app.AppCompatActivity;

  import android.app.ProgressDialog;
  import android.content.Intent;
  import android.os.Bundle;
  import android.view.View;
  import android.widget.EditText;
  import android.widget.ImageView;
  import android.widget.RadioButton;
  import android.widget.RadioGroup;
  import android.widget.TextView;
  import android.widget.Toast;

  import com.google.android.gms.tasks.Task;

  import com.google.firebase.auth.AuthResult;

  import java.util.HashMap;

  import com.google.android.gms.tasks.OnCompleteListener;
  import com.google.android.gms.tasks.Task;
  import com.google.firebase.auth.AuthResult;
  import com.google.firebase.auth.FirebaseAuth;
  import com.google.firebase.database.FirebaseDatabase;

  public class MainActivity extends AppCompatActivity {
      TextView txtLogin;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          txtLogin = findViewById(R.id.txtlogin);
          txtLogin.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent in = new Intent(MainActivity.this,Login.class);
                  startActivity(in);
              }
          });
          //click here to register --

          ImageView Register=findViewById(R.id.img_R);
          Register.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  EditText txt_name = findViewById(R.id.txtname);
                  EditText txt_email = findViewById(R.id.txt_gmail);
                  EditText txt_password = findViewById(R.id.txt_password);
                  EditText txt_number = findViewById(R.id.mobno);
                  String name = txt_name.getText().toString();
                  String email = txt_email.getText().toString();
                  String password = txt_password.getText().toString();
                  String number = txt_number.getText().toString();
                  RadioGroup rdb_gender = findViewById(R.id.rdb_gender);
                  RadioButton rdb_female = findViewById(rdb_gender.getCheckedRadioButtonId());
                  String gender = rdb_female.getText().toString();

                  //lets save the data in authentication and realtime database
                  if (!name.isEmpty()&& !email.isEmpty() && !password.isEmpty()&&!gender.isEmpty()) {
                      ProgressDialog dialog=new ProgressDialog(MainActivity.this);
                      dialog.setTitle("please wait..");

                      dialog.show();

                      FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              dialog.dismiss();
                              if (task.isSuccessful()) {
                                  Toast.makeText(MainActivity.this, "user added in auth", Toast.LENGTH_SHORT).show();
                              } else {
                                  Toast.makeText(MainActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                                  FirebaseAuth.getInstance().signOut();
                              }
                              if (task.isSuccessful()) {
                                  String uid = task.getResult().getUser().getUid();
                                  HashMap<String, String> user = new HashMap<>();
                                  user.put("name", name);
                                  user.put("email", email);
                                  user.put("password", password);
                                  user.put("gender", gender);
                                  FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(user);
                                  Toast.makeText(MainActivity.this, "Now! you are Register", Toast.LENGTH_SHORT).show();

                                  FirebaseAuth.getInstance().signOut();
                                  Intent intent = new Intent(MainActivity.this, Login.class);
                                  startActivity(intent);
                              }

                          }
                      });
                  }
                  else {
                      Toast.makeText(MainActivity.this,"Please fill all Fields Properly",Toast.LENGTH_SHORT).show();
                  }
              }

          });

      }
  }