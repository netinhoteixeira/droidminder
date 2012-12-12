/**
 * Copyright (C) 2009 Emerson Ribeiro de Mello, Michel Vinicius de Melo Euzebio
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 Emerson Ribeiro de Mello, Michel Vinicius de Melo Euzebio
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob
 * os termos da Licença Pública Geral GNU, conforme publicada pela Free Software
 * Foundation; tanto a versão 2 da Licença como (a seu critério) qualquer versão
 * mais nova.
 *
 * Este programa é distribuído na expectativa de ser útil, mas SEM QUALQUER
 * GARANTIA; sem mesmo a garantia implícita de COMERCIALIZAÇÃO ou de ADEQUAÇÃO A
 * QUALQUER PROPÓSITO EM PARTICULAR. Consulte a Licença Pública Geral GNU para
 * obter mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto com este
 * programa; se não, escreva para a Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package pro.francisco.droidminder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import pro.francisco.droidminder.util.mjpegview.MjpegInputStream;
import pro.francisco.droidminder.util.mjpegview.MjpegView;

/**
 * Classe em que sao configuradas os videos das cameras, e os eventos de
 * controle da reproducao das cameras
 */
public class Videos extends Activity implements OnGestureListener, OnDoubleTapListener {

    private LinearLayout LL[] = new LinearLayout[7];
    private RelativeLayout RL[] = new RelativeLayout[7];
    private ImageView im[] = new ImageView[7];
    private TextView desativada[] = new TextView[7];
    private boolean recording[] = new boolean[7];
    private final Runnable r[] = new Runnable[7];
    private MjpegView mView[] = new MjpegView[7];
    private Handler handler;
    private ArrayList<String> url = new ArrayList<String>();
    private ArrayList<String> urlTela = new ArrayList<String>();
    private ArrayList<Integer> ordem = new ArrayList<Integer>();
    protected GestureDetector gestures;
    private boolean pVez;
    private int posX[] = new int[7];
    private int posY[] = new int[7];
    private int largura[] = new int[7];
    private int altura[] = new int[7];
    private boolean ativos[] = new boolean[7];

    public Videos() {
        super();
        handler = new Handler();
        for (int i = 0; i < 7; i++) {
            recording[i] = false;
        }
    }

