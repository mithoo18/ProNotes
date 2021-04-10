package com.example.pronotes.note;
import android.content.Intent;
import android.os.Bundle;

import com.example.pronotes.R;
import com.example.pronotes.note.EditNote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class NoteDetails extends AppCompatActivity {
    Intent data;
    TextView title,content;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = findViewById(R.id.noteDetailsTitle);
        content = findViewById(R.id.noteDetailsContent);
        fab = findViewById(R.id.fab);
        data  = getIntent();//intent ko receive

        //scroll
        content.setMovementMethod(new ScrollingMovementMethod());
        //data ko assign kar raha hai local variable
        title.setText(data.getStringExtra("title"));
        content.setText(data.getStringExtra("content"));
        content.setBackgroundColor(getResources().getColor(data.getIntExtra("code",0),null));//color
        //button click karaga tho edit act with data
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), EditNote.class);
                i.putExtra("title",data.getStringExtra("title"));
                i.putExtra("content",data.getStringExtra("content"));
                i.putExtra("noteId",data.getStringExtra("noteId"));
                startActivity(i);
            }
        });
    }
    //back press pa home pa
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home)
    {
        onBackPressed();
    }
    return super.onOptionsItemSelected(item);
    }
}
