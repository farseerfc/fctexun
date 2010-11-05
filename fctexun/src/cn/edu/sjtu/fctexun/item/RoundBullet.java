package cn.edu.sjtu.fctexun.item;


import cn.edu.sjtu.fctexun.Config;
import cn.edu.sjtu.fctexun.Drawing;
import cn.edu.sjtu.fctexun.load.Load;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class RoundBullet extends Bullet {
    private static final float BULLET_SIZE = Config.BULLET_SIZE;
    
    private static final int WIDTH=(int)(BULLET_SIZE+BULLET_SIZE);
    
    private static final int [] colors = {
    	Drawing.makeARGB(0,0,0,0),
    	Drawing.makeARGB(102,255,205,0),
    	Drawing.makeARGB(204,255,205,0),
    	Drawing.makeARGB(255,255,205,0),
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
    
    @Load(count=WIDTH,discribe="Building Round Bullet")
    public static void load(Context context) {
		bitmap=Drawing.colorBitmap(colors, bit, WIDTH, WIDTH);
	}
	
	public boolean hitJudge(float shipX,float shipY){
		float x = shipX - posX;
        float y = shipY - posY;
        float dis = x * x + y * y;
		return dis< Config.HIT_RES;
	}

	@Override
	public final void draw(Canvas c){
		//c.drawCircle(posX,posY,BULLET_SIZE,bulletPaint);
		
		c.drawBitmap(bitmap, posX-BULLET_SIZE, posY-BULLET_SIZE, null);
		
	}
}
