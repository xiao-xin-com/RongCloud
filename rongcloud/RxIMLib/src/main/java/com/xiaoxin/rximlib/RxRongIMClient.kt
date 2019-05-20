@file:JvmName("RxRongIMClient")
@file:JvmMultifileClass

package com.xiaoxin.rximlib

import android.content.Context
import io.reactivex.*
import io.rong.imlib.RongCommonDefine
import io.rong.imlib.RongIMClient
import io.rong.imlib.TypingMessage.TypingStatus
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.SearchConversationResult
import io.rong.message.RecallNotificationMessage


/**
 * 初始化 SDK，在整个应用程序全局只需要调用一次, 建议在Application继承类中调用。
 *
 * @param context 传入Application类的Context。
 */
fun init(context: Context) = RongIMClient.init(context)

/**
 * <p>初始化 SDK，在整个应用程序全局只需要调用一次, 建议在 Application 继承类中调用。
 * 调用此接口传入 AppKey 与在 AndroidManifest.xml 里写入 RONG_CLOUD_APP_KEY 是同样效果，二选一即可。</p>
 *
 * @param context 传入Application类的Context。
 * @param appKey  融云注册应用的AppKey。
 */
fun init(context: Context, appKey: String) = RongIMClient.init(context, appKey)


/**
 *
 * 如果您基于 IMLib SDK 进行开发，那在初始化 SDK 之后，即可通过 RongIMClient.getInstance() 方法获取实例，然后调用相应的 API 方法即可。示例：
 */
fun setOnReceiveMessageListener(
    flag: ((Message, Int) -> Boolean) = { _, _ -> false }
): Observable<Message> {
    return Observable.create { emitter ->
        RongIMClient.setOnReceiveMessageListener listener@{ message, left ->
            emitter.onNext(message)
            emitter.setCancellable {
                RongIMClient.setOnReceiveMessageListener(null)
            }
            return@listener flag.invoke(message, left)
        }
    }
}

/**
 * /**
 * <p>根据会话类型，发送消息。
 * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback} 中的方法回调发送的消息状态及消息体。</p>
 *
 * @param type        会话类型。
 * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @param content     消息内容，例如 {@link TextMessage}, {@link ImageMessage}。
 * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
 *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
 *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
 * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
 * @return callback    发送消息的回调。参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
 *                    {@link #sendMessage(Message, String, String, IRongCallback.ISendMessageCallback)}
*/
 */
@JvmOverloads
fun sendMessage(
    type: Conversation.ConversationType,
    targetId: String,
    content: MessageContent,
    pushContent: String? = null,
    pushData: String? = null
): Single<Message> {
    return Single.create { emitter ->
        emitter.takeUnless { it.isDisposed }?.apply {
            RongIMClient.getInstance().sendMessage(
                type, targetId, content, pushContent, pushData,
                SendMessageCallback(this)
            )
        }
    }
}

/**
 * /**
 * <p>发送消息。
 * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}
 * 中的方法回调发送的消息状态及消息体。</p>
 *
 * @param message     将要发送的消息体。
 * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
 *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
 *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
 * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
 * @return callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
*/
 */
@JvmOverloads
fun sendMessage(
    message: Message,
    pushContent: String? = null,
    pushData: String? = null
): Single<Message> {
    return Single.create { emitter ->
        emitter.takeUnless { it.isDisposed }?.apply {
            RongIMClient.getInstance().sendMessage(
                message, pushContent, pushData, SendMessageCallback(this)
            )
        }
    }
}

/**
 *
 * <p>发送地理位置消息。</p>
 * <p>发送前构造 {@link Message} 消息实体，消息实体中的 content 必须为 {@link LocationMessage}, 否则返回失败。</p>
 * <p>其中的缩略图地址 scheme 只支持 file:// 和 http://。也可不设置缩略图地址，传入 null。</p>
 *
 * @param message             消息实体。
 * @param pushContent         当下发 push 消息时，在通知栏里会显示这个字段。
 *                            如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
 *                            如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
 * @param pushData            push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
 * @return sendMessageCallback 发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
 */

@JvmOverloads
fun sendLocationMessage(
    message: Message,
    pushContent: String? = null,
    pushData: String? = null
): Single<Message> {
    return Single.create { emitter ->
        emitter.takeUnless { it.isDisposed }?.apply {
            RongIMClient.getInstance().sendLocationMessage(
                message, pushContent, pushData, SendMessageCallback(this)
            )
        }
    }
}


data class SendImageMessageEvent internal constructor(
    val type: Type,
    val message: Message? = null,
    val progress: Int? = null
) {
    enum class Type {
        ON_ATTACHED, ON_PROGRESS, ON_SUCCESS
    }
}

