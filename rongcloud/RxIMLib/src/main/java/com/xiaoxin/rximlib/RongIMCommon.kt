package com.xiaoxin.rximlib

import io.reactivex.CompletableEmitter
import io.reactivex.SingleEmitter
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message

open class ErrorCodeException(val errorCode: RongIMClient.ErrorCode? = null) : Exception()

internal class RxOperationCallback(
    private val emitter: CompletableEmitter
) : RongIMClient.OperationCallback() {
    override fun onSuccess() {
        emitter.takeUnless { it.isDisposed }?.onComplete()
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.takeUnless { it.isDisposed }?.onError(ErrorCodeException(errorCode))
    }
}

internal class SendMessageCallback(
    private val emitter: SingleEmitter<Message>
) : IRongCallback.ISendMessageCallback {
    override fun onAttached(message: Message?) = Unit
    override fun onSuccess(message: Message?) {
        emitter.takeUnless { it.isDisposed }
            ?.onSuccess(message ?: return)
    }

    override fun onError(
        message: Message?,
        errorCode: RongIMClient.ErrorCode?
    ) {
        emitter.takeUnless { it.isDisposed }
            ?.onError(ErrorCodeException(errorCode))
    }
}

internal class RxResultCallback<T>(
    private val emitter: SingleEmitter<T>
) : RongIMClient.ResultCallback<T>() {
    override fun onSuccess(t: T) {
        emitter.takeUnless { it.isDisposed }?.onSuccess(t ?: return)
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.takeUnless { it.isDisposed }?.onError(ErrorCodeException(errorCode))
    }
}
