/**
 * Example of usage:
 * 
public class Example extends AsyncTask {
    private UIController controller;
    public Example(UIController controller) {
        this.controller = controller;
    }
    @Override
    void onPreExecute() {
    
        //This method runs on UI Thread before background task has started
        this.controller.updateProgressLabel("Starting Download")
    }
    @Override
    void doInBackground() {
    //This method runs on background thread
    
    boolean downloading = true;
    
        while (downloading){
        
//            
//            Your download code
//            
            
            double progress = 65.5 //Your progress calculation 
            publishProgress(progress);
        }
    }
    @Override
    void onPostExecute() {
        //This method runs on UI Thread after background task has done
        this.controller.updateProgressLabel("Download is Done");
    }
    @Override
    void progressCallback(Object... params) {
        //This method update your UI Thread during the execution of background thread
        double progress = (double)params[0]
        this.controller.updateProgress(progress);
    }
}
//To call this class you just need to instatiate that doing 
Example testing = new Example(myController);
testing.execute();

 */
package org.ma.motofx.support;

import javafx.application.Platform;

/**
 *
 * @author maria
 */
public abstract class AsyncTask<T1, T2, T3> {

	private boolean daemon = true;

	private T1[] params;

	protected abstract void onPreExecute();

	protected abstract T3 doInBackground(T1... params);

	protected abstract void onPostExecute(T3 params);

	protected abstract void progressCallback(T2... params);

	public void publishProgress(final T2... progressParams) {
		Platform.runLater(() -> progressCallback(progressParams));
	}

	private final Thread backGroundThread = new Thread(new Runnable() {
		@Override
		public void run() {

			final T3 param = doInBackground(params);

			Platform.runLater(() -> onPostExecute(param));
		}
	});

	public void execute(final T1... params) {
		this.params = params;
		Platform.runLater(() -> {

			onPreExecute();

			backGroundThread.setDaemon(daemon);
			backGroundThread.start();
		});
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public final boolean isInterrupted() {
		return this.backGroundThread.isInterrupted();
	}

	public final boolean isAlive() {
		return this.backGroundThread.isAlive();
	}
}