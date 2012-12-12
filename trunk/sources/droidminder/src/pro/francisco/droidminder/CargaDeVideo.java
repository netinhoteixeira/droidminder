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
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import pro.francisco.droidminder.util.mjpegview.MjpegView;

/**
 * Classe em que e feita a espera pelo inicio do video
 */
public class CargaDeVideo extends Activity implements Runnable {

    private MjpegView video;
    private LinearLayout lLayout;
    private RelativeLayout rLayout;
    private ImageView im;
    private Handler handler;
    private int iDPrincipal;

    /**
     * Onde sao configurados os atributos da classe
     *
     * @param v Tela em que o video sera reproduzido
     * @param rl Layout ao qual pertence a Tela
     * @param ll Layout que contem as imagens exibidas enquanto se espera o
     * inicio do video
     * @param i ImageView onde sera carregada a imagem do estado de reproducao
     * do video
     * @param idP ID do video principal
     */
    public CargaDeVideo(MjpegView v, RelativeLayout rl, LinearLayout ll, ImageView i, int idP) {
        this.video = v;
        this.lLayout = ll;
        this.rLayout = rl;
        this.im = i;
        this.handler = new Handler();
        this.iDPrincipal = idP;
    }

    /**
     * Metodo que contem o loop de espera do carregamento do video
     */
    @Override
    public void run() {
        if (video.isPlaying()) {
            if (rLayout.getId() != iDPrincipal) {
                video.stopPlayback();
                im.setImageResource(R.drawable.play);
            } else {
                im.setImageResource(R.drawable.pause);
            }
            lLayout.setVisibility(View.GONE);
            video.setBackgroundColor(Color.TRANSPARENT);

        } else {
            handler.postDelayed((Runnable) this, 200);
        }
    }
}