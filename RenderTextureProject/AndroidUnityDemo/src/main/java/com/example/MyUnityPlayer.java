package com.example;

import android.opengl.GLES10;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

//import com.unity3d.player.UnityPlayer;

public class MyUnityPlayer extends UnityPlayer {

    private UnityPlayerActivityXML activity;


    public static boolean isUseMap = false;

    private UnitySurfaceView unitySurfaceView = null;


    private LayoutParams layoutParams = new LayoutParams(200, 200);


    public MyUnityPlayer(UnityPlayerActivityXML contextWrapper) {
        super(contextWrapper);
        this.activity = contextWrapper;
    }



    // Dont let unity use its own camera
    protected int[] initCamera(int var1, int var2, int var3, int var4) {
//        return null;
        return super.initCamera(var1,var2,var3,var4);
    }

    public void init(int var1, boolean var2) {
        Log.d("zxy", "Test");
    }

    int majorVersionUnity = -1;

    protected void executeGLThreadJobs() {
        // Here it is certain that the Unity renderer is active.
        // Attempt to find the GL context to share with the holokilo
        // opengl context. The two contexts run side by side, the unity
        // context runs in background and passes its results to holokilo.
        EGL10 egl = ((EGL10) EGLContext.getEGL());
        final EGLContext con = egl.eglGetCurrentContext();

        if (majorVersionUnity == -1 && !con.equals(EGL10.EGL_NO_CONTEXT)) {
            String versionString = GLES10.glGetString(GLES10.GL_VERSION);

            for (int i = 0; i < versionString.length(); i++) {
                int cha = versionString.charAt(i);
                if (Character.isDigit(cha)) {
                    majorVersionUnity = Character.getNumericValue(cha);
                    break;
                }
            }
            Log.d("zxy", "GL Version: " + majorVersionUnity);
        }

        // Rescale the unity surface to 1x1 pixel to hide,
        // but keep it regular sized to show the unity splashscreen,
        // in accordance with their license agreement.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(isUseMap) {

                } else {
                    if (con.equals(EGL10.EGL_NO_CONTEXT)) {
                        // no current context.
                    } else if (unitySurfaceView == null) {
//                    glSurfaceView = new AMapGLSurfaceView(con, activity);
                        unitySurfaceView = new UnitySurfaceView(con, activity);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // Do nothing
                        }

                        activity.getLayout().addView(unitySurfaceView);
                        MyUnityPlayer.this.setLayoutParams(layoutParams);
                    } else if (unitySurfaceView.getEglContext().hashCode() != con.hashCode()) {
                        removeView(unitySurfaceView);
                        unitySurfaceView.onPause();
                        activity.getLayout().removeView(unitySurfaceView);

//                        glSurfaceView = new AMapGLSurfaceView(con, activity);
                        unitySurfaceView = new UnitySurfaceView(con, activity);
                        activity.getLayout().addView(unitySurfaceView, 0);
                    }
                }
            }
        });
    }

}