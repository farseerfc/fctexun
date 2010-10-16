package cn.edu.sjtu.fctexun;



import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class FctexunView extends SurfaceView implements Callback , SensorEventListener {
	
	private FctexunThread thread;
	private SensorManager sensor;
	private Context context;
	
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	
	public FctexunView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
        this.context=context;
        
        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

        // create thread only; it's started in surfaceCreated()
        thread = new FctexunThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {

            }
        });

        setFocusable(true); // make sure we get key events
	}
	
	public FctexunThread getThread(){
		return thread;
	}
	
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	super.onTouchEvent(event);
    	if(event.getAction()==MotionEvent.ACTION_UP){
    		thread.tap();
    		return true;
    	}
    	return false;
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		thread.surfaceChanged(holder, format, width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
		if(thread.isRunning()){
			thread.doStart();
		}else{
			sensor = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
			List<Sensor> sensors=sensor.getSensorList(SensorManager.SENSOR_ORIENTATION);
			if(sensors.size()>0){
				sensor.registerListener(this,  sensors.get(0),
	                SensorManager.SENSOR_DELAY_FASTEST);
				
				Log.i("fctexun","Found orientation device!");
			}else{
				Log.i("fctexun","No orientation device found!");
				Toast t = Toast.makeText(context, R.string.no_orientation, Toast.LENGTH_LONG);
				t.show();
			} 
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "fctexun");
			wl.acquire();
			
			thread.setRunning(true);
	        thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
		boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        
        wl.release();
        
        sensor.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int arg1) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		thread.onSensorChanged(event);
		
	}

}
