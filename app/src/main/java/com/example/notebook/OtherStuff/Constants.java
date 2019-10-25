package com.example.notebook.OtherStuff;

import java.text.SimpleDateFormat;

public class Constants {
    public static final String LOG_TAG = "myLogs";

    public static final int REQUEST_CODE_NEWNOTE = 1;
    public static final int REQUEST_CODE_UPDATENOTE = 2;

    public static final int RESULT_DELETE = 2;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat timeToWriteFormat = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat timeToShowFormat = new SimpleDateFormat("HH:mm:ss");
}
