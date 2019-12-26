package com.quncao.larkstatistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quncao.core.statistics.BaseParams;
import com.quncao.core.statistics.CountTrigger;
import com.quncao.core.statistics.LarkStatistics;
import com.quncao.core.statistics.NetChangedTrigger;
import com.quncao.core.statistics.TimeTrigger;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private LarkStatistics mLarkStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        test();
    }

    @Override
    protected void onDestroy() {
        if(null != mLarkStatistics){
            mLarkStatistics.saveData();
            mLarkStatistics.shutdown();
        }
        super.onDestroy();
    }

    private void init() {
        MyLis l = new MyLis();
        findViewById(R.id.start).setOnClickListener(l);
        findViewById(R.id.shutdown).setOnClickListener(l);
        findViewById(R.id.click).setOnClickListener(l);
        findViewById(R.id.click_with_params).setOnClickListener(l);
        findViewById(R.id.persistence_data).setOnClickListener(l);
        findViewById(R.id.read_persistence_data).setOnClickListener(l);
    }

    private void test(){
        BaseParams baseParams = new BaseParams();
        baseParams.larkid = "912";
        baseParams.dev_id = "LAJLA09FQEJF9ADFALDKF";
        baseParams.ver = "V2.3";
        mLarkStatistics = LarkStatistics.getInstance(getApplicationContext());
        mLarkStatistics.setEnableDebug(true)
                .setServerUrl("https://log.quncaotech.com/logstore/upload")
                .addTrigger(new TimeTrigger(1000 * 5, true))
                .addTrigger(new CountTrigger(5, true))
                .addTrigger(new NetChangedTrigger(getApplicationContext()))
                .setBaseParams(baseParams)
                .start();
        mLarkStatistics.loadLastData();
    }

    private class MyLis implements View.OnClickListener{
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.start: mLarkStatistics.start();
                    break;
                case R.id.shutdown: mLarkStatistics.shutdown();
                    break;
                case R.id.click: mLarkStatistics.onEvent("-模拟不带参 event-");
                    break;
                case R.id.click_with_params:
                    Map<String, String> map = new HashMap<>();
                    map.put("key1", "value1");
                    map.put("key2", "value2");
                    map.put("key3", "value3");
                    mLarkStatistics.onEvent("-模拟带参数 event-", map);
                    break;
                case R.id.persistence_data: mLarkStatistics.saveData();
                    break;
                case R.id.read_persistence_data: mLarkStatistics.loadLastData();
                    break;
            }
        }
    }
}
