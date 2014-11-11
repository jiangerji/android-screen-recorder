 package com.intel.inde.mp.domain;
 
 public class PreviewContext
 {
   ISurfaceTexture previewTexture;
   int previewTextureId;
   IEglContext eglContext;
 
   public PreviewContext(ISurfaceTexture previewTexture, int previewTextureId, IEglContext eglContext)
   {
     this.previewTexture = previewTexture;
     this.previewTextureId = previewTextureId;
     this.eglContext = eglContext;
   }
 
   public IEglContext getEglContext() {
     return this.eglContext;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.PreviewContext
 * JD-Core Version:    0.6.1
 */