internal class SendImageMessageCallback(
    private val emitter: FlowableEmitter<SendImageMessageEvent>
) : RongIMClient.SendImageMessageCallback() {

    override fun onSuccess(message: Message) {
        emitter.apply {
            onNext(
                SendImageMessageEvent(
                    SendImageMessageEvent.Type.ON_SUCCESS,
                    message = message
                )
            )
            onComplete()
        }
    }

    override fun onAttached(message: Message?) {
        emitter.onNext(
            SendImageMessageEvent(
                SendImageMessageEvent.Type.ON_ATTACHED,
                message = message
            )
        )
    }

    override fun onProgress(message: Message?, progress: Int) {
        emitter.onNext(
            SendImageMessageEvent(
                SendImageMessageEvent.Type.ON_PROGRESS,
                message = message,
                progress = progress
            )
        )
    }

    override fun onError(message: Message?, errorCode: RongIMClient.ErrorCode?) {
        emitter.onError(ErrorCodeException(errorCode))
    }

}


/**
 * /**
 * <p>根据会话类型，发送图片消息。</p>
 *
 * @param conversationType        会话类型。
 * @param targetId    目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @param content      图片消息内容。
 * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
 *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
 *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，传 null 即可，默认已经指定。
 * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
 * @return callback    发送消息的回调。
*/
 */
@JvmOverloads
fun sendImageMessage(
    conversationType: Conversation.ConversationType,
    targetId: String,
    content: MessageContent,
    pushContent: String? = null,
    pushData: String? = null
): Flowable<SendImageMessageEvent> {
    return Flowable.create({ emitter ->
        RongIMClient.getInstance().sendImageMessage(
            conversationType, targetId, content, pushContent, pushData,
            SendImageMessageCallback(emitter)
        )
    }, BackpressureStrategy.LATEST)
}

/**
 * 清空指定类型，targetId 的某一会话所有聊天消息记录。
 *
 * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @return callback         清空是否成功的回调。
 */
fun clearMessages(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .clearMessages(
                conversationType, targetId,
                RxResultCallback(emitter)
            )
    }
}

/**
 * <p>清除指定会话的消息</p>。
 * <p>此接口会删除指定会话中数据库的所有消息，同时，会清理数据库空间。
 * 如果数据库特别大，超过几百 M，调用该接口会有少许耗时。</p>
 *
 * @param conversationType 指定的会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是userId, groupId, discussionId。
 * @return callback         是否删除成功的回调。
 */
fun deleteMessages(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .deleteMessages(
                conversationType, targetId,
                RxResultCallback(emitter)
            )
    }
}

/**
 * 根据 messageId，删除指定的一条或者一组消息。
 *
 * @param messageIds 要删除的消息 Id 数组。
 * @return callback   是否删除成功的回调。
 */
fun deleteMessages(
    messageIds: IntArray
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance().deleteMessages(messageIds, RxResultCallback(emitter))
    }
}


//获取历史消息

/**
 * <p>
 * 获取指定类型，targetId 的N条历史消息记录。通过此接口可以根据情况分段加载历史消息，节省网络资源，提高用户体验。
 * 该接口不支持拉取聊天室 {io.rong.imlib.model.Conversation.ConversationType#CHATROOM} 历史消息。
 * </p>
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息，没有消息第一次调用应设置为:-1。
 * @param count            要获取的消息数量。
 * @return callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
 */
fun getHistoryMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    oldestMessageId: Int,
    count: Int
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getHistoryMessages(
            conversationType,
            targetId,
            oldestMessageId,
            count,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}

/**
 * <p>获取本地数据库中保存，特定类型，targetId 的N条历史消息记录。通过此接口可以根据情况分段加载历史消息，节省网络资源，提高用户体验。</p>
 * <p>该接口不支持拉取聊天室 {@link io.rong.imlib.model.Conversation.ConversationType#CHATROOM} 历史消息。</p>
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 。
 * @param objectName       消息类型标识。{@link MessageTag#value()}, 比如文本消息"RC:TxtMsg", 图片消息"RC:ImgMsg", 或者您自定义消息的 MessageTag 中 value 的值。
 * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1。
 * @param count            要获取的消息数量
 * @return callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
 */
fun getHistoryMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    objectName: String,
    oldestMessageId: Int,
    count: Int
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getHistoryMessages(
            conversationType,
            targetId,
            objectName,
            oldestMessageId,
            count,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}


