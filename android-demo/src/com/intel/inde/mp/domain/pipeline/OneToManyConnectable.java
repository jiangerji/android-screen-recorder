 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.IInputRaw;
 import com.intel.inde.mp.domain.IOutputRaw;
 import com.intel.inde.mp.domain.IsConnectable;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 class OneToManyConnectable
   implements IsConnectable
 {
   Class outType;
   ManyTypes inTypes;
 
   public OneToManyConnectable(Class outType, ManyTypes inTypes)
   {
     this.outType = outType;
     this.inTypes = inTypes;
   }
 
   public static OneToManyConnectable OneToManyConnection(Class outType, ManyTypes inTypes) {
     return new OneToManyConnectable(outType, inTypes);
   }
 
   public static OneToManyConnectable OneToManyConnection(Class outType, Class inType) {
     return new OneToManyConnectable(outType, new ManyTypes(new Class[] { inType }));
   }
 
   public boolean isConnectable(IOutputRaw output, Collection<IInputRaw> inputs)
   {
     if (!this.outType.isInstance(output)) {
       return false;
     }
     for (IInputRaw input : inputs) {
       boolean instanceDetected = false;
       for (Class mInType : this.inTypes.getTypes()) {
         if (mInType.isInstance(input)) {
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
 
   public boolean isConnectable(Collection<IOutputRaw> output, IInputRaw input)
   {
     if (output.size() != 1) {
       return false;
     }
     LinkedList c = new LinkedList();
     c.add(input);
     return isConnectable((IOutputRaw)output.iterator().next(), c);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.OneToManyConnectable
 * JD-Core Version:    0.6.1
 */