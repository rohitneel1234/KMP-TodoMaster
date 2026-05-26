package com.rohitneel.todomaster.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        printLogger()
        appDeclaration()
        modules(
            platformModule(),
            dataModule,
            useCaseModule,
            viewModelModule
        )
    }

// called by iOS etc
fun doInitKoin() = initKoin {}