/**
 * <p>根据会话类型的目标 Id，回调方式获取某消息类型的某条消息之前或之后的N条历史消息记录。如： 要获取messageId为22的之前的10条图片消息，
 * 则相应参数为 getHistoryMessages(conversationType, targetId, "RC:ImgMsg", 22, 10, true, resultCallback)。
 * 注意：返回的消息列表里面不包含oldestMessageId本身。</p>
 *
 * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param objectName       消息类型标识。如RC:TxtMsg，RC:ImgMsg，RC:VcMsg等。
 * @param baseMessageId    最后一条消息的 Id，获取此消息之前的 count 条消息,没有消息第一次调用应设置为:-1。
 * @param count            要获取的消息数量
 * @param direction        要获取的消息相对于 oldestMessageId 的方向 {@link io.rong.imlib.RongCommonDefine.GetMessageDirection}
 *                         以指定的 message id 作为获取的起始点，时间早于该 id 则为 FRONT，晚于则为 BEHIND。
 * @return callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
 */
fun getHistoryMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    objectName: String,
    baseMessageId: Int,
    count: Int,
    direction: RongCommonDefine.GetMessageDirection
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getHistoryMessages(
            conversationType,
            targetId,
            objectName,
            baseMessageId,
            count,
            direction,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}


/**
 * 获取某会话中指定消息的前 before 数量和 after 数量的消息。
 * 返回的消息列表中会包含指定的消息。消息列表时间顺序从新到旧。
 *
 * @param conversationType 指定的会话类型。
 * @param targetId         指定的会话 id。
 * @param sentTime         指定消息的发送时间，不能为0。
 * @param before           指定消息的前部分消息数量。
 * @param after            指定消息的后部分消息数量。
 * @return resultCallback   搜索结果回调。
 */
fun getHistoryMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    sentTime: Long,
    before: Int,
    after: Int
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getHistoryMessages(
            conversationType,
            targetId,
            sentTime,
            before,
            after,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}


/**
 * <p>获取融云服务器中暂存，特定类型，targetId 的N条（一次不超过40条）历史消息记录。通过此接口可以根据情况分段加载历史消息，节省网络资源，提高用户体验。</p>
 * <p>区别于 {@link #getHistoryMessages}，该接口是从融云服务器中拉取。通常用于更换新设备后，拉取历史消息。
 * 公众服务会话 {@link io.rong.imlib.model.Conversation.ConversationType#APP_PUBLIC_SERVICE}
 * {@link io.rong.imlib.model.Conversation.ConversationType#PUBLIC_SERVICE} </p>
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param dateTime         从该时间点开始获取消息。即：消息中的 sentTime；第一次可传 0，获取最新 count 条。
 * @param count            要获取的消息数量，最多 40 条。
 * @return callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
 */
fun getRemoteHistoryMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    dateTime: Long,
    count: Int
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getRemoteHistoryMessages(
            conversationType,
            targetId,
            dateTime,
            count,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}


//搜索消息
/**
 * 搜索本地历史消息。
 * 此接口可快速返回匹配的会话列表,并且会话中包含已匹配的消息数量。通过 {SearchConversationResult#getMatchCount()} 得到。
 * 如果需要自定义消息也能被搜索到,需要在自定义消息中实现 {@link MessageContent#getSearchableWord()} 方法;
 *
 * @param keyword           搜索的关键字。
 * @param conversationTypes 搜索的会话类型。
 * @param objectNames       搜索的消息类型,例如:RC:TxtMsg。
 * @return resultCallback    搜索结果回调。
 */
fun searchConversations(
    keyword: String,
    conversationTypes: Array<Conversation.ConversationType>,
    objectNames: Array<String>
): Single<List<SearchConversationResult>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().searchConversations(
            keyword,
            conversationTypes,
            objectNames,
            RxResultCallback<List<SearchConversationResult>>(emitter)
        )
    }
}


/**
 * 根据会话,搜索本地历史消息。
 * 搜索结果可分页返回。
 * 如果需要自定义消息也能被搜索到,需要在自定义消息中实现 {@link MessageContent#getSearchableWord()} 方法;
 *
 * @param conversationType 指定的会话类型。
 * @param targetId         指定的会话 id。
 * @param keyword          搜索的关键字。
 * @param count            返回的搜索结果数量, 传0时会返回所有搜索到的消息, 非0时,逐页返回。
 * @param beginTime        查询记录的起始时间, 传0时从最新消息开始搜索。
 * @return resultCallback   搜索结果回调。
 */
fun searchMessages(
    conversationType: Conversation.ConversationType,
    targetId: String,
    keyword: String,
    count: Int,
    beginTime: Long
): Single<List<Message>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().searchMessages(
            conversationType,
            targetId,
            keyword,
            count,
            beginTime,
            RxResultCallback<List<Message>>(emitter)
        )
    }
}

//消息监听

//更改消息状态

