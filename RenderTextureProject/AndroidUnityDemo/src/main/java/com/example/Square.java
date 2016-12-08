package com.example;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {
    float []vertexs = {
            -1f, 1f,0.0f,
            -1f,-1f,0.0f,
            1f,-1f,0.0f,

            1f,-1f,0.0f,
            1f,1f,0.0f,
            -1f,1f,0.0f
    };

    float texture[] = {

            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,

            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f,0.0f
    };

    float[] mVMatrix = new float[16];
    float num = 0;
    int mProgram;
    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;

    String vertexShaderCode =
            "attribute vec3 vPosition;" +
                    "attribute vec2 aTexture;"+
                    "varying  vec2 vTexture;"+
                    "uniform mat4 mVMatrix; "+//总变换矩阵
                    "void main() {" +
                    "  gl_Position = mVMatrix*vec4(vPosition,1.0);" +
                    "	vTexture = aTexture;"+
                    "}";

    String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 vTexture;" +
                    "uniform sampler2D sTexture;"+
                    "void main() {" +
                    " gl_FragColor = texture2D(sTexture, vTexture);" +
                    "}";




    public Square(){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texture.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        textureBuffer = tbb.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glShaderSource(fragmentShader,fragmentShaderCode);

        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);


    }
    public void draw(int textureID){

        GLES20.glUseProgram(mProgram);
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        int mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexture");
        int muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "mVMatrix");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,0,vertexBuffer);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false,0,textureBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);//绑定纹理id

        Matrix.setIdentityM(mVMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1,false,mVMatrix,0);

        // 画三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

    }

}