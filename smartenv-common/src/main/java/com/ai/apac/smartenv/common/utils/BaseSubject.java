package com.ai.apac.smartenv.common.utils;

import java.util.Observable;

public abstract class BaseSubject<T> extends Observable {


//    public abstract void dataChanged(T param);


    /**
     * 当数据变更的时候，通知所有观察者。
     * @param param
     */
    public void dataChanged(T param) {
        checkAndegistAllObserver(); //初始化所有的观察者
        setChanged(); // 设置数据为变更状态
        notifyObservers(param); //通知观察者变更
    }

    public abstract void checkAndegistAllObserver();

}