/**
 * 根据时间戳清除指定类型，目标Id 的某一会话消息未读状态。{@link Message#getSentTime()}在时间戳之前的消息将被置成已读。
 *
 * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param timestamp        时间戳。
 * @return callback         清除是否成功的回调。
 */
fun clearMessagesUnreadStatus(
    conversationType: Conversation.ConversationType,
    targetId: String,
    timestamp: Long
): Completable {
    return Completable.create { emitter ->
        RongIMClient.getInstance().clearMessagesUnreadStatus(
            conversationType,
            targetId,
            timestamp,
            RxOperationCallback(emitter)
        )
    }
}

/**
 * 清除指定类型，targetId 的某一会话消息未读状态。
 *
 * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @return callback         清除是否成功的回调。
 */
fun clearMessagesUnreadStatus(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance().clearMessagesUnreadStatus(
            conversationType,
            targetId,
            RxResultCallback(emitter)
        )
    }
}


/**
 * 根据 messageId 设置接收到的消息状态。用于UI标记消息为已读，已下载等状态。
 *
 * @param messageId      消息 Id。
 * @param receivedStatus 接收到的消息状态。{@link io.rong.imlib.model.Message.ReceivedStatus}
 * @return callback       是否设置成功的回调。
 */
fun setMessageReceivedStatus(
    messageId: Int,
    receivedStatus: Message.ReceivedStatus
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .setMessageReceivedStatus(
                messageId,
                receivedStatus,
                RxResultCallback(emitter)
            )
    }
}

//基础会话管理

//获取会话

/**
 * 根据不同会话类型的目标 Id，回调方式获取某一会话信息。
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @return callback         获取会话信息的回调。
 */
fun getConversation(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Conversation> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .getConversation(conversationType, targetId, RxResultCallback(emitter))
    }
}

/**
 * <p> 获取当前用户本地会话列表的默认方法，该方法返回的是以下类型的会话列表：私聊，群组，系统会话。如果
 * 您需要获取其它类型的会话列表,可以使用{@link #getConversationList(ResultCallback, Conversation.ConversationType...)} 方法。
 * <strong>注意：</strong>当更换设备或者清除缓存后，拉取到的是暂存在融云服务器中该账号当天收发过消息的会话列表。</p>
 *
 * @return callback 获取会话列表的回调。
 */
fun getConversationList(): Single<List<Conversation>> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getConversationList(RxResultCallback(emitter))
    }
}

/**
 * <p>根据会话类型，获取当前用户的本地会话列表。即显示所有本地数据库中收发过消息,并且未被删除的会话。</p>
 * <p><strong>注意：</strong>当更换设备或者清除缓存后，能拉取到暂存在融云服务器中该账号当天收发过消息的会话。</p>
 *
 * @return callback          获取会话列表的回调。
 * @param conversationTypes 选择要获取的会话类型。
 */
fun getConversationList(
    vararg conversationTypes: Conversation.ConversationType
): Single<List<Conversation>> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .getConversationList(RxResultCallback(emitter), *conversationTypes)
    }
}

/**
 * <p>分页获取会话列表</p>
 * <p><strong>注意：</strong>当更换设备或者清除缓存后，能拉取到暂存在融云服务器中该账号当天收发过消息的会话。</p>
 *
 *
 * @return callback          获取会话列表的回调
 * @param timeStamp         时间戳，获取从此时间戳往前的会话，第一次传 0
 * @param count             取回的会话个数。当实际取回的会话个数小于 count 值时，表明已取完数据
 * @param conversationTypes 选择要获取的会话类型
 */
fun getConversationListByPage(
    timeStamp: Long,
    count: Int,
    vararg conversationTypes: Conversation.ConversationType
): Single<List<Conversation>> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .getConversationListByPage(
                RxResultCallback(emitter),
                timeStamp,
                count,
                *conversationTypes
            )
    }
}

/**
 * <p>从会话列表中移除某一会话，但是不删除会话内的消息。</p>
 * <p>如果此会话中有新的消息，该会话将重新在会话列表中显示，并显示最近的历史消息。</p>
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @return callback         移除会话是否成功的回调。
 */
fun removeConversation(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Boolean> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .removeConversation(conversationType, targetId, RxResultCallback(emitter))
    }
}


/**
会话提醒
通过融云 SDK，您可以设置会话的提醒状态来实现免打扰功能。按照免打扰作用范围，分为两种类型：

- 设置单个会话的提醒状态。通过此方法，您可以屏蔽某个会话的通知提醒和推送。

- 设置所有会话的通知免打扰。可以设置某一个时间段免打扰，也可以设置全天免打扰。

 */

