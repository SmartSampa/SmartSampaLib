package com.intellij.smartcity;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by ruan0408 on 11/02/2016.
 */
public class HttpUrlConnectionWrapper {

    public static String executePostWithoutForm(String targetURL) {
        return executeRequest(new HttpPost(targetURL));
    }

    public static String executeGet(String url) {
        return executeRequest(new HttpGet(url));
    }

    @Nullable
    private static String executeRequest(HttpRequestBase request) {
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request);
             BufferedReader rd = new BufferedReader(
                     new InputStreamReader(response.getEntity().getContent()));) {

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) result.append(line);

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
