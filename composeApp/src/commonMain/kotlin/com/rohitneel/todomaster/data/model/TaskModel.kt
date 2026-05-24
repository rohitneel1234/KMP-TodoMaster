package com.rohitneel.todomaster.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitneel.todomaster.domain.model.FontStyleModel
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.util.AppConstants.ALL
import com.rohitneel.todomaster.util.AppConstants.HOME
import com.rohitneel.todomaster.util.AppConstants.OTHER
import com.rohitneel.todomaster.util.AppConstants.PERSONAL
import com.rohitneel.todomaster.util.AppConstants.SCHOOL
import com.rohitneel.todomaster.util.AppConstants.WORK
import kotlinx.serialization.Serializable

@Entity(tableName = "task_table")
@Serializable
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "isCheck") val isCheck: Boolean = false,
    @ColumnInfo(name = "status") val isCompleted: Boolean = false,
    @ColumnInfo(name = "reminder") var reminder: Long? = null,
    @ColumnInfo(name = "pinned") var isPinned: Boolean = false,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean = false,
    @ColumnInfo(name = "creation_date") var creationDate: Long? = null,
    @ColumnInfo(name = "due_date") var dueDate: Long? = null,
    @ColumnInfo(name = "color") val color: Int,
    @ColumnInfo(name = "gradientColor") val gradientColor: List<Int>? = null,
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "fontFamily") val fontFamily: String = "",
    @ColumnInfo(name = "fontSize") val fontSize: Float,
    @ColumnInfo(name = "fontStyleModel") val fontStyleModel: FontStyleModel = FontStyleModel(),
    @ColumnInfo(name = "deletedAt") val deletedAt: Long? = null
) {
    companion object {
        val taskColors = listOf(AppColors.White, AppColors.RedOrange, AppColors.Chocolate, AppColors.Violet, AppColors.DarkKhaki, AppColors.RedPink)
        val categoryType = listOf(ALL, HOME, PERSONAL, WORK, SCHOOL, OTHER)
    }
}

class InvalidTaskException(message: String): Exception(message)
