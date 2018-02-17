/*
 * Copyright (c) 16/2/2018. Projeto desenvolvido por Miguel Lukas.
 * Não remova este quote.
 */

package me.dark.others;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckPremium {

    String nome = null;

    String mojangAPI = "https://api.mojang.com/profiles/minecraft";

    public CheckPremium(String nick) {
        nome = nick;
    }

    public boolean getResult() {
        String l = null;

        try {
            l = enviarPost("[ \"" + nome + "\",\"nonExistingPlayer\"  ]", new URL(mojangAPI));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (l == null) {
            return true;
        } else return l.contains("name") && !l.contains("demo");
    }

    private String enviarPost(String payload, URL url) throws Exception {
        HttpsURLConnection con = (HttpsURLConnection) (url.openConnection());

        con.setReadTimeout(15000);
        con.setConnectTimeout(15000);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream out = con.getOutputStream();
        out.write(payload.getBytes("UTF-8"));
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String output = "";
        String line = null;
        while ((line = in.readLine()) != null)
            output += line;

        in.close();

        return output;
    }
}
