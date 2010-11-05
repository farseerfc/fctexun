package cn.edu.sjtu.fctexun.item;

import cn.edu.sjtu.fctexun.Config;
import cn.edu.sjtu.fctexun.load.Load;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Star extends Item {
	public static final float STAR_SIZE = Config.STAR_SIZE;
	public static final boolean STAR_ANTIALIAS = Config.DRAW_ANTIALIAS;
	private static Paint[] starPaint ;
	
	@Load(count=5.0f,discribe="Loading Star Paint")
	public static void loadPaint(Context context) {
		starPaint = new Paint [5];
        starPaint[0] = new Paint();
        starPaint[0].setAntiAlias(STAR_ANTIALIAS);
        starPaint[0].setARGB(255, 102, 102, 102);
        starPaint[1] = new Paint();
        starPaint[1].setAntiAlias(STAR_ANTIALIAS);
        starPaint[1].setARGB(255, 153, 153, 153);
        starPaint[2] = new Paint();
        starPaint[2].setAntiAlias(STAR_ANTIALIAS);
        starPaint[2].setARGB(255, 204, 204, 204);
        starPaint[3] = new Paint();
        starPaint[3].setAntiAlias(STAR_ANTIALIAS);
        starPaint[3].setARGB(255, 255, 255, 255);
        starPaint[4] = new Paint();
        starPaint[4].setAntiAlias(STAR_ANTIALIAS);
        starPaint[4].setARGB(255, 51, 51, 51);
	}

	@Override
	public void draw(Canvas c) {
		//c.drawCircle(posX,posY,STAR_SIZE,starPaint);
		c.drawPoint(posX, posY, starPaint[(int)(posX+posY) % 5]);
	}

}
