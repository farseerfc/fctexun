package cn.edu.sjtu.fctexun.item;

import cn.edu.sjtu.fctexun.Config;
import cn.edu.sjtu.fctexun.Drawing;
import cn.edu.sjtu.fctexun.load.Load;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TraceBullet extends Bullet {

    private static final float BULLET_SIZE = Config.BULLET_SIZE;
    
    private static final int WIDTH=(int)(BULLET_SIZE+BULLET_SIZE);
    
    private static final int [] colors = {
    	Drawing.makeARGB(0,0,0,0),
    	Drawing.makeARGB(102,255,102,102),
    	Drawing.makeARGB(204,255,102,102),
    	Drawing.makeARGB(255,255,102,102),
    	Drawing.makeARGB(255,255,255,255)
    };
    
    private static final int [] bit = {
    	0,0,0,1,1,0,0,0,
    	0,1,2,2,2,1,1,0,
    	0,2,2,3,3,2,1,0,
    	1,2,3,4,3,2,2,1,
    	1,2,3,3,3,2,2,1,
    	0,1,2,2,2,2,1,0,
    	0,1,1,2,2,1,1,0,
    	0,0,0,1,1,0,0,0
    };
    
    
    private static Bitmap bitmap;
    
    @Load(count=WIDTH,discribe="Loading TraceBullet")
    public static void loadBitmap(Context context){
    	bitmap=Drawing.colorBitmap(colors, bit, WIDTH, WIDTH);
    }
    
	private float dicx,dicy;
	
	public boolean hitJudge(float shipX,float shipY){
		
		dicx = shipX - posX;
        dicy = shipY - posY;
        float dis = dicx * dicx + dicy * dicy;
		return dis< Config.HIT_RES;
	}
	
	@Override
	public void move(int width, int height, long time) {
		double speed=Math.hypot(speedX,speedY);
		
		double dx = dicx /width;
		double dy = dicy /height;
		
		speedX += dx *0.01;
		speedY += dy *0.01;
		
		double s3=speed/Math.hypot(speedX,speedY) ;
		
		speedX *= s3;
		speedY *= s3;
		super.move(width, height, time);
	}

	@Override
	public final void draw(Canvas c){
		//c.drawCircle(posX,posY,BULLET_SIZE,bulletPaint);
		
		c.drawBitmap(bitmap, posX-BULLET_SIZE, posY-BULLET_SIZE, null);
		
	}

}