/**
 * 设置会话消息提醒状态。
 *
 * @param conversationType   会话类型。
 * @param targetId           目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @param notificationStatus 是否屏蔽。
 * @return callback           设置状态的回调。
 */
fun setConversationNotificationStatus(
    conversationType: Conversation.ConversationType,
    targetId: String,
    notificationStatus: Conversation.ConversationNotificationStatus
): Single<Conversation.ConversationNotificationStatus> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .setConversationNotificationStatus(
                conversationType,
                targetId,
                notificationStatus,
                RxResultCallback(emitter)
            )
    }
}


/**
 * 获取会话消息提醒状态。
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id。
 * @return callback         获取状态的回调。
 */
fun getConversationNotificationStatus(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Conversation.ConversationNotificationStatus> {
    return Single.create { emitter ->
        RongIMClient.getInstance()
            .getConversationNotificationStatus(
                conversationType,
                targetId,
                RxResultCallback(emitter)
            )
    }
}

/**
 * 设置消息通知免打扰时间。
 *
 * @param startTime   起始时间 格式 HH:MM:SS。
 * @param spanMinutes 设置的免打扰结束时间距离起始时间的间隔分钟数。 0 &lt; spanMinutes &lt; 1440。
 *                    比如，您设置的起始时间是 00：00， 结束时间为 23：59，则 spanMinutes 为 23 * 60 + 59 = 1339 分钟。
 * @return callback    消息通知免打扰时间回调。
 */
fun setNotificationQuietHours(
    startTime: String,
    spanMinutes: Int
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .setNotificationQuietHours(startTime, spanMinutes, RxOperationCallback(it))
    }
}

/**
 * 移除消息通知免打扰时间。
 *
 * @return callback 移除消息通知免打扰时间回调。
 */
fun removeNotificationQuietHours(): Completable {
    return Completable.create {
        RongIMClient.getInstance().removeNotificationQuietHours(RxOperationCallback(it))
    }
}


/**
 * 根据消息类型，targetId 保存某一会话的文字消息草稿。用于暂存用户输入但未发送的消息。
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @param content          草稿的文字内容。
 * @return callback         是否保存成功的回调。
 */

fun saveTextMessageDraft(
    conversationType: Conversation.ConversationType,
    targetId: String,
    content: String
): Single<Boolean> {
    return Single.create {
        RongIMClient.getInstance()
            .saveTextMessageDraft(conversationType, targetId, content, RxResultCallback(it))
    }
}

/**
 * 根据消息类型，targetId 获取某一会话的文字消息草稿。用于获取用户输入但未发送的暂存消息。
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @return callback         获取草稿文字内容的回调。
 */
fun getTextMessageDraft(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<String> {
    return Single.create {
        RongIMClient.getInstance()
            .getTextMessageDraft(conversationType, targetId, RxResultCallback(it))
    }
}


/**
 * 根据消息类型，targetId 删除某一会话的文字消息草稿
 *
 * @param conversationType 会话类型。
 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @return callback         删除草稿文字内容的回调。
 */
fun clearTextMessageDraft(
    conversationType: Conversation.ConversationType,
    targetId: String
): Single<Boolean> {
    return Single.create {
        RongIMClient.getInstance()
            .clearTextMessageDraft(conversationType, targetId, RxResultCallback(it))
    }
}

/**
 * 设置某一会话为置顶或者取消置顶，回调方式获取设置是否成功。
 *
 * @param conversationType 会话类型。
 * @param id               目标 Id。根据不同的 conversationType，可能是用户 Id、群组 Id 或聊天室 Id。
 * @param isTop            是否置顶。
 * @return callback         设置置顶或取消置顶是否成功的回调。
 */
fun setConversationToTop(
    conversationType: Conversation.ConversationType,
    targetId: String,
    isTop: Boolean
): Single<Boolean> {
    return Single.create {
        RongIMClient.getInstance()
            .setConversationToTop(
                conversationType, targetId, isTop,
                RxResultCallback(it)
            )
    }
}

/**
 * 通过回调方式，获取所有未读消息数。即除了聊天室之外其它所有会话类型的未读消息数。
 *
 * @return callback 消息数的回调。
 */
fun getTotalUnreadCount(): Single<Int> {
    return Single.create {
        RongIMClient.getInstance().getTotalUnreadCount(RxResultCallback(it))
    }
}

/**
 * 通过回调方式，获取所有指定会话的未读消息数（聊天室除外）。
 *
 * @return callback      获取未读数的回调。
 * @param conversations 需要获取未读数的指定会话。
 */
fun getTotalUnreadCount(
    vararg conversations: Conversation
): Single<Int> {
    return Single.create {
        RongIMClient.getInstance().getTotalUnreadCount(RxResultCallback(it), *conversations)
    }
}

/**
 * 回调方式获取某会话类型的未读消息数。
 *
 * @return callback          未读消息数的回调。
 * @param conversationTypes 会话类型。
 */
fun getUnreadCount(
    vararg conversationTypes: Conversation.ConversationType
): Single<Int> {
    return Single.create {
        RongIMClient.getInstance().getUnreadCount(RxResultCallback(it), *conversationTypes)
    }
}


/**

用户关系管理
黑名单
将用户加入黑名单之后，将不再收到对方发来的任何消息。例如，用户 A 将用户 B 加入黑名单，A 仍然可以给 B 发消息，B 也能正常收到。 但 B 无法给 A 发消息，会提示 “您的消息已经发出，但被对方拒收”。

 */

/**
 * 将某个用户加到黑名单中。
 * <p>当把对方加入黑名单后，对方再发消息时，就会提示“您的消息已经发出, 但被对方拒收”。但您仍然可以给对方发送消息。</p>
 *
 * @param userId   用户 Id。
 * @return callback 加到黑名单回调。
 */
fun addToBlacklist(
    userId: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance().addToBlacklist(userId, RxOperationCallback(it))
    }
}

