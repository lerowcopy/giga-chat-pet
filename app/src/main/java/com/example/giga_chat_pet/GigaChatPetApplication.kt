package com.example.giga_chat_pet

import android.app.Application
import com.example.giga_chat_pet.presentation.renderer.MarkdownRenderer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GigaChatPetApplication : Application() {

    @Inject
    lateinit var markdownRenderer: MarkdownRenderer

    override fun onCreate() {
        super.onCreate()
        markdownRenderer.init(this)
    }
}
