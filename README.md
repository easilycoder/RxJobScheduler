# 关于JobScheduler的使用说明

这是一个基于RxJava2的`Scheduler` API实现的一个任务调度器，因此最佳的使用场景是结合RxJava使用。这个库可以解决以下的问题：

* 使用后台线程执行耗时的任务操作

* 根据优先级调度任务

* 对任务进行分组

## 类库的使用

类库的核心主要是`ExecutorProvider`以及`Scheduler`。

* `ExecutorProvider`

  线程池提供者。主要负责提供任务调度执行所需要的线程。类库当中提供了两个实现。

  * `JobExecutorProvider`

    内部持有了一个根据运行时环境的核心数确定的可重复利用的固定线程数的线程池

  * `UIThreadExecutorProvider`

    内部通过ui线程的`Handler`进行消息发送，达到ui线程调度任务的目的

* `Scheduler`

  任务调度者。实际负责任务的入队和出队操作。类库当中只提供了一个实现

  * `PriorityScheduler`

    具备优先级和分组调度功能的调度者。值得注意的是，`PriorityScheduler`本身并没有实现RxJava的`Scheduler`，它只是负责了统一的线程池和队列的管理，实际实现了`Scheduler`的并进行调度的是其内部的`InnerPriorityScheduler`，因此实际构造返回的也是这个实例。

### 构造Scheduler

参考RxJava的工具类`Schedulers`，类库当中提供了一个`JobSchedulers`，其中可以提供了以下三种（以及对应的各种重载方法）方法获取对应的`Scheduler`

* `Scheduler job(@Priority int priority, String groupId)`

  返回一个`InnerPriorityScheduler`的实例，所有被添加调度的任务都会执行在后台线程当中

* `Scheduler main(@Priority int priority, String groupId)`

  返回一个`InnerPriorityScheduler`的实例，所有被添加调度的任务都会执行ui线程

* `Scheduler from(@NonNull ExecutorProvider executorProvider)`

  返回一个`InnerPriorityScheduler`的实例，添加调度的任务会执行在自定义的线程池策略当中。

###  添加任务

使用这个库，与一贯的使用RxJava的方式一致，只要在`subscribeOn(@NonNull Scheduler scheduler)`传入`PriorityScheduler#InnerPriorityScheduler`的实例即可

### 任务取消

取消任务分两种情况，一种是取消单个任务，一种是取消分组任务：

* 取消单个任务

  使用RxJava的事件定于模型，保存`subscribe()`后返回的`Disposable`，并调用它的`dispose()`方法取消订阅事件即可取消任务执行

* 取消分组任务

  返回的`InnerPriorityScheduler ` 的实例中有一个`cancelGroup(@NonNull String groupId)`的API，可以取消分组尚未执行的任务

## 类库执行细节说明

* 类库的优先级调度功能主要依赖于`PriorityBlockingQueue`和`ComparableRunner`。能根据传入的priority进行排队，并且支持优先级相同的情况下，先进先出

* 类库构造的`Scheduler`对象是一个`InnerPriorityScheduler`，它是`PriorityScheduler`的一个内部类，`PriorityScheduler`对象是一个全局单例对象，提供线程池和排队队列。
* 类库仅负责将任务按照策略调度在业务制定的线程池，至于任务的回调、线程切换等职责都是使用RxJava本身的特性。类库唯一支持定制的东西是`ExecutorProvider`，通过实现这个接口可以定制自己的线程策略。

