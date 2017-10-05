package com.android.evernotelogin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cgj on 05/10/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {


    private ArrayList<String>  mTitlesDataset;
    private ArrayList<String>  mContentDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextViewTitle;
        public TextView mTextViewContent;
        public ViewHolder(View v) {
            super(v);
            mTextViewTitle = v.findViewById(R.id.title_tv);
            mTextViewContent = v.findViewById(R.id.content_tv);;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> titlesData, ArrayList<String> contentData) {
        mTitlesDataset = titlesData;
        mContentDataset = contentData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_detail, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.d("DEBUG", "Binding: "+mTitlesDataset.get(position));
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextViewTitle.setText(mTitlesDataset.get(position));
        holder.mTextViewContent.setText(mContentDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTitlesDataset.size();
    }
}
