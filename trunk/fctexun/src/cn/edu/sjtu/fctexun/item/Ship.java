package cn.edu.sjtu.fctexun.item;

import cn.edu.sjtu.fctexun.Config;
import cn.edu.sjtu.fctexun.Drawing;
import cn.edu.sjtu.fctexun.R;
import cn.edu.sjtu.fctexun.load.Load;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Ship extends Item {
    private static final float SHIP_SIZE = Config.SHIP_SIZE;
    
    private static Drawable aircraft;
    
    private static final int NR_ROTATE = 36;
    private static Bitmap [] shipRotate;
    
    @Load(count=SHIP_SIZE*SHIP_SIZE*NR_ROTATE,discribe="Loading and Rotating Aircraft Drawable")
    public static void loadBitmap(Context context) {
		aircraft=context.getResources().getDrawable(R.drawable.spacecraft);
		shipRotate=Drawing.rotateDrawable(aircraft, NR_ROTATE, SHIP_SIZE+SHIP_SIZE);
	}
    	
	@Override
	public void draw(Canvas c) {
		float angle = (float)(Math.atan2(speedX,-speedY)/Math.PI * 0.5);
		
		int rotate =((int)Math.round(angle*NR_ROTATE)+NR_ROTATE)%NR_ROTATE;
		c.drawBitmap(shipRotate[rotate], posX-SHIP_SIZE, posY-SHIP_SIZE, null);
	}

}
