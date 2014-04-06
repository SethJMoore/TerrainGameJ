package com.sethjmoore.terraingame;


import com.sun.prism.impl.BufferUtil;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by Seth on 3/27/2014.
 */
public class Texture {
    private int[] intArray;
    private ByteBuffer byteBuffer;
    private int width;
    private int height;
    private int textureHandle;

    public int getTextureHandle() {
        return textureHandle;
    }

    Texture(int w, int h, byte[] rgbaPixels){
        textureHandle = GL11.glGenTextures();
        //intBuffer = IntBuffer.allocate(pixels.length);
        ByteBuffer bb = ByteBuffer.allocateDirect(rgbaPixels.length).order(ByteOrder.nativeOrder());
        bb.put(rgbaPixels);
        //intBuffer = bb.asIntBuffer();
        //intBuffer = IntBuffer.allocate(pixels.length);

        //intBuffer.put(rgbaPixels);
        bb.flip();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); //GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                w, h,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                bb);
    }

}
