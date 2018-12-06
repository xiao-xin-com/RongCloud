@file:JvmName("RxRongIMClient")
@file:JvmMultifileClass

package com.xiaoxin.rximlib

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Discussion

fun getDiscussion(discussionId: String): Single<Discussion> {
    return Single.create {
        RongIMClient.getInstance().getDiscussion(discussionId, RxResultCallback(it))
    }
}

fun setDiscussionName(
    discussionId: String,
    name: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance().setDiscussionName(
            discussionId, name,
            RxOperationCallback(it)
        )
    }
}

private class RxCreateDiscussionCallback(
    private val emitter: SingleEmitter<String>
) : RongIMClient.CreateDiscussionCallback() {
    override fun onSuccess(discussionId: String) {
        emitter.takeUnless { it.isDisposed }?.onSuccess(discussionId)
    }

    override fun onError(errorCode: RongIMClient.ErrorCode?) {
        emitter.takeUnless { it.isDisposed }?.onError(ErrorCodeException(errorCode))
    }
}


fun createDiscussion(
    name: String,
    userIdList: List<String>
): Single<String> {
    return Single.create {
        RongIMClient.getInstance()
            .createDiscussion(name, userIdList, RxCreateDiscussionCallback(it))
    }
}

fun addMemberToDiscussion(
    discussionId: String,
    userIdList: List<String>
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .addMemberToDiscussion(discussionId, userIdList, RxOperationCallback(it))
    }
}

fun removeMemberFromDiscussion(
    discussionId: String,
    userId: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .removeMemberFromDiscussion(discussionId, userId, RxOperationCallback(it))
    }
}

fun quitDiscussion(
    discussionId: String
): Completable {
    return Completable.create {
        RongIMClient.getInstance()
            .quitDiscussion(discussionId, RxOperationCallback(it))
    }
}
