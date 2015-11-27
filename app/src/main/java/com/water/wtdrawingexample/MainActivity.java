package com.water.wtdrawingexample;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.water.wtdrawing.WTDrawingView;

public class MainActivity extends AppCompatActivity {

    private WTDrawingView wtDrawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wtDrawingView = (WTDrawingView) findViewById(R.id.wtDrawingView);
    }

    public void toggleDrawingMode(View view) {
        Button eraser = (Button)view;
        if ("ERASER".equalsIgnoreCase(eraser.getText().toString())) {
            wtDrawingView.setEraserMode(true);
            eraser.setText("Pencil");
        }
        else {
            wtDrawingView.setEraserMode(false);
            eraser.setText("Eraser");
        }

    }

    public void changeColor(View view) {
        Button button = (Button) view;
        Drawable buttonBackground = button.getBackground();
        ColorDrawable buttonColor = (ColorDrawable) button.getBackground();
        wtDrawingView.setStrokeColor(buttonColor.getColor());
    }

    public void undo(View view) {
        wtDrawingView.undo();
    }

    public void clear(View view) {
        wtDrawingView.clear();
    }
}
