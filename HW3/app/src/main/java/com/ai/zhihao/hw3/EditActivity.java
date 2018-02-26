package com.ai.zhihao.hw3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    private static int position;

    private EditText title;
    private EditText content;

    private Note note;
    private String oldTitle;
    private String oldContent;

    private Boolean saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.editTitle);
        content = findViewById(R.id.editContent);

        content.setMovementMethod(new ScrollingMovementMethod());
        content.setTextIsSelectable(true);

        Intent intent = getIntent();
        if (intent.hasExtra("Edit Existing Note")) {
            note = (Note) intent.getSerializableExtra("Edit Existing Note");
            position = intent.getIntExtra("Position", -2);
            title.setText(note.getTitle());
            content.setText(note.getContent());
            oldTitle = note.getTitle();
            oldContent = note.getContent();
            Log.d(TAG, "onCreate: title = " + note.getTitle());
            Log.d(TAG, "onCreate: content = " + note.getContent());
        } else {
            if (intent.hasExtra("Edit New Note")) {
                position = intent.getIntExtra("Edit New Note", -2);
                note = new Note();
                oldTitle = note.getTitle();
                oldContent = note.getContent();
                Log.d(TAG, "onCreate: new note");
            }
        }
    }

    private Note loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.tmp_file_name));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("title")) {
                    note.setTitle(reader.nextString());
                } else if (name.equals("content")) {
                    note.setContent(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        if (saved == true) { // Currently displayed saved to file
            note = loadFile();
            if (note != null) {
                title.setText(note.getTitle());
                content.setText(note.getContent());
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        note.setTitle(title.getText().toString());
        note.setTime(DateFormat.getDateTimeInstance().format(new Date()));
        note.setContent(content.getText().toString());
        Log.d(TAG, "onPause: Content = " + note.getContent());
        saveFile();
        saved = true;
        super.onPause();
    }

    private void saveFile() {
        Log.d(TAG, "saveFile: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.tmp_file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("title").value(note.getTitle());
            writer.name("time").value(note.getTime());
            writer.name("content").value(note.getContent());
            writer.endObject();
            writer.close();

            /// For log
            StringWriter sw = new StringWriter();
            writer = new JsonWriter(sw);
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("title").value(note.getTitle());
            writer.name("time").value(note.getTime());
            writer.name("content").value(note.getContent());
            writer.endObject();
            writer.close();
            Log.d(TAG, "saveFile: JSON:\n" + sw.toString());
            ///

//            Toast.makeText(this, "Saved to file", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.save:
                if (title.getText().toString().equals("")) {
                    Toast.makeText(this, "Untitled note not saved", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: no title");
                    setResult(RESULT_CANCELED);
                } else if (title.getText().toString().equals(oldTitle)
                        && content.getText().toString().equals(oldContent)) {
                    Log.d(TAG, "onOptionsItemSelected: No change");
                    setResult(RESULT_CANCELED);
                } else {
                    note.setTitle(title.getText().toString());
                    Log.d(TAG, "onOptionsItemSelected: Title = " + title.getText());
                    note.setTime(DateFormat.getDateTimeInstance().format(new Date()));
                    note.setContent(content.getText().toString());
                    Intent data = new Intent();
                    data.putExtra("Note saved", note);
                    data.putExtra("Position", position);
                    setResult(RESULT_OK, data);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if ((title.getText().toString().equals(oldTitle) && content.getText().toString().equals(oldContent))
                || title.getText().toString().equals("")) {
            if (title.getText().toString().equals("")) {
                Toast.makeText(this, "Untitled note not saved", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_CANCELED);
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Your note is not saved!\nSave note '" + title.getText().toString() + "'?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            note.setTitle(title.getText().toString());
                            Log.d(TAG, "onOptionsItemSelected: Title = " + title.getText());
                            note.setTime(DateFormat.getDateTimeInstance().format(new Date()));
                            note.setContent(content.getText().toString());
                            Intent data = new Intent();
                            data.putExtra("Note saved", note);
                            data.putExtra("Position", position);
                            setResult(RESULT_OK, data);
                            EditActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_CANCELED);
                            EditActivity.this.finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
