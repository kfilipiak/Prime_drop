package com.example.primedrop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Klasa główna gry - to tutaj dokonują się wszystkie obliczenia.
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Tutaj inicjowane są wszystkie zmienne.
     */
    //Pole gry

    FrameLayout poleGry;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startGry;

    //Grafika
    private TextView bananyPierw, bananyZloz;
    private int bananPierwWartosc = 0, bananZlozWartosc = 0;
    private ImageView malpa, banan;
    private Drawable imgMalpaLewo, imgMalpaPrawo ;

    //Rozmiar malpy
    private int malpaRozmiar;

    //Pozycja
    private float malpaX, malpaY;
    private float bananyPierwX, bananyPierwY;
    private float bananyZlozX, bananyZlozY;
    private float bananX, bananY;


    //Wynik
    private TextView poleWynik, poleRekord;
    private int Wynik, Rekord, Czas;
    private SharedPreferences ustawienia;


    //Klasa
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    //Start
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean banan_flg = false;

    //Tablice z libczami zlozonymi i pierwszymi
    private static final int[] COMPOSITE = {4,6,8,9,10,12,14,15,16,18,20,21,22,24,25,26,27,28,30,32,33,34,35,36,38,39,
            40,42,44,45,46,48,49,50,51,52,54,55,56,57,58,60,62,63,64,65,66,68,69,70,
            72,74,75,76,77,78,80,81,82,84,85,86,87,88,90,91,92,93,94,95,96,98,99,100};

    private static final int[] PRIMES = {1,2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};

    //Zmienna Random
    private final Random rnd = new Random();

    /**
     * Wczytywanie widoku gry oraz wywołanie klasy z dzwiękami.
     *
     * @param savedInstanceState - zapisywanie najwyższego wyniku również po wyłączeniu gry
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        poleGry = findViewById(R.id.poleGry);
        startGry = findViewById(R.id.startGry);
        malpa = findViewById(R.id.malpa);
        bananyPierw = findViewById(R.id.bananyPierw);
        bananyZloz = findViewById(R.id.bananyZloz);
        banan = findViewById(R.id.banan);
        poleWynik = findViewById(R.id.poleWynik);
        poleRekord = findViewById(R.id.poleRekord);


        imgMalpaLewo = getResources().getDrawable(R.drawable.malpa2);
        imgMalpaPrawo = getResources().getDrawable(R.drawable.malpa1);

        //Rekord punktowy
/**
 * Ustawienie rekordu punktowego
 */
        ustawienia = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        Rekord = ustawienia.getInt("NAJWYZSZY_WYNIK", 0);
        poleRekord.setText("Najwyzszy wynik : " + Rekord);

    }

    /**
     * Funkcja odpowiadająca za zmianę pozycji elementów spadających i za ruch małpy.
     */
    public void zmianaPozycji() {
        /**
         * Fragment opisu ruchu banana z liczbą pierwszą.
         */
        //Banany z liczba pierwsza
        bananyPierwY +=12;

        float bananyPierwCenterX = bananyPierwX + bananyPierw.getWidth()/2;
        float bananyPierwCenterY = bananyPierwY + bananyPierw.getHeight()/2;

        if (hitCheck(bananyPierwCenterX, bananyPierwCenterY)) {
            bananyPierwY = frameHeight + 100;

            Wynik += 10;
            //Wywoływanie funkcji odtwarzającej dźwięk.
            soundPlayer.playHitPierwSound();
        }

        if (bananyPierwY > frameHeight && bananyPierwCenterX!= (bananyZlozX + bananyZloz.getWidth() /2)){
            bananyPierwY = - 100;
            bananyPierwX = (float) Math.floor(Math.random() * (frameWidth - bananyPierw.getWidth()));
            calculateAndSetNewPrimeNumber();
        }
        bananyPierw.setX(bananyPierwX);
        bananyPierw.setY(bananyPierwY);

        /**
        * Fragment opisu ruchu bonusowego banana.
        */
        //Banan
        if (!banan_flg && Czas % 10000 == 0){
            banan_flg = true;
            bananY = -5000;
            bananX = (float)Math.floor(Math.random()*(frameWidth - banan.getWidth()));
        }

        if (banan_flg){
            bananY += 20;

            float bananCenterX = bananX + banan.getWidth() /2;
            float bananCenterY = bananY + banan.getHeight() /2;

            if (hitCheck(bananCenterX,bananCenterY)){
                bananY = frameHeight + 30;
                Wynik += 30;

                /**
                 * Poszerzanie ramki.
                 */
                // Zmiana szerokosci ramki

                if (initialFrameWidth > frameWidth * 110/100){
                    frameWidth = frameWidth * 110/100;
                    zmianaSzerokosciPola(frameWidth);
                }
                //Wywołanie funkcji odtwarzającej dźwięk.
                soundPlayer.playHitBananSound();
            }

            if (bananY > frameHeight) banan_flg = false;
            banan.setX(bananX);
            banan.setY(bananY);
        }
        /**
         * Fragment opisu ruchu banana z liczbą złożoną.
         */
        //Banany z liczba zlozona

        bananyZlozY += 12;

        float bananyZlozCenterX = bananyZlozX + bananyZloz.getWidth() /2;
        float bananyZlozCenterY = bananyZlozY + bananyZloz.getHeight() /2;

        if (hitCheck(bananyZlozCenterX, bananyZlozCenterY)) {
            bananyZlozY = frameHeight + 100;


            /**
             *  Zwężanie ramki.
             */
            // Zmiana szerokosci ramki

            frameWidth = frameWidth * 80/100;
            zmianaSzerokosciPola(frameWidth);
            soundPlayer.playHitZlozSound();
            if (frameWidth <= malpaRozmiar) {
                koniecGry();


            }
        }

        if (bananyZlozY > frameHeight) {
            bananyZlozY = -100;
            bananyZlozX = (float) Math.floor(Math.random() * (frameWidth - bananyZloz.getWidth()));
            calculateAndSetNewCompositeNumber();
        }

        bananyZloz.setX(bananyZlozX);
        bananyZloz.setY(bananyZlozY);

        /**
         * Fragment opisu ruchu małpy podczas dotykania ekranu.
         */
        //Ruch Malpy
        if (action_flg){
            //W prawo
            malpaX += 14;
            malpa.setImageDrawable(imgMalpaPrawo);
        }else {
            //w lewo
            malpaX -= 14;
            malpa.setImageDrawable(imgMalpaLewo);
        }

        //Sprawdzanie pozycji malpy
        if (malpaX < 0) {
            malpaX = 0;
            malpa.setImageDrawable(imgMalpaPrawo);
        }
        if (frameWidth - malpaRozmiar < malpaX) {
            malpaX = frameWidth - malpaRozmiar;
            malpa.setImageDrawable(imgMalpaLewo);
        }

        malpa.setX(malpaX);

        poleWynik.setText("Wynik : " + Wynik);
    }


    /**
     * Funkcja sprawdzająca czy spadający obiekt dotknie małpę. Opis pola, w którym małpa złapie banany jeśli ją dotkną.
     * @param x - szerokość małpy
     * @param y - wysokość małpy
     * @return - zwraca informację czy gracz złapał spadający element.
     */
    public boolean hitCheck(float x, float y){
        if (malpaX <= x && x<= malpaX + malpaRozmiar &&
                malpaY <= y && y<= frameHeight) {
            return true;
        }
        return false;

    }

    /**
     * Funkcja odpowiedzialna za zmianę szerokości pola gry.
     * @param frameWidth - szerokość pola gry
     */
    public void zmianaSzerokosciPola(int frameWidth){
        ViewGroup.LayoutParams params = poleGry.getLayoutParams();
        params.width = frameWidth;
        poleGry.setLayoutParams(params);
    }

    /**
     * Funkcja kończąca grę jeśli gracz przegra, wyświetlająca ekran główny i aktualizująca wynik, o ile rekord został pobity.
     */
    public void koniecGry() {
        //Stop zegara
        timer.cancel();
        timer = null;
        start_flg = false;

        //Przed wyswietleniem ekranu startowego sleep 1s

        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        zmianaSzerokosciPola(initialFrameWidth);

        //wyswietlenie ekranu startowego
        startGry.setVisibility(View.VISIBLE);
        malpa.setVisibility(View.INVISIBLE);
        bananyZloz.setVisibility(View.INVISIBLE);
        bananyPierw.setVisibility(View.INVISIBLE);
        banan.setVisibility(View.INVISIBLE);


        //Aktualizacja najwyzszego wyniku

        if(Wynik > Rekord){
            Rekord = Wynik;
            poleRekord.setText("Najwyzszy wynik : " + Rekord);

            SharedPreferences.Editor edycja = ustawienia.edit();
            edycja.putInt("NAJWYZSZY_WYNIK", Rekord);
            edycja.commit();
        }

    }

    /**
     * Wprowadzenie akcji podczas dotknięcia ekranu.
     * @param event - dotknięcie ekranu.
     * @return - true/false w zależności od tego czy ekran jest dotykany przez gracza.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            }else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    /**
     * Funkcja odpowiadająca za start gry. Znika ekran główny, wszystkie elementy gry są ustawiane na domyślnej pozycji, losowanie liczb, rozpoczyna się ruch elementów po kliknięciu przycisku.
     * @param view -
     */
    public void startGame(View view) {

        calculateAndSetNewCompositeNumber();
        calculateAndSetNewPrimeNumber();

        start_flg = true;
        startGry.setVisibility(View.INVISIBLE);

        if (frameHeight ==0) {
            frameHeight = poleGry.getHeight();
            frameWidth = poleGry.getWidth();
            initialFrameWidth = frameWidth;

            malpaRozmiar = malpa.getHeight();
            malpaX = malpa.getX();
            malpaY = malpa.getY();

        }

        frameWidth = initialFrameWidth;

        malpa.setX(0.0f);
        bananyPierw.setY(3000.0f);
        bananyZloz.setY(3000.0f);
        banan.setY(3000.0f);

        bananyPierwY = bananyPierw.getY();
        bananyZlozY = bananyZloz.getY();
        bananY = banan.getY();

        malpa.setVisibility(View.VISIBLE);
        bananyPierw.setVisibility(View.VISIBLE);
        bananyZloz.setVisibility(View.VISIBLE);
        banan.setVisibility(View.VISIBLE);

        Czas = 0;
        Wynik = 0;
        poleWynik.setText("Wynik : 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (start_flg){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            zmianaPozycji();
                        }
                    });
                }
            }
        }, 0, 20);

    }

    /**
     * Wyjście z gry. Koniec pracy programu po kliknięciu przycisku.
     * @param view -
     */
    public void quitGame(View view) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            finishAndRemoveTask();
        }else{
            finish();
        }
    }

    /**
     * Funkcja losująca liczbę złożoną z tablicy.
     */
    private void calculateAndSetNewCompositeNumber() {


        int x = rnd.nextInt(COMPOSITE.length);
        bananZlozWartosc = COMPOSITE[x];
        bananyZloz.setText(String.valueOf(bananZlozWartosc));

    }

    /**
     * Funkcja losująca liczbę pierwszą z tablicy.
     */
    private void calculateAndSetNewPrimeNumber() {


        int n = rnd.nextInt(PRIMES.length);
        bananPierwWartosc = PRIMES[n];
        bananyPierw.setText(String.valueOf(bananPierwWartosc));
}



}

