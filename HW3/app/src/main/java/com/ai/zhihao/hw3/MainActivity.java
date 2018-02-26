package com.ai.zhihao.hw3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int EDIT_REQUEST_CODE = 666;

    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AsyncLoadAll asyncLoad = new AsyncLoadAll(this);
        asyncLoad.execute(getString(R.string.file_name), getString(R.string.encoding));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    private void saveAll() {
        Log.d(TAG, "saveAll: ");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));

            writer.setIndent("  ");
            writer.beginArray();
            for (Note note : notesList) {
                writer.beginObject();
                writer.name("title").value(note.getTitle());
                writer.name("time").value(note.getTime());
                writer.name("content").value(note.getContent());
                writer.endObject();
            }
            writer.endArray();
            writer.close();

            /// For log
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, getString(R.string.encoding)));

            StringBuilder saved = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null){
                saved.append(tmp);
            }
            br.close();
            Log.d(TAG, "saveAll: " + saved);
            ///
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        saveAll();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Intent info = new Intent(this, InfoActivity.class);
                startActivity(info);
                return true;
            case R.id.edit:
                Intent edit = new Intent(this, EditActivity.class);
                edit.putExtra("Edit New Note", -1);
                startActivityForResult(edit, EDIT_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note m = notesList.get(pos);

        Intent edit = new Intent(MainActivity.this, EditActivity.class);
        edit.putExtra("Edit Existing Note", m);
        edit.putExtra("Position", pos);
        startActivityForResult(edit, EDIT_REQUEST_CODE);

        Log.d(TAG, "onClick: " + m.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("Note saved");
                int position = data.getIntExtra("Position", -2);
                if (position == -2) {
                    Log.d(TAG, "onActivityResult: Position is wrong");
                } else if (position >= 0){
                    notesList.remove(position);
                }
                notesList.add(0, note);
                notesAdapter.notifyDataSetChanged();
                Log.d(TAG, "onActivityResult: add note = " + note.toString());
                Log.d(TAG, "onActivityResult: position = " + position);
            } else if (resultCode == RESULT_CANCELED){
                Log.d(TAG, "onActivityResult: RESULT CODE = RESULT_CANCELED. Note not saved." );
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        final int pos = recyclerView.getChildLayoutPosition(view);
        final Note m = notesList.get(pos);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete note '" + m.getTitle() + "'?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(view.getContext(), "Note '" + m.getTitle() + "' deleted", Toast.LENGTH_SHORT).show();
                        notesList.remove(pos);
                        notesAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void whenAsyncIsDone(JSONArray array){
        Log.d(TAG, "whenAsyncIsDone: ");
        try {
            JSONObject tmp;
            for (int i = 0; i < array.length(); i++) {
                tmp = array.getJSONObject(i);
                notesList.add(new
                        Note(tmp.getString("title"),
                        tmp.getString("time"),
                        tmp.getString("content")));
                Log.d(TAG, "whenAsyncIsDone: " + tmp.toString());
            }
            notesAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "whenAsyncIsDone: error when getting JSON object");
        }
    }
}
