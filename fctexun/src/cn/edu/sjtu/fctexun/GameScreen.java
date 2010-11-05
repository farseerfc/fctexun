package cn.edu.sjtu.fctexun;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import cn.edu.sjtu.fctexun.item.Bullet;
import cn.edu.sjtu.fctexun.item.Item;
import cn.edu.sjtu.fctexun.item.NarrowBullet;
import cn.edu.sjtu.fctexun.item.RoundBullet;
import cn.edu.sjtu.fctexun.item.Ship;
import cn.edu.sjtu.fctexun.item.Star;
import cn.edu.sjtu.fctexun.item.TraceBullet;

public class GameScreen extends Screen {
	private Handler handler;
	
    private Paint backPaint;
    private Paint textPaint;
    private Paint capText;

    private Rect screenRect;

    private Bullet bullets [];
    private Star stars [];
    private Ship ship;
    private Item items [];
    

    private long bestAlive;
    private long aliveStart;
    private long lastFrame;
    private long aliveTime;
    private long hitCount;
    private boolean lastHit;
    private boolean hit;

    private int height;
    private int width;

    private long frames;
    private long fpsLastFrame;
    private float fps;
    


    public final static int SAMPLE_PERIOD_FRAMES = Config.SAMPLE_PERIOD_FRAMES;
    private final static float SAMPLE_FACTOR = 1000.0f * SAMPLE_PERIOD_FRAMES;


    public static final int NR_STAR = Config.NR_STAR;

	public static final int NR_BULLET = Config.NR_BULLET;
	
	public static final int NR_ITEM = NR_STAR+NR_BULLET+1;
	
    
    public static final float BULLET_SIZE = Config.BULLET_SIZE;
    public static final float SHIP_SIZE = Config.SHIP_SIZE;

    public static final float HIT_RES = (BULLET_SIZE + SHIP_SIZE) * (BULLET_SIZE + SHIP_SIZE);
    
    
    private static final int MODE_START = 0;
    private static final int MODE_RUNNING = 1;
    private static final int MODE_PAUSE = 2;
    private static final int MODE_NO_ORIENT = 3;
    
    private int mode;
    
    public GameScreen(Handler handler){
        backPaint = new Paint();
        backPaint.setAntiAlias(Config.DRAW_ANTIALIAS);
        backPaint.setARGB(150, 0, 0, 0);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setARGB(255, 255, 255, 255);
        
        capText=new Paint();        
        capText.setTextAlign(Align.CENTER);
        capText.setTextSize(32);
        capText.setAntiAlias(true);
        capText.setARGB(255, 255, 255, 255);
        
        
        bestAlive = 0;
        hitCount = 0;
    }
    

    public void restartGame() {
        aliveStart = System.currentTimeMillis();
        lastFrame = System.currentTimeMillis();
        frames = 0;
        buildBullets();
        setMode ( MODE_START);
    }

    private void buildBullets() {
    	items = new Item [NR_ITEM];
    	
        bullets = new Bullet [NR_BULLET];
        Random r = new Random();
        for (int i = 0; i < NR_BULLET; ++i) {
            float ran=(float)((r.nextFloat()-0.5f)*2.0f*Math.PI);
            float x =  (float)Math.cos(ran)* width*.5f+width*.5f;
            float y = (float)Math.sin(ran) * height*.5f+height*.5f;
            float sx = r.nextFloat() * 0.1f - 0.05f;
            float sy = r.nextFloat() * 0.1f - 0.05f;
            switch (i%13){
            case 1:
            	bullets[i]=new TraceBullet();
            	break;
            case 2:case 3:case 4:case 5:case 6:
            	bullets[i]=new NarrowBullet();
            	break;
            default:
            	bullets[i]=new RoundBullet();
            }
            bullets[i].setPos(x,y);
            bullets[i].setSpeed(sx, sy);
            
            items[i]=bullets[i];
        }

        stars = new Star[NR_STAR];
        for (int i = 0; i < NR_STAR; ++i) {
            float x = r.nextFloat() * width;
            float y = r.nextFloat() * height;
            float sx = r.nextFloat() * 0.02f - 0.01f;
            float sy = r.nextFloat() * 0.02f - 0.01f;
            stars[i]=new Star();
            stars[i].setPos(x, y);
            stars[i].setSpeed(sx,sy);
            
            items[NR_BULLET+i]=stars[i];
        }

        ship = new Ship();
        ship.setPos(width/2, height/2);
        ship.setSpeed(0.0f, 0.0f);
        items[NR_BULLET+NR_STAR]=ship;
    }

