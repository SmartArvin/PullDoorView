package com.example.pulldoorview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * ���ߣ�dls on 2016/9/26 11:05
 * ���䣺836680084@qq.com
 * �汾��1.0.0
 * �������ֲ�View  Ӧ�ó���  �ֻ�����  ����������� 
 */
public class PullDoorView extends RelativeLayout{

    private Context mContext;

    private Scroller mScroller;

    private int mScreenHeight = 0;

    private int mLastDownY = 0;

    private int mCurryY;

    private int mDelY;

    private boolean mCloseFlag = false;

    private TextView mTvHint;

    private TextView mTvTime;

    private TextView mTvTimeDetail;

    private Handler handler;

    public PullDoorView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public PullDoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public PullDoorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullDoorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        initView();
    }

    private void initView(){

        // Interpolator  ���ò�ֵ��  �ɵ���
        Interpolator polator = new BounceInterpolator();
        mScroller = new Scroller(mContext, polator);

        // ��ȡ��Ļ�ֱ���
        WindowManager wm = (WindowManager) (mContext
                .getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenHeight = dm.heightPixels;

        // ������һ��Ҫ���ó�͸������,��Ȼ��Ӱ���㿴���ײ㲼��
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));

        mTvHint = new TextView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.y20);  //�������õ����Զ������Ļ����  ���Ըĳ������������͵���ֵ
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,-1);//-1 ��ʾ����ڸ��ؼ���λ��
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,-1);
        mTvHint.setText("�ϻ�����");
        mTvHint.setTextColor(Color.WHITE);
        mTvHint.setTextSize(16);
        mTvHint.setLayoutParams(params);
        Animation ani = new AlphaAnimation(0f, 1f);
        ani.setDuration(1500);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(Animation.INFINITE);
        mTvHint.startAnimation(ani);
        addView(mTvHint);

        mTvTime = new TextView(mContext);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) mContext.getResources().getDimension(R.dimen.y40);
        params.leftMargin = (int) mContext.getResources().getDimension(R.dimen.x40);
        mTvTime.setTextColor(Color.WHITE);
        mTvTime.setTextSize((int) mContext.getResources().getDimension(R.dimen.x24));
        mTvTime.setLayoutParams(params);
        addView(mTvTime);

        mTvTimeDetail = new TextView(mContext);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) mContext.getResources().getDimension(R.dimen.y130);
        params.leftMargin = (int) mContext.getResources().getDimension(R.dimen.x40);
        mTvTimeDetail.setTextColor(Color.WHITE);
        mTvTimeDetail.setTextSize((int) mContext.getResources().getDimension(R.dimen.x14));
        mTvTimeDetail.setLayoutParams(params);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy��MM��dd��");
        String str=sdf.format(new Date());
        mTvTimeDetail.setText(str);
        addView(mTvTimeDetail);

        handler = new Handler() {//����ˢ����ʾʱ��
            public void handleMessage(Message msg) {
                mTvTime.setText((String)msg.obj);
            }
        };

        startShowTime();
    }

    //������ʾ����
    public void setText(String text){
        mTvHint.setText(text+"");
    }

    // ��㶯��
    public void startBounceAnim(int startY, int dy, int duration) {
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();//����������invalidate()���ܱ�֤computeScroll()�ᱻ���ã�����һ����ˢ�½��棬����������Ч��
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = (int) event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                mCurryY = (int) event.getY();
                mDelY = mCurryY - mLastDownY;
                if (mDelY < 0) {
                    scrollTo(0, -mDelY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurryY = (int) event.getY();
                mDelY = mCurryY - mLastDownY;
                if (mDelY < 0) {
                    if (Math.abs(mDelY) > mScreenHeight / 2) {
                        // ���ϻ������������Ļ�ߵ�ʱ�� ����������ʧ����
                        startBounceAnim(this.getScrollY(), mScreenHeight, 450);
                        mCloseFlag = true;
                    } else {
                        // ���ϻ���δ���������Ļ�ߵ�ʱ�� �������µ�������
                        startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
                        mCloseFlag = false;
                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//�жϹ����Ƿ����  true ��ʾ������δ���
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else {
            if (mCloseFlag) {
                this.setVisibility(View.GONE);
            }
        }
    }

    public void startShowTime(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    while(true){
                        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
                        String str=sdf.format(new Date());
                        handler.sendMessage(handler.obtainMessage(100,str));
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
