package com.example.primedrop;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Klasa odpowiedzialna za przypisanie dzwięków do elementów.
 */

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundPool;
    private static int hitPierwSound;
    private static int hitBananSound;
    private static int hitZlozSound;

    /**
     * Konstruktor SoundPlayera, który odtwarza dzwieki elementów spadających.
     *
     * @param context - konktekst uruchomienia gry.
     */
    public SoundPlayer(Context context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitPierwSound = soundPool.load(context, R.raw.pierw, 1);
        hitBananSound = soundPool.load(context, R.raw.banan, 1);
        hitZlozSound = soundPool.load(context, R.raw.zloz, 1);
    }

    /**
     * Funkcja przypisująca dźwię do bananów z liczbą pierwszą.
     */
    public void playHitPierwSound() {
        soundPool.play(hitPierwSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    /**
     * Funkcja przypisująca dźwię do bananów bonusowych.
     */
    public void playHitBananSound() {
        soundPool.play(hitBananSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    /**
     * Funkcja przypisująca dźwię do bananów z liczbą złożoną.
     */
    public void playHitZlozSound() {
        soundPool.play(hitZlozSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