    private final void onPhysic(long time, Vibrator vibrator, float[] sensorValues) {
        
        long delta = time - lastFrame;
        lastFrame = time;

        if (sensorValues != null) {
            ship.speedX = sensorValues[0] * -0.0002f * width;
            ship.speedY = sensorValues[1] * 0.0002f * height;
        }else{
            setMode ( MODE_NO_ORIENT);
        }

        for (int i = 0; i < NR_ITEM; ++i) {
            items[i].move(width, height, delta);
        }


        hit = false;
        for (int i = 0; i < NR_BULLET; ++i) {
            hit = hit || bullets[i].hitJudge(ship.posX, ship.posY);;
        }

        if (hit) {
            vibrator.cancel();
            vibrator.vibrate(50);

            if (!lastHit) {
                hitCount++;
                
                setMode(MODE_START);
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
    
    private final void onPaint(Canvas c, float[] sensorValues) 
    {
        c.drawRect(screenRect, backPaint);

        
        for (int i = 0; i < NR_ITEM; ++i) {
            items[i].draw(c);
        }
        
        c.drawText("FPS:   " + Float.toString(fps), width - 80, height - 70, textPaint);
        c.drawText("Hits:  " + Long.toString(hitCount), width - 80, height - 50, textPaint);
        c.drawText("Alive: " + Long.toString(aliveTime), width - 80, height - 30, textPaint);
        c.drawText("Best:  " + Long.toString(bestAlive), width - 80, height - 10, textPaint);
        
        
        if (getMode() == MODE_RUNNING) return;
        
        
        c.drawRect(screenRect, backPaint);
        
        switch(getMode()){
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
            if (sensorValues != null) 
            	setMode(MODE_RUNNING);
            break;
        }
        
    }
    
    private final void onFps(long time){
        if (frames++ == SAMPLE_PERIOD_FRAMES) {
            frames = 0;
            long fpsDelta = time - fpsLastFrame;
            fpsLastFrame = time;
            fps =  SAMPLE_FACTOR / fpsDelta;
        }
    }
    

    public boolean isPaused(){
    	return getMode()!=MODE_RUNNING;
    }
    
    public void onPause(){
        if(getMode() == MODE_RUNNING){
            setMode ( MODE_PAUSE);
            lastFrame=System.currentTimeMillis()-lastFrame;
            fpsLastFrame= System.currentTimeMillis()-fpsLastFrame;
            aliveStart = aliveStart-System.currentTimeMillis();
        }
    }
    
    
   

	private int getMode() {
		return mode;
	}
    
    
    public void onResume(){
        if(getMode()==MODE_PAUSE){
            setMode(MODE_RUNNING);
            aliveStart+=System.currentTimeMillis();
            lastFrame+=System.currentTimeMillis();
            fpsLastFrame+=System.currentTimeMillis();
        }
        
        if(getMode()==MODE_START){
            restartGame();
            setMode(MODE_RUNNING);
        }
    }
    
    private void setMode(int m)
    {
    	mode=m;
    	/*
    	if(getMode() == MODE_RUNNING){
    		Message msg=new Message();
    		msg.arg1=FctexunView.RESUME_SENSOR;
    		handler.sendMessage(msg);
    	}else{
    		Message msg=new Message();
    		msg.arg1=FctexunView.PAUSE_SENSOR;
    		handler.sendMessage(msg);
    	}
    	*/
    }


	@Override
	public void onSizeChanged(int width, int height) {
		this.height = height;
        this.width = width;
        screenRect = new Rect (0,0,width,height);
        
        restartGame();
	}


	@Override
	public void onFrame(long time, Canvas c, Vibrator v, float[] sensorValues) {
		if(getMode() == MODE_RUNNING)
            onPhysic(time,v,sensorValues);
        onFps(time);
        onPaint(c,sensorValues);
	}


	@Override
	public void onTap() {
		if(isPaused())
        	onResume();
        else
        	onPause();
	}
}
