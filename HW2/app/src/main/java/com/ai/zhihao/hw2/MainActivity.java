package com.ai.zhihao.hw2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView lastUpdateTime;
    private EditText notes;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastUpdateTime = findViewById(R.id.lastUpdateTime);
        notes = findViewById(R.id.notes);

        notes.setMovementMethod(new ScrollingMovementMethod());
        notes.setTextIsSelectable(true);

        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        note = loadFile();  // Load the JSON containing the product data - if it exists
        if (note != null) { // null means no file was loaded
            lastUpdateTime.setText("Last Update: " + note.getLastUpdateTime());
            notes.setText(note.getNotes());
        }
        super.onResume();
    }

    private Note loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");
        note = new Note();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("lastUpdateTime")) {
                    note.setLastUpdateTime(reader.nextString());
                } else if (name.equals("notes")) {
                    note.setNotes(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            //Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            note.setLastUpdateTime(DateFormat.getDateTimeInstance().format(new Date()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        note.setLastUpdateTime(DateFormat.getDateTimeInstance().format(new Date()).toString());
        note.setNotes(notes.getText().toString());
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        saveNotes();
        super.onStop();
    }

    private void saveNotes() {
        Log.d(TAG, "saveNotes: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("lastUpdateTime").value(note.getLastUpdateTime());
            writer.name("notes").value(note.getNotes());
            writer.endObject();
            writer.close();

            /// For log
            StringWriter sw = new StringWriter();
            writer = new JsonWriter(sw);
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("lastUpdateTime").value(note.getLastUpdateTime());
            writer.name("notes").value(note.getNotes());
            writer.endObject();
            writer.close();
            Log.d(TAG, "saveNotes: JSON:\n" + sw.toString());
            ///

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