/**
 * 将个某用户从黑名单中移出。
 *
 * @param userId   用户 Id。
 * @return callback 移除黑名单回调。
 */
fun removeFromBlacklist(
    userId: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance().removeFromBlacklist(userId, RxOperationCallback(it))
    }
}


/**
 * 获取某用户是否在黑名单中。
 *
 * @param userId   用户 Id。
 * @return callback 获取用户是否在黑名单回调。
 */
fun getBlacklistStatus(
    userId: String
): Single<RongIMClient.BlacklistStatus> {
    return Single.create {
        RongIMClient.getInstance().getBlacklistStatus(userId, RxResultCallback(it))
    }
}

private class RxGetBlacklistCallback(
    private val emitter: SingleEmitter<Array<String>>
) : RongIMClient.GetBlacklistCallback() {
    override fun onSuccess(users: Array<String>?) {
        emitter.takeUnless { it.isDisposed }?.onSuccess(users ?: arrayOf())
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.takeUnless { it.isDisposed }?.onError(ErrorCodeException(errorCode))
    }
}

/**
 * 获取当前用户设置的黑名单列表。
 *
 * @return callback 获取黑名单回调。
 */
fun getBlacklist(): Single<Array<String>> {
    return Single.create {
        RongIMClient.getInstance().getBlacklist(RxGetBlacklistCallback(it))
    }
}


/**
高级功能
 */

/**
 * 撤回消息
 *
 * @param message 将被撤回的消息
 * @return callback onSuccess里回调{@link RecallNotificationMessage}，IMLib 已经在数据库里将被撤回的消息用{@link RecallNotificationMessage} 替换，
 *                 用户需要在界面上对{@link RecallNotificationMessage} 进行展示。
 */
@JvmOverloads
fun recallMessage(
    message: Message,
    pushContent: String? = null
): Single<RecallNotificationMessage> {
    return Single.create {
        RongIMClient.getInstance().recallMessage(message, pushContent, RxResultCallback(it))
    }
}

data class MessageRecalled(
    val message: Message,
    val recallNotificationMessage: RecallNotificationMessage
)

/**
 * 设置撤回消息监听器
 *
 * @return listener 撤回消息监听器
 */
fun setOnRecallMessageListener(
    flag: ((Message, RecallNotificationMessage) -> Boolean) = { _, _ -> true }
): Observable<MessageRecalled> {
    return Observable.create { emitter ->
        RongIMClient.setOnRecallMessageListener listener@{ message, recallNotificationMessage ->
            emitter.takeUnless { it.isDisposed }
                ?.onNext(MessageRecalled(message, recallNotificationMessage))
            emitter.setCancellable {
                RongIMClient.setOnRecallMessageListener(null)
            }
            return@listener flag.invoke(message, recallNotificationMessage)
        }
    }
}

/**
消息阅读回执
单聊消息阅读回执
您可以在用户查看了单聊会话中的未读消息之后，向会话中发送阅读回执，会话中的用户可以根据此回执，在 UI 中更新消息的显示。

其中，timestamp 为会话中用户已经阅读的最后一条消息的发送时间戳（Message 的 sentTime 属性），代表用户已经阅读了该会话中此消息之前的所有消息。
 */


/**
 * 发起群组消息回执请求。
 * 只能对自己发送的消息发起消息回执请求。
 *
 * @param message       需要请求回执的那条消息，io.rong.imlib.model.Message对象
 * @return callback      回调函数
 */
