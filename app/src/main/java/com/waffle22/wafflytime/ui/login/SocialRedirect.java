package com.waffle22.wafflytime.ui.login;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class SocialRedirect {
    private InputStream openConnectionCheckRedirects(URLConnection c) throws IOException {
    boolean redir;
    int redirects = 0;
    InputStream in = null;
    do
    {
        if (c instanceof HttpURLConnection)
        {
            ((HttpURLConnection) c).setInstanceFollowRedirects(false);
        }
        in = c.getInputStream();
        redir = false;
        if (c instanceof HttpURLConnection)
        {
            HttpURLConnection http = (HttpURLConnection) c;
            int stat = http.getResponseCode();
            if (stat >= 300 && stat <= 307 && stat != 306 &&
                    stat != HttpURLConnection.HTTP_NOT_MODIFIED)
            {
                URL base = http.getURL();
                String loc = http.getHeaderField("Location");
                URL target = null;
                if (loc != null)
                {
                    target = new URL(base, loc);
                }
                http.disconnect();
                if (target == null || !(target.getProtocol().equals("http")
                        || target.getProtocol().equals("https"))
                        || redirects >= 5)
                {
                    throw new SecurityException("illegal URL redirect");
                }
                Log.i(TAG, "openConnectionCheckRedirects: "+target);;
            }
        }
    }
    while (redir);
    return in;
}

    public String makeConnection(String str) throws IOException {
        URL url = null;
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        java.net.URLConnection conn = url.openConnection();
        InputStream is = openConnectionCheckRedirects(conn);

        System.out.println("to String(): " + conn.toString());
        String ans = conn.getURL().toString();

        is.close();
        return ans;
    }
}
