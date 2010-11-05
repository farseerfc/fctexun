package cn.edu.sjtu.fctexun;

import android.graphics.Canvas;
import android.os.Vibrator;

public abstract class Screen {
	public abstract void onSizeChanged(int width,int height);
	public abstract void onFrame(long time,Canvas c,Vibrator v,float[] sensorValues);
	public abstract void onPause();
	public abstract void onResume();
	public abstract boolean isPaused();
	public abstract void onTap();
}
