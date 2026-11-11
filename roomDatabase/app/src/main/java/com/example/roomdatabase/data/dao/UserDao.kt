package com.example.roomdatabase.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.roomdatabase.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUserList(people: List<UserEntity>): List<Long>

    @Update
    suspend fun updateUser(user: UserEntity): Int

    @Delete
    suspend fun deleteUser(user: UserEntity): Int

    @Query("SELECT * FROM users ORDER BY userId DESC")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY userId DESC")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM users WHERE userId IN (:userIds)")
    suspend fun getAllById(userIds: IntArray): List<UserEntity>

    @Query("SELECT * FROM users WHERE last_name LIKE '%' || :query || '%' ORDER BY userId DESC")
    fun searchByLastName(query: String): Flow<List<UserEntity>>
}
