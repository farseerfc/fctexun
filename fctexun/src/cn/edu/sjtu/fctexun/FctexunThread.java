package cn.edu.sjtu.fctexun;


import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;


class FctexunThread extends Thread implements SensorEventListener {

	private boolean running;

    private SurfaceHolder surfaceHolder;
   
    
    private float[] sensorValues;
    private Vibrator vibrator;

    private SensorManager sensor;

	public Context context;


	private Screen screen;

	public final Handler handler;

	private int width;

	private int height;


    public FctexunThread(SurfaceHolder holder, Context context, Handler han) {
        this.surfaceHolder = holder;
        this.context=context;
        this.handler = han;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        screen = new LoadScreen(this);

        
    }
    
    public void setRunning(boolean value) {
        synchronized (surfaceHolder) {
            running = value;
        }
    }

    public boolean isRunning() {
        return running;
    }
    
    public void changeScreen(Screen s)
    {
    	 synchronized (surfaceHolder) {
    		 screen = s;
    		 s.onSizeChanged(width, height);
    	 }
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (surfaceHolder) {
            synchronized (holder){
            	surfaceHolder = holder;
            	this.width=width;
            	this.height=height;
            	screen.onSizeChanged(width, height);
            }
        }
    }

    public void onTap(){
        synchronized (surfaceHolder) {
            screen.onTap();
        }
    }
    
    public void onPause()
    {
    	synchronized (surfaceHolder) {
    		screen.onPause();
    	}
    }
    
    public void onResume()
    {
    	synchronized (surfaceHolder) {
    		screen.onResume();
    	}
    }

    
    @Override
    public void run() {
    	if (Config.PROFILE){
    		String filepath=context.getResources().getString(R.string.profile_filepath);
    		Debug.startMethodTracing(filepath);
    	}
        while (running) {
            Canvas c = null;
            try {
                c = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    long time = System.currentTimeMillis();
                    
                    screen.onFrame(time, c, vibrator, sensorValues);
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
        if (Config.PROFILE){
        	Debug.stopMethodTracing();
        }
    }    

    @Override
    public void onAccuracyChanged(Sensor sensor, int arg1) {
        // do nothing
    }
    
    private float[] cali;
    
    public void caliSensor(){
    	cali=null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    	synchronized (surfaceHolder) {
	    	if(cali==null)
	    		cali=event.values.clone();
	    	else{
	    		float [] values=event.values;
	    		values[0]-=cali[0];values[1]-=cali[1];values[2]-=cali[2];
	    		while(values[0]<-10.0f)values[0]+=20.f;
	    		while(values[1]<-10.0f)values[1]+=20.f;
	    		while(values[2]<-10.0f)values[2]+=20.f;
	    		if(cali[2]<0){
	    			//upside down
	    			values[0]=-values[0];
	    			values[1]=-values[1];
	    			values[2]=-values[2];
	    		}
	    		
	    		sensorValues = values.clone();
	    	}
    	}
    }
    
	public void startSensor() {
		sensor = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors=sensor.getSensorList(SensorManager.SENSOR_ORIENTATION);
        if(sensors.size()>0){
            sensor.registerListener(this,  sensors.get(0),
                SensorManager.SENSOR_DELAY_FASTEST);
        }else{
            Log.e("fctexun","No orientation device found!");
        }
	}
	
	public void stopSensor() {
		sensor.unregisterListener(this);
	}

}