fun sendReadReceiptRequest(message: Message): Completable {
    return Completable.create {
        RongIMClient.getInstance().sendReadReceiptRequest(message, RxOperationCallback(it))
    }
}


/**
 * 发送群消息已读回执
 *
 * @param type          会话类型，Conversation.ConversationType对象
 * @param targetId      会话 id
 * @param messageList   会话中需要发送回执的消息列表，List<io.rong.imlib.model.Message>对象
 * @return callback      回调函数
 */
fun sendReadReceiptResponse(
    conversationType: Conversation.ConversationType,
    targetId: String,
    messageList: List<Message>
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .sendReadReceiptResponse(
                conversationType,
                targetId,
                messageList,
                RxOperationCallback(it)
            )
    }
}

/**
 * 多端登录时，通知其它终端清除某个会话的未读消息数
 *
 * @param type      会话类型，Conversation.ConversationType对象
 * @param targetId  目标会话 ID
 * @param timestamp 该会话中已读的最后一条消息的发送时间戳{@link Message#getSentTime()}
 * @return callback  回调函数
 */
fun syncConversationReadStatus(
    conversationType: Conversation.ConversationType,
    targetId: String,
    timestamp: Long
): Completable {
    return Completable.create {
        RongIMClient.getInstance().syncConversationReadStatus(
            conversationType,
            targetId,
            timestamp,
            RxOperationCallback(it)
        )
    }
}


data class ConversationInfo(
    val conversationType: Conversation.ConversationType,
    val targetId: String
)

/**
 * 同步阅读状态监听
 * 多端登录，收到其它端清除某一会话未读数通知的时候，回调 onSyncMessageReadStatus
 */
fun setSyncConversationReadStatusListener(): Observable<ConversationInfo> {
    return Observable.create { emitter ->
        RongIMClient.getInstance()
            .setSyncConversationReadStatusListener { conversationType, targetId ->
                emitter.takeUnless { it.isDisposed }
                    ?.onNext(ConversationInfo(conversationType, targetId))
            }
        emitter.setCancellable {
            RongIMClient.getInstance()
                .setSyncConversationReadStatusListener(null)
        }
    }
}


/**
输入状态提醒
您可以在用户正在输入的时候，向对方发送正在输入的状态。目前该功能只支持单聊。

其中，您可以在 typingContentType 中传入消息的类型名，会话中的其他用户输入状态监听中会收到此消息类型。您可以通过此消息类型，自定义不同的输入状态提示（如：正在输入、正在讲话、正在拍摄等）。

在 6 秒之内，如果同一个用户在同一个会话中多次调用此接口发送正在输入的状态，为保证产品体验和网络优化，将只有最开始的一次生效。
 */

data class ConversationTypingStatus(
    val conversationType: Conversation.ConversationType,
    val targetId: String,
    val typingStatus: Collection<TypingStatus>
)

fun setTypingStatusListener(): Observable<ConversationTypingStatus> {
    return Observable.create { emitter ->
        RongIMClient.setTypingStatusListener { conversationType, targetId, mutableCollection ->
            emitter.takeUnless { it.isDisposed }
                ?.onNext(ConversationTypingStatus(conversationType, targetId, mutableCollection))
        }
        emitter.setCancellable {
            RongIMClient.setTypingStatusListener(null)
        }
    }
}

/**
 * 设置连接服务器阶段的 Log 输出监听。需要在connect之前调用，
 *
 * @return listener Log 输出监听器
 */
fun setRCLogInfoListener(): Observable<String> {
    return Observable.create { emitter ->
        RongIMClient.setRCLogInfoListener { logStr ->
            emitter.takeUnless { it.isDisposed }?.onNext(logStr)
        }
        emitter.setCancellable {
            RongIMClient.setRCLogInfoListener(null)
        }
    }
}


/**
群定向消息
此方法用于在群组中给部分用户发送消息，其它用户不会收到这条消息，建议向群中部分用户发送状态类消息时使用此功能。

注：群定向消息不存储到云端，通过“单群聊消息云存储”服务无法获取到定向消息。

 */


/**
 * <p>此方法用于在群组中发送消息给其中的部分用户，其它用户不会收到这条消息。
 * 通过 {@link io.rong.imlib.IRongCallback.ISendMessageCallback} 中的方法回调发送的消息状态及消息体。</p>
 *
 * @param type        会话类型。
 * @param targetId    目标 Id。只能是群组 Id。
 * @param content     消息内容，例如 {@link TextMessage}, {@link ImageMessage}。
 * @param pushContent 当下发 push 消息时，在通知栏里会显示这个字段。
 *                    如果发送的是自定义消息，该字段必须填写，否则无法收到 push 消息。
 *                    如果发送 sdk 中默认的消息类型，例如 RC:TxtMsg, RC:VcMsg, RC:ImgMsg，则不需要填写，默认已经指定。
 * @param pushData    push 附加信息。如果设置该字段，用户在收到 push 消息时，能通过 {@link io.rong.push.notification.PushNotificationMessage#getPushData()} 方法获取。
 * @param userIds     群组会话中将会接收到此消息的用户列表。
 * @return callback    发送消息的回调，参考 {@link io.rong.imlib.IRongCallback.ISendMessageCallback}。
 */

