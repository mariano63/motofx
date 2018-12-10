/*

 */
package org.ma.motofx.data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.xml.bind.DatatypeConverter;
import org.ma.motofx.support.Utility;

/**
 *
 * @author maria
 */
public class ArduinoData {

    private static final SimpleIntegerProperty acceleratore = new SimpleIntegerProperty(1);
    static public final SimpleIntegerProperty acceleratoreMin = new SimpleIntegerProperty(65000);
    static public final SimpleIntegerProperty acceleratoreMax = new SimpleIntegerProperty(0);
    static public final SimpleIntegerProperty acceleratorePercent = new SimpleIntegerProperty(75);
    private static final SimpleIntegerProperty frenoPosteriore = new SimpleIntegerProperty(2);
    static public final SimpleIntegerProperty frenoPosterioreMin = new SimpleIntegerProperty(65000);
    static public final SimpleIntegerProperty frenoPosterioreMax = new SimpleIntegerProperty(0);
    static public final SimpleIntegerProperty frenoPosteriorePercent = new SimpleIntegerProperty(50);
    private static final SimpleIntegerProperty frenoAnteriore = new SimpleIntegerProperty(3);
    static public final SimpleIntegerProperty frenoAnterioreMin = new SimpleIntegerProperty(65000);
    static public final SimpleIntegerProperty frenoAnterioreMax = new SimpleIntegerProperty(0);
    static public final SimpleIntegerProperty frenoAnteriorePercent = new SimpleIntegerProperty(3);
    static public final SimpleIntegerProperty angolo = new SimpleIntegerProperty(90);
    private static final SimpleBooleanProperty marciaInc = new SimpleBooleanProperty();
    private static final SimpleBooleanProperty marciaDec = new SimpleBooleanProperty();

    private ArduinoData() {
    }

    /**
     * Dati da arduino !!!! F0 & F7 non vengono passati 11 byte: F0 -> Inizio
     * messaggio
     *
     * MSB LSB 0000_0xxx 0xxx_xxxx 2 byte (utilizzati solo 10 bit) Freno
     * Anteriore ( 0 - 1023 ) 0000_0yyy 0yyy_yyyy 2 byte (utilizzati solo 10
     * bit) Freno Posteriore ( 0 - 1023 ) 0000_0zzz 0zzz_zzzz 2 byte (utilizzati
     * solo 10 bit) Acceleratore ( 0 - 1023 ) 0000_0aaa 0aaa_aaaa 2 byte
     * (utilizzati solo 10 bit) angolo ( 0 - 1023 ) 0000_00id 1 byte (utilizzati
     * solo 2 bit) incrementa marcia (0000_0010), decrementa marcia (0000_0001)
     * F7 -> Fine messaggio
     *
     * @param buf
     */
    public void setData(byte[] buf) {
        Utility.msgDebug(DatatypeConverter.printHexBinary(buf));
        frenoAnteriore.set(from10BitsToInt(buf[0], buf[1]));
        frenoPosteriore.set(from10BitsToInt(buf[2], buf[3]));
        acceleratore.set(from10BitsToInt(buf[4], buf[5]));
        angolo.set(from10BitsToInt(buf[6], buf[7]));
        marciaInc.set(isMarciaInc(buf[8]));
        marciaDec.set(isMarciaDec(buf[8]));
        Utility.msgDebug(toString());

        //setup
        if (Prop.Desc.FRENO_ANTERIORE_MIN.getValueInt() > frenoAnteriore.get()) {
            Prop.Desc.FRENO_ANTERIORE_MIN.setValue(frenoAnteriore.get() + "");
        }
        frenoAnterioreMin.set(Prop.Desc.FRENO_ANTERIORE_MIN.getValueInt());
        if (Prop.Desc.FRENO_ANTERIORE_MAX.getValueInt() < frenoAnteriore.get()) {
            Prop.Desc.FRENO_ANTERIORE_MAX.setValue(frenoAnteriore.get() + "");
        }
        frenoAnterioreMax.set(Prop.Desc.FRENO_ANTERIORE_MAX.getValueInt());
        frenoAnteriorePercent.set(percent(
                Prop.Desc.FRENO_ANTERIORE_MIN.getValueInt()
                , Prop.Desc.FRENO_ANTERIORE_MAX.getValueInt()
                ,frenoAnteriore.get()));
        if (Prop.Desc.FRENO_POSTERIORE_MIN.getValueInt() > frenoPosteriore.get()) {
            Prop.Desc.FRENO_POSTERIORE_MIN.setValue(frenoPosteriore.get() + "");
        }
        frenoPosterioreMin.set(Prop.Desc.FRENO_POSTERIORE_MIN.getValueInt());
        if (Prop.Desc.FRENO_POSTERIORE_MAX.getValueInt() < frenoPosteriore.get()) {
            Prop.Desc.FRENO_POSTERIORE_MAX.setValue(frenoPosteriore.get() + "");
        }
        frenoPosterioreMax.set(Prop.Desc.FRENO_POSTERIORE_MAX.getValueInt());
        frenoPosteriorePercent.set(percent(
                Prop.Desc.FRENO_POSTERIORE_MIN.getValueInt()
                , Prop.Desc.FRENO_POSTERIORE_MAX.getValueInt()
                ,frenoPosteriore.get()));
        if (Prop.Desc.ACCELERATORE_MIN.getValueInt() > acceleratore.get()) {
            Prop.Desc.ACCELERATORE_MIN.setValue(acceleratore.get() + "");
        }
        acceleratoreMin.set(Prop.Desc.ACCELERATORE_MIN.getValueInt());
        if (Prop.Desc.ACCELERATORE_MAX.getValueInt() < acceleratore.get()) {
            Prop.Desc.ACCELERATORE_MAX.setValue(acceleratore.get() + "");
        }
        acceleratoreMax.set(Prop.Desc.ACCELERATORE_MAX.getValueInt());
        acceleratorePercent.set(percent(
                Prop.Desc.ACCELERATORE_MIN.getValueInt()
                , Prop.Desc.ACCELERATORE_MAX.getValueInt()
                ,acceleratore.get()));
    }

