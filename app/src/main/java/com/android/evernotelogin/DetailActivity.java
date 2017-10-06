package com.android.evernotelogin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;

public class DetailActivity extends AppCompatActivity {

    private EvernoteNoteStoreClient mNoteStoreClient;
    private String mTitle;
    private String mContent;
    private TextView contentTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        contentTV = (TextView) findViewById(R.id.content_tv);

        mTitle = getIntent().getStringExtra("Title");
        mContent = getIntent().getStringExtra("Content");
        setTitle(mTitle);
        contentTV.setText(mContent);

        mNoteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_detail, menu);

        MenuItem menuCancel = menu.findItem(R.id.menu_cancel);

        menuCancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                DetailActivity.this.setResult(RESULT_CANCELED);
                DetailActivity.this.finish();

                return true;
            }
        });

        return  super.onCreateOptionsMenu(menu);
    }
}
