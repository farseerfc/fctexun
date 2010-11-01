package cn.edu.sjtu.fctexun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Ship extends Item {
    private static final float SHIP_SIZE = Config.SHIP_SIZE;
    
    private Drawable aircraft;
    
    private static final int NR_ROTATE = 36;
    private static Bitmap [] shipRotate;
    
	public Ship(Context context){
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
