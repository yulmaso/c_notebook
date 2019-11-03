package com.example.notebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.R;

import org.w3c.dom.Text;

import java.util.List;
import java.util.zip.Inflater;

public class HashtaBarAdapter extends RecyclerView.Adapter<HashtaBarAdapter.HashtagBarViewHolder> {

    private List<Hashtag> hashtags;
    private Context context;
    private int color;

    public HashtaBarAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Hashtag> hashtags){
        this.hashtags = hashtags;
        if (hashtags.size() != 0) {
            notifyDataSetChanged();
            color = hashtags.get(0).getColor();
        }
    }

    public void clearItems(){
        hashtags.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HashtagBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hashtag_bar, parent, false);
        return new HashtagBarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagBarViewHolder holder, int position) {
        holder.bind(hashtags.get(position));
    }

    @Override
    public int getItemCount() {
        return hashtags.size();
    }

    class HashtagBarViewHolder extends RecyclerView.ViewHolder {
        private TextView hashtag_tv;

        public HashtagBarViewHolder(@NonNull View itemView) {
            super(itemView);
            hashtag_tv = itemView.findViewById(R.id.item_hashtag_bar_tv);
            itemView.setBackgroundColor(color);
        }

        public void bind(Hashtag hashtag){
            hashtag_tv.setText(hashtag.getHashtag());
        }
    }
}
