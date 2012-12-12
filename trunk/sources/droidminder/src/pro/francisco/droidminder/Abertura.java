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

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe inicial do programa, responsavel pelo encaminhamento tanto para a tela
 * de configuracoes quanto para a tela de videos, podendo ir para essa apenas se
 * as cameras ja tiverem sido configuradas
 */
public class Abertura extends Activity implements OnClickListener {

    private File fl;
    private File f2;
    private ArrayList<String> IP = new ArrayList<String>();
    private ArrayList<String> ID = new ArrayList<String>();
    private ArrayList<String> IDtemp = new ArrayList<String>();
    private ArrayList<Integer> ordem = new ArrayList<Integer>();
    private String usuario;
    private String senha;
    private String ipServidor;
    private BufferedReader in;
    private final int ID_STREAM = 0;
    private final int AUTH_STREAM = 4;
    private String auth;

    /**
     * Metodo onde e montada a tela de abertura
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abertura);

        Button btVid = (Button) findViewById(R.id.btVideo);
        Button btConf = (Button) findViewById(R.id.btConfig);
        Button btSobre = (Button) findViewById(R.id.btSobre);

        fl = new File("/data/data/" + getClass().getPackage().getName() + "/files/conf1.txt");
        f2 = new File("/data/data/" + getClass().getPackage().getName() + "/files/conf2.txt");

        btVid.setOnClickListener(this);
        btConf.setOnClickListener(this);
        btSobre.setOnClickListener(this);
    }

    /**
     * Metodo onde sao tratados os eventos do click dos botoes "Configuracoes" e
     * "Ver Cameras"
     *
     * @param v Botao clicado
     */
    @Override
    public void onClick(View v) {
        //Verifica qual botao foi clicado e abre a classe correspondente
        switch (v.getId()) {
            case R.id.btVideo:
                if (fl.exists() && f2.exists()) {
                    ID.clear();
                    ordem.clear();

                    try {
                        FileInputStream fIn1 = openFileInput("conf1.txt");
                        FileInputStream fIn2 = openFileInput("conf2.txt");

                        Scanner fileScan1 = new Scanner(fIn1);
                        Scanner fileScan2 = new Scanner(fIn2);

                        if (fileScan1.hasNext()) {
                            usuario = fileScan1.nextLine();
                        }
                        if (fileScan1.hasNext()) {
                            senha = fileScan1.nextLine();
                        }
                        for (int i = 0; i < 3; i++) {
                            if (fileScan1.hasNext()) {
                                fileScan1.nextLine();
                            }
                        }
                        if (fileScan1.hasNext()) {
                            ipServidor = fileScan1.nextLine();
                        }
                        while (fileScan1.hasNext()) {
                            ID.add(fileScan1.nextLine());
                        }
                        while (fileScan2.hasNext()) {
                            ordem.add(Integer.parseInt(fileScan2.nextLine()));
                            fileScan2.nextLine();
                        }

                        fileScan1.close();
                        fileScan2.close();
                        fIn1.close();
                        fIn2.close();
                    } catch (Exception e) {
                    }

                    IDtemp.clear();
                    IDtemp = this.buscador("mid=\\d+", "colName", ID_STREAM, "", 4);

                    this.auth = "&auth=" + this.buscador("auth=(\"[^\"]*\"|'[^']*'|[^'\">])*&amp", "", AUTH_STREAM, IDtemp.get(0), 5).get(0);

                    // Cria IPs para os video apenas com as IDs que ainda existem
                    IP.clear();
                    for (int j = 0; j < ID.size(); j++) {
                        IP.add("");
                        for (int i = 0; i < IDtemp.size(); i++) {
                            if (ID.get(j).equals(IDtemp.get(i))) {
                                IP.set(j, ipServidor + "/cgi-bin/nph-zms?monitor=" + IDtemp.get(i) + this.auth);
                            }
                        }
                    }

                    // Abre a classe videos passando como parametros os
                    // IPs e a ordem que as cameras serao exibidas
                    Intent intent = new Intent(this, Videos.class);
                    intent.putExtra("IP", IP);
                    intent.putExtra("ordem", ordem);
                    startActivity(intent);
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(this).create();
                    dialog.setTitle(Html.fromHtml("<font color=#FF0000>Erro</font>"));
                    dialog.setIcon(drawable.ic_dialog_alert);
                    dialog.setMessage("É necessario configurar as câmeras.");
                    dialog.setButton("OK", new DialogInterface.OnClickListener() {
                        // ao clicar
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    dialog.show();
                }
                break;

            case R.id.btConfig:
                Intent j = new Intent(this, Config1.class);
                startActivity(j);
                break;

            case R.id.btSobre:
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Sobre");
                dialog.setIcon(drawable.ic_dialog_info);
                dialog.setMessage(Html.fromHtml("<font color=\"#fff\">Autores Originais:<br />"
                        + "Michel Vinicius de Melo Euzébio e Emerson Ribeiro de Mello<br /><br />"
                        + "Mantido por: Francisco Ernesto Teixeira<br /><br />"
                        + "Página: <br>http://code.google.com/p/droidminder/</font>"));
                dialog.setButton("OK", new DialogInterface.OnClickListener() {
                    // ao clicar
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                dialog.show();
        }
    }

    /**
     * Método onde são buscados as IDs e o valor "AUTH" dos vídeos.
     *
     * @param padrao String que sera procurada
     * @param subpadrao String auxiliar que sera procurada
     * @param tipo O tipo de conexao que sera feita
     * @param Id String auxiliar caso o tipo de conexao seja o AUTH_STREAM
     * @param start Posicao inicial em que sera cortada a string encontrada
     * @return ArrayList com strings encontradas
     */
    public ArrayList<String> buscador(String padrao, String subpadrao, int tipo, String Id, int start) {
        ArrayList<String> encontrado = new ArrayList<String>();
        Conexoes conec = new Conexoes(this.ipServidor, this.usuario, this.senha, tipo, Id);

        try {
            if (this.ipServidor.substring(0, 4).equals("https")) {
                this.in = new BufferedReader(new InputStreamReader(conec.ConectarHttps()));
            } else {
                this.in = new BufferedReader(new InputStreamReader(conec.ConectarHttp()));
            }

            String inputLine;
            while ((inputLine = this.in.readLine()) != null) {
                Matcher matcher = Pattern.compile(padrao).matcher(inputLine);
                Matcher submatcher = Pattern.compile(subpadrao).matcher(inputLine);

                if (submatcher.find()) {
                    if (matcher.find()) {
                        encontrado.add(matcher.group().substring(start, matcher.group().length() - tipo));
                    }
                }
            }

            this.in.close();
        } catch (Exception e) {
            AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setTitle(Html.fromHtml("<font color=#FF0000>Erro</font>"));
            dialog.setIcon(drawable.ic_dialog_alert);
            dialog.setMessage("Não foi possível conectar no servidor.");
            dialog.setButton("OK", new DialogInterface.OnClickListener() {
                // ao clicar
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            dialog.show();
        }

        if (encontrado.size() <= 0) {
            encontrado.add(new String());
        }

        return encontrado;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}