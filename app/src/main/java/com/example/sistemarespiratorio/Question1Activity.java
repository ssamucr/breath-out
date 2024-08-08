package com.example.sistemarespiratorio;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class Question1Activity extends AppCompatActivity {

    private LinearLayout option1, option2, option3;
    private Button nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        nextButton = findViewById(R.id.nextButton);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Question1Activity.this, Question2Activity.class);
                startActivity(intent);
            }
        });

    }

    private void checkAnswer(int selectedOption) {
        resetOptions();
        LinearLayout selectedLayout;
        if (selectedOption == 1) {
            selectedLayout = option1;
        } else if (selectedOption == 2) {
            selectedLayout = option2;
        } else {
            selectedLayout = option3;
        }
        selectedLayout.setSelected(true);

        int correctOption = 2;
        if (selectedOption == correctOption) {
            // showToast("¡Correcto!", Color.GREEN);
            MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.correct_sound);
            soundPlayer.start();
            showPopup("¡Correcto, muy bien!", android.R.color.holo_green_dark, "good");
            nextButton.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> soundPlayer.release(), 1000);
        } else {
            // showToast("Inténtalo de nuevo.", Color.RED);
            MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.tryagain_sound);
            soundPlayer.start();
            showPopup("Casi, inténtalo de nuevo.", android.R.color.white, "tryagain");
            nextButton.setVisibility(View.GONE);
            new Handler().postDelayed(() -> soundPlayer.release(), 1000);
        }

    }

    private void showToast(String message, int textColor) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1000);
    }

    private void showPopup(String message, int color, String image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_custom, null);
        builder.setView(dialogLayout);

        TextView messageTextView = dialogLayout.findViewById(R.id.messageTextView);
        ImageView imageView = dialogLayout.findViewById(R.id.imageView);
        messageTextView.setText(message);
        messageTextView.setTextColor(getResources().getColor(color));

        if (Objects.equals(image, "good")) {
            imageView.setImageResource(R.drawable.ok);
        } else {
            imageView.setImageResource(R.drawable.tryagain);
        }


        AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);
    }

    private void resetOptions() {
        option1.setSelected(false);
        option2.setSelected(false);
        option3.setSelected(false);
    }
}