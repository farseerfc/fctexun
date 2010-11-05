package cn.edu.sjtu.fctexun.item;

import cn.edu.sjtu.fctexun.Config;
import cn.edu.sjtu.fctexun.Drawing;
import cn.edu.sjtu.fctexun.load.Load;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class NarrowBullet extends Bullet {
	private static final int WIDTH=16;
	
	private static final int [] colors = {
    	Drawing.makeARGB(0,0,0,0),
    	Drawing.makeARGB(102,205,205,255),
    	Drawing.makeARGB(204,205,205,255),
    	Drawing.makeARGB(255,205,205,255),
    	Drawing.makeARGB(255,255,255,255)
    };
    
    private static final int [] bit = {
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,
    	0,0,0,1,1,1,2,2,2,2,1,1,1,0,0,0,
    	0,1,1,2,2,3,3,3,3,3,3,2,2,1,1,0,
    	1,2,2,2,3,3,3,4,4,3,3,3,2,2,2,1,
    	1,2,2,2,3,3,3,4,4,3,3,3,2,2,2,1,
    	0,1,1,2,2,3,3,3,3,3,3,2,2,1,1,0,
    	0,0,0,1,1,1,2,2,2,2,1,1,1,0,0,0,
    	0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
    };
    
    private static final int NR_ROTATE = 36; 
    private static Bitmap[] rotate;
    private static Bitmap bitmap;
    
    @Load(count=WIDTH*NR_ROTATE,discribe="Loading Narrow Bullet")
    public static void loadBitmap(Context context){
        bitmap=Drawing.colorBitmap(colors, bit, WIDTH, WIDTH);
        rotate =Drawing.rotateDrawable(new BitmapDrawable(bitmap), NR_ROTATE, WIDTH);
    }
    
	//private boolean hit;
	

	@Override
	public boolean hitJudge(float shipX, float shipY) {
		double x = shipX - posX;
        double y = shipY - posY;
		double alpha = Math.atan2(speedY,speedX);
		double beta = Math.atan2(y,x);
		double angle=-alpha+beta;
		double res = Math.abs(6.0 * Math.cos(angle))+Math.abs(3.0*Math.sin(angle)) +Config.SHIP_SIZE;
		
        double dis = x * x + y * y;
		boolean hit= dis < res *res;
		return hit;
	}

	@Override
	public void draw(Canvas c) {
		//if(hit)return;
		float angle = (float)(Math.atan2(speedY,speedX)/Math.PI * 0.5);
		int index =((int)Math.round(angle*NR_ROTATE)+NR_ROTATE)%NR_ROTATE;
		c.drawBitmap(rotate[index], posX-8, posY-8, null);
	}

}