@JvmOverloads
fun sendDirectionalMessage(
    conversationType: Conversation.ConversationType,
    targetId: String,
    content: MessageContent,
    userIds: Array<String>,
    pushContent: String? = null,
    pushData: String? = null
): Single<Message> {
    return Single.create { emitter ->
        RongIMClient.getInstance().sendDirectionalMessage(
            conversationType,
            targetId,
            content,
            userIds,
            pushContent,
            pushData,
            SendMessageCallback(emitter)
        )
    }
}

/**
 * 设置当前用户离线消息存储时间
 *
 * @param duration 离线消息时间，参数取值范围为 int 值 1~7。
 * @return callback
 */
fun setOfflineMessageDuration(duration: Int): Single<Long> {
    return Single.create { emitter ->
        RongIMClient.getInstance().setOfflineMessageDuration(duration, RxResultCallback(emitter))
    }
}

/**
 *
 * 获取当前用户离线消息时间
 *
 * @return callback
 */
fun getOfflineMessageDuration(): Single<String> {
    return Single.create { emitter ->
        RongIMClient.getInstance().getOfflineMessageDuration(RxResultCallback(emitter))
    }
}

/**
 * 设置连接状态变化的监听器。在自定义UI时，会需要调用此接口判断当前连接状态，来绘制UI及决定逻辑走向。
 *
 * @return listener 连接状态变化的监听器。
 */
fun setConnectionStatusListener(): Observable<RongIMClient.ConnectionStatusListener.ConnectionStatus> {
    return Observable.create { emitter ->
        RongIMClient.setConnectionStatusListener { status ->
            emitter.takeUnless { it.isDisposed }?.onNext(status)
        }
        emitter.setCancellable {
            RongIMClient.setConnectionStatusListener(null)
        }
    }
}


fun getMessage(messageId: Int): Single<Message> {
    return Single.create {
        RongIMClient.getInstance().getMessage(messageId, RxResultCallback(it))
    }
}

@JvmOverloads
fun insertOutgoingMessage(
    type: Conversation.ConversationType,
    targetId: String,
    sentStatus: Message.SentStatus,
    content: MessageContent,
    sentTime: Long = System.currentTimeMillis()
): Single<Message> {
    return Single.create {
        RongIMClient.getInstance().insertOutgoingMessage(
            type,
            targetId,
            sentStatus,
            content,
            sentTime,
            RxResultCallback(it)
        )
    }
}

@JvmOverloads
fun insertIncomingMessage(
    type: Conversation.ConversationType,
    targetId: String,
    senderUserId: String,
    receivedStatus: Message.ReceivedStatus,
    content: MessageContent,
    sentTime: Long = System.currentTimeMillis()
): Single<Message> {
    return Single.create {
        RongIMClient.getInstance().insertIncomingMessage(
            type,
            targetId,
            senderUserId,
            receivedStatus,
            content,
            sentTime,
            RxResultCallback(it)
        )
    }
}


data class DownloadEvent(
    val action: DownloadAction,
    val id: String? = null,
    val progress: Int? = null
) {
    enum class DownloadAction { ON_SUCCESS, ON_PROGRESS, ON_ERROR }
}

private class RxDownloadMediaCallback(
    private val emitter: ObservableEmitter<DownloadEvent>
) : RongIMClient.DownloadMediaCallback() {
    override fun onSuccess(id: String?) {
        emitter.onNext(DownloadEvent(DownloadEvent.DownloadAction.ON_SUCCESS, id = id))
    }

    override fun onProgress(progress: Int) {
        emitter.onNext(DownloadEvent(DownloadEvent.DownloadAction.ON_PROGRESS, progress = progress))
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.onError(ErrorCodeException(errorCode))
    }
}

fun downloadMedia(
    conversationType: Conversation.ConversationType,
    targetId: String,
    mediaType: RongIMClient.MediaType,
    imageUrl: String
): Observable<DownloadEvent> {
    return Observable.create {
        RongIMClient.getInstance().downloadMedia(
            conversationType,
            targetId,
            mediaType,
            imageUrl,
            RxDownloadMediaCallback(it)
        )
    }
}