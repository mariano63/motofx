package org.ma.motofx.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ma.motofx.data.ArduinoData;
import org.ma.motofx.support.Utility;
import purejavacomm.*;

/**
 *
 * @author maria
 */
public class Rs232 {

    private SerialPort serialPort = null;
    private InputStream ins = null;
    
    public void close(){
        Utility.msgDebug("Close the rs232 port...");
        if (serialPort != null){
            serialPort.close();
        }   serialPort=null;
    }
    
    /**
     *
     * @return
     */
    public boolean open() {
        CommPortIdentifier portid = null;
        Enumeration e = CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            portid = (CommPortIdentifier) e.nextElement();
            Utility.msgDebug("found " + portid.getName());
        }
        if (portid == null) {
            Utility.msgDebug("COM port not found! exit...");
            return false;
        }
        try {
            serialPort = (SerialPort) portid.open("PureJavaCommMa", 1000);
        } catch (PortInUseException ex) {
            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnOutputEmpty(false);  //Non devo spedire nulla ad arduino
        try {
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN + SerialPort.FLOWCONTROL_XONXOFF_OUT);
        } catch (UnsupportedCommOperationException ex) {
            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try {
            OutputStream outs = serialPort.getOutputStream();
            ins = serialPort.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        try {
            serialPort.addEventListener(new SerialPortEventListener() {
                final byte[] lineBuf = new byte[9];  //da 0xF0 a 0xF7 non compresi, quindi ne basta 9
                int contatoreBytes = 0;
                
                @Override
                public void serialEvent(SerialPortEvent event) {
                    
                    switch (event.getEventType()) {
                        case SerialPortEvent.DATA_AVAILABLE:
                            try {
                                byte[] buffer = new byte[ins.available()];
                                int n = ins.read(buffer, 0, buffer.length);
                                for (byte b : buffer) {
                                    if (b == (byte)0xF0) {
                                        contatoreBytes = 0;
                                        continue;
                                    }
                                    if (b == (byte)0xF7) {
                                        ArduinoData.getInstance().setData(lineBuf);
                                        continue;
                                    }
                                    if (contatoreBytes>=lineBuf.length){
                                        continue;
                                    }
                                    lineBuf[contatoreBytes++] = b;
                                }
                                
                            } catch (IOException ex) {
                                Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                            
                        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                            break;
                    }
                    
                    try {
                        //Nel caso dovessi trasmettere
                        if (event.getEventType() == SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
                            //byte[] buffer = new byte[10];
                            //Utility.msgDebug("Sending: " + new String(buffer));
                            //outs.write(buffer, 0, buffer.length);
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            Utility.msgDebug("PureJavaCommDemo test");
             CommPortIdentifier portid = null;
            Enumeration e = CommPortIdentifier.getPortIdentifiers();
            while (e.hasMoreElements()) {
                portid = (CommPortIdentifier) e.nextElement();
                Utility.msgDebug("found " + portid.getName());
            }
            if (portid != null) {
                Utility.msgDebug("use " + portid.getName());
                SerialPort port = (SerialPort) portid.open("PureJavaCommDemo", 1000);
                port.notifyOnDataAvailable(true);
                port.notifyOnOutputEmpty(true);
                port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN + SerialPort.FLOWCONTROL_XONXOFF_OUT);
                final OutputStream outs = port.getOutputStream();
                final InputStream ins = port.getInputStream();
                final boolean[] stop = {false};
                port.addEventListener(new SerialPortEventListener() {
                    final byte[] linebuf = new byte[10000];
                    int inp = 0;
                    int okcnt = 0;
                    int errcnt = 0;
                    final Random rnd = new Random();

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        try {
                            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                                int n = ins.available();
                                byte[] buffer = new byte[n];
                                n = ins.read(buffer, 0, n);
                                for (int i = 0; i < n; ++i) {
                                    byte b = buffer[i];
                                    linebuf[inp++] = b;
                                    if (b == '\n') {
                                        //Utility.msgDebug("Received: " + new String(linebuf, 0, inp));
                                        int s = 0;
                                        int j;
                                        for (j = 0; j < inp - 2; j++) {
                                            s += linebuf[j];
                                        }
                                        byte cb = (byte) (32 + (s & 63));
                                        okcnt++;
                                        if (cb != linebuf[j]) {
                                            Utility.msgDebug("check sum failure");
                                            errcnt++;
                                        }
                                        Utility.msgDebug("msg " + inp + " ok " + okcnt + " err " + errcnt);
                                        inp = 0;
                                    }
                                }
                            }
                            if (event.getEventType() == SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
                                int n = 4 + (rnd.nextInt() & 63);
                                byte[] buffer = new byte[n + 2];
                                //Utility.msgDebug("Sending: " + new String(buffer));
                                int s = 0;
                                int i;
                                for (i = 0; i < n; i++) {
                                    byte b = (byte) (32 + (rnd.nextInt() & 63));
                                    buffer[i] = b;
                                    s += b;
                                }
                                buffer[i++] = (byte) (32 + (s & 63));
                                buffer[i++] = '\n';
                                outs.write(buffer, 0, buffer.length);
                            }
                        } catch (IOException e) {
                            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                });
                /**
                 * Forse un Thread.sleep() ?
                 */
            }
        } catch (IOException | TooManyListenersException | PortInUseException | UnsupportedCommOperationException e) {
            Logger.getLogger(Rs232.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
