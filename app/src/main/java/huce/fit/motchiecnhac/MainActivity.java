package huce.fit.motchiecnhac;


import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button bt, bt2, btnStart, btnStop;
    EditText editText;
    private File filePath = new File(Environment.getExternalStorageDirectory() + "/ABC.pdf");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=findViewById(R.id.button);
        bt2=findViewById(R.id.button2);
        btnStart=findViewById(R.id.buttonStart);
        editText=findViewById(R.id.editText);
        btnStop = findViewById(R.id.buttonStop);
        
        try {
            filePath.createNewFile();
            Toast.makeText(this, "File Created Successfully!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCreateFile(view);
                System.out.println("Clicked");
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonDeleteFile(view);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStartService();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickStopService();
            }
        });

    }

    private void clickStopService() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
    }

    private void clickStartService() {

    }


    public void buttonCreateFile(View view){
        try {
            System.out.println("add");
            filePath.createNewFile();

            Toast.makeText(this, "File Created Successfully!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonDeleteFile(View view){
        if (filePath.exists()){
            filePath.delete();
            Toast.makeText(this, "File Deleted Successfully!", Toast.LENGTH_LONG).show();
        }
    }
}