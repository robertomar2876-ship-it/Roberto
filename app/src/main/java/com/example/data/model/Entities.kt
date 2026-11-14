package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disorders")
data class MentalDisorderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryNumber: Int,
    val categoryName: String,
    val title: String,
    val dsmReference: String,
    val summary: String,
    val mainCharacteristics: String,
    val manifestationsSymptoms: String,
    val riskFactors: String,
    val dailyImpact: String,
    val treatmentOptions: String,
    val whenToSeekHelp: String,
    val apaReferences: String,
    val keywords: String
)

@Entity(tableName = "videos")
data class VideoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val coverImagePath: String = "",
    val videoUri: String,
    val isExternalUrl: Boolean = false,
    val subtitlesText: String = "",
    val durationMs: Long = 0L,
    val fileSizeBytes: Long = 0L,
    val publishedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "photo_galleries")
data class PhotoGalleryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "photos")
data class PhotoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val galleryId: Int,
    val title: String,
    val description: String,
    val category: String,
    val imagePath: String,
    val fileSizeBytes: Long = 0L,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val coverImagePath: String = "",
    val bundledIllustrationKey: String = "brain",
    val introduction: String,
    val bodyContent: String,
    val conclusions: String,
    val apaReferences: String,
    val isFeatured: Boolean = false,
    val publishedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "resources")
data class EducationalResourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val resourceType: String,
    val filePath: String = "",
    val mimeType: String = "application/pdf",
    val fileSizeBytes: Long = 0L,
    val contentSummary: String = "",
    val publishedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val scope: String,
    val isSystemDefault: Boolean = false
)

@Entity(tableName = "contact_messages")
data class ContactMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val message: String,
    val submittedAtMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
