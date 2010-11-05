package cn.edu.sjtu.fctexun;

import android.util.Log;

public class Config {
    public final static int SAMPLE_PERIOD_FRAMES = 40;
    public static final boolean DRAW_ANTIALIAS=false;
    
    public static final int NR_STAR = 128;

	public static final int NR_BULLET = 64;
	
    public static final float STAR_SIZE = 1.0f;
    public static final float BULLET_SIZE = 4.0f;
    public static final float SHIP_SIZE = 16.0f;

    public static final float HIT_RES = (BULLET_SIZE + SHIP_SIZE) * (BULLET_SIZE + SHIP_SIZE);
    

	public static final boolean PROFILE = true;
	
	public static void LogThrowable(Throwable e){
		do{
			Log.e("fctexun", "Exception: "+e.getClass().getCanonicalName()+" : "+e.getMessage());
			for(StackTraceElement st:e.getStackTrace()){
				Log.e("fctexun","    at: "+st.toString());
			}
			if(e.getCause()!=null){
				e=e.getCause();
				Log.e("fctexun","Caused by:");
			}else{
				break;
			}
		}while(true);
	}
}
