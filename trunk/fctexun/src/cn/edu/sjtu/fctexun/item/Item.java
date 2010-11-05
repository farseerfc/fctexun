package cn.edu.sjtu.fctexun.item;

import android.graphics.Canvas;

public abstract class Item {
	public float posX;
	public float posY;
	public float speedX;
	public float speedY;
	

	
	public void setPos(float posx,float posy){
		posX=posx;
		posY=posy;
	}
	
	public void setSpeed(float speedx,float speedy){
		speedX=speedx;
		speedY=speedy;
	}
	
	public void move(int width,int height, long time){
		posX+=speedX*time;
		posY+=speedY*time;
		
		if(posX<0 || posX>width){
			posX=posX+width;
			while(posX>width)posX-=width;
			
		}
		
		if(posY<0 || posY>height){
			posY+=posY+height;
			while(posY>height)posY-=height;
		}
	}
	
	public abstract void draw(Canvas c);
}
