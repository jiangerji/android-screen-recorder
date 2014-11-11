 package com.intel.inde.mp.android.graphics;
 
 import android.opengl.GLES20;
 import android.opengl.Matrix;
 import java.nio.ByteBuffer;
 
 public class FullFrameTexture
 {
   private static final String VERTEXT_SHADER = "uniform mat4 uOrientationM;\nuniform mat4 uTransformM;\nattribute vec2 aPosition;\nvarying vec2 vTextureCoord;\nvoid main() {\ngl_Position = vec4(aPosition, 0.0, 1.0);\nvTextureCoord = (uTransformM * ((uOrientationM * gl_Position + 1.0) * 0.5)).xy;}";
   private static final String FRAGMENT_SHADER = "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 vTextureCoord;\nvoid main() {\ngl_FragColor = texture2D(sTexture, vTextureCoord);\n}";
   private final byte[] FULL_QUAD_COORDINATES = { -1, 1, -1, -1, 1, 1, 1, -1 };
   private ShaderProgram shader;
   private ByteBuffer fullQuadVertices;
   private final float[] orientationMatrix = new float[16];
   private final float[] transformMatrix = new float[16];
 
   public FullFrameTexture() {
     if (this.shader != null) {
       this.shader = null;
     }
 
     this.shader = new ShaderProgram(EglUtil.getInstance());
 
     this.shader.create("uniform mat4 uOrientationM;\nuniform mat4 uTransformM;\nattribute vec2 aPosition;\nvarying vec2 vTextureCoord;\nvoid main() {\ngl_Position = vec4(aPosition, 0.0, 1.0);\nvTextureCoord = (uTransformM * ((uOrientationM * gl_Position + 1.0) * 0.5)).xy;}", "precision mediump float;\nuniform sampler2D sTexture;\nvarying vec2 vTextureCoord;\nvoid main() {\ngl_FragColor = texture2D(sTexture, vTextureCoord);\n}");
 
     this.fullQuadVertices = ByteBuffer.allocateDirect(8);
 
     this.fullQuadVertices.put(this.FULL_QUAD_COORDINATES).position(0);
 
     Matrix.setRotateM(this.orientationMatrix, 0, 0.0F, 0.0F, 0.0F, 1.0F);
     Matrix.setIdentityM(this.transformMatrix, 0);
   }
 
   public void release() {
     this.shader = null;
     this.fullQuadVertices = null;
   }
 
   public void draw(int textureId) {
     this.shader.use();
 
     GLES20.glActiveTexture(33984);
     GLES20.glBindTexture(3553, textureId);
 
     int uOrientationM = this.shader.getAttributeLocation("uOrientationM");
     int uTransformM = this.shader.getAttributeLocation("uTransformM");
 
     GLES20.glUniformMatrix4fv(uOrientationM, 1, false, this.orientationMatrix, 0);
     GLES20.glUniformMatrix4fv(uTransformM, 1, false, this.transformMatrix, 0);
 
     renderQuad(this.shader.getAttributeLocation("aPosition"));
 
     this.shader.unUse();
   }
 
   private void renderQuad(int aPosition) {
     GLES20.glVertexAttribPointer(aPosition, 2, 5120, false, 0, this.fullQuadVertices);
     GLES20.glEnableVertexAttribArray(aPosition);
     GLES20.glDrawArrays(5, 0, 4);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.android.graphics.FullFrameTexture
 * JD-Core Version:    0.6.1
 */