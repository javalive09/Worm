package com.peter.worm;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import android.view.animation.Animation;

public class Worm extends SurfaceView implements SurfaceHolder.Callback,Runnable {
	
	private Paint paint;
	private Canvas canvas;   
	private SurfaceHolder sfh;
	private int ScreenW, ScreenH;  
	
	private int foodX;
	private int foodY;
	private int foodC;
	public boolean go;
	public boolean exit;
	public boolean canGo;
	public boolean gravityCnotrol;
	
	private int[] worm = new int[200*3]; 
	private int cell = 19;
	public int wormNum;
	
	public int direction;
	public final int DIR_UP = 0;
	public final int DIR_DOWN = 1;
	public final int DIR_LEFT = 2;
	public final int DIR_RIGHT = 3;
	private Bitmap scaled;
	private int mTouchSlop;
	private float startX = 0;
	private float startY = 0;
	
	Random random = new Random();
    private final int[] COLOR = {0xFFFF0000, 0xFF00FF00, 0xFFFFFF00, 0xFFFF00FF, 
    		0xFF00FFFF, 0xFFFF000F, 0xFFFFF000};
    
	public Worm(Context context,AttributeSet attrs) {
		super(context,attrs);
		init();
	} 
	public Worm(Context context){
		super(context); 
		init();
	}

	private void init() {
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		cell = getContext().getResources().getDimensionPixelOffset(R.dimen.cell);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 3;
	}

	public String getWormScore() {
		return wormNum - 6 + "";
	}

	public void startAnimation(Animation animation) {   
		super.startAnimation(animation); 
	}    

	public void surfaceCreated(SurfaceHolder holder) {
		ScreenW = this.getWidth();
		ScreenH = this.getHeight();

		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
		float scale = (float)background.getHeight()/(float)getHeight();
		int newWidth = Math.round(background.getWidth()/scale);
		int newHeight = Math.round(background.getHeight()/scale);
		scaled = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

		initinate();
		new Thread(this).start(); 
	}

	public void initinate(){
		go = true;
		exit = false;
		wormNum = 6;
		foodX = cell*11;
		foodY = cell*11;
		foodC = 0xFFFF0000;
		direction = DIR_RIGHT;
		
		for(int i=0;i<wormNum;i++){
			worm[i*3 + 0] = cell*9 - cell*i; //X
			worm[i*3 + 1] = cell*5;//Y
			worm[i*3 + 2] = COLOR[i%7];
		}
	}
	
	private void clearScrean(){
//		RadialGradient rg = new RadialGradient(ScreenW/2, ScreenH/2, ScreenH, 0xFFAAAAAA,Color.BLACK, TileMode.CLAMP);
//		paint.setShader(rg);
//		paint.setShader(null);
//		paint.setColor(Color.GRAY);
//		canvas.drawRect(0, 0, ScreenW, ScreenH, paint);
		canvas.drawBitmap(scaled, 0, 0, null);
	}

	private void paint() { 
		canvas = sfh.lockCanvas();   
		clearScrean();
		
		for(int i=0;i<wormNum;i++){
			drawWorm(canvas,worm[i*3+0],worm[i*3+1],worm[i*3+2]);
		}
		
		drawWorm(canvas,foodX,foodY,foodC);
		
		if(isGameOver()){
			drawGameOver(canvas);
		}		
		sfh.unlockCanvasAndPost(canvas);	
	}
	
