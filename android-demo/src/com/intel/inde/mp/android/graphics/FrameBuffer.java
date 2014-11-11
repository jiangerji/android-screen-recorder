 package com.intel.inde.mp.android.graphics;
 
 import android.opengl.GLES20;
 import com.intel.inde.mp.domain.IFrameBuffer;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 
 public class FrameBuffer
   implements IFrameBuffer
 {
   private int framebuffer;
   private int offScreenTexture;
   private int depthBuffer;
   private IEglUtil utils;
   private Resolution resolution;
 
   public FrameBuffer(IEglUtil utils)
   {
     this.utils = utils;
     this.framebuffer = -1;
     this.offScreenTexture = -1;
     this.depthBuffer = -1;
   }
 
   public void setResolution(Resolution res)
   {
     this.resolution = res;
     if (this.framebuffer != -1) {
       release();
     }
     int[] glValues = new int[1];
 
     GLES20.glGenTextures(1, glValues, 0);
     this.utils.checkEglError("glGenTextures");
     this.offScreenTexture = glValues[0];
 
     GLES20.glBindTexture(3553, this.offScreenTexture);
     this.utils.checkEglError("glBindTexture");
 
     GLES20.glTexImage2D(3553, 0, 6408, res.width(), res.height(), 0, 6408, 5121, null);
     this.utils.checkEglError("glTexImage2D");
 
     GLES20.glTexParameterf(3553, 10241, 9728.0F);
     GLES20.glTexParameterf(3553, 10240, 9729.0F);
     GLES20.glTexParameteri(3553, 10242, 33071);
     GLES20.glTexParameteri(3553, 10243, 33071);
 
     GLES20.glGenFramebuffers(1, glValues, 0);
     this.utils.checkEglError("glGenFramebuffers");
 
     this.framebuffer = glValues[0];
 
     GLES20.glBindFramebuffer(36160, this.framebuffer);
     this.utils.checkEglError("glBindFramebuffer");
 
     GLES20.glGenRenderbuffers(1, glValues, 0);
     this.utils.checkEglError("glGenRenderbuffers");
     this.depthBuffer = glValues[0];
 
     GLES20.glBindRenderbuffer(36161, this.depthBuffer);
     this.utils.checkEglError("glBindRenderbuffer");
 
     GLES20.glRenderbufferStorage(36161, 33189, res.width(), res.height());
     this.utils.checkEglError("glRenderbufferStorage");
 
     GLES20.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthBuffer);
     this.utils.checkEglError("glFramebufferRenderbuffer");
 
     GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.offScreenTexture, 0);
     this.utils.checkEglError("glFramebufferTexture2D");
 
     int status = GLES20.glCheckFramebufferStatus(36160);
     this.utils.checkEglError("glCheckFramebufferStatus");
 
     if (status != 36053) {
       throw new RuntimeException("Incomplete framebuffer. Status: " + status);
     }
 
     GLES20.glBindFramebuffer(36160, 0);
     this.utils.checkEglError("glBindFramebuffer(0)");
   }
 
   public int getTextureId()
   {
     return this.offScreenTexture;
   }
 
   public void release()
   {
     int[] glValues = new int[1];
 
     if (this.offScreenTexture > 0) {
       glValues[0] = this.offScreenTexture;
       GLES20.glDeleteTextures(1, glValues, 0);
       this.offScreenTexture = -1;
     }
 
     if (this.framebuffer > 0) {
       glValues[0] = this.framebuffer;
       GLES20.glDeleteFramebuffers(1, glValues, 0);
       this.framebuffer = -1;
     }
 
     if (this.depthBuffer > 0) {
       glValues[0] = this.depthBuffer;
       GLES20.glDeleteRenderbuffers(1, glValues, 0);
       this.depthBuffer = -1;
     }
   }
 
   public void bind()
   {
     GLES20.glViewport(0, 0, this.resolution.width(), this.resolution.height());
     GLES20.glBindFramebuffer(36160, this.framebuffer);
   }
 
   public void unbind()
   {
     GLES20.glBindFramebuffer(36160, 0);
   }
 
   protected void finalize() throws Throwable
   {
     super.finalize();
     release();
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.graphics.FrameBuffer
 * JD-Core Version:    0.6.1
 */