 package com.intel.inde.mp.android.graphics;
 
 import com.intel.inde.mp.IVideoEffect;
 import com.intel.inde.mp.domain.Pair;
 import com.intel.inde.mp.domain.Resolution;
 import com.intel.inde.mp.domain.graphics.IEglUtil;
 import com.intel.inde.mp.domain.graphics.Program;
 import com.intel.inde.mp.domain.graphics.TextureType;
 import com.intel.inde.mp.domain.pipeline.TriangleVerticesCalculator;
 import java.nio.ByteBuffer;
 import java.nio.ByteOrder;
 import java.nio.FloatBuffer;
 
 public class VideoEffect
   implements IVideoEffect
 {
   protected static final int FLOAT_SIZE_BYTES = 4;
   protected static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
   protected static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
   protected static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
   protected Resolution inputResolution = new Resolution(0, 0);
   private Pair<Long, Long> segment = new Pair(Long.valueOf(0L), Long.valueOf(0L));
   protected IEglUtil eglUtil;
   protected Program eglProgram = new Program();
   protected boolean wasStarted;
   protected float[] mvpMatrix = new float[16];
   private FloatBuffer triangleVertices;
   private int angle;
   protected ShaderProgram shaderProgram;
   private boolean fitToContext = true;
   private String vertexShader = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
   private String fragmentShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
 
   public VideoEffect(int angle, IEglUtil eglUtil) {
     this.angle = angle;
     this.eglUtil = eglUtil;
   }
   public void setVertexShader(String verexShader) {
     this.vertexShader = verexShader;
   }
 
   public void setFragmentShader(String fragmentShader) {
     this.fragmentShader = fragmentShader;
   }
 
   public Pair<Long, Long> getSegment()
   {
     return this.segment;
   }
 
   public void setSegment(Pair<Long, Long> segment)
   {
     this.segment = segment;
   }
 
   protected void addEffectSpecific()
   {
   }
 
   public void start()
   {
     this.triangleVertices = ByteBuffer.allocateDirect(TriangleVerticesCalculator.getDefaultTriangleVerticesData().length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
 
     createProgram(this.vertexShader, this.fragmentShader);
 
     this.eglProgram.programHandle = this.shaderProgram.getProgramHandle();
     this.eglProgram.positionHandle = this.shaderProgram.getAttributeLocation("aPosition");
     this.eglProgram.textureHandle = this.shaderProgram.getAttributeLocation("aTextureCoord");
     this.eglProgram.mvpMatrixHandle = this.shaderProgram.getAttributeLocation("uMVPMatrix");
     this.eglProgram.stMatrixHandle = this.shaderProgram.getAttributeLocation("uSTMatrix");
 
     this.wasStarted = true;
   }
 
   public void applyEffect(int inputTextureId, long timeProgress, float[] transformMatrix)
   {
     if (!this.wasStarted) {
       start();
     }
     this.triangleVertices.clear();
     this.triangleVertices.put(TriangleVerticesCalculator.getDefaultTriangleVerticesData()).position(0);
 
     this.eglUtil.drawFrameStart(this.eglProgram, this.triangleVertices, this.mvpMatrix, transformMatrix, this.angle, TextureType.GL_TEXTURE_EXTERNAL_OES, inputTextureId, this.inputResolution, this.fitToContext);
     addEffectSpecific();
     this.eglUtil.drawFrameFinish();
   }
 
   public void setInputResolution(Resolution resolution)
   {
     this.inputResolution = resolution;
   }
 
   public boolean fitToCurrentSurface(boolean should)
   {
     boolean toRet = this.fitToContext;
     this.fitToContext = should;
     return toRet;
   }
 
   protected int createProgram(String vertexSource, String fragmentSource) {
     this.shaderProgram = new ShaderProgram(this.eglUtil);
     this.shaderProgram.create(vertexSource, fragmentSource);
     return this.shaderProgram.getProgramHandle();
   }
 
   protected void checkGlError(String component) {
     this.eglUtil.checkEglError(component);
   }
 
   protected void checkGlError() {
     this.eglUtil.checkEglError("VideoEffect");
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.graphics.VideoEffect
 * JD-Core Version:    0.6.1
 */