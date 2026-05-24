package com.rohitneel.todomaster.data.repository

import com.rohitneel.todomaster.data.dao.CheckListDao
import com.rohitneel.todomaster.data.model.CheckListModel

class CheckListRepositoryImpl(private val checkListDao: CheckListDao) : CheckListRepository {
    override suspend fun insertCheckList(checkList: List<CheckListModel>) {
        return checkListDao.insertCheckList(checkList)
    }

    override suspend fun updateCheckList(checkList: List<CheckListModel>) {
        return checkListDao.updateCheckList(checkList)
    }

    override suspend fun deleteCheckList(id: Long) {
        return checkListDao.deleteCheckList(id)
    }

    override suspend fun getCheckList(taskId: Int): List<CheckListModel> {
        return checkListDao.getCheckList(taskId)
    }
}
