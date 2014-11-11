 package com.intel.inde.mp.domain;
 
 public class Pair<T, U>
 {
   public T left;
   public U right;
 
   public Pair(T left, U right)
   {
     this.left = left;
     this.right = right;
   }
 
   public String toString()
   {
     return new StringBuilder().append("(").append(this.left == null ? "NULL" : this.left.toString()).append(", ").append(this.right == null ? "NULL" : this.right.toString()).append(")").toString();
   }
 
   public boolean equals(Object o)
   {
     if (this == o) return true;
     if (!(o instanceof Pair)) return false;
 
     Pair pair = (Pair)o;
 
     if (((this.left == null) && (pair.left != null)) || ((this.left != null) && (pair.left == null)) || ((this.left != null) && (!this.left.equals(pair.left))))
       return false;
     if (((this.right == null) && (pair.right != null)) || ((this.right != null) && (pair.right == null)) || ((this.right != null) && (!this.right.equals(pair.right)))) {
       return false;
     }
     return true;
   }
 
   public int hashCode()
   {
     int result = 0;
     if (this.left != null) result += this.left.hashCode();
     if (this.right != null) result = 31 * result + this.right.hashCode();
     return result;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.Pair
 * JD-Core Version:    0.6.1
 */