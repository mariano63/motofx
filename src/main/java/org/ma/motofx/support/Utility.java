package org.ma.motofx.support;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.ma.motofx.data.Prop;
import org.ma.motofx.tables.CRank;
import org.ma.motofx.tables.Tolerances;

/**
 *
 * @author maria
 */
public class Utility {

    private static final boolean DEBUG = true;

    public static void msgDebug(String s) {
        if (DEBUG) {
            System.out.println("[" + LocalDateTime.now().toString() + "]" + s);
        }
    }

    public static String getMacAddress() {
        InetAddress ip;
        StringBuilder sb = new StringBuilder();

        try {
            ip = InetAddress.getLocalHost();
            msgDebug("Current IP address : " + ip.getHostAddress());
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
        } catch (UnknownHostException | SocketException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        msgDebug("Current MAC address : "+sb.toString());
        return sb.toString();
    }

    public static ObservableList<Label> listVideoCircuiti() {
        File f = Prop.CIRCUITI.toFile();

        FilenameFilter filter = (File directory, String fileName) -> fileName.toLowerCase().endsWith(".mp4");

        File[] files = f.listFiles(filter);
        if (files == null) {
            return null;
        }
        List<Label> list = new ArrayList<>();
        for (File fil : files) {
            String s = fil.getName().substring(0, fil.getName().length() - 4);
            String masterTrack
                    = Prop.CIRCUITI
                    + System.getProperty("file.separator")
                    + s
                    + Prop.MASTER_TRACK_EXT;
            if (new File(masterTrack).exists()) {
                list.add(new Label(s + Prop.MARK_TRACK_EXT));
            } else {
                list.add(new Label(s));
            }
        }
        return FXCollections.observableArrayList(list);
    }

    public static void alertError(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.initOwner(MainApp.getStage());
        alert.setTitle(title);
        if (headerText != null) {
            alert.setHeaderText(headerText);
        } else {
            alert.setHeaderText(title);
        }
        if (contentText != null) {
            alert.setContentText(contentText);
        }
        //Recover Java BUG!! for alert and stage on external monitor
//            alert.setX(MainApp.getStage().getX()+
//                    MainApp.getStage().getWidth()/2);
//            alert.setY(MainApp.getStage().getY()+
//                    MainApp.getStage().getHeight()/2);
        alert.showAndWait();
    }

    public static void importToleranceFromCSV(ObservableList<Tolerances> table,
            String pathToImport) throws Exception {

        List<String> list = Files.readAllLines(Paths.get(pathToImport), StandardCharsets.UTF_8);
        String[] a = list.toArray(new String[list.size()]);
        if (a.length != 7) {  //Numero di linee del file
            throw new Exception("The tolerance file is incorrect: Line number doesn't match.(!=7)");
        }
        table.clear();
        //la riga 0 contiene headers, quindi la skyppo
        for (int ndx = 1; ndx < a.length; ndx++) {
            String[] s = a[ndx].split(",");
            if (s.length > 8) {
                table.add(new Tolerances(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8]));
            } else {
                table.add(new Tolerances(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]));
            }
        }
    }

    public static void exportToleranceToCSV(ObservableList<Tolerances> table,
                                            String pathToExportTo) {
        try {
            try (FileWriter csv = new FileWriter(new File(pathToExportTo))) {
                csv.write(",Beginner,,Intermediate,,Expert,,Rider,,\n");
                for (Tolerances rec : table) {
                    csv.write(rec.toString() + "\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void importRankFromCSV(ObservableList<CRank> table, String pathToImport) throws Exception {
        table.clear();
        List<String> list = Files.readAllLines(Paths.get(pathToImport), StandardCharsets.UTF_8);
        String[] a = list.toArray(new String[list.size()]);
        int counter = 1;
        for (String a1 : a) {
            String[] s = a1.split(",");
            if (s.length != 9) {
                throw new Exception(pathToImport + " Data errors!");
            }
            table.add(0, new CRank(counter++, s[0], s[1], s[2], s[3], s[4], s[5], s[6], Integer.parseInt(s[7]), s[8]));
        }

        /**
         * Start the sort.
         */
        Comparator<CRank> byScore = new Comparator<CRank>() {
            @Override
            public int compare(CRank o1, CRank o2) {
                return o2.getColScore().compareTo(o1.getColScore());
            }
        };
        SortedList<CRank> sortedList = new SortedList<>(table, byScore);

        /**
         * Now adjust RANK number on first unordered list.
         */
        int positionToUnorderList = 1;
        for (CRank r : sortedList) {
            table.get(r.getColPosition() - 1).
                    setcolPosition(positionToUnorderList++);
        }
    }
    public static void main(String[] args){
        String macAddress = getMacAddress();
    }
}
