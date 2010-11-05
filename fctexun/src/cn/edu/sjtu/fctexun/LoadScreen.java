package cn.edu.sjtu.fctexun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Vibrator;
import cn.edu.sjtu.fctexun.load.Loading;

public class LoadScreen extends Screen {
	
    private Paint backPaint;
    private Paint textPaint;
    private Paint capText;
    
    private float percent;
    private String discribe;
	private int height;
	private int width;
	private Rect screenRect;
	
	private boolean finished;
	private FctexunThread thread;
	
	public LoadScreen(FctexunThread thre){
        backPaint = new Paint();
        backPaint.setAntiAlias(Config.DRAW_ANTIALIAS);
        backPaint.setARGB(150, 0, 0, 0);

        textPaint = new Paint();
        textPaint.setTextAlign(Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 255, 255, 255);
        
        capText=new Paint();        
        capText.setTextAlign(Align.CENTER);
        capText.setTextSize(32);
        capText.setAntiAlias(true);
        capText.setARGB(255, 255, 255, 255);
        
        finished = false;
        this.thread=thre;
        
        new Thread(new Runnable(){

			@Override
			public void run() {
				Loading.setContext(thread.context);
				Loading.load(new Loading.LoadingCallback(){
					@Override
					public void onLoading(float per, String dis) {
						percent=per;
						discribe=dis;
						
						if(percent >=1.0f){
							finished = true;
						}
					}
		        }, "Load finished");	
			}
        	
        }).start();
        
	}

	@Override
	public void onSizeChanged(int width, int height) {
		this.height = height;
        this.width = width;
        screenRect = new Rect (0,0,width,height);
	}

	@Override
	public void onFrame(long time, Canvas c, Vibrator v, float[] sensorValues) {
		c.drawRect(screenRect, backPaint);
		if(discribe!=null){
			c.drawText("Loading Fxtexun",width/2,height/2-40,capText);
			c.drawText(discribe,width/2,height/2,textPaint);
			c.drawText(Integer.toString((int)(percent*100.0f))+"%",width/2,height/2+80,capText);
			c.drawText("Calibrating device with current orientation",width/2,height/2+100,textPaint);
		}
	}

	@Override
	public void onPause() {
		
	}

	@Override
	public void onResume() {
		
	}

	@Override
	public boolean isPaused() {
		return !finished;
	}

	@Override
	public void onTap() {
		if (finished){
			thread.caliSensor();
			thread.changeScreen(new GameScreen(thread.handler));
		}
	}

}
