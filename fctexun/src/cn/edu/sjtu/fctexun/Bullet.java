package cn.edu.sjtu.fctexun;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;

public class Bullet {
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

	private float posX;
	private float posY;
	private float speedX;
	private float speedY;
	
	public Bullet(float posx,float posy,float speedx,float speedy){
		posX=posx;
		posY=posy;
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
	
	public void draw(Canvas c,Paint p,Paint center,float size){
		c.drawArc(new RectF(posX-size,posY-size,posX+size,posY+size), 0, 360.0f, false, p);
		c.drawArc(new RectF(posX-(size*.5f),posY-size*.5f,posX+size*.5f,posY+size*.5f), 0, 360.0f, false, center);
		//c.drawArc(new RectF(posX-(size*.25f),posY-size*.5f,posX+size*.25f,posY+size*.25f), 0, 360.0f, false, center);
	}
}
