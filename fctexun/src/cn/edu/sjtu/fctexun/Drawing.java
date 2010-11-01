package cn.edu.sjtu.fctexun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Drawing {
    public static int makeARGB(int a,int r,int g,int b){
    	return (a<<24)+(r<<16)+(g<<8)+b;
    }
    
    public static Bitmap colorBitmap(int [] color, int [] bitmap,int width, int height){
    	int [] map = new int [bitmap.length];
        for(int i=0;i<bitmap.length;++i){
        	map[i]=color[bitmap[i]];
        }
        
        Bitmap result = Bitmap.createBitmap(map, width, height, Bitmap.Config.ARGB_8888);
        result.prepareToDraw();
        return result;
    }
    
    public static Bitmap [] rotateDrawable(Drawable drawable,int nr_rotate,float width){
		float half_width=width*0.5f;
		int int_width=(int)width;
    	Bitmap [] result =new Bitmap[nr_rotate];
    	Rect bounds=new Rect();
    	Matrix rotateMatrix=new Matrix();
    	float rotate_angle=360.0f/nr_rotate;
    	
		for(int i=0;i<nr_rotate;++i){
			result[i]=Bitmap.createBitmap(int_width, int_width, Bitmap.Config.ARGB_8888);
			Canvas c=new Canvas(result[i]);
			rotateMatrix.setRotate(i*rotate_angle,half_width,half_width);
			bounds.set(0,0,int_width,int_width);
			drawable.setBounds(bounds);
			c.concat(rotateMatrix);
			drawable.draw(c);
		}
		return result;
    }
    
    public static float MAX2(float a,float b){
    	return a>b?a:b;
    }
    
    public static float MIN2(float a,float b){
    	return a<b?a:b;
    }
    
    public static float MIN3(float a, float b , float c)
    {
    	return MIN2(MIN2(a,b),c);
    }
    
    public static float MAX3(float a,float b, float c){
    	return MAX2(MAX2(a,b),c);
    }
    
    // r,g,b values are from 0 to 1
    // h = [0,360], s = [0,1], v = [0,1]
    //	if s == 0, then h = -1 (undefined)
    public static float [] rgb2hsvf( float r, float g, float b )
    {
    	float min, max, delta;
    	float h,s,v;

    	min = MIN3( r, g, b );
    	max = MAX3( r, g, b );
    	v = max;				// v

    	delta = max - min;

    	if( max != 0 )
    		s = delta / max;		// s
    	else {
    		// r = g = b = 0		// s = 0, v is undefined
    		s = 0;
    		h = -1;
    		return new float [] {h,s,v};
    	}

    	if( r == max )
    		h = ( g - b ) / delta;		// between yellow & magenta
    	else if( g == max )
    		h = 2 + ( b - r ) / delta;	// between cyan & yellow
    	else
    		h = 4 + ( r - g ) / delta;	// between magenta & cyan

    	h *= 60;				// degrees
    	if( h < 0 )
    		h += 360;
    	
    	return new float [] {h,s,v};
    }

    public static float [] hsv2rgbf(  float h, float s, float v )
    {
    	int i;
    	float f, p, q, t;
    	float r,g,b;

    	if( s == 0 ) {
    		// achromatic (grey)
    		r = g = b = v;
    		return new float[] {r,g,b};
    	}

    	h /= 60;			// sector 0 to 5
    	i = (int)Math.floor( h );
    	f = h - i;			// factorial part of h
    	p = v * ( 1 - s );
    	q = v * ( 1 - s * f );
    	t = v * ( 1 - s * ( 1 - f ) );

    	switch( i ) {
    		case 0:
    			r = v;
    			g = t;
    			b = p;
    			break;
    		case 1:
    			r = q;
    			g = v;
    			b = p;
    			break;
    		case 2:
    			r = p;
    			g = v;
    			b = t;
    			break;
    		case 3:
    			r = p;
    			g = q;
    			b = v;
    			break;
    		case 4:
    			r = t;
    			g = p;
    			b = v;
    			break;
    		default:		// case 5:
    			r = v;
    			g = p;
    			b = q;
    			break;
    	}
    	return new float[] {r,g,b};
    }
}
