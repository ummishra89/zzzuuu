package com.mentorz.requester

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class BackgroundExecutor private constructor() {
    private val executorService: ExecutorService = Executors.newFixedThreadPool(5)

    fun execute(runnable: Runnable) {
        executorService.submit(runnable)
    }

    companion object {

        private var mInstance: BackgroundExecutor? = null

        val instance: BackgroundExecutor
            get() {
                if (mInstance == null) {
                    mInstance = BackgroundExecutor()
                }
                return mInstance!!
            }
    }
}
