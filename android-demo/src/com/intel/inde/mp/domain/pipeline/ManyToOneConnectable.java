 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.IInputRaw;
 import com.intel.inde.mp.domain.IOutputRaw;
 import com.intel.inde.mp.domain.IsConnectable;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 class ManyToOneConnectable
   implements IsConnectable
 {
   ManyTypes mOutTypes;
   Class mInType;
 
   ManyToOneConnectable(ManyTypes manyTypes, Class inType)
   {
     this.mOutTypes = manyTypes;
     this.mInType = inType;
   }
 
   public static ManyToOneConnectable ManyToOneConnections(ManyTypes manyTypes, Class inType)
   {
     return new ManyToOneConnectable(manyTypes, inType);
   }
 
   public boolean isConnectable(IOutputRaw output, Collection<IInputRaw> input)
   {
     if (input.size() != 1) {
       return false;
     }
     LinkedList c = new LinkedList();
     c.add(output);
     return isConnectable(c, (IInputRaw)input.iterator().next());
   }
 
   public boolean isConnectable(Collection<IOutputRaw> outputs, IInputRaw input)
   {
     if (!this.mInType.isInstance(input)) {
       return false;
     }
     for (IOutputRaw output : outputs) {
       boolean instanceDetected = false;
       for (Class mOutType : this.mOutTypes.getTypes()) {
         if (mOutType.isInstance(output)) {
           instanceDetected = true;
           break;
         }
       }
       if (!instanceDetected) {
         return false;
       }
     }
 
     return true;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.ManyToOneConnectable
 * JD-Core Version:    0.6.1
 */