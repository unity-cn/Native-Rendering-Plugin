package com.example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class UnityRenderer implements GLSurfaceView.Renderer {
    private static UnityRenderer instance = null;
    protected int width, height;

    private boolean isStopped = false;

    private Context context;
    public UnityRenderer(Context context) {
        instance = this;
        this.context = context;
    }

    // Function accessible to unity
    public static UnityRenderer getInstance() {
        return instance;
    }

    // Function accessible to unity
    public boolean isStopped() {
        return isStopped;
    }

    private int unityTextureID = -1;

    // Function accessible to unity
    public void setUnityTextureID(int tex) {
        unityTextureID = tex;
        Log.d("zxy", "Received unity texture: " + tex);
    }



    Square square = null;

    float[] mvp = {
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1,
    };

    int textureId = -1;

    //绘制Unity部分
    protected void drawScene() {
        setViewport();
        if(textureId == -1) {
            textureId = loadTexture(context, R.drawable.ic_launcher);
        }

        if(square == null) {
            square = new Square();
        }
//        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(square != null) {
//            square.draw(textureId);
            square.draw(unityTextureID);
            return;
        }
    }

    public void resume() {
        isStopped = false;
    }

    public void pause() {
        isStopped = true;
    }

    public void destroy() {
        isStopped = true;
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    public int getScreenWidth() {
        return width;
    }

    public int getScreenHeight() {
        return height;
    }

    public void setViewport() {
        GLES20.glViewport(0, 0, width, height);
    }

////////////////////



    public void onDrawFrame(GL10 gl) {

        drawScene();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {

        this.width = width;
        this.height = height;

        setViewport();

    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    public void onSurfaceDestory() {
    }

}