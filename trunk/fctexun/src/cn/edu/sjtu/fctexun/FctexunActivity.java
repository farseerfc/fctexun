package cn.edu.sjtu.fctexun;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class FctexunActivity extends Activity {
	
	private static final int MENU_RESUME = 1;
	private static final int MENU_EXIT = 2;
	
	
    /** Called when the activity is first created. */
	private FctexunView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        view = (FctexunView) findViewById(R.id.fctexunview);
        

    }
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        view.exitGame();
        finish();
        //view.getThread().pause(); // pause game when Activity pauses
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if(!super.onCreateOptionsMenu(menu))return false;
    	
    	menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
    	menu.add(0, MENU_EXIT, 0, R.string.menu_exit);
    	
    	return true;
    	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(!super.onOptionsItemSelected(item))return false;
    	Log.e("fctexun","menu");
    	
    	switch(item.getItemId()){
    	case MENU_RESUME:
    		Log.e("fctexun","menu resume");
    		view.bringToFront();
    		view.getThread().doResume();
    		return true;
    	case MENU_EXIT:
    		Log.e("fctexun","menu exit");
    		view.exitGame();
    		finish();
    		return true;
    	}
    	
    	return false;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return view.onTouchEvent(event);
    }
}