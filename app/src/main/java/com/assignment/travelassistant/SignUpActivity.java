package com.assignment.travelassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btnSignUp)
    AppCompatButton btnSignUp;
    @BindView(R.id.link_login)
    TextView linkLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        init();

    }

    public static boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public boolean checkValid(String name, String email, String password){
        if (name.length() == 0) {
            Toast.makeText(SignUpActivity.this,"Please input name",Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.length() == 0) {
            Toast.makeText(SignUpActivity.this,"Please input email",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(SignUpActivity.this,"Invalid email",Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length() == 0) {
            Toast.makeText(SignUpActivity.this,"Please input password",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    public void init() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (checkValid(name,email,password)) {
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest userProfileChangeRequest
                                                = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SignUpActivity.this, "Create account successfully!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
