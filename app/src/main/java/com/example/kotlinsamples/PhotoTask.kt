package com.example.kotlinsamples

class PhotoTask: Runnable {
    override fun run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)

        TODO("Not yet implemented")
        val thread = Thread.currentThread()
        thread.start()

        print("test photo task")
    }
}