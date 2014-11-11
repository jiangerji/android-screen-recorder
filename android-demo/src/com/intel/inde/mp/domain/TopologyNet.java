 package com.intel.inde.mp.domain;
 
 import java.util.Collection;
 
 class TopologyNet
   implements ITopologyTree
 {
   Object value;
   private Collection<ITopologyTree> mRight;
 
   TopologyNet(Object current)
   {
     this.value = current;
   }
 
   public Object current()
   {
     return this.value;
   }
 
   public Collection<ITopologyTree> next()
   {
     return this.mRight;
   }
 
   public void setNext(Collection<ITopologyTree> mRight) {
     this.mRight = mRight;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.TopologyNet
 * JD-Core Version:    0.6.1
 */