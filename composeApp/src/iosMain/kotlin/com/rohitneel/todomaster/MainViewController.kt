package com.rohitneel.todomaster

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import com.rohitneel.todomaster.di.initKoin

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
