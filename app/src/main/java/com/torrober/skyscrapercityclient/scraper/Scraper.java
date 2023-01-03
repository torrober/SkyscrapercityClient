package com.torrober.skyscrapercityclient.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Scraper {
    public static String getCSRF() throws IOException {
        Connection.Response response = Jsoup.connect("https://skyscrapercity.com")
                .header("Content-Type","application/x-www-form-urlencoded")
                .referrer("https://skyscrapercity.com")
                .header("X-Requested-With", "XMLHttpRequest")
                .userAgent("Mozilla/5.0 (Linux; Android 10; SM-G9600) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Mobile Safari/537.36")
                .execute();
        String[] bodyLines = response.body().split("\n");
        String firstLine = bodyLines[0];
        return firstLine;
    }
}
