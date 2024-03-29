package com.example.memoria;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;


import java.util.ArrayList;
import java.util.Collections;

import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    private int indice = 0;
    private int imagenes[];
    // imagen de fondo;
    private int fondo;


    private ImageButton el0, el1, el2, el3, el4, el5, el6, el7, el8, el9, el10, el11;
    private ImageButton [] botonera = new ImageButton[12];
    private Button reiniciar, iniciar, tiempo, salir;

    // para barajar
    // el vector que recoge el resultado del "barajamiento" (o "barajación" o "barajancia" o como leshes se diga)
    ArrayList<Integer> arrayBarajado;

    // COMPARACIÓN
    // los botones que se han pulsado y se comparan
    ImageButton primero;
    // posiciones de las imágenes a comparar en el vector de imágenes
    int numeroPrimero, numeroSegundo;
    // durante un segundo se bloquea el juego y no se puede pulsar ningún botón
    boolean bloqueo = false;

    // para controlar la pausa de un segundo
    final Handler handler = new Handler();

    // finalmente, el número de aciertos y la puntuación
    int aciertos=0;
    int puntuacion=0;
    TextView textoPuntuacion;

    // CRONOMETRO
    TextView l_cronometro;
    MiHandler miHandler;
    String salida;
    int min, seg, micros;
    boolean pausado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cargarImagenes();
        cargarBotones();
        botonesMenu();
        reiniciar.setEnabled(false);

        miHandler = new MiHandler( this );
        l_cronometro = findViewById(R.id.crono);
        //inicializo las variables
        min=0;seg=0;micros =0;
        pausado=false;
        // MOSTRAMOS FONDO
        for (int i = 0; i < botonera.length; i++) {
            botonera[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            botonera[i].setImageResource(fondo);
        }

    }
    public void iniciar(){
        arrayBarajado = barajar(imagenes.length*2);

        //MOSTRAMOS LA IMAGEN
        for(int i=0; i<botonera.length; i++) {
            botonera[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            botonera[i].setImageResource(imagenes[arrayBarajado.get(i)]);
        }

        //Y EN UN SEGUNDO LA OCULTAMOS
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < botonera.length; i++) {
                    botonera[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                    botonera[i].setImageResource(fondo);
                }
            }
        }, 1000);

        //AÑADIMOS LOS EVENTOS A LOS BOTONES DEL JUEGO
        for(int i=0; i <arrayBarajado.size(); i++){
            final int j=i;
            botonera[i].setEnabled(true);
            botonera[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!bloqueo)
                        comprobar(j, botonera[j]);
                }
            });
        }
        aciertos=0;
        puntuacion=0;
        textoPuntuacion.setText("Puntuación: " + puntuacion);
    }



    public ArrayList<Integer> barajar(int longitud) {
        ArrayList resultadoA = new ArrayList<Integer>();
        for(int i=0; i<longitud; i++)
            resultadoA.add(i % longitud/2);
        Collections.shuffle(resultadoA);
        return  resultadoA;
    }

    public void comprobar(int i, final ImageButton imgb){
        if(primero==null){//ningún botón ha sido pulsado
            //el botón primero será el que acabamos de pulsar
            primero = imgb;
            /*le asignamos la imagen del vector imágenes situada
            en la posición vectorBarajado[i], que tendrá un valor entre 0 y 7*/
            primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
            primero.setImageResource(imagenes[arrayBarajado.get(i)]);
            //bloqueamos el botón
            primero.setEnabled(false);
            //almacenamos el valor de vectorBarajado[i]
            numeroPrimero=arrayBarajado.get(i);
        }else{//ya hay un botón descubierto
            //bloqueamos todos los demás
            bloqueo=true;
            //el botón segundo será el que acabamos de pulsar
            /*le asignamos la imagen del vector imágenes situada
            en la posición vectorBarajado[i], que tendrá un valor entre 0 y 7*/
            imgb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgb.setImageResource(imagenes[arrayBarajado.get(i)]);
            //bloqueamos el botón
            imgb.setEnabled(false);
            //almacenamos el valor de vectorBarajado[i]
            numeroSegundo=arrayBarajado.get(i);
            //if(primero.getDrawable().getConstantState().equals(imgb.getDrawable().getConstantState())){
            if(numeroPrimero==numeroSegundo){//si coincide el valor los dejamos destapados
                //reiniciamos
                primero=null;
                bloqueo=false;
                //aumentamos los aciertos y la puntuación
                aciertos++;
                puntuacion++;
                textoPuntuacion.setText("Puntuación: " + puntuacion);
                //al llegar a8 aciertos se ha ganado el juego
                if(aciertos==8){
                    Toast toast = Toast.makeText(getApplicationContext(), "Has ganado!!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }else{//si NO coincide el valor los volvemos a tapar al cabo de un segundo
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //les ponemos la imagen de fondo
                        primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        primero.setImageResource(R.drawable.naipe);
                        imgb.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgb.setImageResource(R.drawable.naipe);
                        //los volvemos a habilitar
                        primero.setEnabled(true);
                        imgb.setEnabled(true);
                        //reiniciamos la variables auxiliaares
                        primero = null;
                        bloqueo = false;
                        // restamos uno a la puntuación
                        /*
                        if (puntuacion > 0) {
                            puntuacion--;
                            textoPuntuacion.setText("Puntuación: " + puntuacion);
                        }
                        */

                    }
                }, 1000);//al cabo de un segundo
            }
            // cuando llega a puntuacion maxima
            if (puntuacion == 6 ) {
                pausado=true;
            }
        }

    }
    public void cargarImagenes(){
        imagenes = new int[]{
                R.drawable.memocharlie,
                R.drawable.memomarcia,
                R.drawable.memorerun,
                R.drawable.memosally,
                R.drawable.memoschroeder,
                R.drawable.memosnoopy
        };

        fondo = R.drawable.naipe;
    }

    public void cargarBotones(){
        el0 = (ImageButton) findViewById(R.id.boton00);
        botonera[0] = el0;
        el1 = (ImageButton) findViewById(R.id.boton01);
        botonera[1] = el1;
        el2 = (ImageButton) findViewById(R.id.boton02);
        botonera[2] = el2;
        el3 = (ImageButton) findViewById(R.id.boton03);
        botonera[3] = el3;
        el4 = (ImageButton) findViewById(R.id.boton04);
        botonera[4] = el4;
        el5 = (ImageButton) findViewById(R.id.boton05);
        botonera[5] = el5;
        el6 = (ImageButton) findViewById(R.id.boton06);
        botonera[6] = el6;
        el7 = (ImageButton) findViewById(R.id.boton07);
        botonera[7] = el7;
        el8 = (ImageButton) findViewById(R.id.boton08);
        botonera[8] = el8;
        el9 = (ImageButton) findViewById(R.id.boton09);
        botonera[9] = el9;
        el10 = (ImageButton) findViewById(R.id.boton10);
        botonera[10] = el10;
        el11 = (ImageButton) findViewById(R.id.boton11);
        botonera[11] = el11;

        textoPuntuacion = (TextView)findViewById(R.id.textoPuntuacion);
        textoPuntuacion.setText("Puntuación: " + puntuacion);
    }
    public void botonesMenu(){
        reiniciar = (Button) findViewById(R.id.Reiniciar);
        iniciar = (Button) findViewById(R.id.Iniciar);

        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar.setEnabled(true);
                reiniciar.setEnabled(false);
                pausado=true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < botonera.length; i++) {
                            botonera[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                            botonera[i].setImageResource(fondo);
                        }
                        min=0;seg=0;micros=0;
                        l_cronometro.setText("00:00:00");
                    }
                }, 500);
            }
        });
        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar();

                iniciar.setEnabled(false);
                reiniciar.setEnabled(true);

                if(pausado) {
                    synchronized (this) {
                        min=0;seg=0;micros =0;
                        l_cronometro.setText("00:00:00");
                        pausado = false;
                        cronopost();
                        this.notifyAll();
                    }
                }
                else{
                    cronopost();

                }
            }
        });
    }
    // CRONOMETRO
    public void cronopost(){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (pausado) {
                        synchronized (this) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    espera(10);

                    if (!pausado) {
                        Message msg = new Message();
                        msg.what=1;
                        miHandler.sendMessage(msg);

                    }
                }
            }
        });
        th.start();

    }
    public void crono(){
        micros++;
        if(micros == 100){
            seg++;
            micros =0;
        }
        if (seg == 60) {
            min++;
            seg = 0;
        }
    }
    public String formato(){
        salida="";
        if (min <= 9) {
            salida += "0";
        }
        salida += min;
        salida += ":";
        if (seg <= 9) {
            salida += "0";
        }
        salida += seg + ":" ;
        if(micros<=9){
            salida+="0";
        }
        salida+=micros;

        return salida;

    }

    static public void espera(int e) {
        try {
            Thread.sleep(e);
        } catch (InterruptedException ex) {
        }
    }


    static class MiHandler extends Handler {
        MainActivity ma;

        MiHandler(MainActivity ma ){
            this.ma = ma;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch( msg.what){
                case 1:
                    ma.crono();
                    //Formato de la salida:
                    String salida = ma.formato();
                    ma.l_cronometro.setText(salida);
                    break;
                default:
                    break;
            }
        }
    }


}
