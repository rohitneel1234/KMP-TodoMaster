package com.rohitneel.todomaster.domain.usecase

import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.repository.CheckListRepository

class InsertCheckList(private val repository: CheckListRepository) {
    suspend operator fun invoke(checkList: List<CheckListModel>) {
        repository.insertCheckList(checkList)
    }
}

class UpdateCheckList(private val repository: CheckListRepository) {
    suspend operator fun invoke(checkList: List<CheckListModel>) {
        repository.updateCheckList(checkList)
    }
}

class DeleteCheckList(private val repository: CheckListRepository) {
    suspend operator fun invoke(id: Long) {
        repository.deleteCheckList(id)
    }
}

class GetCheckList(private val repository: CheckListRepository) {
    suspend operator fun invoke(taskId: Int): List<CheckListModel> {
        return repository.getCheckList(taskId)
    }
}

data class CheckListUseCases(
    val insertCheckList: InsertCheckList,
    val updateCheckList: UpdateCheckList,
    val deleteCheckList: DeleteCheckList,
    val getCheckList: GetCheckList
)
