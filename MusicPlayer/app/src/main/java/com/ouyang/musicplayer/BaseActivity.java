package com.ouyang.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * 绑定service
 *
 */
public abstract class BaseActivity extends FragmentActivity {

    protected PlayService playService;

    private boolean isbound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());//根据playService的getCurrentPosition方法得到当前所调用的歌曲位置，使得在绑定服务的时候更新歌曲状态。
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isbound = false;
        }
    };

    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }
        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    //让子类自己实现更新UI的方法。
    public abstract void publish(int progress);
    public abstract void change(int position);


    //绑定服务方法。由子类决定何时调用
    public void bindPlayService() {
        if (!isbound) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, BIND_AUTO_CREATE);
            isbound = true;
        }
    }

    //解除绑定方法，由子类决定何时调用
    public void unbindPlayService() {
        if (isbound) {
            unbindService(conn);
            isbound = false;
        }
    }
}
