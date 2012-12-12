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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsavel pela autenticacao do usuario e configuracao da conexao com
 * o sevidor de videos
 */
public class Config1 extends Activity implements OnClickListener {

    private EditText etIp;
    private EditText etUsuario;
    private EditText etSenha;
    private EditText etPorta;
    private Button btAutenticar;
    private CheckBox cbLembrar;
    private RadioButton rbHttp;
    private RadioButton rbHttps;
    private String ipServidor;
    private BufferedReader in;
    private InputStream iStream;
    private String usuario;
    private String senha;
    private final int ID_STREAM = 0;

    /**
     * Metodo que cria a tela da classe Config1 e configura atributos a partir
     * do arquivo de configuracao
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config1);

        etIp = (EditText) findViewById(R.id.etIp);
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        etSenha = (EditText) findViewById(R.id.etSenha);
        etPorta = (EditText) findViewById(R.id.etPorta);
        btAutenticar = (Button) findViewById(R.id.btAutenticar);
        cbLembrar = (CheckBox) findViewById(R.id.cbLembrar);
        rbHttp = (RadioButton) findViewById(R.id.rbHttp);
        rbHttps = (RadioButton) findViewById(R.id.rbHttps);

        // Abre arquivo de configuracao para configurar atributos
        try {
            FileInputStream fIn = openFileInput("conf1.txt");
            Scanner file_scan = new Scanner(fIn);

            if (file_scan.hasNext()) {
                etUsuario.setText(file_scan.nextLine());
            }
            if (file_scan.hasNext()) {
                etSenha.setText(file_scan.nextLine());
            }
            if (file_scan.hasNext()) {
                etIp.setText(file_scan.nextLine());
            }
            if (file_scan.hasNext()) {
                etPorta.setText(file_scan.nextLine());
            }
            if (file_scan.hasNext()) {
                if (file_scan.nextLine().equals("1")) {
                    rbHttp.setChecked(true);
                } else {
                    rbHttps.setChecked(true);
                }
                cbLembrar.setChecked(true);
            }
            file_scan.close();
            fIn.close();

        } catch (IOException ioe) {
        }

        btAutenticar.setOnClickListener(this);
        rbHttp.setOnClickListener(this);
        rbHttps.setOnClickListener(this);
    }

    /**
     * Metodo que trata eventos do click do botao "Autenticar" e dos
     * RadioButton's "HTTP" e "HTTPS" e onde sao pegas as informacoes das
     * cameras, e tambem onde salvas as configuracoes da classe Config1
     *
     * @param v Botao ou RadioButton clicado
     */
    @Override
    public void onClick(View v) {

        // Quando o botao autenticar for clicado
        if (v == btAutenticar) {
            // Verifica se os campos estao vazios
            if (etIp.getText().toString().equals("")
                    || etPorta.getText().toString().equals("")
                    || (!rbHttp.isChecked() && !rbHttps.isChecked())) {
                this.Dialog(1);
            } else {

                ArrayList<String> nome = new ArrayList<String>();
                ArrayList<String> ID = new ArrayList<String>();

                usuario = etUsuario.getText().toString();
                senha = etSenha.getText().toString();

                // Conecta no servidor e pega os nomes e as IDs da cameras que estiverem configuradas
                try {

                    if (rbHttp.isChecked()) {
                        ipServidor = "http://" + etIp.getText().toString() + ":" + etPorta.getText().toString();
                        iStream = new Conexoes(ipServidor, usuario, senha, ID_STREAM, "").ConectarHttp();
                        in = new BufferedReader(new InputStreamReader(iStream));
                    } else {
                        ipServidor = "https://" + etIp.getText().toString() + ":" + etPorta.getText().toString();
                        iStream = new Conexoes(ipServidor, usuario, senha, ID_STREAM, "").ConectarHttps();
                        in = new BufferedReader(new InputStreamReader(iStream));
                    }

                    String monitorId = "mid=\\d+";
                    String monitorName = "\\d\">(\"[^\"]*\"|'[^']*'|[^'\">])*<";
                    String inputLine;
                    boolean authErro = false;
                    boolean serverOff = false;

                    while (((inputLine = in.readLine()) != null) && (authErro == false) && (serverOff == false)) {

                        Matcher matcher1 = Pattern.compile(monitorId).matcher(inputLine);
                        Matcher matcher2 = Pattern.compile(monitorName).matcher(inputLine);
                        Matcher matcher3 = Pattern.compile("Login").matcher(inputLine);
                        Matcher matcher = Pattern.compile("colName").matcher(inputLine);

                        if (matcher.find()) {
                            if (matcher1.find() && matcher2.find()) {
                                ID.add(matcher1.group().substring(4, matcher1.group().length()));
                                nome.add(matcher2.group().substring(3, matcher2.group().length() - 1));
                            } else {
                                serverOff = true;
                            }
                        } else if (matcher3.find()) {
                            authErro = true;
                        }
                    }
                    in.close();

                    if (authErro == true) {
                        this.Dialog(2);
                    } else if (serverOff == true) {
                        this.Dialog(3);
                    } else if (nome.isEmpty()) {
                        this.Dialog(0);
                    } else {
                        Intent i = new Intent(this, Config2.class);
                        i.putExtra("nome", nome);
                        i.putExtra("ID", ID);
                        i.putExtra("ipServidor", ipServidor);
                        i.putExtra("usuario", usuario);
                        i.putExtra("senha", senha);
                        startActivity(i);
                    }

                } catch (Exception e) {
                    this.Dialog(0);
                } finally {

                    // Deleta o arquivo de configuracao para evitar misturar configuracoes anteriores com as novas
                    File config1 = new File("/data/data/" + getClass().getPackage().getName() + "/files/conf1.txt");
                    if (config1.exists()) {
                        config1.delete();
                    }

                    if (cbLembrar.isChecked()) {
                        // Se a CheckBox "Lembrar" estiver marcada salva as informacoes no arquivo de configuracao
                        try {
                            FileOutputStream fOut = openFileOutput("conf1.txt", MODE_PRIVATE);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);

                            if (cbLembrar.isChecked()) {
                                osw.write(etUsuario.getText().toString() + "\n"
                                        + etSenha.getText().toString() + "\n"
                                        + etIp.getText().toString() + "\n"
                                        + etPorta.getText().toString() + "\n");
                                if (rbHttp.isChecked()) {
                                    osw.append("1\n");
                                } else {
                                    osw.append("0\n");
                                }
                                osw.append(ipServidor + "\n");
                                for (String s : ID) {
                                    osw.append(s + "\n");
                                }
                            }
                            osw.close();
                            fOut.close();

                        } catch (IOException ioe) {
                        }
                    }
                }
            }
        } else {
            if (v == rbHttp) {
                etPorta.setText("80");
            } else {
                etPorta.setText("443");
            }
        }
    }

    /**
     * Metodo que exibe as mensagens de erro
     *
     * @param n tipo de mensagem de erro n = 0 - Erro de conexao com o servidor
     * n = 1 - Campos obrigatorios nao foram preenchidos n = 2 - Erro de
     * autenticação
     */
    public void Dialog(int n) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setIcon(drawable.ic_dialog_alert);
        switch (n) {

            case 0:
                dialog.setTitle(Html.fromHtml("<font color=#FF0000>Erro</font>"));
                dialog.setMessage("Não foi possível conectar no servidor");
                break;

            case 1:
                dialog.setTitle(Html.fromHtml("<font color=#FFFF00>Atenção</font>"));
                dialog.setMessage("É necessário preencher os campos Endereço do Servidor e Porta");
                break;

            case 2:
                dialog.setTitle(Html.fromHtml("<font color=#FF0000>Erro</font>"));
                dialog.setMessage("Ocorreu um erro na autenticação, preencha os campos usuário e senha corretamente");
                break;

            case 3:
                dialog.setTitle(Html.fromHtml("<font color=#FF0000>Erro</font>"));
                dialog.setMessage("O servidor está desligado");
                break;
        }
        dialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {/*acao quando clicado*/

            }
        });
        dialog.show();
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
