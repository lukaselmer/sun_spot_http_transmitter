package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;


class HostPublisher implements Runnable {

    public void run() {
        while (true) {
            try {
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://broadcast:41" /*, Connector.READ_WRITE, true*/); //0014.4F01.0000.57C0
                Datagram dg = conn.newDatagram(conn.getMaximumLength());
                try {
                    dg.writeUTF("host");
                    conn.send(dg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    conn.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Utils.sleep(2000);
        }
    }
}
