package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.MentalDisorderEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisorderDao {
    @Query("SELECT * FROM disorders ORDER BY categoryNumber ASC, id ASC")
    fun observeAll(): Flow<List<MentalDisorderEntity>>

    @Query("SELECT COUNT(*) FROM disorders")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MentalDisorderEntity>)

    @Query("DELETE FROM disorders")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<MentalDisorderEntity>) {
        deleteAll()
        insertAll(items)
    }
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY publishedAtMillis DESC")
    fun observeAll(): Flow<List<VideoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoItemEntity): Long

    @Update
    suspend fun update(video: VideoItemEntity)

    @Delete
    suspend fun delete(video: VideoItemEntity)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface GalleryDao {
    @Query("SELECT * FROM photo_galleries ORDER BY createdAtMillis DESC")
    fun observeAllGalleries(): Flow<List<PhotoGalleryEntity>>

    @Query("SELECT * FROM photos ORDER BY createdAtMillis DESC")
    fun observeAllPhotos(): Flow<List<PhotoItemEntity>>

    @Query("SELECT * FROM photos WHERE galleryId = :galleryId ORDER BY createdAtMillis DESC")
    fun observePhotosByGallery(galleryId: Int): Flow<List<PhotoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGallery(gallery: PhotoGalleryEntity): Long

    @Update
    suspend fun updateGallery(gallery: PhotoGalleryEntity)

    @Query("DELETE FROM photo_galleries WHERE id = :galleryId")
    suspend fun deleteGalleryById(galleryId: Int)

    @Query("DELETE FROM photos WHERE galleryId = :galleryId")
    suspend fun deletePhotosByGalleryId(galleryId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoItemEntity>)

    @Update
    suspend fun updatePhoto(photo: PhotoItemEntity)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Int)
}

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAtMillis DESC")
    fun observeAll(): Flow<List<ArticleEntity>>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Update
    suspend fun update(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources ORDER BY publishedAtMillis DESC")
    fun observeAll(): Flow<List<EducationalResourceEntity>>

    @Query("SELECT COUNT(*) FROM resources")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resource: EducationalResourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(resources: List<EducationalResourceEntity>)

    @Update
    suspend fun update(resource: EducationalResourceEntity)

    @Query("DELETE FROM resources WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY scope ASC, name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ContactMessageDao {
    @Query("SELECT * FROM contact_messages ORDER BY submittedAtMillis DESC")
    fun observeAll(): Flow<List<ContactMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ContactMessageEntity): Long

    @Query("UPDATE contact_messages SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Int)

    @Query("DELETE FROM contact_messages WHERE id = :id")
    suspend fun deleteById(id: Int)
}
