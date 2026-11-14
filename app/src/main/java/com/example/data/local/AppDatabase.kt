package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.MentalDisorderEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity

@Database(
    entities = [
        MentalDisorderEntity::class,
        VideoItemEntity::class,
        PhotoGalleryEntity::class,
        PhotoItemEntity::class,
        ArticleEntity::class,
        EducationalResourceEntity::class,
        CategoryEntity::class,
        ContactMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun disorderDao(): DisorderDao
    abstract fun videoDao(): VideoDao
    abstract fun galleryDao(): GalleryDao
    abstract fun articleDao(): ArticleDao
    abstract fun resourceDao(): ResourceDao
    abstract fun categoryDao(): CategoryDao
    abstract fun contactMessageDao(): ContactMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "psicologia_para_todos_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
