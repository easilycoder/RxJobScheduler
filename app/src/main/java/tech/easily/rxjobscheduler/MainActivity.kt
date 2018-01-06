package tech.easily.rxjobscheduler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Single
import tech.easily.jobscheduler.JobSchedulers
import tech.easily.jobscheduler.Priority

class MainActivity : AppCompatActivity() {

    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        runTask()
    }

    fun runTask() {
        val group1 = "group1"
        val group2 = "group2"
        val group3 = "group3"

        Single.fromCallable { }.map { }
        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d ,is running at : %s", Priority.PRIORITY_NORMAL, Thread.currentThread().name))
            Thread.sleep(3000)
        }.subscribeOn(JobSchedulers.job()).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d ,is running at : %s", Priority.PRIORITY_MIN, Thread.currentThread().name))
            Thread.sleep(3000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MIN)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d ,is running at : %s", Priority.PRIORITY_MAX, Thread.currentThread().name))
            Thread.sleep(3000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MAX)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_NORMAL, group1, Thread.currentThread().name))
            Thread.sleep(3000)
        }.subscribeOn(JobSchedulers.job(group1)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_MIN, group1, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MIN, group1)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_NORMAL, group3, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(group3)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_NORMAL, group2, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(group2)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_MAX, group1, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MAX, group1)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_MIN, group1, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MIN, group1)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_MAX, group3, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MAX, group3))

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_NORMAL, group2, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(group2)).subscribe()

        Flowable.fromCallable {
            Log.e(TAG, String.format("the task with priority : %d and groupId=%s ,is running at : %s", Priority.PRIORITY_MAX, group2, Thread.currentThread().name))
            Thread.sleep(1000)
        }.subscribeOn(JobSchedulers.job(Priority.PRIORITY_MAX, group2)).subscribe()

    }
}
