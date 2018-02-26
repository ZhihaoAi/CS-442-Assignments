package com.ai.zhihao.converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final String F2C = "Fahrenheit to Celsius";
    private String selected = F2C;
    private String history = "";

    private static final String TAG = "CONVERT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void radioClicked(View v) {
        selected = ((RadioButton) v).getText().toString();
    }

    public void convert(View v) {
        Log.d(TAG, "convert: in convert");
        EditText ptInput = findViewById(R.id.PlainTextInput);
        double value = Double.parseDouble(ptInput.getText().toString());
        Log.d(TAG, "convert: value = " + value);

        double answer;
        if (this.selected.equals(this.F2C)) {
            answer = (value - 32.0) * 5.0 / 9.0;
            history = "F to C: " + value + " -> " + String.format("%.1f", answer) + "\n" + history;
        } else {
            answer = (value * 9.0 / 5.0) + 32.0;
            history = "C to F: " + value + " -> " + String.format("%.1f", answer) + "\n" + history;
        }
        Log.d(TAG, "convert: answer = " + answer);

        TextView tvResult = findViewById(R.id.TextViewResult);
        tvResult.setText(String.format("%.1f", answer));

        TextView tvHistory = findViewById(R.id.TextViewHistory);
        tvHistory.setText(history);

    }
}
