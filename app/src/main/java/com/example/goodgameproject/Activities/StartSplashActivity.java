package com.example.goodgameproject.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.goodgameproject.R;

public class StartSplashActivity extends AppCompatActivity {

    private AppCompatImageView splash_IMG_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_splash);
        findViews();
        startAnimation(splash_IMG_logo);
    }

    private void startAnimation(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        view.setY(-height/2.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.setAlpha(0.0f);

        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationY(0)
                .setDuration(4000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        TransactToLoginActivity();

                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                }
                );
    }

    private void TransactToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
            splash_IMG_logo=findViewById(R.id.splash_IMG_logo);
    }


}