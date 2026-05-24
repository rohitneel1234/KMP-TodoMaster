package com.rohitneel.todomaster.data.repository

import com.rohitneel.todomaster.data.model.CheckListModel

interface CheckListRepository {

    suspend fun insertCheckList(checkList: List<CheckListModel>)

    suspend fun updateCheckList(checkList: List<CheckListModel>)

    suspend fun deleteCheckList(id: Long)

    suspend fun getCheckList(taskId: Int): List<CheckListModel>

}
