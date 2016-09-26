package com.tali.admin.gak.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tali.admin.gak.R;

public class ListCursorAdapter extends CursorRecyclerViewAdapter<ListCursorAdapter.ViewHolder> {

    OnItemClickListener mItemClickListener;

    public ListCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        public TextView mNo;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.m_title);
            mNo = (TextView) view.findViewById(R.id.m_no);
            mTextView.setOnClickListener(this);
            mNo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                String temp = mTextView.getText().toString();
                mItemClickListener.onItemClick(itemView, getPosition(),temp);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, String title);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.mNo.setText("Билет номер: "+cursor.getString(0));
        viewHolder.mTextView.setText(cursor.getString(1));
    }

}