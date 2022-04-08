package com.example.smallfeatureuse.bean;

public class ItemData<T> {
    T t;
    int dataType;

    public ItemData(T t, int type){
        this.t = t;
        this.dataType = type;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
