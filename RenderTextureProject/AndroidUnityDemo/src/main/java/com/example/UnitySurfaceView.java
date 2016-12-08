package com.example;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class UnitySurfaceView extends GLSurfaceView {
    private EGLContext eglContext = null;

    private UnityRenderer myGLRenderer;

    public EGLContext getEglContext() {
        return eglContext;
    }

    public UnitySurfaceView(EGLContext sharedContext, Context context) {
        this(context,sharedContext,0);
    }

    public UnitySurfaceView(Context context, EGLContext sharedContext, final int version) {
        super(context);
        this.eglContext = sharedContext;

        myGLRenderer = new UnityRenderer(context);

        setEGLContextFactory(new EGLContextFactory() {
            @Override
            public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
                int[] attrib_list = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
                EGLContext context = egl.eglCreateContext(display, eglConfig, eglContext,
                        attrib_list);

                return context;
            }

            @Override
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                if (!egl.eglDestroyContext(display, context)) {
                    Log.e("zxy", "display:" + display + " context: " + context);
                }
            }
        });

        setRenderer(myGLRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    @Override
    public void onPause() {
        super.onPause();
        if(myGLRenderer != null) {
            myGLRenderer.resume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myGLRenderer != null) {
            myGLRenderer.pause();
        }
    }
}