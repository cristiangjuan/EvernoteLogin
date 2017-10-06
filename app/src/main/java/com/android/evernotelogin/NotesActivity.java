package com.android.evernotelogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private ArrayList<String> mArrayTitles;
    private ArrayList<String> mArrayContent;
    private int mIndexNote = 0;
    private int mNotesInPage = 0;
    private EvernoteNoteStoreClient mNoteStoreClient;
    private int sortBy = SORT_BY_MODIFICATION;

    private static final int SORT_BY_CREATION = 0;
    private static final int SORT_BY_MODIFICATION = 1;
    private static final int SORT_BY_ALPHABETICAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        setTitle("Notas");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
              DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mNoteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        recoverNotes(mNoteStoreClient, NoteSortOrder.UPDATED.getValue());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_notes, menu);

        MenuItem sortByCreationTimeMenu = menu.findItem(R.id.menu_created);

        sortByCreationTimeMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (sortBy != SORT_BY_CREATION) {

                    sortBy = SORT_BY_CREATION;
                    recoverNotes(mNoteStoreClient, NoteSortOrder.CREATED.getValue());
                }
                else{

                    Toast toast = Toast.makeText(NotesActivity.this, "Already sorted by creation", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                    toast.show();
                }

                return true;
            }
        });

        MenuItem sortByModifiedTimeMenu = menu.findItem(R.id.menu_modified);

        sortByModifiedTimeMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (sortBy != SORT_BY_MODIFICATION) {

                    sortBy = SORT_BY_MODIFICATION;
                    recoverNotes(mNoteStoreClient, NoteSortOrder.UPDATED.getValue());
                }
                else {

                    Toast toast = Toast.makeText(NotesActivity.this, "Already sorted by modification", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                    toast.show();
                }

                return true;
            }
        });

        MenuItem sortByAlphaMenu = menu.findItem(R.id.menu_alpha);

        sortByAlphaMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if (sortBy != SORT_BY_ALPHABETICAL) {

                    sortBy = SORT_BY_ALPHABETICAL;
                    recoverNotes(mNoteStoreClient, NoteSortOrder.TITLE.getValue());
                }
                else{

                    Toast toast = Toast.makeText(NotesActivity.this, "Already sorted alphabetically", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 150);
                    toast.show();
                }

                return true;
            }
        });

        return  super.onCreateOptionsMenu(menu);
    }

    private void recoverNotes(final EvernoteNoteStoreClient noteStoreClient, int filterType) {

        Log.d("DEBUG","Recovering notes by: "+sortBy);

        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        int pageSize = 50;

        NoteFilter filter = new NoteFilter();
        filter.setOrder(filterType);
        filter.setAscending(true);

        NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(true);

        noteStoreClient.findNotesMetadataAsync(filter, 0, pageSize, spec, new EvernoteCallback<NotesMetadataList>() {
            @Override
            public void onSuccess(NotesMetadataList result) {

                int matchingNotes = result.getTotalNotes();
                mNotesInPage = result.getNotes().size();
                mIndexNote = 0;

                mArrayTitles = new ArrayList<String>();
                mArrayContent = new ArrayList<String>();

                for (NoteMetadata note : result.getNotes()) {
                    Log.d("DEBUG",note.getTitle());

                    mArrayTitles.add(note.getTitle());

                    noteStoreClient.getNoteAsync(note.getGuid(), true, false, false, false, new EvernoteCallback<Note>() {
                        @Override
                        public void onSuccess(Note result) {

                            Document document = Jsoup.parse(result.getContent());
                            String content = document.text();

                            Log.d("DEBUG", content);

                            if (content.length() > 260){

                                mArrayContent.add(content.substring(0,260)+"...");
                            }
                            else{

                                mArrayContent.add(content);
                            }

                            if (mIndexNote == mNotesInPage-1){

                                updateAdapter();
                            }
                            mIndexNote++;
                        }

                        @Override
                        public void onException(Exception exception) {

                        }
                    });
                }
            }

            @Override
            public void onException(Exception exception) {

                Log.e("ERR", exception.getMessage());
            }
        });

    }

    private void updateAdapter() {

        progressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        mAdapter = new MyAdapter(mArrayTitles, mArrayContent);
        mRecyclerView.setAdapter(mAdapter);

        String sortByString = "";
        switch (sortBy) {

            case SORT_BY_CREATION:

                sortByString = "by creation";
                break;

            case SORT_BY_MODIFICATION:

                sortByString = "by modification";
                break;

            case SORT_BY_ALPHABETICAL:

                sortByString = "alphabetically";
                break;
        }
        Toast toast = Toast.makeText(NotesActivity.this, "Sorted "+sortByString, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 150);
        toast.show();
    }
}
