package com.example.notebook.utils;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.notebook.POJO.Hashtag;
import com.example.notebook.R;

import java.util.ArrayList;

import static com.example.notebook.storage.Constants.LOG_TAG;

public class NoteEditText extends AppCompatEditText {

    private final Context context;
    private int note_id;

    private Spannable spannable;

    private final HashtagListener hashtagListener;

    private int colorSwitcher;

    public NoteEditText(Context context, int note_id, HashtagListener hashtagListener) {
        super(context);
        this.context = context;
        this.note_id = note_id;
        this.hashtagListener = hashtagListener;
        init();
    }

    public interface HashtagListener {
        void onEditHashtag();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        spannable = getText();
        if (spannable.length() != 0) findAllHashtags();
    }

    private void init() {
        colorSwitcher = 0;
        initHashtagListener();
    }

    private void initHashtagListener() {
        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean onHashtag = false;

                int i = start;
                try {
                    i = start - 1;
                    while (symbolIsLetter(s.charAt(i))) {
                        i--;
                    }
                } catch (IndexOutOfBoundsException e1) {
                    Log.d(LOG_TAG, e1.getMessage());
                }
                try {
                    if (s.charAt(i) == '#') {
                        onHashtag = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.d(LOG_TAG, e.getMessage());
                }

                if (onHashtag) {
                    hashtagListener.onEditHashtag();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean symbolIsLetter(char l) {
        return Character.isLetter(l);
    }

    private int getColor() {
        int color;
        switch (colorSwitcher) {
            case 0:
                color = context.getResources().getColor(R.color.hashtag_zero);
                break;
            case 1:
                color = context.getResources().getColor(R.color.hashtag_one);
                break;
            case 2:
                color = context.getResources().getColor(R.color.hashtag_two);
                break;
            case 3:
                color = context.getResources().getColor(R.color.hashtag_three);
                break;
            case 4:
                color = context.getResources().getColor(R.color.hashtag_four);
                break;
            case 5:
                color = context.getResources().getColor(R.color.hashtag_five);
                break;
            default:
                color = context.getResources().getColor(R.color.hashtag_zero);
        }
        colorSwitcher++;
        if (colorSwitcher == 6) colorSwitcher = 0;
        return color;
    }

    private ArrayList<Hashtag> findAllHashtags() {
        colorSwitcher = 0;
        ArrayList<Hashtag> tags = new ArrayList<>();
        boolean hashtagIsFound = false;
        StringBuilder tagText = new StringBuilder();
        int startOfSegment = 0, endOfSegment = 0;
        int color = getColor();
        for (int i = 0; i < spannable.length(); i++) {
            String letter = Character.toString(spannable.charAt(i));

            //adds hashtag only on the next step of cycle, when it stands on the next symbol right after the hashtag
            if (!symbolIsLetter(letter.charAt(0)) && tagText.length() != 0) {
                endOfSegment = i - 1;
                int[] segment = new int[2];
                segment[0] = startOfSegment;
                segment[1] = endOfSegment;

                Hashtag h = new Hashtag(note_id, tagText.toString(), color);
                tags.add(h);
                tagText.delete(0, tagText.length());

                hashtagIsFound = false;
                color = getColor();
            }

            if (letter.equals("#")) {
                startOfSegment = i;
                hashtagIsFound = true;
            }

            if (hashtagIsFound) {
                spannable.setSpan(new ForegroundColorSpan(color), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (!letter.equals("#")) tagText.append(letter);
            }

            //if the note ends with a hashtag then we need to add it on the last step
            if (i == spannable.length() - 1 && hashtagIsFound && tagText.length() != 0) {
                endOfSegment = i;
                int[] segment = new int[2];
                segment[0] = startOfSegment;
                segment[1] = endOfSegment;

                Hashtag h = new Hashtag(note_id, tagText.toString(), color);
                tags.add(h);
                tagText.delete(0, tagText.length());
            }
        }
        return tags;
    }

    public ArrayList<Hashtag> getHashtags() {
        return findAllHashtags();
    }
}
