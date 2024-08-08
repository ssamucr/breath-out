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
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.widget.RelativeLayout;

import java.util.Objects;

public class DiagramActivity extends AppCompatActivity {

    private LinearLayout wordBankLayout;
    private RelativeLayout blankSpacesLayout;
    private Button endBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diagram);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        wordBankLayout = findViewById(R.id.wordBankLayout);
        blankSpacesLayout = findViewById(R.id.blankSpacesLayout);

        setDragAndDropListeners();

        endBtn = findViewById(R.id.endBtn);

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiagramActivity.this, ResultadoActivity.class);

                startActivity(intent);
            }
        });
    }

    private void setDragAndDropListeners() {
        for (int i = 0; i < wordBankLayout.getChildCount(); i++) {
            View word = wordBankLayout.getChildAt(i);
            word.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                    String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

                    v.startDragAndDrop(dragData, myShadow, null, 0);
                    return true;
                }
            });
        }

        for (int i = 0; i < blankSpacesLayout.getChildCount(); i++) {
            View blankSpace = blankSpacesLayout.getChildAt(i);
            blankSpace.setOnDragListener(new MyDragListener());
        }
    }

    private class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    ((TextView) v).setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.cardBackground));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    ((TextView) v).setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.cardBackground));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String dragData = item.getText().toString();

                    if (v.getTag().equals(dragData)) {
                        ((TextView) v).setText(dragData);
                        ((TextView) v).setBackgroundColor(Color.GREEN);
                        if (checkAllCorrect()) {
                            endBtn.setVisibility(View.VISIBLE);
                        }
                        showPopup("¡Correcto!", android.R.color.holo_green_dark, "good");
                    } else {
                        ((TextView) v).setBackgroundColor(Color.WHITE);
                        showPopup("Inténtalo de nuevo.", android.R.color.white, "tryagain");
                    }
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    ((TextView) v).setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.cardBackground));
                    v.invalidate();
                    return true;
                default:
                    break;
            }
            return true;
        }
    }

    private boolean checkAllCorrect() {
        for (int i = 0; i < blankSpacesLayout.getChildCount(); i++) {
            View blankSpace = blankSpacesLayout.getChildAt(i);
            String tag = (String) blankSpace.getTag();
            String text = ((TextView) blankSpace).getText().toString().trim();
            if (!tag.equals(text)) {
                return false;
            }
        }
        return true;
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
            MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.correct_sound);
            soundPlayer.start();
            imageView.setImageResource(R.drawable.ok);
            new Handler().postDelayed(() -> soundPlayer.release(), 1000);
        } else {
            MediaPlayer soundPlayer = MediaPlayer.create(this, R.raw.tryagain_sound);
            soundPlayer.start();
            imageView.setImageResource(R.drawable.tryagain);
            new Handler().postDelayed(() -> soundPlayer.release(), 1000);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handler para hacer desaparecer el popup después de 2 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);
    }
}