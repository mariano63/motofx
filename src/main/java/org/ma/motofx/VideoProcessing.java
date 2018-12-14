/*
 * lavora qui per interazione con filmato!!!
 */
package org.ma.motofx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.ma.motofx.support.AsyncTask;

/**
 *
 * @author maria
 */
public class VideoProcessing {

    private final MediaPlayer mp;
    private final FXMLVideoController fxmlVideo;
    Duration duration;
    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public VideoProcessing(MediaPlayer mp, MediaView mediaView, FXMLVideoController fxml) {

//        Utility.msgDebug("is VideoProcessing class running on FXThread?"+isFxApplicationThread());
//Yes it is, so i called directly(be care about time consuming tasks)
        this.mp = mp;
        this.fxmlVideo = fxml;
        //crea un BIND coi LAPS
        FXMLSetupController fxmlData = (FXMLSetupController) 
                StageManager.getController(EStage.SETUP);
        String formatted = "Lap %d of "+(int)fxmlData.getSliderLaps().getValue();
        fxml.getLabelLap().textProperty().bind(
                mp.cycleCountProperty().asString(formatted));
                
        //Non mi funzia, boh...
//        mp.cycleCountProperty().addListener(new InvalidationListener() {
//            @Override
//            public void invalidated(Observable observable) {
//                FXMLDataController fxmlData = (FXMLDataController) SCENA.get(SceneManager.LeScene.SETUP).getController();
//                fxml.getLabelLap().setText(mp.getCycleCount()+" / "
//                        +fxmlData.getSpinnerLaps().getValue());
//            }
//        });
        mp.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
        mp.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                System.out.println("count:"+mp.getCurrentCount()+"-"+
                        "Rate:"+mp.getCurrentRate()+"-"+
                        ""+mp.getCycleCount());
            }
        });
        
        
        //Inizializzazioni start 
        //Numero di laps
        mp.cycleCountProperty().set((int)fxmlData.getSliderLaps().getValue());
        //1st lap
        mp.setCycleCount(1);

    }

    /**
     * This method is call Approx every 100mSecs after the play of video
     * pay attention to consuming time, and use AsyncTask.
     *
     */
    private void updateValues() {

        elapsedTime thd1 = new elapsedTime(fxmlVideo);
        thd1.execute();
        
//        {   //tst breaks progressing bar. Remove when done
//            int fa = ArduinoData.frenoAnteriorePercent.get();
//            if(fa>100) fa=0;
//            ArduinoData.frenoAnteriorePercent.set(++fa);
//            int fp = ArduinoData.frenoPosteriorePercent.get();
//            if(fp>100) fp=0;
//            ArduinoData.frenoPosteriorePercent.set(++fp);
//        }
        
    }

    /**
     * !!!non sarebbe cosi necessario usare un' Async...ma vabbè serve anche per descrizione...
     * We don't need to use the progressCallback(Object... params)
     * of AsyncTask (at least for now...)
     * because updateValue is called every 100msec, and so
     * the AsyncTask job arrive on onPostExecute(every 100 msecs.)
     **/
    class elapsedTime extends AsyncTask {

        private final FXMLVideoController controller;
        
        elapsedTime(FXMLVideoController controller) {
            this.controller = controller;
        }
        String tempo;

        @Override
        public void onPreExecute() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        //Datosi che il timer mi arriva da sistema 
        @Override
        public Object doInBackground(Object... params) {
//            Utility.msgDebug("isFxApplicationThread dovrebbe essere falso:" + isFxApplicationThread());
            Duration currentTime = mp.getCurrentTime();
            double minuti = currentTime.toMinutes() % 60;
            double secondi =  currentTime.toSeconds() % 60;
            double msecs = (currentTime.toMillis()) % 1000;
            tempo = String.format("%02.0f:%02.0f%c%1.0f", minuti, secondi, '.', msecs/100);
            return tempo;
        }

        @Override
        public void onPostExecute(Object params) {
//            Utility.msgDebug("la callback!!!, isFxApplicationThread dovrebbe essere vero:" + isFxApplicationThread());
//            fxmlVideo.getLabelTime().setText(tempo);
            controller.getLabelTime().setText(tempo);
        }

        @Override
        public void progressCallback(Object... params) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
