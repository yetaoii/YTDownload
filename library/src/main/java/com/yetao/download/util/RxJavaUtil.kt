package com.yetao.download.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.TimeUnit

/**
 *  Created by yetao on 2019-09-29
 *  description
 **/


/**
 * rxjava等待，action条件成立，才执行回调
 */
fun waitLoad(
    action: () -> Boolean,
    callback: (() -> Void)? = null,
    error: ((Throwable) -> Unit)? = null
): Disposable {
    var i = 0
    return Observable.create<Any> {
        if (!action()) {
            it.onNext(++i)
            it.onError(WaitException())
        } else {
            it.onComplete()
        }
    }.subscribeOnNew()
        .retryWhen { throwableObservable ->
            throwableObservable.flatMap { throwable ->
                if (throwable.isT<WaitException>()) {
                    return@flatMap Observable.timer(100, TimeUnit.MILLISECONDS)
                }
                return@flatMap Observable.error<Any>(throwable)
            }
        }
        .observeOnMain()
        .subscribeWith(object : DisposableObserver<Any>() {
            override fun onComplete() {
                callback?.let { it() }
                "onComplete()".log()

            }

            override fun onNext(t: Any) {
                //                    "onNext():$t".log()

            }

            override fun onError(e: Throwable) {
                "onError():$e".log()
                error?.let { it(e) }

            }

        })
}

fun <T> Observable<T>.io2main(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.new2main(): Observable<T> {
    return this.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnIo(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.subscribeOnNew(): Observable<T> {
    return this.subscribeOn(Schedulers.newThread())
}

fun <T> Observable<T>.subscribeOnMain(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.observeOnIo(): Observable<T> {
    return this.observeOn(Schedulers.io())
}

fun <T> Observable<T>.observeOnNew(): Observable<T> {
    return this.observeOn(Schedulers.newThread())
}

fun <T> Observable<T>.observeOnMain(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}


class WaitException : Exception("this exception is that this thread is waiting for other action")
