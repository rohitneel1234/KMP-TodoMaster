package com.rohitneel.todomaster.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "check_list_table",
    foreignKeys = [ForeignKey(
        entity = TaskModel::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["value"])]
)
@Serializable
data class CheckListModel(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    @ColumnInfo(name = "value") var value: String = "",
    @ColumnInfo(name = "uid") var uid: Long = 0L,
    @ColumnInfo(name = "checked") var checked: Boolean = false,
    @ColumnInfo(name = "taskId") var taskId: Int  // Foreign key referencing TaskModel
)
