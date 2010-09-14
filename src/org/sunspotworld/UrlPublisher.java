package org.sunspotworld;

import com.sun.spot.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.*;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lukas Elmer
 */
class UrlPublisher implements Runnable {

    private Queue<String> urls = new PriorityQueue<String>();

    public void run() {
        while (true) {
            if (!urls.isEmpty()) {
                BufferedReader reader = null;
                OutputStreamWriter writer = null;
                try {
                    String urlString = urls.poll();
                    URL url = new URL(urlString);
                    System.out.println("Queue size = " + urls.size() + ", Publishing " + urlString + "...");
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);

                    writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.flush();

                    StringBuffer answer = new StringBuffer();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        answer.append(line);
                    }
                    //Output the response
                    System.out.println("Answer:");
                    //System.out.println(answer.toString());
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } else {
                Utils.sleep(1000);
                System.out.println("No urls to submit...");
            }
        }
    }

    public void addUrl(String url) {
        System.out.println("URL added: '" + url + "'");
        urls.add(url);
    }
}