    public void resetFrenoAnteriore() {
        Prop.Desc.FRENO_ANTERIORE_MIN.setValue(Short.MAX_VALUE*2 + "");
        ArduinoData.frenoAnterioreMin.set(Short.MAX_VALUE*2);
        Prop.Desc.FRENO_ANTERIORE_MAX.setValue(0 + "");
        ArduinoData.frenoAnterioreMax.set(0);
    }

    public void resetFrenoPosteriore() {
        Prop.Desc.FRENO_POSTERIORE_MIN.setValue(Short.MAX_VALUE*2 + "");
        ArduinoData.frenoPosterioreMin.set(Short.MAX_VALUE*2);
        Prop.Desc.FRENO_POSTERIORE_MAX.setValue(0 + "");
        ArduinoData.frenoPosterioreMax.set(0);
    }

    public void resetAcceleratore() {
        Prop.Desc.ACCELERATORE_MIN.setValue(Short.MAX_VALUE*2 + "");
        ArduinoData.acceleratoreMin.set(Short.MAX_VALUE*2);
        Prop.Desc.ACCELERATORE_MAX.setValue(0 + "");
        ArduinoData.acceleratoreMax.set(0);
    }

    //0000_0xxx 0xxx_xxxx  2 byte (utilizzati solo 10 bit)
    private int from10BitsToInt(int msb, int lsb) {
        if ((msb & 0x01) != 0) {
            lsb |= 0x80;
        }
        msb >>= 1;
        return (msb & 0x03) * 256 + lsb;
    }

    private boolean isMarciaInc(byte b) {
        return ((b & 0x02) != 0);
    }

    private boolean isMarciaDec(byte b) {
        return ((b & 0x01) != 0);
    }

    private int percent(int min, int max, int value) {
        //A=(max-min), B=(value-min)
        //A : 100 = B : X
        //result = B*100/A
        return (value - min) * 100 / (max - min);
    }

    @Override
    public String toString() {
        return "Arduino ha spedito: Freno anteriore:" + frenoAnteriore.get()
                + " Freno posteriore:" + frenoPosteriore.get()
                + " Acceleratore:" + acceleratore.get()
                + " Angolo:" + angolo.get()
                + " Marcia Inc.:" + marciaInc.get()
                + " Marcia Dec.:" + marciaDec.get();
    }

    public static ArduinoData getInstance() {
        return ArduinoDataHolder.INSTANCE;
    }

    private static class ArduinoDataHolder {

        private static final ArduinoData INSTANCE = new ArduinoData();
    }
}