	private void drawGameOver(Canvas canvas){
		paint.setShader(null);
		paint.setColor(Color.BLACK);
		int x1 = ScreenW/3;
		int y1 = (ScreenH-ScreenW/3)/2;
		paint.setColor(Color.RED);
		float size = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics());
		paint.setTextSize(size);
		canvas.drawText("Game over !", x1, y1+x1/2, paint);
	}
	
	private void drawWorm(Canvas canvas,int x,int y,int color){
		paint.setShader(null);
		paint.setColor(Color.BLACK);
		canvas.drawRect(x, y, x+cell, y+cell, paint);
		
		LinearGradient lg = new LinearGradient(x,y,x+cell,y+cell,Color.WHITE,color,TileMode.CLAMP);
		paint.setShader(lg);
		canvas.drawRect(x+1, y+1, x+cell-1, y+cell-1, paint);
	}
	
	private void makeFood(){
        foodX = Math.abs(random.nextInt() % (ScreenW - cell + 1)) / cell * cell;
        foodY = Math.abs(random.nextInt() % (ScreenH - cell + 1)) / cell * cell;
        foodC = COLOR[Math.abs(random.nextInt() % COLOR.length)];
	}
	
	private boolean isGameOver(){
		if(worm[0*3+0]<0||worm[0*3+0]>(ScreenW - cell)||
				worm[0*3+1]<0||worm[0*3+1]>(ScreenH - cell)){
			return true;
		}
		
		for(int i = 4; i < wormNum; i++){
			if(worm[0*3+0]== worm[i*3+0] && worm[0*3+1] == worm[i*3+1]){
				return true;
			}
		}
		return false;
	}
	
	private void update(){
        int stepX = 0;
        int stepY = 0;
        switch (direction) {
            case DIR_UP:
                stepY = -cell;
                break;
            case DIR_DOWN:
                stepY = cell;
                break;
            case DIR_LEFT:
                stepX = -cell;
                break;
            case DIR_RIGHT:
                stepX = cell;
                break;
        }

        int newX = worm[0 * 3 + 0] + stepX;
        int newY = worm[0 * 3 + 1] + stepY;

        if (newX == foodX && newY == foodY) {
            wormNum++;
            for (int i = wormNum - 1; i > 0; i--) {
                worm[i * 3 + 0] = worm[(i - 1) * 3 + 0];
                worm[i * 3 + 1] = worm[(i - 1) * 3 + 1];
                worm[i * 3 + 2] = worm[(i - 1) * 3 + 2];
            }
            worm[0 * 3 + 0] = foodX;
            worm[0 * 3 + 1] = foodY;
            worm[0 * 3 + 2] = foodC;
            makeFood();
        }
        if (isGameOver()){
            go = false;
            return;
        }
        
        for (int i = wormNum - 1; i > 0; i--) {
        	worm[i * 3 + 0] = worm[(i - 1) * 3 + 0];
        	worm[i * 3 + 1] = worm[(i - 1) * 3 + 1];
        }
        
        stepX = 0;
        stepY = 0;
        switch (direction) {
            case DIR_UP:
                stepY = -cell;
                break;
            case DIR_DOWN:
                stepY = cell;
                break;
            case DIR_LEFT:
                stepX = -cell;
                break;
            case DIR_RIGHT:
                stepX = cell;
                break;
        }
        worm[0 * 3 + 0] += stepX;
        worm[0 * 3 + 1] += stepY;
	}
	
	private void logic(){
		update();
		paint();
	}
	
	public void run() {
		while (!exit) {
			if(go){
				logic();
				try {
					Thread.sleep(300);
					canGo = true;
				} catch (InterruptedException e) { 
					e.printStackTrace(); 
				} 
			}
		} 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(gravityCnotrol) return false;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float currentX = event.getX();
				float currentY = event.getY();
				float deltaX = currentX - startX;
				float deltaY = currentY - startY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);

				if(absDeltaX > mTouchSlop || absDeltaY > mTouchSlop) {
					if(absDeltaX > absDeltaY) {// x
						if(deltaX < 0) {//left
							direction = DIR_LEFT;
						}else {
							direction = DIR_RIGHT;
						}
					}else {//Y
						if(deltaY < 0) { //top
							direction = DIR_UP;
						}else {
							direction = DIR_DOWN;
						}
					}
				}
				break;
		}

		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
}    
