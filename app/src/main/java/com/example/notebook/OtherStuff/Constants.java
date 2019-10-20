package com.example.notebook.OtherStuff;

import java.text.SimpleDateFormat;

public class Constants {
    public static final String LOG_TAG = "myLogs";

    public static final String[] exampleNames = {"Первая заметка", "Вторая заметка", "Третья заметка", "Четвертая заметка", "Пятая заметка", "Шестая заметка", "Седьмая заметка", "Восьмая заметка"};
    public static final String[] exampleTimes = {"14:01", "14:02", "14:03", "14:04", "14:05", "14:06", "14:07", "14:08"};

    public static boolean LOCK;

    public static final int REQUEST_CODE_NEWNOTE = 1;
    public static final int REQUEST_CODE_UPDATENOTE = 2;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat timeToWriteFormat = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat timeToShowFormat = new SimpleDateFormat("HH:mm:ss");
}
