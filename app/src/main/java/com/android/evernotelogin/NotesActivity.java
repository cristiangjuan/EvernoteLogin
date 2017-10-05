package com.android.evernotelogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        setTitle("Notas");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        recoverNotes(noteStoreClient);

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

                ArrayList<String> arrayTitles = new ArrayList<String>();
                ArrayList<String> arrayContent = new ArrayList<String>();

                for (NoteMetadata note : result.getNotes()) {
                    Log.d("DEBUG",note.getTitle());

                    arrayTitles.add(note.getTitle());
                    arrayContent.add(note.getTitle());
                }

                mAdapter = new MyAdapter(arrayTitles, arrayContent);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onException(Exception exception) {

                Log.e("ERR", exception.getMessage());
            }
        });

    }
}
