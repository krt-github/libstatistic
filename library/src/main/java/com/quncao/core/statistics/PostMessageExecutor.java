package com.quncao.core.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 数据提交
 */

class PostMessageExecutor <T> implements IPostMessage <T> {
    private IHttpClient<T> mHttpClient;
    private static PostMessageExecutor mPostMessageExecutor;
    private boolean mExit = false;
    private boolean mStart = false;

    private final List<Task> taskList = new ArrayList<>();

    private PostMessageExecutor(){
        mHttpClient = new HttpClientHelper<>();
    }

    static PostMessageExecutor getInstance(){
        if(null == mPostMessageExecutor){
            synchronized (PostMessageExecutor.class){
                if(null == mPostMessageExecutor){
                    mPostMessageExecutor = new PostMessageExecutor();
                }
            }
        }
        return mPostMessageExecutor;
    }

    @Override
    public void post(String url, T content) {
        if(null == url || "".equals(url) || null == content || !isReady4Post()) {
            //Maybe need callback
            Logger.e("---Some error occur, cancel post. Request url: " + url);
            return;
        }

        synchronized(taskList) {
            taskList.add(new Task(url, content));
            taskList.notify();
            Logger.d("---post list size: " + taskList.size());
        }
    }

    @Override
    public void post(String url, List<T> contentList) {
        if(null == contentList || contentList.size() <= 0) {
            Logger.e("---Empty message list, return---");
            return;
        }

        if(null == url || "".equals(url) || !isReady4Post()) {
            //Maybe need callback
            Logger.e("---Some error occur, cancel post. Request url: " + url);
            return;
        }

        synchronized (taskList){
            taskList.add(new Task(url, contentList));
            taskList.notify();
            Logger.d("---[contentList] post list size: " + taskList.size());
        }

    }

    @Override
    public void setPostMessageCallback(IHttpCallback<T> callback) {
        mHttpClient.setCallback(callback);
    }

    @Override
    public void setConverter(IConverter<List<T>, String> converter) {
        mHttpClient.setConverter(converter);
    }

    @Override
    public void start() {
        if(mExit){
            Logger.e("---Can not reuse a shutdown PostMessageExecutor---");
            return;
        }
        if(!mStart) {
            mStart = true;
            mExit = false;
            new Thread(getRunnable()).start();
            Logger.d("---PostMessageExecutor start---");
        }
    }

    @Override
    public void shutdown() {
        mExit = true;
        mHttpClient.cancel();
        synchronized(taskList){
            taskList.clear();
            taskList.notify();
        }
        mPostMessageExecutor = null;
        Logger.d("---PostMessageExecutor shutdown---");
    }

    private boolean isReady4Post(){
        if(!mStart){
            Logger.e("---Can not do post, you must call obj.start() before any post---");
            return false;
        }else if(mExit){
            Logger.e("---Can not reuse a shutdown executor---");
            return false;
        }
        return true;
    }

    private void doPostMessage(){
        List<Task> tasks = new ArrayList<>();
        synchronized(taskList) {
            if (taskList.size() <= 0)
                return;

            for(Task task : taskList){
                tasks.add(task);
            }
            taskList.clear();
        }

        Task task;
        for (int i = 0; i < tasks.size(); i++) {
            task = tasks.get(i);
            mHttpClient.request(task.url, task.contentList);
        }
        tasks.clear();
    }

    private Runnable getRunnable(){
        return new Runnable() {
            public void run() {
                while(!mExit){
                    try {
                        Logger.i("---*** wait ***--- ");
                        synchronized (taskList) {
                            //当正在执行 doPostMessage 时，可能有新的请求添加到 taskList ，在 doPostMessage 执行完成后
                            //回到此处进入 wait 前判断 taskList 是否为空，不为空则直接 doPostMessage
                            if(taskList.size() <= 0)
                                taskList.wait();
                        }
                        Logger.i("---*** wake up ***---");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!mExit) {
                        doPostMessage();
                    }
                }
                Logger.i("---PostMessageExecutor exit---");
            }
        };
    }

    private class Task{
        String url;
        List<T> contentList;

        Task(String url, T content){
            this.url = url;
            this.contentList = new ArrayList<>();
            this.contentList.add(content);
        }

        Task(String url, List<T> contentList){
            this.url = url;
            this.contentList = contentList;
        }
    }
}
