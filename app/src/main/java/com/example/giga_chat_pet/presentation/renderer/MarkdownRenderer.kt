package com.example.giga_chat_pet.presentation.renderer

import android.content.Context
import android.text.Spanned
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import com.example.giga_chat_pet.domain.MarkdownPreprocessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkdownRenderer @Inject constructor(
    private val markdownPreprocessor: MarkdownPreprocessor
) {

    private var markwon: Markwon? = null

    fun init(context: Context) {
        if (markwon == null) {
            markwon = Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(TaskListPlugin.create(context))
                .build()
        }
    }

    fun render(context: Context, text: String): Spanned {
        if (markwon == null) {
            init(context)
        }
        val processedText = markdownPreprocessor.preprocess(text)
        return markwon!!.toMarkdown(processedText)
    }
}
