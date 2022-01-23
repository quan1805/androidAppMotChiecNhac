package huce.fit.motchiecnhac;

import static huce.fit.motchiecnhac.ApplicationClass.ACTION_CLEAR;
import static huce.fit.motchiecnhac.ApplicationClass.ACTION_NEXT;
import static huce.fit.motchiecnhac.ApplicationClass.ACTION_PLAY;
import static huce.fit.motchiecnhac.ApplicationClass.ACTION_PREVIOUS;
import static huce.fit.motchiecnhac.ApplicationClass.CHANNEL_ID_1;
import static huce.fit.motchiecnhac.ApplicationClass.CHANNEL_ID_2;
import static huce.fit.motchiecnhac.PlayMusic.listSongs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"My Audio" );

        //musicFiles = listSongs;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MotChiecNhac", "Service Destroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if (myPosition != -1 ) {
            playMedia(myPosition);
        }
        if (actionName != null){
            switch (actionName){
                case "playPause":
                    Toast.makeText(this,
                            "PlayPause", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null) {
                        Log.e("Inside", "Action");
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
                    Toast.makeText(this,
                            "Next", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null) {
                        Log.e("Inside", "Action");
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    Toast.makeText(this,
                            "Previous", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        Log.e("Inside", "Action");
                        actionPlaying.prevBtnClicked();
                    }
                    break;
                case "clear":
                    Toast.makeText(this,
                            "Clear", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null){
                        Log.e("Unbind and Destroy", "Service");

                        stopSelf();
                        //unbind
                        mediaPlayer.pause();
                        //actionPlaying.playPauseBtnClicked();


                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listSongs;
        position = StartPosition;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start() {
        mediaPlayer.start();
    }

    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    void stop() {
        mediaPlayer.stop();
    }

    void release() {
        mediaPlayer.release();
    }

    int getDuration() {
        return mediaPlayer.getDuration();
    }

    void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    void createMediaPlayer(int positionInner) {
        position = positionInner;
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    void pause() {
        mediaPlayer.pause();
    }

    void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
            if (mediaPlayer != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
                onCompleted();


            }
        }

    }
    void setCallBack(ActionPlaying actionPlaying)
    {
        this.actionPlaying = actionPlaying;
    }
    void showNotification(int playPauseBtn){
        Intent intent = new Intent(this, PlayMusic.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,
                0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent
                .getBroadcast(this, 0,prevIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent
                .getBroadcast(this, 0,pauseIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent clearIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_CLEAR);
        PendingIntent clearPending = PendingIntent
                .getBroadcast(this, 0,clearIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent
                .getBroadcast(this, 0,nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        byte [] picture = null;
        picture = getAlbumArt(musicFiles.get(position).getPath());
        Bitmap thumb = null;
        if ( picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0 , picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.artist);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.ic_music_logo_white)
                .setLargeIcon(thumb)
                //.setSubText(listSongs.get(position).getAlbum())
                .setContentTitle(musicFiles.get(position).getTitle())
                .setContentText(musicFiles.get(position).getArtist())
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_previous, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_next, "Next", nextPending)
                .addAction(R.drawable.ic_clear, "Clear",clearPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                //.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.notify(0, notification);
                startForeground(1, notification);
    }
    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
