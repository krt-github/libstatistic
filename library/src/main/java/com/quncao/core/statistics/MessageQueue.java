package com.quncao.core.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 消息队列
 */

class MessageQueue <T> implements IMessageQueue<T> {
    private static final int MAX_MESSAGE_SIZE = 5;

    private final LinkedList<T> mMessageQueue = new LinkedList<>();
    private int mMaxMessageSize = MAX_MESSAGE_SIZE;
    private boolean mShutdown = false;

    private IOnMessageSizeChangedListener mOnMessageSizeChangedListener;
    public void setOnMessageSizeChangedListener(IOnMessageSizeChangedListener l ){
        mOnMessageSizeChangedListener = l;
    }

    @Override
    public void shutdown() {
        mShutdown = true;
        int oldSize = mMessageQueue.size();
        mMessageQueue.clear();
        onSizeChanged(oldSize, mMessageQueue.size());
        mOnMessageSizeChangedListener = null;
        Logger.d("---MessageQueue shutdown---");
    }

    MessageQueue(){}

    public MessageQueue(int maxMessage){
        setMaxMessageSize(maxMessage);
    }

    public int getMaxMessageSize() {
        return mMaxMessageSize;
    }

    private void setMaxMessageSize(int maxMessageSize) {
        if(maxMessageSize <= 0)
            return;

        this.mMaxMessageSize = maxMessageSize;
    }

    public synchronized int getCurrentMessageSize(){
        return mMessageQueue.size();
    }

    public synchronized void addMessage(T msg){
        if(mShutdown){
            Logger.e("---MessageQueue has shutdown---");
            return;
        }

        int oldSize = mMessageQueue.size();
        mMessageQueue.add(msg);

        Logger.d("---addMessage: " + msg + "  size: " + mMessageQueue.size());
        onSizeChanged(oldSize, mMessageQueue.size());
    }

    @Override
    public synchronized void addMessage(List<T> list) {
        if(mShutdown){
            Logger.e("---MessageQueue has shutdown---");
            return;
        }

        if(null == list)
            return;

        int oldSize = mMessageQueue.size();
        T t;
        for(int i = list.size() - 1; i >= 0; i--){
            t = list.get(i);
            if(null != t){
                mMessageQueue.addFirst(t);
            }
        }

        Logger.d("---addMessage list: " + list + "  size: " + mMessageQueue.size());
        onSizeChanged(oldSize, mMessageQueue.size());
    }

    /**
     * 从 queue 中第一条 message 开始，获取 size 条 message 并移除
     * @param size 要获取的数量
     * @return list
     */
    public synchronized List<T> pollMessage(int size){
        if(mShutdown){
            Logger.e("---MessageQueue has shutdown---");
            return null;
        }

        try {
            int oldSize = mMessageQueue.size();
            List<T> list = new ArrayList<>();
            Iterator<T> iterator = mMessageQueue.iterator();
            while(iterator.hasNext()){
                if(list.size() >= size)
                    break;
                list.add(iterator.next());
                iterator.remove();
            }

            if(list.size() <= 0)
                return null;

            onSizeChanged(oldSize, mMessageQueue.size());
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> pollAllMessage() {
        return pollMessage(mMessageQueue.size());
    }

    /**
     * 从 queue 中获取第一条 message 并从 queue 中移除
     * @return t
     */
    public synchronized T pollMessage(){
        if(mShutdown){
            Logger.e("---MessageQueue has shutdown---");
            return null;
        }

        int oldSize = mMessageQueue.size();
        T t = mMessageQueue.poll();
        onSizeChanged(oldSize, mMessageQueue.size());
        return t;
    }

    private synchronized void onSizeChanged(int oldSize, int newSize){
        if(null != mOnMessageSizeChangedListener){
            mOnMessageSizeChangedListener.onMessageSizeChanged(newSize - oldSize);
            if(newSize >= mMaxMessageSize){
                // TODO: 2016/11/28 满，持久化
                mOnMessageSizeChangedListener.onMessageReachMax(newSize);
            }
        }
    }

}
