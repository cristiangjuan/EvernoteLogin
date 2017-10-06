package com.android.evernotelogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;

public class AddNoteActivity extends AppCompatActivity {

    private EvernoteNoteStoreClient mNoteStoreClient;
    private String mTitle;
    private String mContent;
    private EditText titleET;
    private EditText contentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        titleET = (EditText) findViewById(R.id.titulo_et);
        contentET = (EditText) findViewById(R.id.content_et);

        setTitle("Nueva Nota");

        mNoteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_add_note, menu);

        MenuItem menuOk = menu.findItem(R.id.menu_ok);

        menuOk.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                mTitle = titleET.getText().toString();
                if (mTitle.charAt(mTitle.length()-1) == ' ') {

                    mTitle = mTitle.substring(0,(mTitle.length()-1));
                }
                mContent = contentET.getText().toString();

                makeNote(mNoteStoreClient, mTitle, mContent);

                return true;
            }
        });

        MenuItem menuCancel = menu.findItem(R.id.menu_cancel);

        menuCancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AddNoteActivity.this.setResult(RESULT_CANCELED);
                AddNoteActivity.this.finish();

                return true;
            }
        });

        return  super.onCreateOptionsMenu(menu);
    }

    public Note makeNote(EvernoteNoteStoreClient noteStore, final String noteTitle, String noteBody) {

        Log.d("DEBUG", "makeNote: "+noteTitle);

        String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
        nBody += "<en-note>" + noteBody + "</en-note>";

        // Create note object
        Note ourNote = new Note();
        ourNote.setTitle(noteTitle);
        ourNote.setContent(EvernoteUtil.NOTE_PREFIX+noteBody+EvernoteUtil.NOTE_SUFFIX);

//        Note note = null;
//
//        try {
//            note = noteStore.createNote(ourNote);
//        } catch (EDAMUserException e) {
//            e.printStackTrace();
//        } catch (EDAMSystemException e) {
//            e.printStackTrace();
//        } catch (EDAMNotFoundException e) {
//            e.printStackTrace();
//        } catch (TException e) {
//            e.printStackTrace();
//        }

        noteStore.createNoteAsync(ourNote, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {

                Log.d("DEBUG","Note "+noteTitle+" added");

                AddNoteActivity.this.setResult(RESULT_OK);
                AddNoteActivity.this.finish();
            }

            @Override
            public void onException(Exception exception) {

                Log.e("ERR", exception.toString());
            }
        });

        // Return created note object
        return ourNote;

    }
}
