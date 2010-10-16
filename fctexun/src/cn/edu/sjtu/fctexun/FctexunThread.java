package cn.edu.sjtu.fctexun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

class FctexunThread extends Thread {

	private boolean running;

	private SurfaceHolder surfaceHolder;
	private Handler handler;
	private int height;
	private int width;

	private Paint bulletPaint;
	private Paint backPaint;
	private Paint shipPaint;
	private Paint hitPaint;
	private Paint starPaint;
	private Paint textPaint;
	private Paint capText;
	private Paint centerPaint;
	
	private Drawable aircraft;
	
	private static final boolean BULLET_ANTIALIAS=false;

	private Rect screen;

	private List<Bullet> bullets;
	private List<Bullet> stars;
	private Bullet ship;

	private float[] sensorValues;
	private Vibrator vibrator;

	private long bestAlive;
	private long aliveStart;
	private long lastFrame;
	private long aliveTime;
	private long hitCount;
	private boolean lastHit;
	private boolean hit;

	private long frames;
	private long fpsLastFrame;
	private int fps;
	private final static int SAMPLE_PERIOD_FRAMES = 15;
	private final static float SAMPLE_FACTOR = 1.0f / SAMPLE_PERIOD_FRAMES;

	private static final float STAR_SIZE = 1.0f;
	private static final float BULLET_SIZE = 4.0f;
	private static final float SHIP_SIZE = 16.0f;
	private static final float HIT_RES = (BULLET_SIZE + SHIP_SIZE) * (BULLET_SIZE + SHIP_SIZE);
	
	private int mode;

	private Context context;
	
	private static final int MODE_START = 0;
	private static final int MODE_RUNNING = 1;
	private static final int MODE_PAUSE = 2;
	private static final int MODE_NO_ORIENT = 3;

	public void setRunning(boolean value) {
		synchronized (surfaceHolder) {
			running = value;
		}
	}

	public boolean isRunning() {
		return running;
	}

	public FctexunThread(SurfaceHolder holder, Context context, Handler handler) {
		this.surfaceHolder = holder;
		this.handler = handler;
		this.context=context;

		bulletPaint = new Paint();
		bulletPaint.setAntiAlias(BULLET_ANTIALIAS);
		bulletPaint.setARGB(255, 255, 205, 0);

		backPaint = new Paint();
		backPaint.setAntiAlias(BULLET_ANTIALIAS);
		backPaint.setARGB(150, 0, 0, 0);

		shipPaint = new Paint();
		shipPaint.setAntiAlias(BULLET_ANTIALIAS);
		shipPaint.setARGB(255, 51, 204, 255);

		hitPaint = new Paint();
		hitPaint.setAntiAlias(BULLET_ANTIALIAS);
		hitPaint.setARGB(255, 255, 0, 0);

		starPaint = new Paint();
		starPaint.setAntiAlias(BULLET_ANTIALIAS);
		starPaint.setARGB(255, 102, 102, 102);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setARGB(255, 255, 255, 255);
		
		capText=new Paint();		
		capText.setTextAlign(Align.CENTER);
		capText.setTextSize(32);
		capText.setAntiAlias(true);
		capText.setARGB(255, 255, 255, 255);
		
		centerPaint = new Paint();
		centerPaint.setAntiAlias(BULLET_ANTIALIAS);
		centerPaint.setARGB(100, 255, 255, 255);
		
		aircraft=context.getResources().getDrawable(R.drawable.spacecraft);

		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		
		bestAlive = 0;
		hitCount = 0;
	}

	public void restartGame() {

		aliveStart = System.currentTimeMillis();
		lastFrame = System.currentTimeMillis();
		frames = 0;
		buildBullets();
		mode = MODE_START;
		
	}

	private void buildBullets() {
		bullets = new ArrayList<Bullet>();
		Random r = new Random();
		for (int i = 1; i < 64; ++i) {
			float ran=(float)((r.nextFloat()-0.5f)*2.0f*Math.PI);
			float x =  (float)Math.cos(ran)* width*.5f+width*.5f;
			float y = (float)Math.sin(ran) * height*.5f+height*.5f;
			float sx = r.nextFloat() * 0.1f - 0.05f;
			float sy = r.nextFloat() * 0.1f - 0.05f;
			bullets.add(new Bullet(x, y, sx, sy));
		}

		stars = new ArrayList<Bullet>();
		for (int i = 1; i < 128; ++i) {
			float x = r.nextFloat() * width;
			float y = r.nextFloat() * height;
			float sx = r.nextFloat() * 0.01f - 0.005f;
			float sy = r.nextFloat() * 0.01f - 0.005f;
			stars.add(new Bullet(x, y, sx, sy));
		}

		ship = new Bullet(width * 0.5f, height * 0.5f, 0, 0);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		synchronized (surfaceHolder) {
			this.height = height;
			this.width = width;
			screen = new Rect (0,0,width,height);
			
			restartGame();

		}
	}

