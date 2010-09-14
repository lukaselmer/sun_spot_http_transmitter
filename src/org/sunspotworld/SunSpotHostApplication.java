/*
 * SunSpotHostApplication.java
 *
 * Created on 16.07.2010 17:52:22;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.IEEEAddress;


import java.io.*;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import javax.microedition.io.*;

public class SunSpotHostApplication implements Runnable {

    private HostPublisher hostPublisher;
    private Thread hostPublisherThread;
    private UrlPublisher urlPublisher;
    private Thread urlPublisherThread;

    public void run() {
        hostPublisher = new HostPublisher();
        hostPublisherThread = new Thread(hostPublisher);
        hostPublisherThread.start();

        urlPublisher = new UrlPublisher();
        urlPublisherThread = new Thread(urlPublisher);
        urlPublisherThread.start();

        while (true) {
            try {
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://:40"); //0014.4F01.0000.57C0
                Datagram dg = conn.newDatagram(conn.getMaximumLength());
                Datagram dgReply = conn.newDatagram(conn.getMaximumLength());
                try {
                    conn.receive(dg);

                    int urlsLeft = dg.readInt();
                    Vector<String> urlRequests = new Vector<String>(urlsLeft);
                    while ((urlsLeft--) > 0) {
                        urlRequests.add(dg.readUTF());
                    }
                    addUrl(urlRequests);
                    dgReply.reset();
                    dgReply.setAddress(dg);
                    dgReply.writeUTF("ok");
                    conn.send(dgReply);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
    }

    private void addUrl(String urlList) {
        String[] urls = urlList.split(";");
        for (int i = 0; i < urls.length; i++) {
            urlPublisher.addUrl(urls[i]);
        }
    }

    private void addUrl(Vector<String> urlList) {
        for (String url : urlList) {
            urlPublisher.addUrl(url);
        }
    }
}
