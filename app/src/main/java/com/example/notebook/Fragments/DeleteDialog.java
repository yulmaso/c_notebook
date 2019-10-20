package com.example.notebook.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.notebook.R;

public class DeleteDialog extends DialogFragment implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Delete");
        View v = inflater.inflate(R.layout.delete_dialog_layout, null);
        v.findViewById(R.id.cancel_dialog_button).setOnClickListener(this);
        v.findViewById(R.id.delete_dialog_button).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {

    }
}
