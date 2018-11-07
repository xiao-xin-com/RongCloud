@file:JvmName("RxRongIMClient")
@file:JvmMultifileClass

package com.xiaoxin.rximlib

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.rong.imlib.RongIMClient
import java.util.concurrent.atomic.AtomicBoolean


/**
 * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
 * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
 * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
 *
 * @param token    从服务端获取的用户身份令牌（Token）。
 * @return RongIMClient  客户端核心类的实例。
 */
fun connect(token: String): Single<String> = ConnectSingle(token)

/**
 * <p>断开与融云服务器的连接。当调用此接口断开连接后，仍然可以接收 Push 消息。</p>
 * <p>若想断开连接后不接受 Push 消息，可以调用{@link #logout()}</p>
 */
fun disconnect() = RongIMClient.getInstance().disconnect()

/**
 * <p>断开与融云服务器的连接，并且不再接收 Push 消息。</p>
 * <p>若想断开连接后仍然接受 Push 消息，可以调用 {@link #disconnect()}</p>
 */
fun logout() = RongIMClient.getInstance().logout()

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
