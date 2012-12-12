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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Classe que cria e configura as conexoes com o servidor de videos
 *
 */
public class Conexoes {

    private URL url;
    private String usuario;
    private String senha;
    private String dados;
    private String mid;
    private final int VIDEO_STREAM = 1;
    private final int ID_STREAM = 0;
    private final int AUTH_STREAM = 4;

    /**
     * Onde sao configurados os atributos da classe Conexoes
     *
     * @param urlServidor URL do servidor que se quer conectar
     * @param u Usuario
     * @param s Senha
     * @param tipo Tipo de conexao que sera criada
     * @param m String auxiliar caso o tipo de conexao seja o AUTH_STREAM
     */
    public Conexoes(String urlServidor, String u, String s, int tipo, String m) {
        this.usuario = u;
        this.senha = s;
        this.mid = m;
        this.dados = "";

        try {
            if ((tipo == ID_STREAM) || (tipo == AUTH_STREAM)) {

                this.url = new URL(urlServidor + "/zm/index.php?skin=mobile");

                dados = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("login", "UTF-8") + "&"
                        + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(this.usuario, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(this.senha, "UTF-8");

                if (tipo == AUTH_STREAM) {
                    dados += "&" + URLEncoder.encode("view", "UTF-8") + "=" + URLEncoder.encode("watch", "UTF-8") + "&"
                            + URLEncoder.encode("mid", "UTF-8") + "=" + URLEncoder.encode(this.mid, "UTF-8");

                }
            } else if (tipo == VIDEO_STREAM) {
                this.url = new URL(urlServidor);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Cria uma conexao HTTP com o servidor de videos, e envia, se existir, a
     * informacao de autenticacao
     *
     * @return Retorna o stream de entrada da conexao criada
     * @throws IOException
     */
    public InputStream ConectarHttp() throws IOException {

        HttpURLConnection urlCon = (HttpURLConnection) this.url.openConnection();

        if (!this.usuario.equals("")) {
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);

            OutputStreamWriter osw = new OutputStreamWriter(urlCon.getOutputStream(), "8859_1");
            osw.write(this.dados);
            osw.flush();
            osw.close();
        }

        return urlCon.getInputStream();
    }

    /**
     * Cria uma conexao HTTPS com o servidor de videos, configura os parametros
     * do SSL, e envia, se existir, a informacao de autenticacao
     *
     * @return Retorna o stream de entrada da conexao criada
     * @throws IOException
     */
    public InputStream ConectarHttps() throws Exception {

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");

        context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        HttpsURLConnection urlCon = (HttpsURLConnection) this.url.openConnection();

        if (!this.usuario.equals("")) {
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);

            OutputStreamWriter osw = new OutputStreamWriter(urlCon.getOutputStream(), "8859_1");
            osw.write(this.dados);
            osw.flush();
            osw.close();
        }

        return urlCon.getInputStream();
    }
}
