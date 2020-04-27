package bkp.com.registrationformandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput, nationalityInput;
    private Button create_account;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        addressInput = findViewById(R.id.address_input);
        nationalityInput = findViewById(R.id.nationality_input);

        create_account = (Button)findViewById(R.id.create_account);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

    }

    private void createUser() {

        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();
        final String nationality = nationalityInput.getText().toString();

        if(!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("") && !nationality.equals("")){

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, While we checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference();


                        String uid = mAuth.getCurrentUser().getUid();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("uid",uid);
                        userMap.put("name",name);
                        userMap.put("phone",phone);
                        userMap.put("email",email);
                        userMap.put("address",address);
                        userMap.put("nationality",nationality);

                        ref.child("Users").child(uid).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    loadingBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    Toast.makeText(RegisterActivity.this, "Please fill the valid credientials.at update", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this, "Please fill the valid credientials. at create", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });





        }else {
            loadingBar.dismiss();
            Toast.makeText(this, "Please complete the credientials.", Toast.LENGTH_SHORT).show();
        }


    }

}
