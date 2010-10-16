package cn.edu.sjtu.fctexun;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

public class Fctexun extends Activity {
    /** Called when the activity is first created. */
	private FctexunView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        
        setContentView(R.layout.main);
        
        view = (FctexunView) findViewById(R.id.fctexunview);
        
    }
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        view.getThread().pause(); // pause game when Activity pauses
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return view.onTouchEvent(event);
    }
}