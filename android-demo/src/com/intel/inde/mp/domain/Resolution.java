 package com.intel.inde.mp.domain;
 
 import java.io.Serializable;
 
 public class Resolution
   implements Serializable
 {
   private final int width;
   private final int height;
 
   public Resolution(int width, int height)
   {
     this.width = width;
     this.height = height;
   }
 
   public int width() {
     return this.width;
   }
 
   public int height() {
     return this.height;
   }
 
   public boolean equals(Object o)
   {
     if (this == o) return true;
     if ((o == null) || (getClass() != o.getClass())) return false;
 
     Resolution that = (Resolution)o;
 
     if (this.height != that.height) return false;
     if (this.width != that.width) return false;
 
     return true;
   }
 
   public int hashCode()
   {
     int result = this.width;
     result = 31 * result + this.height;
     return result;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Resolution
 * JD-Core Version:    0.6.1
 */