 package com.intel.inde.mp.domain.graphics;
 
 import com.intel.inde.mp.domain.ISurfaceTexture;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.pipeline.TriangleVerticesCalculator;
 import java.nio.ByteBuffer;
 import java.nio.ByteOrder;
 import java.nio.FloatBuffer;
 
 public class TextureRenderer
 {
   private static final int FLOAT_SIZE_BYTES = 4;
   private FloatBuffer triangleVertices;
   private static final String VERTEX_SHADER_SAMPLER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition* vec4(1.0, -1.0, 1.0, 1.0);\n  vTextureCoord = ((uSTMatrix * aTextureCoord) * vec4(1.0, 1.0, 1.0, 1.0) ).xy;\n}\n";
   private static final String FRAGMENT_SHADER_SAMPLER = "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}";
   private float[] mvpMatrix = new float[16];
   private float[] stMatrix = new float[16];
   private IEglUtil eglUtil;
   private Program programWithOES;
   private Program programWithSampler;
   private Resolution resolution;
 
   public TextureRenderer(IEglUtil eglUtil)
   {
     this.eglUtil = eglUtil;
 
     float[] data = TriangleVerticesCalculator.getDefaultTriangleVerticesData();
     this.triangleVertices = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
     putTriangleVertices(data);
     this.eglUtil.setIdentityMatrix(this.stMatrix, 0);
   }
 
   public void setInputSize(int widthIn, int heightIn) {
     this.resolution = new Resolution(widthIn, heightIn);
   }
 
   private void putTriangleVertices(float[] data) {
     this.triangleVertices.position(0);
     this.triangleVertices.put(data);
     this.triangleVertices.position(0);
   }
 
   public void drawFrame2D(ISurfaceTexture surfaceTexture, int textureId, float angle, boolean fitToSurface) {
     this.stMatrix = surfaceTexture.getTransformMatrix();
     drawFrame2D(this.stMatrix, textureId, angle, fitToSurface);
   }
 
   public void drawFrame2D(float[] stMatrix, int textureId, float angle, boolean fitToSurface) {
     this.eglUtil.drawFrame(this.programWithSampler, this.triangleVertices, this.mvpMatrix, stMatrix, angle, TextureType.GL_TEXTURE_2D, textureId, this.resolution, fitToSurface);
   }
 
   public void drawFrameOES(ISurfaceTexture surfaceTexture, int textureId, float angle, boolean fitToSurface) {
     this.stMatrix = surfaceTexture.getTransformMatrix();
     drawFrameOES(this.stMatrix, textureId, angle, fitToSurface);
   }
 
   public void drawFrameOES(float[] stMatrix, int textureId, float angle, boolean fitToSurface) {
     this.eglUtil.drawFrame(this.programWithOES, this.triangleVertices, this.mvpMatrix, stMatrix, angle, TextureType.GL_TEXTURE_EXTERNAL_OES, textureId, this.resolution, fitToSurface);
   }
 
   public void surfaceCreated()
   {
     this.programWithOES = this.eglUtil.createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
     this.programWithSampler = this.eglUtil.createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition* vec4(1.0, -1.0, 1.0, 1.0);\n  vTextureCoord = ((uSTMatrix * aTextureCoord) * vec4(1.0, 1.0, 1.0, 1.0) ).xy;\n}\n", "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}");
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.graphics.TextureRenderer
 * JD-Core Version:    0.6.1
 */