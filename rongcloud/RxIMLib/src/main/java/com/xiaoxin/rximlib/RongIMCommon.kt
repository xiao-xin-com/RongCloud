package com.xiaoxin.rximlib

import io.reactivex.CompletableEmitter
import io.reactivex.SingleEmitter
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Message

open class ErrorCodeException(val errorCode: RongIMClient.ErrorCode? = null) : Exception()

class SendMessageException @JvmOverloads constructor(
    val rongMessage: Message? = null,
    errorCode: RongIMClient.ErrorCode? = null
) : ErrorCodeException(errorCode)

internal class RxOperationCallback(
    private val emitter: CompletableEmitter
) : RongIMClient.OperationCallback() {
    override fun onSuccess() = emitter.onComplete()
    override fun onError(errorCode: RongIMClient.ErrorCode?) =
        emitter.onError(ErrorCodeException(errorCode))
}

internal class SendMessageCallback(
    private val emitter: SingleEmitter<Message>
) : IRongCallback.ISendMessageCallback {
    override fun onAttached(message: Message?) = Unit
    override fun onSuccess(message: Message?) =
        message?.let { emitter.onSuccess(it) } ?: Unit

    override fun onError(
        message: Message?,
        errorCode: RongIMClient.ErrorCode?
    ) = emitter.onError(SendMessageException(message, errorCode))
}

internal class RxResultCallback<T>(
    private val emitter: SingleEmitter<T>
) : RongIMClient.ResultCallback<T>() {
    override fun onSuccess(t: T) = t.let { emitter.onSuccess(it) }

    override fun onError(errorCode: RongIMClient.ErrorCode?) =
        emitter.onError(ErrorCodeException(errorCode))
}
