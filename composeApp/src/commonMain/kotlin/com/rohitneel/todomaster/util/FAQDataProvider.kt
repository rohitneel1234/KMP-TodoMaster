package com.rohitneel.todomaster.util

import com.rohitneel.todomaster.domain.model.FAQItem

object FAQDataProvider {
    val faqList = listOf(
        FAQItem(
            question = "How to create a new task?",
            answer = "1. Tap the '+' floating action button at the bottom of task screen .\n2. Enter the task details such as title, description, and category.\n3. Set a reminder time if needed.\n4. Save the task."
        ),
        FAQItem(
            question = "How to set a reminder for a task?",
            answer = "1. Click the reminder icon on the top right of the new or edit task screen.\n2. Set your date, time, and repeat option, then click 'Confirm'.\n3. Last, save your task, and the reminder will be successfully set."
        ),
        FAQItem(
            question = "How to set a due date for a task?",
            answer = "1. In the new or edit task screen, click the calendar icon to set the due date.\n2. Select your desired date and time, then click 'OK'.\n3. Save your task, and the due date will be successfully set.\n4. If the task is not marked as completed before the due date, it will be shown as overdue."
        ),
        FAQItem(
            question = "How to search for a task?",
            answer = "1. Tap the search icon in the top bar of the task list screen.\n2. Enter the title of the task in the search bar.\n3. Then matching tasks will be displayed on screen."
        ),
        FAQItem(
            question = "How to delete a task?",
            answer = "1. Swipe the task item left to delete or long-press the task to delete.\n2. Select the 'Delete' option.\n3. Alternatively, tap the more icon in the top bar, choose the 'Select' option, and then tap the 'Select All' icon to delete all tasks at once."
        ),
        FAQItem(
            question = "How to edit an existing task?",
            answer = "1. Tap on the task or swipe the task item right to edit it.\n2. Make the necessary changes.\n3. Save the updates."
        ),
        FAQItem(
            question = "How to categorize tasks?",
            answer = "1. Select a 'Category' from the dropdown when adding or editing a task.\n2. This helps organize tasks into sections like Home, Work, Personal, etc."
        ),
        FAQItem(
            question = "How to modify categories for tasks?",
            answer = "1. Tap the more icon in the top bar, select the 'Modify Categories' option to modify categories.\n2. Click on 'Add Category' button to add new category.\n3. Tap vertical dot icon next to the category to rename or delete it.\n4. Long press and drag to reorder categories."
        ),
        FAQItem(
            question = "How to mark a task as completed?",
            answer = "1. Tap the checkbox next to the task title.\n2. Completed tasks are moved to the 'DONE' section for easy tracking."
        ),
        FAQItem(
            question = "Can a task be pinned to prioritize it?",
            answer = "1. Long-press the task you want to pin.\n2. Select the 'Pin' option.\n3. Pinned tasks will be moved to the top of the task list."
        ),
        FAQItem(
            question = "How to view tasks by category?",
            answer = "1. Go to the 'Category' section in the navigation drawer menu.\n2. Select the desired category.\n3. This will display all tasks associated with that category."
        ),
        FAQItem(
            question = "How to access the task overview?",
            answer = "1. Go to the 'Task Overview' section in the navigation drawer menu.\n2. A circular progress bar will show the percentage of completed tasks along with graphical representation of completed and pending task in both bar and pie chart.\n3. See the count of completed and pending tasks for better task management."
        ),
        FAQItem(
            question = "How to view tasks by date?",
            answer = "1. Navigate to the 'Calendar' section from the bottom bar or navigation drawer menu.\n2. The calendar will display all tasks for each day, with completed tasks marked accordingly.\n3. Tap on any date to view the tasks scheduled for that day or create a new task directly from the calendar."
        ),
        FAQItem(
            question = "How to use the Pomodoro timer?",
            answer = "1. Navigate to the 'Pomodoro' section in the app's bottom bar.\n2. Set the desired focus duration.\n3. Start the timer, and the app will notify when timer completed.\n4. Use the Pomodoro technique to boost productivity and manage your tasks efficiently."
        ),
        FAQItem(
            question = "How to mark a task as a favorite?",
            answer = "1. Long-press the task you want to mark as favorite in the task list.\n2. Select the 'Add to Favorites' option.\n3. Favorite tasks are easily accessible and can be viewed separately in the 'Favorites' section from navigation drawer menu."
        ),
        FAQItem(
            question = "How to change the app theme?",
            answer = "1. Go to the 'Theme' section in the navigation drawer menu.\n2. Tap on 'Theme' to select different colors shown in circular shapes.\n3. Choose a preferred color to apply the new theme to the app."
        ),
        FAQItem(
            question = "How to change the language of the app?",
            answer = "1. Go to the 'Settings' section in the navigation drawer menu.\n2. Tap on 'Language' and select the preferred language from the available options.\n3. The app will update to reflect the selected language immediately."
        ),
        FAQItem(
            question = "How to enable dark mode?",
            answer = "1. Go to the 'Settings' section in the navigation drawer menu.\n2. Toggle the 'Dark Mode' switch to turn it on.\n3. The app will switch to dark mode immediately."
        ),
        FAQItem(
            question = "Is it possible to recover a deleted task?",
            answer = "1. Click on the 'UNDO' option from the SnackBar message within a certain time frame after deleting the task item to recover the deleted task.\n2. After this period, the task will be permanently deleted."
        ),
        FAQItem(
            question = "How to backup and restore tasks?",
            answer = "1. To backup tasks, go to the 'Settings' menu and select 'Backup Data.' This will save your task data to your phone's storage.\n2. To restore tasks, select 'Restore Data' from the 'Settings' menu, and choose the backup file you wish to restore.\n3. Ensure you have a recent backup to prevent data loss when restoring."
        ),
        FAQItem(
            question = "How to recover a task from the Trash?",
            answer = "1. Go to the 'Trash' section in the navigation drawer menu where deleted tasks are temporarily stored.\n" +
                    "2. Select the task you want to recover from the list of deleted tasks.\n" +
                    "3. Tap on 'Restore' to move the task back to your active task list.\n" +
                    "4. The task will be restored and visible in your main task list again."
        ),
        FAQItem(
            question = "How long do tasks stay in the Trash?",
            answer = "1. Tasks remain in the 'Trash' for up to 10 days after being deleted.\n" +
                    "2. After this period, the tasks will be permanently deleted and cannot be recovered.\n" +
                    "3. You can empty the Trash at any time from the 'Trash' section to permanently remove tasks."
        )
    )
}