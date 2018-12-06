@file:JvmName("RxRongIMClient")
@file:JvmMultifileClass

package com.xiaoxin.rximlib

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.ChatRoomInfo
import io.rong.imlib.model.Message

/**
聊天室业务
聊天室业务基本概念
聊天室是指多个用户一起聊天，用户数量没有上限。和其它业务场景的主要区别如下：

用户退出聊天界面后即视为离开聊天室，不会再接收到任何聊天室消息。

聊天室消息不会保存到本地数据库，融云服务端最多保存聊天室最近的 50 条消息。客户端在调用加入聊天室接口时可以设置进入聊天室时的拉取消息数量。

聊天室的会话关系由融云负责建立并保持连接，通过 SDK 相关接口，可以让用户加入或者退出聊天室。
 */

/**
 * 加入聊天室。如果聊天室不存在，sdk 会创建聊天室并加入，如果已存在，则直接加入。加入聊天室时，可以选择拉取聊天室消息数目。
 *
 * @param chatRoomId      聊天室 Id。
 * @param defMessageCount 进入聊天室拉取消息数目，-1 时不拉取任何消息，0 时拉取 10 条消息，最多只能拉取 50 条。
 * @return callback        状态回调。
 */
fun joinChatRoom(
    chatRoomId: String,
    defMessageCount: Int
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .joinChatRoom(chatRoomId, defMessageCount, RxOperationCallback(it))
    }
}

/**
 * 加入已存在的聊天室。如果聊天室不存在，则加入失败。加入聊天室时，可以选择拉取聊天室消息数目。
 *
 * @param chatRoomId      聊天室 Id。
 * @param defMessageCount 进入聊天室拉取消息数目，-1 时不拉取任何消息，0 时拉取 10 条消息，最多只能拉取 50 条。
 * @return callback        状态回调。
 */
fun joinExistChatRoom(
    chatRoomId: String,
    defMessageCount: Int
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .joinExistChatRoom(chatRoomId, defMessageCount, RxOperationCallback(it))
    }
}


/**
 * 退出聊天室。
 *
 * @param chatRoomId 聊天室 Id。
 * @return callback   状态回调。
 */
fun quitChatRoom(
    chatRoomId: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .quitChatRoom(chatRoomId, RxOperationCallback(it))
    }
}

/**
 * 查询聊天室信息。回调中返回{@link ChatRoomInfo}
 *
 * @param chatRoomId     聊天室 Id。
 * @param defMemberCount 进入聊天室拉成员数目，最多 20 条。
 * @param order          按照何种顺序返回聊天室成员信息。升序, 返回最早加入的用户列表; 降序, 返回最晚加入的用户列表。{@link io.rong.imlib.model.ChatRoomInfo.ChatRoomMemberOrder}
 * @return callback       状态回调。
 */
fun getChatRoomInfo(
    chatRoomId: String,
    defMemberCount: Int,
    order: ChatRoomInfo.ChatRoomMemberOrder
): Single<ChatRoomInfo> {
    return Single.create {
        RongIMClient.getInstance()
            .getChatRoomInfo(chatRoomId, defMemberCount, order, RxResultCallback(it))
    }
}

/*
开通聊天室消息存储功能后，融云内置的文字、语音、图片、图文、位置、文件等消息会自动在服务器端进行存储，如果您的聊天室中用到了自定义类消息，可通过定义 MessageTag.ISPERSISTED 来设置消息是否进行存储。

从服务器端获取聊天室历史消息的接口如下：
 */

private class RxChatRoomHistoryMessageCallback(
    private val emitter: SingleEmitter<List<Message>>
) : IRongCallback.IChatRoomHistoryMessageCallback {
    override fun onSuccess(list: MutableList<Message>?, p1: Long) {
        emitter.takeUnless { it.isDisposed }?.onSuccess(list ?: listOf())
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.takeUnless { it.isDisposed }?.onError(ErrorCodeException(errorCode))
    }
}


/**
 * 获取聊天室历史消息记录。
 * 此方法从服务器端获取之前的历史消息，但是必须先开通聊天室消息云存储功能。
 * 如果指定时间 0,则从存储的第一条消息开始拉取。
 *
 * @param targetId   目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param recordTime 起始的消息发送时间戳，单位: 毫秒。
 * @param count      要获取的消息数量，count 大于 0 ，小于等于 200。
 * @param order      拉取顺序: 降序, 按照时间戳从大到小; 升序, 按照时间戳从小到大。
 */
fun getChatroomHistoryMessages(
    targetId: String,
    recordTime: Long,
    count: Int,
    order: RongIMClient.TimestampOrder
): Single<List<Message>> {
    return Single.create {
        RongIMClient.getInstance().getChatroomHistoryMessages(
            targetId,
            recordTime,
            count,
            order,
            RxChatRoomHistoryMessageCallback(it)
        )
    }
}


enum class ChatRoomAction { ON_JOINING, ON_JOINED, ON_QUITED }
data class ChatRoomEvent(val action: ChatRoomAction, val userId: String?)

class ChatRoomException(
    val userId: String?, errorCode: RongIMClient.ErrorCode?
) : ErrorCodeException(errorCode)

fun setChatRoomActionListener(): Observable<ChatRoomEvent> {
    return Observable.create { emitter ->
        RongIMClient.setChatRoomActionListener(object : RongIMClient.ChatRoomActionListener {
            override fun onJoining(id: String?) {
                emitter.takeUnless { it.isDisposed }
                    ?.onNext(ChatRoomEvent(ChatRoomAction.ON_JOINING, id))
            }

            override fun onJoined(id: String?) {
                emitter.takeUnless { it.isDisposed }
                    ?.onNext(ChatRoomEvent(ChatRoomAction.ON_JOINED, id))
            }

            override fun onQuited(id: String?) {
                emitter.takeUnless { it.isDisposed }
                    ?.onNext(ChatRoomEvent(ChatRoomAction.ON_QUITED, id))
            }

            override fun onError(id: String?, errorCode: RongIMClient.ErrorCode?) {
                emitter.takeUnless { it.isDisposed }
                    ?.onError(ChatRoomException(id, errorCode))
            }
        })
        emitter.setCancellable { RongIMClient.setChatRoomActionListener(null) }
    }
}
