package com.example.notebook.Adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notebook.POJO.Note;
import com.example.notebook.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    //TODO: make context menu with delete item on every list item

    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener onNoteClickListener;
    private Context context;

    public NotesAdapter(OnNoteClickListener onNoteClickListener, Context context){
        this.onNoteClickListener = onNoteClickListener;
        this.context = context;
    }

    public interface OnNoteClickListener{
        void onNoteClick(Note note);
    }

    public void setItems(Collection<Note> notes){
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public void clearItems(){
        notes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item, viewGroup, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder notesViewHolder, int i) {
        notesViewHolder.bind(notes.get(i));
        setAnimation(notesViewHolder.itemView);
    }

    private void setAnimation(View view){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.animation_fall_down);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView header_tv;
        private TextView date_tv;
        private TextView time_tv;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            header_tv = itemView.findViewById(R.id.header_tv);
            date_tv = itemView.findViewById(R.id.date_tv);
            time_tv = itemView.findViewById(R.id.time_tv);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Note note = notes.get(getLayoutPosition());
                    onNoteClickListener.onNoteClick(note);
                }
            });
            itemView.setOnCreateContextMenuListener(this);

        }

        public void bind(Note note){
            header_tv.setText(note.getText());
            date_tv.setText(note.getDate());
            String time = note.getTime();
            time = time.substring(0, time.length() - 3);
            time_tv.setText(time);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }
}
