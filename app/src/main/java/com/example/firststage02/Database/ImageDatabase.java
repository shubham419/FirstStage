package com.example.firststage02.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ImageEntity.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {

    public abstract ImageDao getImage();

    private static ImageDatabase ImageDB;

    // synchronized is use to avoid concurrent access in multithred environment
    public static /*synchronized*/ ImageDatabase getInstance(Context context) {
        if (null == ImageDB) {
            ImageDB = buildDatabaseInstance(context);
        }
        return ImageDB;
    }

    private static ImageDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                ImageDatabase.class,
                "image.db").allowMainThreadQueries().build();
    }

    public  void cleanUp(){
        ImageDB = null;
    }

}
