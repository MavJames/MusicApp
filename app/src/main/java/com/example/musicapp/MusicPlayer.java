package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayer extends AppCompatActivity {
    final MediaPlayer mediaPlayer = new MediaPlayer();

    public final String TAG = "CPTR312";

    private final String PLAYER_STATE_KEY = "CURR_STATE";

    int currentPosition = 0;
    MusicDatabase dbase = null;
    private SeekBar seekBar;
    ImageButton playButton, pauseButton, rewindButton, forwardButton, nextButton, previousButton;
    private Timer timer;
    private TimerTask task;
    private boolean currState = false; // indicates if player is playing music
    private boolean prepared = false;
    private boolean shuffleMode = false;

    private boolean loopMode = false;

    String[] regularPlaylist;
    String[] shuffledPlaylist;

    String[] currentPlaylist;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);
        dbase = getIntent().getParcelableExtra(MusicApp.EXTRA_MESSAGE);
        shuffleMode = getIntent().getBooleanExtra(MusicApp.EXTRA_SHUFFLE_MODE, false);
        loopMode = getIntent().getBooleanExtra(MusicApp.EXTRA_LOOPING_MODE, false);


        regularPlaylist = dbase.getTitles();
        shuffledPlaylist = Arrays.copyOf(regularPlaylist, regularPlaylist.length);
        knuthShuffle(shuffledPlaylist);
        //Collections.shuffle(Arrays.asList(shuffledPlaylist));
        if (shuffleMode) {
            currentPlaylist = shuffledPlaylist;
        } else {
            currentPlaylist = regularPlaylist;
        }

        Song song = dbase.getSelection();
        setUpImage(song);
        seekBar = findViewById(R.id.seekBar);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        rewindButton = findViewById(R.id.rewindButton);
        forwardButton = findViewById(R.id.forwardButton);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);

        if(shuffleMode){
            Toast toast = Toast.makeText(getApplicationContext(), "The music player is automatically looping  ", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setUpImage(Song song) {
        TextView textView = findViewById(R.id.textView);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(getDrawable(song.getPicture()));
        textView.setText(dbase.getSelection().getTitle());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // currPosition = getPreferences(MODE_PRIVATE).getInt(PLAYER_POSITION_KEY,0);
        currState = getPreferences(MODE_PRIVATE).getBoolean(PLAYER_STATE_KEY, false);

        setUpMediaPlayer();
        setUpSeekBar();
        setUpTimer();
        setupPlay();
        setupPause();
        setupRewind();
        setupForward();
        setupNext();
        setupPrevious();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called...");
        getPreferences(MODE_PRIVATE).edit().putBoolean(PLAYER_STATE_KEY, mediaPlayer.isPlaying()).commit();

        prepared = false;
        mediaPlayer.reset();
    }

    private void setupPlay() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "MP Play Requested...");
                if (prepared) {
                    mediaPlayer.start();
                    currState = true;
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            int index = dbase.getCurrentSongIndex(dbase.getSelection().getTitle());
                            if (shuffleMode) {
                                index = currentSelectionToShuffledIndex();
                                if (index == currentPlaylist.length - 1) {
                                    knuthShuffle(currentPlaylist);
                                }
                            }

                            if (!loopMode) {
                                if (!shuffleMode) {
                                    if (index == currentPlaylist.length - 1) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "To continue playing turn looping on or shuffle", Toast.LENGTH_SHORT);
                                        toast.show();
                                        return;
                                    }
                                }

                            }
                            // String[] titles;
                            index = (index + 1) % currentPlaylist.length;
                            dbase.setSelection(currentPlaylist[index]);
                            mediaPlayer.reset();
                            setUpMediaPlayer();
                            setUpSeekBar();
                            setUpImage(dbase.getSelection());
                            index = dbase.getCurrentSongIndex(dbase.getSelection().getTitle());
                        }
                    });
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_SHORT);
                    toast.show();

                }

            }
        });
    }

    private void setupPause() {
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prepared && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    currState = false;

                }
            }
        });
    }

    private void setupRewind() {
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = mediaPlayer.getCurrentPosition();
                if (currentPosition - 5000 > 0) {
                    currentPosition -= 5000;
                    mediaPlayer.seekTo(currentPosition);
                    seekBar.setProgress(currentPosition);
                }
            }
        });
    }

    private void setupForward() {
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                currentPosition += 5000;
                mediaPlayer.seekTo(currentPosition);
                seekBar.setProgress(currentPosition);
            }
        });

    }

    private void setupNext() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Count " + count);
                int index = dbase.getCurrentSongIndex(dbase.getSelection().getTitle());
                if (shuffleMode) {
                    index = currentSelectionToShuffledIndex();
                    if (index == currentPlaylist.length - 1) {
                        knuthShuffle(currentPlaylist);
                    }
                }

                if (!loopMode) {
                    if (!shuffleMode) {
                        if (index == currentPlaylist.length - 1) {
                            Toast toast = Toast.makeText(getApplicationContext(), "To continue playing turn looping on or shuffle", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                    }

                }
                index = (index + 1) % currentPlaylist.length;
                dbase.setSelection(currentPlaylist[index]);
               // count = count++ % currentPlaylist.length;


                String title = dbase.getSelection().getTitle();
                Log.i(TAG, "Current selection " + title);
                Log.i(TAG, "Index is " + index);
                Log.i(TAG, "List is " + Arrays.toString(currentPlaylist));
                Log.i(TAG, "Unshuffled list is " + Arrays.toString(regularPlaylist));
                mediaPlayer.reset();
                setUpMediaPlayer();
                setUpSeekBar();
                setUpImage(dbase.getSelection());
            }
        });
    }

    private int currentSelectionToShuffledIndex() {
        String title = dbase.getSelection().getTitle();
        for (int i = 0; i < currentPlaylist.length; i++) {
            if (currentPlaylist[i].equalsIgnoreCase(title))
                return i;
        }
        return -1;
    }


    private void setupPrevious() {
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = dbase.getCurrentSongIndex(dbase.getSelection().getTitle());
                //String[] titles;
                if (shuffleMode) {
                    index = currentSelectionToShuffledIndex();
                    if (index == currentPlaylist.length - 1) {
                        knuthShuffle(currentPlaylist);
                    }
                }

                index = (index - 1);
                if (index < 0) {
                    index = currentPlaylist.length - 1;
                }

                dbase.setSelection(currentPlaylist[index]);
                mediaPlayer.reset();
                setUpMediaPlayer();
                setUpSeekBar();
                setUpImage(dbase.getSelection());
            }

        });
    }


    private void setUpMediaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                prepared = true;
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setMin(0);
                mediaPlayer.seekTo(0);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                if (currState) {
                    mp.start();
                }
            }
        });
        AssetFileDescriptor afd = getResources().openRawResourceFd(dbase.getSelection().getId());
        try {
            mediaPlayer.setDataSource(afd);
            seekBar.setProgress(0);
            mediaPlayer.prepareAsync();
            afd.close();
        } catch (IOException e) {
            Log.d(TAG, "Exception when setting data source!");
            Log.d(TAG, e.getMessage());
        }
    }

    private void setUpTimer() {
        task = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };
        timer = new Timer();
        timer.schedule(task, 50, 200);
    }

    private void setUpSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "OnSeekbarListener...");
                int curr = seekBar.getProgress();
                mediaPlayer.seekTo(curr);
            }
        });
    }


    private void knuthShuffle(String[] keys) {
        for (int i = 1; i < keys.length; i++) {
            Random rand = new Random();
            swap(i, rand.nextInt(i + 1), keys);
        }
        return;
    }

    private void swap(int i, int j, String[] keys) {
        String tmp = keys[i];
        keys[i] = keys[j];
        keys[j] = tmp;
    }

}