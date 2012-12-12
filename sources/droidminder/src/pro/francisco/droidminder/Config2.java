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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe onde e configurada a sequencia em que os videos serao apresentados na
 * classe Videos, e onde sao feitos os tratamentos de possiveis erros causados
 * por auteracoes no servidor
 */
public class Config2 extends Activity implements OnClickListener,
        OnCheckedChangeListener {

    private ArrayList<String> ID = new ArrayList<String>();
    private ArrayList<String> IDtemp = new ArrayList<String>();
    private ArrayList<String> nome = new ArrayList<String>();
    private ArrayList<String> IP = new ArrayList<String>();
    private ArrayList<EditText> et = new ArrayList<EditText>();
    private ArrayList<LinearLayout> ll = new ArrayList<LinearLayout>();
    private ArrayList<Button> bt = new ArrayList<Button>();
    private ArrayList<Integer> ordem = new ArrayList<Integer>();
    private int cont = 1;
    private String ipServidor;
    private LayoutParams parametros;
    private Button btCam;
    private Button btRevert;
    private CheckBox cbt;
    private LinearLayout layout;
    private BufferedReader in;
    private String usuario;
    private String senha;
    private final int ID_STREAM = 0;
    private final int AUTH_STREAM = 4;
    private String auth;
    private File config2;

    /**
     * Metodo onde e criada a tela da classe Config2 e configurado os atributos
     * a partir do arquivo de configuracao
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config2);

        // Recebe dados da classe Config2
        Intent recievedIntent = getIntent();
        nome = recievedIntent.getStringArrayListExtra("nome");
        ID = recievedIntent.getStringArrayListExtra("ID");
        ipServidor = recievedIntent.getStringExtra("ipServidor");
        usuario = recievedIntent.getStringExtra("usuario");
        senha = recievedIntent.getStringExtra("senha");

        btCam = (Button) findViewById(R.id.btVerCameras);
        btRevert = (Button) findViewById(R.id.btReverter);
        cbt = (CheckBox) findViewById(R.id.CheckBox01);
        layout = (LinearLayout) findViewById(R.id.LinearLayout01);

        parametros = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        cbt.setOnCheckedChangeListener(this);
        btCam.setOnClickListener(this);
        btRevert.setOnClickListener(this);

        // Cria lista com as caixas de selecao e os nomes das cameras
        for (int i = 0; i < nome.size(); i++) {
            bt.add(new Button(this));
            et.add(new EditText(this));
            ll.add(new LinearLayout(this));
            ordem.add(0);

            EditText etAux = et.get(i);
            LinearLayout llAux = ll.get(i);
            Button btAux = bt.get(i);

            btAux.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            btAux.setBackgroundResource(R.drawable.check_fundo);
            btAux.setTextColor(Color.rgb(0, 0, 51));
            btAux.setOnClickListener(this);

            etAux.setLayoutParams(parametros);
            etAux.setText(nome.get(i));
            etAux.setSingleLine(true);
            etAux.setTextColor(Color.WHITE);
            etAux.setBackgroundColor(Color.TRANSPARENT);
            etAux.setSelectAllOnFocus(true);

            llAux.setLayoutParams(parametros);
            llAux.setOrientation(LinearLayout.HORIZONTAL);
            llAux.addView(btAux);
            llAux.addView(etAux);

            layout.addView(llAux);

        }

        config2 = new File("/data/data/" + getClass().getPackage().getName() + "/files/conf2.txt");

        // Configura atributos
        if (config2.exists()) {
            int aux;
            try {
                FileInputStream fIn = openFileInput("conf2.txt");
                Scanner fileScan = new Scanner(fIn);

                int i = 0;
                while (fileScan.hasNext()) {
                    aux = Integer.parseInt(fileScan.nextLine());
                    if (aux > 0) {
                        ordem.set(i, aux);
                        bt.get(i).setText("" + aux);
                        cont++;
                    }
                    et.get(i).setText(fileScan.nextLine());
                    i++;
                }
                fileScan.close();
                fIn.close();

            } catch (Exception ioe) {
            }
        }

        if (cont == 8) {
            cbt.setChecked(true);
        }
    }

    /**
     * Metodo onde sao tratados os eventos do click do botao "Cameras" e do
     * "Restaurar Nomes" e tambem os eventos das caixas de selecao, nela tambem
     * sao salvas as configurcoes da classe Config2
     */
    @Override
    public void onClick(View v) {
        if (v == btCam) {
            IDtemp.clear();
            IDtemp = this.buscador("mid=\\d+", "colName", ID_STREAM, "", 4);

            this.auth = "&auth=" + this.buscador("auth=(\"[^\"]*\"|'[^']*'|[^'\">])*&amp", "", AUTH_STREAM, IDtemp.get(0), 5).get(0);

            IP.clear();
            for (int j = 0; j < ID.size(); j++) {
                IP.add("");
                for (int i = 0; i < IDtemp.size(); i++) {
                    if (ID.get(j).equals(IDtemp.get(i))) {
                        IP.set(j, ipServidor + "/cgi-bin/nph-zms?monitor=" + IDtemp.get(i) + this.auth);
                        et.get(j).setTextColor(Color.WHITE);
                    }
                }
                if (IP.get(j).equals("")) {
                    et.get(j).setTextColor(Color.RED);
                }
            }

            File config1 = new File("/data/data/" + getClass().getPackage().getName() + "/files/conf1.txt");
            // Deleta o arquivo de configuracao para evitar misturar configuracoes anteriores com as novas
            if (config2.exists()) {
                config2.delete();
            }
            // exista o arquivo de configuracao da classe Config1
            if (config1.exists()) {
                // Salva a ordem das cameras e os nomes delas no arquivo de configuracao
                try {
                    FileOutputStream fOut = openFileOutput("conf2.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    for (int i = 0; i < ordem.size(); i++) {
                        osw.append("" + ordem.get(i) + "\n");
                        osw.append(et.get(i).getText().toString() + "\n");
                    }
                    osw.close();
                } catch (IOException ioe) {
                }
            }
            // Abre a janela de videos
            Intent i = new Intent(this, Videos.class);
            i.putExtra("IP", IP);
            i.putExtra("ordem", ordem);
            startActivity(i);

        } else {
            // Configura os nomes das cameras a partir dos nomes recebidos da classe Config1
            if (v == btRevert) {
                for (int i = 0; i < nome.size(); i++) {
                    et.get(i).setText(nome.get(i));
                }
            } else {
                int i = 0;
                while (i < bt.size()) {
                    if (bt.get(i) == v) {
                        if ((ordem.get(i) == 0) && (cont <= 7)) {
                            ordem.set(i, cont);
                            bt.get(i).setText("" + (cont));
                            cont++;
                        } else {
                            if (ordem.get(i) > 0) {
                                cont--;
                                int aux = ordem.get(i);
                                ordem.set(i, 0);
                                bt.get(i).setText("");

                                for (int j = 0; j < ordem.size(); j++) {
                                    if ((ordem.get(j) > aux)) {
                                        ordem.set(j, ordem.get(j) - 1);
                                        bt.get(j).setText("" + ordem.get(j));
                                    }
                                }
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    /**
     * Esse metodo e invocado quando a CheckBox "Selecionar Todas" for marcada
     * ou desmarcada, se for marcada entao as sete primeras cameras da lista
     * serao marcadas em ordem crescente, se for desmarcada, enteao todas as
     * cameras que estiverem marcadas serao desmarcadas
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            for (int i = 0; i < bt.size(); i++) {
                if (i < 7) {
                    ordem.set(i, (i + 1));
                    bt.get(i).setText("" + (i + 1));
                } else {
                    ordem.set(i, 0);
                    bt.get(i).setText("");
                }
            }
            cont = 8;
        } else {
            for (int i = 0; i < bt.size(); i++) {
                ordem.set(i, 0);
                bt.get(i).setText("");
                cont = 1;
            }
        }
    }

    /**
     * Metodo onde sao buscados as IDs e o valor "AUTH" dos videos
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
            dialog.setMessage("Nao foi possivel conectar no servidor");
            dialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {/*acao quando clicado*/

                }
            });
            dialog.show();
        }

        if (encontrado.size() <= 0) {
            encontrado.add("");
        }
        return encontrado;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}