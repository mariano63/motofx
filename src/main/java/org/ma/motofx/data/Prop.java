/**
 * File delle proprietà.
 * con Prop si accede agli static come i vari paths(es:Prop.CIRCUITI)
 * ed anche alle properties read/write che andranno sul file di proprietà
 * Prop.Desc.FRENO_ANTERIORE_MIN.getValue(XXX), attraverso l' enum Desc.
 */

package org.ma.motofx.data;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ma.motofx.support.Utility;

/**
 *
 * @author maria
 */
public class Prop {

    private static final String PATH
            //            = System.getProperty("user.home")
            //            + System.getProperty("file.separator")
            = "conf"
            + System.getProperty("file.separator");

    //File proprietà
    private static final Path FILPROP = Paths.get(PATH, "prop.xml");
    //Path circuiti
    public static final Path CIRCUITI = Paths.get(PATH, "circuiti");
    //Path Ranks
    public static final Path RANKS = Paths.get(PATH, "ranks");
    //File tolleranze
    public static final Path FILETOLERANCE = Paths.get(PATH,"Tolleranze", "tolerance.csv");
    //Background
    public static final Path IMGBACKGROUND = Paths.get(PATH,"background-img.png");

    final static public String RANK_EXT = ".rnk";
    final static public String MASTER_TRACK_EXT = ".mtk";
    final static public String MARK_TRACK_EXT = "(*)";

    //File circuito di default
    private static Path mediaUrl
            = Paths.get(CIRCUITI.toString(), "default.mp4");
    private final static Path MEDIA_URL_DEFAULT
            = Paths.get(CIRCUITI.toString(), "default.webm");

    //non è statica!
    public static Path getMediaUrlDefault() {
        return MEDIA_URL_DEFAULT;
    }

    public static Path getMediaUrl() {
        return mediaUrl;
    }

    public static void setMediaUrl(Path mediaUrl) {
        Prop.mediaUrl = mediaUrl;
    }
    /**
     * Notare che è privato.
     */
    private static final Properties PROP = new Properties();

    //Setup
    public enum Desc {
        //Setup macchina SECTION
        ACCELERATORE_MIN( "65000"),
        ACCELERATORE_MAX( "0"),
        FRENO_POSTERIORE_MIN( "65000"),
        FRENO_POSTERIORE_MAX("0"),
        FRENO_ANTERIORE_MIN("65000"),
        FRENO_ANTERIORE_MAX("0"),
        //Client-Server SECTION
        SERVER("1"), // 0 i'm a client!
        PORT("56565"), //Socket port
        MACADDRESS("*"),    //MAC-ADDRESS  for handshake
        //Admin SECTION
        PASSWORD("mototrainer");
        final String defaultValue;

        Desc( String value) {
            this.defaultValue = value;
        }

        public String getValue() {
            return (String) PROP.getOrDefault(this.name(), this.defaultValue);
        }

        public int getValueInt() {
            return Integer.parseInt((String) PROP.getOrDefault(this.name(), this.defaultValue));
        }

        public void setValue(String val) {
            PROP.setProperty(this.name(), val);
        }

        void setDefaultValue() {
            PROP.setProperty(this.name(), defaultValue);
        }
    }

    private static void oneShotLoadProperties() {
        try {
//            FILPROP.toFile().getParentFile().mkdirs();
            FILPROP.toFile().createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Prop.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        boolean erroreParse = false;
        try {
            FileInputStream fi = new FileInputStream(FILPROP.toFile());
            PROP.loadFromXML(fi);
        } catch (FileNotFoundException ex) {
            erroreParse = true;
            Logger.getLogger(Prop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            erroreParse = true;
            Logger.getLogger(Prop.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (erroreParse) {
                Utility.msgDebug("Errore NON FATALE di parsing file properties."
                        + "Probabile il file non sia ancora consistente."
                        + "Verranno gestiti i dati di default.");
            }
        }
        Utility.msgDebug("Properties initialized!"+ ((erroreParse)?"...anyway":"") );
    }

    /**
     * !!!!!!!!!! Ultimo blocco statico da eseguire!!!!! NON MUOVERE. Carica il
     * file di proprietà, che necessita di tutte le variabili precedenti mettilo
     * sempre per ultimo.
     */
    static {
        oneShotLoadProperties();
    }

    private Prop() {
        Utility.msgDebug("File properties:" + FILPROP.toUri().toString());
    }

    /**
     * I dati di properties vengono scritti sul file FILPROP
     */
    public void saveProperties() {
        try {
            FileOutputStream fi = new FileOutputStream(FILPROP.toFile());
            PROP.storeToXML(fi, "File properties");
        } catch (IOException ex) {
            Logger.getLogger(Prop.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Prop getInstance() {
        return PropHolder.INSTANCE;
    }

    private static class PropHolder {

        private static final Prop INSTANCE = new Prop();
    }

    public static void main(String[] s) {
        Utility.msgDebug("start some test...");

        Utility.msgDebug("...Default values");
        for (Desc f : Desc.values()) {
            f.setDefaultValue();
            Utility.msgDebug(f.name() + "=" + f.getValue());
        }
        Utility.msgDebug("...rewrite the default values");
        int i = 0;
        for (Desc f : Desc.values()) {
            f.setValue(i + "");
            i++;
            Utility.msgDebug(f.name() + "=" + f.getValue());
        }
    }
}
