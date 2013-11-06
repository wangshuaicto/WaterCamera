package com.example.watercamera;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class AppMain extends Activity implements SurfaceHolder.Callback,ShutterCallback,PictureCallback{
	
	//surfaceview
	public SurfaceView cameraSurface;
	//布局Linear
	public FrameLayout linear,anotherliner;
	
	public ImageButton cameraButton ,anothercameraButton;
	//camerabutton  状态变化drawable
	public StateListDrawable cameraStateListDrawable;
	
	//AssetManager
	public AssetManager assetManager;
	
	//相机
	public SurfaceView m_sSurfaceView;
	public SurfaceHolder m_holder;
	public Camera m_caCamera;
	
	//测试button  添加到surfaceView上
	public Button m_button;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		linear = new FrameLayout(AppMain.this);
		linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		cameraButton = new ImageButton(AppMain.this);
//		cameraButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(100, 100);
//		cameraButton.setLayoutParams(new LayoutParams(100, 100));
		layout.bottomMargin=100;
		layout.gravity=Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
		cameraButton.setLayoutParams(layout);
		//从asserts获取图片资源
		Drawable pressDrawable = new BitmapDrawable(getAssetBitMap(this, "btn_navi_reality_pressed.png"));
		Drawable normalDrawable = new BitmapDrawable(getAssetBitMap(this, "btn_navi_reality_normal.png"));
		cameraStateListDrawable = new StateListDrawable();
		cameraStateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
		cameraStateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);

		cameraButton.setBackgroundDrawable(cameraStateListDrawable);
		
		cameraButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//切换到anotherliner布局和前面布局liner无关了
				anothercameraButton = new ImageButton(AppMain.this);
				FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(100, 100);
				layout.bottomMargin=100;
				layout.gravity=Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
				anothercameraButton.setLayoutParams(layout);
				//从asserts获取图片资源
				Drawable pressDrawable = new BitmapDrawable(getAssetBitMap(AppMain.this, "btn_navi_reality_pressed.png"));
				Drawable normalDrawable = new BitmapDrawable(getAssetBitMap(AppMain.this, "btn_navi_reality_normal.png"));
				cameraStateListDrawable = new StateListDrawable();
				cameraStateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressDrawable);
				cameraStateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
				anothercameraButton.setBackgroundDrawable(cameraStateListDrawable);
				
				anothercameraButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(m_caCamera == null)return;
//						m_caCamera.ta
						m_caCamera.takePicture(null, null, AppMain.this);
					}
				});
				
				// TODO Auto-generated method stub
				anotherliner = new FrameLayout(AppMain.this);
				anotherliner.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				
				
				m_sSurfaceView = new SurfaceView(AppMain.this);
				m_sSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				m_holder = m_sSurfaceView.getHolder();
				m_holder.addCallback(AppMain.this);
				m_holder.setKeepScreenOn(true);
				m_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				anotherliner.addView(m_sSurfaceView,0);
				anotherliner.addView(anothercameraButton);
				setContentView(anotherliner);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN	, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
				
			}
		});
		linear.addView(cameraButton);
		super.onCreate(savedInstanceState);
		setContentView(linear);
	}
	
	

	//Asset获取图片资源文件返回bitmap方法
	
	public Bitmap getAssetBitMap(Context context,String picabsolutename)
	{
		if(context == null || picabsolutename =="")
			return null;
		AssetManager assetManager = context.getAssets();
		if(assetManager == null)return null;
		InputStream is = null;
		
		try {
			is = assetManager.open(picabsolutename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(is == null) return null;
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bitmap == null) return null;
		return bitmap;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_main, menu);
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (m_caCamera == null)
			return;
		Parameters parameters = m_caCamera.getParameters();
		WindowManager windowManager = this.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				m_caCamera.setDisplayOrientation(90);
			} else {
				parameters.setRotation(90);
			}
			break;
		case Surface.ROTATION_90:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				m_caCamera.setDisplayOrientation(0);
			} else {
				parameters.setRotation(0);
			}
			break;
		case Surface.ROTATION_270:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				m_caCamera.setDisplayOrientation(180);
			} else {
				parameters.setRotation(180);
			}
		}
		m_caCamera.setParameters(parameters);
		m_caCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		m_caCamera = Camera.open();
		try {
			m_caCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_caCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(m_caCamera ==null)
			return;
		m_caCamera.release();
		m_caCamera = null;
	}



	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
//		if(data == null)return;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length-1);
//		BitmapFactory.
//		if(bitmap == null) return;
		anothercameraButton.setBackgroundDrawable(new BitmapDrawable(bitmap));
		Uri imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		try {
			OutputStream os = this.getContentResolver().openOutputStream(imageUri);
			os.write(data);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		m_caCamera.stopPreview();
		try {
			m_caCamera.reconnect();
			m_caCamera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	@Override
	public void onShutter() {
		// TODO Auto-generated method stub
		Toast.makeText(AppMain.this, "拍照", Toast.LENGTH_SHORT).show();
	}

}
