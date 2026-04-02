package com.example.giga_chat_pet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileData)

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}