    /**
     * Metodo onde e criada a tela da classe Videos e configurada as tranmissoes
     * dos videos
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videos);

        Intent recievedIntent = getIntent();
        url = recievedIntent.getStringArrayListExtra("IP");
        ordem = recievedIntent.getIntegerArrayListExtra("ordem");

        gestures = new GestureDetector(this);
        gestures.setOnDoubleTapListener(this);
        pVez = true;

        RL[0] = (RelativeLayout) findViewById(R.layout.rl0);
        RL[1] = (RelativeLayout) findViewById(R.layout.rl1);
        RL[2] = (RelativeLayout) findViewById(R.layout.rl2);
        RL[3] = (RelativeLayout) findViewById(R.layout.rl3);
        RL[4] = (RelativeLayout) findViewById(R.layout.rl4);
        RL[5] = (RelativeLayout) findViewById(R.layout.rl5);
        RL[6] = (RelativeLayout) findViewById(R.layout.rl6);

        im[0] = (ImageView) findViewById(R.id.Icon0);
        im[1] = (ImageView) findViewById(R.id.Icon1);
        im[2] = (ImageView) findViewById(R.id.Icon2);
        im[3] = (ImageView) findViewById(R.id.Icon3);
        im[4] = (ImageView) findViewById(R.id.Icon4);
        im[5] = (ImageView) findViewById(R.id.Icon5);
        im[6] = (ImageView) findViewById(R.id.Icon6);

        LL[0] = (LinearLayout) findViewById(R.layout.ll_0);
        LL[1] = (LinearLayout) findViewById(R.layout.ll_1);
        LL[2] = (LinearLayout) findViewById(R.layout.ll_2);
        LL[3] = (LinearLayout) findViewById(R.layout.ll_3);
        LL[4] = (LinearLayout) findViewById(R.layout.ll_4);
        LL[5] = (LinearLayout) findViewById(R.layout.ll_5);
        LL[6] = (LinearLayout) findViewById(R.layout.ll_6);

        desativada[0] = (TextView) findViewById(R.id.tvDesativada00);
        desativada[1] = (TextView) findViewById(R.id.tvDesativada01);
        desativada[2] = (TextView) findViewById(R.id.tvDesativada02);
        desativada[3] = (TextView) findViewById(R.id.tvDesativada03);
        desativada[4] = (TextView) findViewById(R.id.tvDesativada04);
        desativada[5] = (TextView) findViewById(R.id.tvDesativada05);
        desativada[6] = (TextView) findViewById(R.id.tvDesativada06);

        for (int i = 0; i < 7; i++) {
            mView[i] = new MjpegView(this);
            mView[i].setBackgroundColor(Color.BLACK);
            RL[i].addView(mView[i]);
            LL[i].setVisibility(View.GONE);
            desativada[i].bringToFront();
            LL[i].bringToFront();
            im[i].bringToFront();
            ativos[i] = false;
        }

        //Inicia os videos e seta eles como clicaveis
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < ordem.size(); j++) {
                if ((ordem.get(j) == i + 1) && !url.get(j).equals("")) {
                    LL[i].setVisibility(View.VISIBLE);
                    desativada[i].setVisibility(View.GONE);

                    mView[i].setBackgroundResource(0);
                    mView[i].setSource(MjpegInputStream.read(url.get(j)));

                    urlTela.add(url.get(j));
                    mView[i].setDisplayMode(MjpegView.SIZE_BEST_FIT);

                    r[i] = new CargaDeVideo(mView[i], RL[i], LL[i], im[i], RL[0].getId());
                    handler.post((Runnable) r[i]);

                    ativos[i] = true;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        this.gestures.onTouchEvent(me);
        return super.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Metodo que controla o evento de toque longo que iniciaria o, ainda nao
     * implementado, metodo de gravacao dos videos
     */
    @Override
    public void onLongPress(MotionEvent e) {
        this.getCoordenadas();
        int i = 0;
        while (i < 7) {
            if ((e.getX() >= posX[i]) && (e.getX() <= (posX[i] + largura[i])) && (e.getY() >= posY[i]) && (e.getY() <= (posY[i] + altura[i]))) {
                if (mView[i].isPlaying()) {
                    im[i].setImageResource(R.drawable.record);
                    recording[i] = true;
                }
                i = 7;
            }
            i++;
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Metodo que trata o evento de toque simples, que controla a reproducao dos
     * videos
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    /**
     * Metodo que trata os eventos de toque duplo, que seve para controlar
     * disposicao dos videos nas telas em eles aparecem
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.getCoordenadas();

        String aux = urlTela.get(0); // principal

        int i = 1;
        while (i < 7) {
            if ((e.getX() >= posX[i]) && (e.getX() <= (posX[i] + largura[i])) && (e.getY() >= posY[i]) && (e.getY() <= (posY[i] + altura[i]))) {

                mView[0].stopPlayback();
                mView[i].stopPlayback();

                im[0].setImageResource(0);
                im[i].setImageResource(0);

                mView[0].setBackgroundColor(Color.BLACK);
                mView[i].setBackgroundColor(Color.BLACK);

                LL[0].setVisibility(View.VISIBLE);
                LL[i].setVisibility(View.VISIBLE);

                urlTela.set(0, urlTela.get(i));
                urlTela.set(i, aux);

                mView[0].setSource(MjpegInputStream.read(urlTela.get(0)));
                mView[i].setSource(MjpegInputStream.read(urlTela.get(i)));

                handler.post(r[i]);
                handler.post(r[0]);

                recording[i] = false;
                recording[0] = false;

                i = 7;
            }
            i++;
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        this.getCoordenadas();
        int i = 0;
        while (i < 7) {
            if ((e.getX() >= posX[i]) && (e.getX() <= (posX[i] + largura[i])) && (e.getY() >= posY[i]) && (e.getY() <= (posY[i] + altura[i]))) {
                if (mView[i].isPlaying() && (recording[i] == false)) {
                    mView[i].stopPlayback();
                    im[i].setImageResource(R.drawable.play);
                } else {
                    mView[i].startPlayback();
                    im[i].setImageResource(R.drawable.pause);
                    recording[i] = false;
                }
                i = 7;
            }
            i++;
        }
        return false;
    }

    public void getCoordenadas() {
        if (pVez == true) {
            for (int i = 0; i < 7; i++) {
                if (ativos[i] == true) {
                    int[] xy = new int[2];
                    RL[i].getLocationOnScreen(xy);

                    posX[i] = xy[0];
                    posY[i] = xy[1];
                    largura[i] = RL[i].getWidth();
                    altura[i] = RL[i].getHeight();
                } else {
                    posX[i] = -1;
                    posY[i] = -1;
                    largura[i] = -1;
                    altura[i] = -1;
                }
            }
            pVez = false;
        }
    }
}