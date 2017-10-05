package com.android.evernotelogin;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String CONSUMER_KEY = "cristiangjuan";
    private static final String CONSUMER_SECRET = "6cda1b18ce7d0322";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    public static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    private TextView textView;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Login");

        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();


        button = (ImageButton) findViewById(R.id.arrow_right_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EvernoteSession.getInstance().authenticate(MainActivity.this);
            }
        });


        if (!EvernoteSession.getInstance().isLoggedIn()) {

            Log.d("DEBUG", "NOT Logged");
        }
        else {

            Log.d("DEBUG", "NOT Logged");
        }



    }

    private void recoverNoteBooks(EvernoteNoteStoreClient noteStoreClient) {
        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            @Override
            public void onSuccess(List<Notebook> result) {
                List<String> namesList = new ArrayList<>(result.size());
                for (Notebook notebook : result) {
                    namesList.add(notebook.getName());
                }
                String notebookNames = TextUtils.join(", ", namesList);
                Toast.makeText(getApplicationContext(), notebookNames + " notebooks have been retrieved", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onException(Exception exception) {
                Log.e("ERR", "Error retrieving notebooks", exception);
            }
        });
    }

    private void recoverNotes(EvernoteNoteStoreClient noteStoreClient) {
        int pageSize = 10;

        NoteFilter filter = new NoteFilter();
        filter.setOrder(NoteSortOrder.UPDATED.getValue());

        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(true);


        noteStoreClient.findNotesMetadataAsync(filter, 0, pageSize, spec, new EvernoteCallback<NotesMetadataList>() {
            @Override
            public void onSuccess(NotesMetadataList result) {

                int matchingNotes = result.getTotalNotes();
                int notesThisPage = result.getNotes().size();

                for (NoteMetadata note : result.getNotes()) {
                    Log.d("DEBUG",note.getTitle());
                }
            }

            @Override
            public void onException(Exception exception) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EvernoteSession.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    // handle success
                    Log.d("DEBUG", "Login OK");
                } else {
                    // handle failure
                    Log.d("DEBUG", "Login FAIL");
                }
                break;

            default:
                Log.d("DEBUG", "DEFAULT");
                super.onActivityResult(requestCode, resultCode, data);

                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent);



                break;
        }
    }
}
