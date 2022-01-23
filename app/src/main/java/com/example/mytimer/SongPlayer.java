package com.example.mytimer;

import android.media.MediaPlayer;
import android.util.Log;

/*Custom song player class*/
/*Use MediaPlayer class and flag to track current stage of player*/
public class SongPlayer{
    private MediaPlayer player;
    private boolean isPlaying = false;
/*Check if MediaPLayer is already initialize, if yes, continue the player*/
/*If not, initialize and start the player*/
    public void playSong(MediaPlayer mediaPlayer){
        if(player == null){
            player = mediaPlayer;
            player.setLooping(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                    isPlaying = false;
                }
            });
        }
        player.start();
        isPlaying = true;
        }
/*Pause the player*/
    public void pauseSong() {
        if (player != null) {
            player.pause();
        }
        isPlaying = false;
    }

    public void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Log.d("Player","release");
        }
    }
/*Get if the player is playing or not*/
    public boolean getPlayerStatus(){
        return isPlaying;
    }
    }

