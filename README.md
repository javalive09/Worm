Worm
======

一个可以使用重力感应或触摸手势控制的贪食蛇游戏.

代码片段：
---------------------

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

![file list][1] 

  [1]: http://7xoxmg.com1.z0.glb.clouddn.com/worm.png