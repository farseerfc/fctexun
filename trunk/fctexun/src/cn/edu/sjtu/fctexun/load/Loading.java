package cn.edu.sjtu.fctexun.load;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.edu.sjtu.fctexun.Config;



public class Loading {
	static class Loader{
		float count;
		String discribe;
		Method method;
		
		public Loader(float count,String discribe,Method me){
			this.count=count;
			this.discribe=discribe;
			this.method=me;
		}

	}
	
	public interface LoadingCallback{
		public void onLoading(float percent,String discribe);
	}
	
	public static void setContext(Context con){
		context=con;
	}
	
	private static List<Loader> loaders= new ArrayList<Loader>();
	private static Context context=null;
	private static boolean collected=false;
	
	public static final String [] classNames={
		"cn.edu.sjtu.fctexun.item.NarrowBullet",
		"cn.edu.sjtu.fctexun.item.RoundBullet",
		"cn.edu.sjtu.fctexun.item.TraceBullet",
		"cn.edu.sjtu.fctexun.item.Ship",
		"cn.edu.sjtu.fctexun.item.Star",
	};
	
	public static void register(Loader loader){
		loaders.add(loader);
	}
	
	private static void collect() throws ClassNotFoundException
	{
		Class [] list=new Class [classNames.length];
		for (int i=0;i<classNames.length;++i){
			list[i]=Class.forName(classNames[i]);
		}		
		 
		for(Class c : list){
			Method[] ms = c.getMethods();
			for (Method me : ms){
				final Method meth=me;
				Load an=me.getAnnotation(Load.class);
				if(an!=null){
					register(new Loader(an.count(),an.discribe(),meth));
				}
			}
		}
		
	}
	
	public static void load(LoadingCallback callback,String finishDiscribe)
	{
		try {
			collect();
			
			float total=0.0f;
			for(int i=0;i<loaders.size();++i){
				total+=loaders.get(i).count;
			}
			float current=0.0f;


			for(int i=0;i<loaders.size();++i){
				callback.onLoading(current/total,loaders.get(i).discribe);
				
				loaders.get(i).method.invoke(null, new Object[]{context});
				
				current+=loaders.get(i).count;
			}
			
			callback.onLoading(1.0f,finishDiscribe);
		} catch (Exception e) {
			Config.LogThrowable(e);
			
		} 

	}
}
