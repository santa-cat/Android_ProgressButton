package com.example.santa.pathanim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ProgressButton progressButton;
    ProgressButton errButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressButton = (ProgressButton) findViewById(R.id.progress_button);
        progressButton.setOnProgressClickListener(new ProgressButton.OnProgressClickListener() {
            @Override
            public void onStart() {
                ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(6000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        progressButton.setPercent(val);
                    }
                });
                animator.start();
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
            }
        });

        errButton = (ProgressButton) findViewById(R.id.progress_err);
        errButton.setOnProgressClickListener(new ProgressButton.OnProgressClickListener() {
            @Override
            public void onStart() {
                ValueAnimator animator = ObjectAnimator.ofFloat(0f, 1.0f);
                animator.setDuration(2000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        if (val >= 1.0f) {
                            val = -val;
                        }
                        errButton.setPercent(val);
                    }
                });


                animator.start();
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();

            }
        });

        final ProgressButton succRect = (ProgressButton) findViewById(R.id.progress_succRect);
        assert succRect != null;
        succRect.setOnProgressClickListener(new ProgressButton.OnProgressClickListener() {
            @Override
            public void onStart() {
                ValueAnimator animator = ObjectAnimator.ofInt(0, 1);
                animator.setDuration(5000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (int) animation.getAnimatedValue();

                        if (val == 1) {
                            succRect.setPercent(val);
                        }
                    }
                });
                animator.start();
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();

            }
        });


        final ProgressButton errRect = (ProgressButton) findViewById(R.id.progress_errRect);
        assert errRect != null;
        errRect.setOnProgressClickListener(new ProgressButton.OnProgressClickListener() {
            @Override
            public void onStart() {
                ValueAnimator animator = ObjectAnimator.ofInt(0, -1);
                animator.setDuration(5000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (int) animation.getAnimatedValue();
                        if (val == -1) {
                            errRect.setPercent(val);
                        }
                    }
                });
                animator.start();

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
