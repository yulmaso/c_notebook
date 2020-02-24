package com.example.notebook.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.notebook.screens.note.NoteActivity;
import com.example.notebook.R;

public class DeleteDialogFragment extends DialogFragment implements View.OnClickListener {

    private NoteActivity noteActivity;

    public DeleteDialogFragment(NoteActivity noteActivity){
        this.noteActivity = noteActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Delete");
        View v = inflater.inflate(R.layout.dialog_delete_layout, null);
        v.findViewById(R.id.cancel_dialog_button).setOnClickListener(this);
        v.findViewById(R.id.delete_dialog_button).setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_dialog_button:{
                noteActivity.deleteNote();
                dismiss();
                break;
            }
            case R.id.cancel_dialog_button:{
                dismiss();
                break;
            }
        }
    }
}
