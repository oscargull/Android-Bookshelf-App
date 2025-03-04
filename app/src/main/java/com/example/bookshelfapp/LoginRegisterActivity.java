package com.example.bookshelfapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.bookshelfapp.db.SQLiteBooksHelper;
import com.example.bookshelfapp.fragments.LoginRegisterFragment;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class LoginRegisterActivity extends AppCompatActivity  implements View.OnClickListener, LoginRegisterFragment.OnFragmentEventListener {

    private static final String ID_CANAL ="canal_id";
    private static final int NOTIF_ID = 1;

    SQLiteDatabase db;
    SQLiteBooksHelper DBhelper;

    Button btnLogin;
    Button btnSignUp;

    NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permisos();

        NotificationChannel notificationChannel = new NotificationChannel(ID_CANAL, "canal1",NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("canal de notificaciones");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        //setTheme(R.style.Base_Theme_BookshelfApp_NoActionBar);
        db = openOrCreateDatabase("Bookshelf", Context.MODE_PRIVATE,null);
        DBhelper= new SQLiteBooksHelper(this);

        btnLogin=findViewById(R.id.btnLogin);
        btnSignUp=findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginRegisterFragment myFragment = new LoginRegisterFragment();

        Bundle args = new Bundle();
        if (v == btnLogin) {
            args.putString("layout", "login");
        } else if (v == btnSignUp) {
            args.putString("layout", "signup");
        }
        myFragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragmentLayout, myFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentEvent(Bundle args) throws Exception {
        String username = args.getString("username");
        String password = args.getString("password");
        Boolean isRegistro = args.getBoolean("signup_mode");
        if(isRegistro) {
            String email = args.getString("email");
            String[] hash= hashing(password);
            DBhelper.registerUser(username,email,hash);
            notifRegistro(username);
            //Toast.makeText(this,"Usuario registrado",Toast.LENGTH_SHORT).show();
        }else{
            if(DBhelper.logIn(this,username,password)){
                Intent nextAct = new Intent(LoginRegisterActivity.this, MainActivity.class);
                nextAct.putExtra("user",username);
                startActivity(nextAct);
            }
        }
    }


    public String[] hashing(String passw){
        char[] password = passw.toCharArray();
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        String[] hash = new String[2];
        hash[0] = Base64.getEncoder().encodeToString(salt);

        try {
            SecretKey key = pbkdf2(password, salt);
            hash[1] = Base64.getEncoder().encodeToString(key.getEncoded());
        }catch (Exception e){
            e.printStackTrace();
        }
        return hash;
    }

    public SecretKey pbkdf2(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSha1");
        PBEKeySpec spec = new PBEKeySpec(password, salt, 4096, 256);
        return factory.generateSecret(spec);
    }

    private void permisos() {
        if(checkSelfPermission("android.permission.RECEIVE_SMS")!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{"android.permission.RECEIVE_SMS"},1);
        }
    }

    public void notifRegistro(String user){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,ID_CANAL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Usuario "+user+" registrado")
                .setOngoing(true);

        notificationManager.notify(NOTIF_ID, builder.build());
    }

}