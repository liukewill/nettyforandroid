package com.kenan.nettyforandroid.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by kenan
 * 定时任务
 * 用于心跳检测
 */
public class TimerSchedule {

    private long mPeriod;
    private long mAppendPeriod;
    private long mIgnoreInterval;
    private long mLastExecTime;

    private Context mContext;
    private String mBroadcastAction = "com.baidu.waimai.action.timer.schedule";
    private TimerScheduleCallback mCallback;

    private boolean mIsReceiverRegisted = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public TimerSchedule(Context context,TimerScheduleCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public TimerSchedule(Context context, String broadcastAction, TimerScheduleCallback callback) {
        this.mContext = context;
        this.mBroadcastAction = broadcastAction;
        this.mCallback = callback;
    }

    /**
     * suggest appendPeriod = period
     * suggest ignoreInterval = period / 2
     * or
     * suggest appendPeriod = period * 3
     * suggest ignoreInterval = period / 3
     */
    public void start(long delay, long period, long appendPeriod, long ignoreInterval) {
        mPeriod = period;
        mAppendPeriod = appendPeriod;
        mIgnoreInterval = ignoreInterval;
        startPeriodTask(delay);
        startAppendPeriodTask(delay + appendPeriod);
        registeBroadcastReceiver();
    }

    public void stop() {
        stopPeriodTask();
        stopAppendPeriodTask();
        unregisteBroadcastReceiver();
    }

    private void startPeriodTask(long delay) {
        if (mPeriod <= 0) {
            return;
        }
        stopPeriodTask();
        mHandler.postDelayed(mRunnable, delay);
    }

    private void stopPeriodTask() {
        mHandler.removeCallbacks(mRunnable);
    }

    private void startAppendPeriodTask(long delay) {
        if (mAppendPeriod <= 0) {
            return;
        }
        stopAppendPeriodTask();
        Intent intent = new Intent();
        intent.setAction(mBroadcastAction);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Service.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay,
                mAppendPeriod, pendingIntent);
    }

    private void stopAppendPeriodTask() {
        Intent intent = new Intent();
        intent.setAction(mBroadcastAction);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Service.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void registeBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(mBroadcastAction);
        try {
            mContext.registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsReceiverRegisted = true;
    }

    private void unregisteBroadcastReceiver() {
        if (!mIsReceiverRegisted) {
            return;
        }
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsReceiverRegisted = false;
    }

    private void doSchedule() {
        long freeTime = System.currentTimeMillis() - mLastExecTime;
        if (freeTime > mIgnoreInterval) {
            mLastExecTime = System.currentTimeMillis();
            if (mCallback != null) {
                mCallback.doSchedule();
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSchedule();
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, mPeriod);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBroadcastAction.equals(intent.getAction())) {
                doSchedule();
            }
        }
    };

    public interface TimerScheduleCallback {
        public void doSchedule();
    }
}

