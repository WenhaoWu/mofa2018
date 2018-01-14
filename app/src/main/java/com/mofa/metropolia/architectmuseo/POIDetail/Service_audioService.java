package com.mofa.metropolia.architectmuseo.POIDetail;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class Service_audioService extends Service {

    public static final String ARG_TRACK = "track_argument";

    private MediaPlayer player;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String trackID = intent.getStringExtra(ARG_TRACK);

        String url = "https://api.soundcloud.com/tracks/"+trackID+"/stream?client_id=9e5573e809642860302485cbd28feb09";
        try{
            player.setDataSource(url);
        } catch(Exception e){
            Log.e("Cannot open given URL", e.toString());
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.prepareAsync();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });

        return 1;
    }

    public void onStop(){
        player.stop();
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

}
