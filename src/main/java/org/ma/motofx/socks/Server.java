package org.ma.motofx.socks;

import org.ma.motofx.data.Prop;
import org.ma.motofx.support.Utility;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.currentThread;

public class Server {

    private ServerSocket ss;
    private ExecutorService executorServer;
    private final ExecutorService executorSockets = Executors.newFixedThreadPool(100) ;
    private final int serverPort = Prop.Desc.SERVER_PORT.getValueInt();

    class thServer implements Runnable {

        thServer() throws IOException {
            ss = new ServerSocket(serverPort);
        }

        @Override
        public void run() {
            Utility.msgDebug("Starting Server...");
            while (!currentThread().isInterrupted()) {
                try {
                    Utility.msgDebug("Wait for client...");
                    ss.setSoTimeout(5000);
                    Socket socket = ss.accept();
                    executorSockets.execute(new sockClient(socket));
                } catch (IOException ex) {
                    if (ex instanceof SocketTimeoutException) {
                        Utility.msgDebug("Server accept() timeout!");
                    } else {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Utility.msgDebug("Server interrupted!");
            try {
                ss.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Server begin() {
        executorServer = Executors.newSingleThreadExecutor();
        try {
            executorServer.execute(new thServer());
        } catch (IOException ex) {
            Utility.msgDebug("Server can't start!\n" + ex.getMessage());
        }
        return this;
    }

    public Server end() {
        Utility.msgDebug("Performing Server shutdown cleanup...");
        executorServer.shutdown();
        try {
            if (!executorServer.awaitTermination(3, TimeUnit.SECONDS)) {
                executorServer.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServer.shutdownNow();
        }
        Utility.msgDebug("Done cleaning");
        return this;
    }

    public boolean isTerminated() {
        return executorServer.isTerminated() | executorServer.isShutdown();
    }

    public Server() {
        Utility.msgDebug("Create Server on port:"+ serverPort);
    }

    public static void main(String[] args) {
        Server s = new Server().begin();
        while (!s.isTerminated()) {
            try {
                Thread.sleep(15000);
//            Server.getInstance().end();
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
class sockClient implements Runnable{
    Socket socket;
    sockClient(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        while (!currentThread().isInterrupted()) {
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                //Client spedisce oggetto
                TravellingObj trObj = (TravellingObj) ois.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static void main(String[] args) {
        Server s = new Server().begin();
        while ( !s.isTerminated() ) {
            try {
                Thread.sleep(15000);
//            Server.getInstance().end();
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
