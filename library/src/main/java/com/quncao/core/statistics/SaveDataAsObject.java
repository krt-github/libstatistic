package com.quncao.core.statistics;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zehong.tang on 2016/11/25.
 * 数据持久化，存对象
 */

class SaveDataAsObject <T> implements IDataPersistence<T> {
    private File mSavePath;

    SaveDataAsObject(){
        try {
            mSavePath = new File(Environment.getExternalStorageDirectory(), "larkSP"); //lark statistics persistence
            mSavePath.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean persistenceData(T data) {
        Logger.i("---save data: " + data);

        if(null == data || null == mSavePath)
            return false;

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mSavePath.getAbsolutePath() + File.separatorChar + System.currentTimeMillis() + ".obj");
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != objectOutputStream){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != fileOutputStream){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public List<T> getAllData() {
        Logger.i("---get all data---");

        if(null == mSavePath)
            return null;

        File[] files = mSavePath.listFiles();
        if(null == files || files.length <= 0)
            return null;

        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            List<T> res = new ArrayList<>();
            for(File f : files){
                if(!f.isFile())
                    continue;
                fileInputStream = new FileInputStream(f);
                objectInputStream = new ObjectInputStream(fileInputStream);
                res.add((T) objectInputStream.readObject());
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != objectInputStream){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != fileInputStream){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void clearAll() {
        Logger.d("---clear all persistence data---path: " + mSavePath);
        if(null == mSavePath)
            return;

        try {
            deleteFile(mSavePath);
            mSavePath.mkdirs();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        mSavePath = null;
    }

    private void deleteFile(File file){
        if(null == file)
            return;
        if(file.isFile()){
            file.delete();
        }else{
            File[] files = file.listFiles();
            for(File f : files) {
                deleteFile(f);
            }
            file.delete();
        }
    }
}
