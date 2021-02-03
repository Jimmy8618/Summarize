package com.jimmy.summarize.handler;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * AsyncTask
 * AsyncTask是什么？ 是 AsyncTask是⼀种轻量级的异步任务类，它可以在线程池中执⾏后 台任务，
 * 然后把执⾏的进度和最终结果传递给主线程并在主线程中更新UI。
 *
 * AsyncTask是⼀个抽象的泛型类，它提供了Params、Progress和Result这三个泛型参数， 其中Params表⽰参数的类型，
 * Progress表⽰后台任务的执⾏进度和类型，⽽Result则表⽰ 后台任务的返回结果的类型，
 * 如果AsyncTask不需要传递具体的参数，那么这三个泛型参 数可以⽤Void来代替。
 *
 * 关于线程池： 关 AsyncTask对应的线程池ThreadPoolExecutor都是进程范围内共享的，且都 是static的，
 * 所以是Asynctask控制着进程范围内所有的⼦类实例。由于这个限制的存在， 当使⽤默认线程池时，
 * 如果线程数超过线程池的最⼤容量，线程池就会爆掉(3.0后默认串 ⾏执⾏，不会出现个问题)。
 * 针对这种情况，可以尝试⾃定义线程池，配合Asynctask使 ⽤。
 *
 * 关于默认线程池： 关 AsyncTask⾥⾯线程池是⼀个核⼼线程数为CPU + 1，最⼤线程数为 CPU * 2 + 1，
 * ⼯作队列⻓度为128的线程池，线程等待队列的最⼤等待数为28，但是可 以⾃定义线程池。
 * 线程池是由AsyncTask来处理的，线程池允许tasks并⾏运⾏，需要注意 的是并发情况下数据的⼀致性问题，
 * 新数据可能会被⽼数据覆盖掉。所以希望tasks能够 串⾏运⾏的话，使⽤SERIAL_EXECUTOR。
 *
 * AsyncTask在不同的 在 SDK版本中的区别： 版 调⽤AsyncTask的execute⽅法不能⽴即执⾏程 序的原因及改善⽅案通过查阅官⽅⽂档发现，
 * AsyncTask⾸次引⼊时，异步任务是在⼀个 独⽴的线程中顺序的执⾏，也就是说⼀次只执⾏⼀个任务，不能并⾏的执⾏，
 * 从1.6开 始，AsyncTask引⼊了线程池，⽀持同时执⾏5个异步任务，也就是说只能有5个线程运 ⾏，超过的线程只能等待，
 * 等待前的线程直到某个执⾏完了才被调度和运⾏。
 * 换句话说， 如果进程中的AsyncTask实例个数超过5个，那么假如前5都运⾏很⻓时间的话，那么第6 个只能等待机会了。
 * 这是AsyncTask的⼀个限制，⽽且对于2.3以前的版本⽆法解决。如果 你的应⽤需要⼤量的后台线程去执⾏任务，
 * 那么只能放弃使⽤AsyncTask，⾃⼰创建线程 池来管理Thread。不得不说，虽然AsyncTask较Thread使⽤起来⽅便，
 * 但是它最多只能同 时运⾏5个线程，这也⼤⼤局限了它的作⽤，你必须要⼩⼼设计你的应⽤，错开使⽤ AsyncTask时间，尽⼒做到分时，或者保证数量不会⼤于5个，否就会遇到上⾯提到的问 题。可能是Google意识到了AsynTask的局限性了，从Android 3.0开始对AsyncTask的API 做出了⼀些调整：每次只启动⼀个线程执⾏⼀个任务，完了之后再执⾏第⼆个任务，也就 是相当于只有⼀个后台线程在执⾏所提交的任务。 ⼀些问题： ⼀1.⽣命周期 很多开发者会认为⼀个在Activity中创建的AsyncTask会随着Activity的销毁⽽ 销毁。然⽽事实并⾮如此。AsynTask会⼀直执⾏，直到doInBackground()⽅法执⾏完 毕，然后，如果cancel(boolean)被调⽤,那么onCancelled(Result result)⽅法会被执⾏；否4
 * 毕，然后，如果cancel(boolean)被调⽤,那么onCancelled(Result result)⽅法会被执⾏；否 则，执⾏onPostExecute(Result result)⽅法。如果我们的Activity销毁之前，没有取消 AsyncTask，这有可能让我们的应⽤崩溃(crash)。因为它想要处理的view已经不存在了。 所以，我们是必须确保在销毁活动之前取消任务。总之，我们使⽤AsyncTask需要确保 AsyncTask正确的取消。 2.内存泄漏 如果AsyncTask被声明为Activity的⾮静态内部类，那么AsyncTask会保留⼀个 对Activity的引⽤。如果Activity已经被销毁，AsyncTask的后台线程还在执⾏，它将继续 在内存⾥保留这个引⽤，导致Activity⽆法被回收，引起内存泄漏。 3.结果丢失 屏幕旋转或Activity在后台被系统杀掉等情况会导致Activity的重新创建，之 前运⾏的AsyncTask会持有⼀个之前Activity的引⽤，这个引⽤已经⽆效，这时调⽤ onPostExecute()再去更新界⾯将不再⽣效。 4.并⾏还是串⾏ 在Android1.6之前的版本，AsyncTask是串⾏的，在1.6之后的版本，采 ⽤线程池处理并⾏任务，但是从Android 3.0开始，为了避免AsyncTask所带来的并发错 误，⼜采⽤⼀个线程来串⾏执⾏任务。可以使⽤executeOnExecutor()⽅法来并⾏地执⾏ 任务。 AsyncTask原理原 AsyncTask中有两个线程池（SerialExecutor和THREAD_POOL_EXECUTOR）和⼀个 Handler（InternalHandler），其中线程池SerialExecutor⽤于任务的排队，⽽线程池 THREAD_POOL_EXECUTOR⽤于真正地执⾏任务，InternalHandler⽤于将执⾏环境从线 程池切换到主线程。 sHandler是⼀个静态的Handler对象，为了能够将执⾏环境切换到主线程，这就要求 sHandler这个对象必须在主线程创建。由于静态成员会在加载类的时候进⾏初始化，因此 这就变相要求AsyncTask的类必须在主线程中加载，否则同⼀个进程中的AsyncTask都将 ⽆法正常⼯作。
 * 1.本质：
 *     封装了线程池和handler的异步框架
 * 2.场景：
 *      使用在耗时比较短
 * 3.使用方法
 *      1.3个参数Params后台任务中使用, Progress显示进度, Result返回结果
 *      2.5个方法
 */
public class UpdateInfoAsyncTask extends AsyncTask<Params, Progress, Result>{
    private TextView textView;
    private ProgressBar progressBar;
    public UpdateInfoAsyncTask() {
        super();
    }

    public UpdateInfoAsyncTask(TextView tv,ProgressBar progressBar) {
        super();
        textView = tv;
        progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        textView.setText("开始执行异步线程");
    }


    @Override
    protected void onPostExecute(Result result) {
        textView.setText("异步操作执行结束" + result);
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        int value = values[0];
        progressBar.setProgress(value);
    }

    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
    }

    @Override
    protected Result doInBackground(Params... params) {
        int i;
        for(i = 0;i <= 100; i +=10){
            publishProgress(i);
        }
        return i + params[0].intValue() + "";
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
