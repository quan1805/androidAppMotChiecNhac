package huce.fit.motchiecnhac;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingApp extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.proBar);
        textView = findViewById(R.id.txtLoading);

        progressBar.setMax(100);
        //progressBar.setScaleY(3f);

        progressAnimation();
    }
    public void progressAnimation(){
        ProgressBarAnimaition anim = new ProgressBarAnimaition(this, progressBar, textView, 0f, 100f);
        anim.setDuration(5000);
        progressBar.setAnimation(anim);
    }
}
