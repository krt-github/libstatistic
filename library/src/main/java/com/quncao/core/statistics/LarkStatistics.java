package com.quncao.core.statistics;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by zehong.tang on 2016/11/29.
 * 百灵鸟统计工具
 */

public class LarkStatistics {
    private static LarkStatistics mLarkStatistics;

    private IMessageQueue<MessageBean> mMessageQueue;
    private ITriggerManager mTriggerManager;
    private CountTrigger mCountTrigger;
    private IPostMessage<MessageBean> mPostExecutor;
    private IDataPersistence<List<MessageBean>> mDataPersistence;
    private BaseParams mBaseParams;

    private Context mContext;
    private String mServerUrl;
    private boolean mOnlyWiFiDoPost = false;

    private LarkStatistics(Context context){
        if(null == context){
            throw new IllegalArgumentException("---Context is null reference---");
        }

        mContext = context;
        mMessageQueue = new MessageQueue<>();
        mPostExecutor = PostMessageExecutor.getInstance();
        mDataPersistence = new SaveDataAsObject<>();
        mTriggerManager = TriggerManager.getInstance();

        mPostExecutor.setConverter(new IConverter<List<MessageBean>, String>() {
            public String convert(List<MessageBean> data) {
                return convert2HttpParams(data);
            }
        });
        mPostExecutor.setPostMessageCallback(new IHttpCallback<MessageBean>() {
            public void onSuccess() {
                toast("---onSuccess---");
            }
            public void onFailed(String msg, List<MessageBean> requestData) {
                toast("---onFailed, errMsg: " + msg + "  requestData: " + requestData);
                mMessageQueue.addMessage(requestData);
            }
        });
        mTriggerManager.setOnTriggerListener(new OnTriggerListener() {
            public void onTrigger(ITrigger sourceTrigger) {
                Logger.i("---onTrigger source: " + (null == sourceTrigger ? "[null]" : sourceTrigger.getName()));
                doPost();
            }
        });
        mMessageQueue.setOnMessageSizeChangedListener(new IOnMessageSizeChangedListener() {
            public void onMessageSizeChanged(int size) {
                mCountTrigger.onCountChanged(size);
            }
            public void onMessageReachMax(int max) {}
        });
    }

    public static LarkStatistics getInstance(Context context){
        if(null == mLarkStatistics){
            synchronized (LarkStatistics.class){
                if(null == mLarkStatistics){
                    mLarkStatistics = new LarkStatistics(context);
                }
            }
        }
        return mLarkStatistics;
    }

    public LarkStatistics addTrigger(ITrigger trigger){
        mTriggerManager.addTrigger(trigger);
        if(trigger instanceof CountTrigger){
            mCountTrigger = (CountTrigger) trigger;
        }
        return this;
    }

    public LarkStatistics setBaseParams(BaseParams baseParams){
        mBaseParams = baseParams;
        return this;
    }

    public LarkStatistics setServerUrl(String url){
        mServerUrl = url;
        return this;
    }

    public void onEvent(String eventId){
        mMessageQueue.addMessage(generateMessageBean(eventId));
    }

    public void onEvent(String eventId, Map<String, String> keyValues){
        mMessageQueue.addMessage(generateMessageBean(eventId, keyValues));
    }

    public void start(){
        mTriggerManager.start();
        mPostExecutor.start();
        NetUtils.init(mContext);
        Logger.e("---Server url: " + mServerUrl);
    }

    public void shutdown(){
        mTriggerManager.shutdown();
        mPostExecutor.shutdown();
        mMessageQueue.shutdown();
        mServerUrl = null;
        mContext = null;
        mLarkStatistics = null;
        NetUtils.shutdown();
    }

    public void manualPost(){
        doPost();
    }

    public void saveData(){
        mDataPersistence.persistenceData(mMessageQueue.pollAllMessage());
    }

    public void loadLastData(){
        List<List<MessageBean>> lastData = mDataPersistence.getAllData();
        if(null != lastData) {
            Logger.d("---last data size: " + lastData.size());
            boolean isNetReady = isWiFiReady();
            for(List<MessageBean> subList : lastData) {
                if(isNetReady){
                    mPostExecutor.post(mServerUrl, subList);
                }else {
                    mMessageQueue.addMessage(subList);
                }
            }
            mDataPersistence.clearAll();
        }
    }

    public LarkStatistics setEnableDebug(boolean enabled){
        Logger.setEnableDebug(enabled);
        return this;
    }

    public LarkStatistics setOnlyWiFiWork(boolean onlyWiFiWork){
        mOnlyWiFiDoPost = onlyWiFiWork;
        return this;
    }

    public boolean isOnlyWiFiWork(){
        return mOnlyWiFiDoPost;
    }

    private void toast(String tip){
        Logger.i("---tip: " + tip);
    }

    private boolean isWiFiReady(){
        return NetUtils.isWiFi();
    }

    private boolean isNetReady(){
        return NetUtils.isConnected();
    }

    private void doPost(){
        if(isNetReady()){
            if(!isOnlyWiFiWork()){
                post();
            }else if(isWiFiReady()){
                post();
            }else{
                Logger.e("---WiFi not ready---");
            }
        }else{
            Logger.e("---Net not ready---");
        }
    }

    private void post(){
        mPostExecutor.post(mServerUrl, mMessageQueue.pollAllMessage());
    }

    private MessageBean generateMessageBean(String eventId){
        MessageBean messageBean = new MessageBean();
        messageBean.event_id = eventId;
        messageBean.time = System.currentTimeMillis() + "";
        return messageBean;
    }

    private MessageBean generateMessageBean(String eventId, Map<String, String> keyValues){
        MessageBean messageBean = generateMessageBean(eventId);
        messageBean.content = keyValues;
        return messageBean;
    }

    private String convert2HttpParams(List<MessageBean> messageList){
        if(null == messageList || messageList.size() <= 0) {
            Logger.e("---Data is empty, convert return---");
            return null;
        }

        if(null == mBaseParams){
            mBaseParams = new BaseParams();
        }

        try{
            JSONObject jsonParams = new JSONObject(new Gson().toJson(mBaseParams));
            jsonParams.put("logs", new JSONArray(new Gson().toJson(messageList)));
            return jsonParams.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }

}
