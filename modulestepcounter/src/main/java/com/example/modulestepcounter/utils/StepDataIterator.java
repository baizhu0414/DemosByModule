package com.example.modulestepcounter.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

public class StepDataIterator {
    private final DBOpenHelper stepHelper;
    private SQLiteDatabase stepDb;

    public StepDataIterator(Context context) {
        stepHelper = new DBOpenHelper(context);
    }

    /**
     * 添加一条新记录
     *
     * @param stepEntity
     */
    public void insertNewData(StepEntity stepEntity) {
        stepDb = stepHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("curDate", stepEntity.curDate);
        values.put("totalSteps", stepEntity.steps);
        stepDb.insert("step", null, values);
        stepDb.close();
    }

    /**
     * 根据日期查询记录
     *
     * @param curDate
     * @return
     */
    public StepEntity getCurDataByDate(String curDate) {
        stepDb = stepHelper.getReadableDatabase();
        StepEntity stepEntity = null;
        Cursor cursor = stepDb.query("step", null, null,
                null, null, null, null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow("curDate"));
            if (date.equals(curDate)) {
                String steps = cursor.getString(cursor.getColumnIndexOrThrow("totalSteps"));
                stepEntity = new StepEntity(date, steps);
                //跳出循环
                break;
            }
        }
        //关闭
        stepDb.close();
        cursor.close();
        return stepEntity;
    }

    /**
     * 查询所有的记录
     *
     * @return
     */
    public List<StepEntity> getAllDatas() {
        List<StepEntity> dataList = new ArrayList<StepEntity>();
        stepDb = stepHelper.getReadableDatabase();
        Cursor cursor = stepDb.rawQuery("select * from step", null);

        while (cursor.moveToNext()) {
            String curDate = cursor.getString(cursor.getColumnIndex("curDate"));
            String totalSteps = cursor.getString(cursor.getColumnIndex("totalSteps"));
            StepEntity entity = new StepEntity(curDate, totalSteps);
            dataList.add(entity);
        }

        //关闭数据库
        if (stepDb != null) {
            stepDb.close();
        }
        cursor.close();
        return dataList;
    }

    /**
     * 更新数据
     *
     * @param stepEntity
     */
    public void updateCurData(StepEntity stepEntity) {
        stepDb = stepHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("curDate", stepEntity.curDate);
        values.put("totalSteps", stepEntity.steps);
        stepDb.update("step", values, "curDate=?", new String[]{stepEntity.curDate});

        stepDb.close();
    }


    /**
     * 删除指定日期的记录
     *
     * @param curDate
     */
    public void deleteCurData(String curDate) {
        stepDb = stepHelper.getReadableDatabase();

        if (stepDb.isOpen()) {
            stepDb.delete("step", "curDate", new String[]{curDate});
        }
        stepDb.close();
    }
}