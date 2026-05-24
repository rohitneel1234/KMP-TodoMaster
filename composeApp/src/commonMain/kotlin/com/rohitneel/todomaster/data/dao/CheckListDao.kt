package com.rohitneel.todomaster.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rohitneel.todomaster.data.model.CheckListModel

@Dao
interface CheckListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckList(checkList: List<CheckListModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCheckList(checkList: List<CheckListModel>)

    @Query("DELETE FROM check_list_table WHERE id = :id")
    suspend fun deleteCheckList(id: Long)

    @Query("SELECT * FROM check_list_table WHERE taskId = :taskId")
    suspend fun getCheckList(taskId: Int): List<CheckListModel>
}
