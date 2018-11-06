package com.xiaoxin.rximlib

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.rong.imlib.RongIMClient
import java.util.concurrent.atomic.AtomicBoolean

class ErrorCodeException(val errorCode: RongIMClient.ErrorCode? = null) : Exception()

class TokenIncorrectException(val token: String) : Exception()

internal class ConnectSingle(private val token: String) : Single<String>() {
    override fun subscribeActual(observer: SingleObserver<in String>) {
        val disposable = ConnectDisposable(token, observer)
        observer.onSubscribe(disposable)
        if (!disposable.isDisposed) {
            RongIMClient.connect(token, disposable)
        }
    }

    private class ConnectDisposable(
        private val token: String,
        private val observer: SingleObserver<in String>
    ) : RongIMClient.ConnectCallback(), Disposable {
        private val flag = AtomicBoolean()
        override fun onSuccess(userid: String?) {
            if (!isDisposed && userid != null) {
                observer.onSuccess(userid)
            }
        }

        override fun onError(errorCode: RongIMClient.ErrorCode?) {
            observer.takeUnless { isDisposed }
                ?.onError(ErrorCodeException(errorCode))
        }

        override fun onTokenIncorrect() {
            observer.takeUnless { isDisposed }
                ?.onError(TokenIncorrectException(token))
        }

        override fun isDisposed(): Boolean = flag.get()

        override fun dispose(): Unit = flag.set(true)

    }
}
