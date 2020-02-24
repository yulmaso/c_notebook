package com.example.notebook.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.notebook.storage.Constants.LOG_TAG;

public class TestTagsListAdapter extends RecyclerView.Adapter<TestTagsListAdapter.TagsViewHolder> {

//TODO: make context menu with delete item on every list item

    private List<Hashtag> tags = new ArrayList<>();
    private OnSmthingClickListener onSmthingClickListener;

    public TestTagsListAdapter(OnSmthingClickListener onSmthingClickListener){
        this.onSmthingClickListener = onSmthingClickListener;
    }

    public interface OnSmthingClickListener{
        void onHashtagClick(Hashtag hashtag);
    }

    public void setItems(Collection<Hashtag> tags) {
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    public void clearItems() {
        tags.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hashtag, viewGroup, false);
        return new TagsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestTagsListAdapter.TagsViewHolder notesViewHolder, int i) {
        notesViewHolder.bind(tags.get(i));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagsViewHolder extends RecyclerView.ViewHolder {
        private TextView tag_name;

        public TagsViewHolder(@NonNull View itemView) {
            super(itemView);
            tag_name = itemView.findViewById(R.id.tag_name);

            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Hashtag hashtag = tags.get(getLayoutPosition());
                        onSmthingClickListener.onHashtagClick(hashtag);
                    }
                });
            } catch (NullPointerException e){
                Log.d(LOG_TAG,e.getMessage());
            }
        }

        public void bind(Hashtag hashtag) {
            tag_name.setText(hashtag.getHashtag());
        }
    }
}
