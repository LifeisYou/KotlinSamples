package com.example.kotlinsamples

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ImageView
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object PhotoManager {
//    fun startDownload(imageView: ImageView, )


    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private val decodeWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    private const val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    private val decodeThreadPool: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,
        NUMBER_OF_CORES,
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        decodeWorkQueue
    )

    fun handleState(photoTask: PhotoTask, state: Int) {
        when(state) {
            1 -> decodeThreadPool.execute(photoTask)
        }
    }
}