	private void onPhysic(long time) {
		
		long delta = time - lastFrame;
		lastFrame = time;

		for (int i = 0; i < stars.size(); ++i) {
			stars.get(i).move(width, height, delta);
		}

		for (int i = 0; i < bullets.size(); ++i) {
			bullets.get(i).move(width, height, delta);
		}

		if (sensorValues != null) {
			ship.setSpeedX(sensorValues[0] * -0.0002f * width);
			ship.setSpeedY(sensorValues[1] * 0.0002f * height);
		}else{
			mode = MODE_NO_ORIENT;
		}

		ship.move(width, height, delta);

		hit = false;
		for (int i = 0; i < bullets.size(); ++i) {
			float x = ship.getPosX() - bullets.get(i).getPosX();
			float y = ship.getPosY() - bullets.get(i).getPosY();
			float dis = x * x + y * y;
			hit = hit || dis < HIT_RES;
		}

		if (hit) {
			vibrator.cancel();
			vibrator.vibrate(50);

			if (!lastHit) {
				hitCount++;
				
				mode=MODE_START;
			}
		} else {
			vibrator.cancel();

			if (lastHit) {
				aliveStart = time;
			}
		}
		lastHit = hit;

		aliveTime = time - aliveStart;
		if (aliveTime > bestAlive)
			bestAlive = aliveTime;

	}
	
	private void onFps(long time){
		if (frames++ == SAMPLE_PERIOD_FRAMES) {
			frames = 0;
			long fpsDelta = time - fpsLastFrame;
			fpsLastFrame = time;
			fps = (int) (1000 / (fpsDelta * SAMPLE_FACTOR));
		}
	}

	private void onPaint(Canvas c) {
		c.drawRect(screen, backPaint);

		for (int i = 0; i < stars.size(); ++i) {
			stars.get(i).draw(c, starPaint,starPaint, STAR_SIZE);
		}

		for (int i = 0; i < bullets.size(); ++i) {
			bullets.get(i).draw(c, bulletPaint,centerPaint, BULLET_SIZE);
		}

		//ship.draw(c, hit ? hitPaint : shipPaint,centerPaint, SHIP_SIZE);
		
		float angle = (float)(Math.atan2(ship.getSpeedX(),-ship.getSpeedY())/Math.PI * 180);
		Matrix m=new Matrix();
		m.setRotate(angle, ship.getPosX(), ship.getPosY());
		aircraft.setBounds(new Rect(
				(int)(ship.getPosX()-SHIP_SIZE),
				(int)(ship.getPosY()-SHIP_SIZE),
				(int)(ship.getPosX()+SHIP_SIZE),
				(int)(ship.getPosY()+SHIP_SIZE)));
		
		c.save();
		c.concat(m);
		aircraft.draw(c);
		c.restore();

		c.drawText("FPS:   " + Integer.toString(fps), width - 70, height - 70, textPaint);
		c.drawText("Hits:  " + Long.toString(hitCount), width - 70, height - 50, textPaint);
		c.drawText("Alive: " + Long.toString(aliveTime), width - 70, height - 30, textPaint);
		c.drawText("Best:  " + Long.toString(bestAlive), width - 70, height - 10, textPaint);
		
		
		if (mode == MODE_RUNNING) return;
		
		
		c.drawRect(screen, backPaint);
		
		switch(mode){
		case MODE_PAUSE:
			c.drawText("Paused",width/2,height/2-40,capText);
			c.drawText("Alive:"+ Long.toString(aliveTime), width/2, height/2, capText);
			break;
		case MODE_START:
			c.drawText("Tap to Start!",width/2,height/2-40,capText);
			c.drawText("Alive: " + Long.toString(aliveTime), width/2, height/2, capText);
			c.drawText("Best:  "+ Long.toString(bestAlive), width/2, height/2+40, capText);
			c.drawText("HIT:   "+ Long.toString(hitCount), width/2, height/2+80, capText);
			break;
		case MODE_NO_ORIENT:
			c.drawText("No Orientation!",width/2,height/2-40,capText);
			c.drawText("Are you running in a android virtual device? " ,0,height/2-10,textPaint);
			c.drawText("We need orientation device to play this game." ,0,height/2+10,textPaint);
			break;
		}
		
	}
	
	public void pause(){
		synchronized (surfaceHolder) {
			if(mode == MODE_RUNNING){
				mode = MODE_PAUSE;
				lastFrame=System.currentTimeMillis()-lastFrame;
				fpsLastFrame= System.currentTimeMillis()-fpsLastFrame;
				aliveStart = aliveStart-System.currentTimeMillis();
			}
		}
	}
	
	
	public void tap(){
		synchronized (surfaceHolder) {
			if(mode == MODE_RUNNING)
				pause();
			else
				doStart();
		}
	}
	
	
	public void doStart(){
		synchronized (surfaceHolder) {
			if(mode==MODE_PAUSE){
				mode=MODE_RUNNING;
				aliveStart+=System.currentTimeMillis();
				lastFrame+=System.currentTimeMillis();
				fpsLastFrame+=System.currentTimeMillis();
			}
			
			if(mode==MODE_START){
				restartGame();
				mode=MODE_RUNNING;
			}
		}
	}

	@Override
	public void run() {

		while (running) {
			Canvas c = null;
			try {
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					long time = System.currentTimeMillis();
					if(mode == MODE_RUNNING)
						onPhysic(time);
					onFps(time);
					onPaint(c);
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

	}

	public void onSensorChanged(SensorEvent event) {
		synchronized (surfaceHolder) {
			sensorValues = event.values;
		}
	}

}