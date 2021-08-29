package com.example.firststage02.Database;


import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

@Dao
public interface ImageDao {

    @Insert
    void insert(ImageEntity image);

    @Query("DELETE FROM Image_Database")
    void deleteAll();

    @Query("SELECT * FROM Image_Database")
    List<ImageEntity> getAlImages();


}

//@Database(entities = {ImageEntity.class}, version = 1)
//public abstract class ImageRoomDatabase extends RoomDatabase {
//
//    public abstract ImageDao getImage();
//
//    private static ImageRoomDatabase ImageDB;
//
//    // synchronized is use to avoid concurrent access in multithred environment
//    public static /*synchronized*/ ImageRoomDatabase getInstance(Context context) {
//        if (null == ImageDB) {
//            ImageDB = buildDatabaseInstance(context);
//        }
//        return ImageDB;
//    }
//
//    private static ImageRoomDatabase buildDatabaseInstance(Context context) {
//        return Room.databaseBuilder(context,
//                ImageRoomDatabase.class,
//                "image.db").allowMainThreadQueries().build();
//    }
//
//    public  void cleanUp(){
//        ImageDB = null;
//    }
//
//
//}

