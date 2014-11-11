 package com.intel.inde.mp.domain.pipeline;
 
 import com.intel.inde.mp.domain.IInputRaw;
 import com.intel.inde.mp.domain.IOutputRaw;
 import com.intel.inde.mp.domain.IsConnectable;
 import java.util.Collection;
 import java.util.Iterator;
 
 class OneToOneConnectable<TLeft, TRight>
   implements IsConnectable
 {
   private Class<TLeft> leftClass;
   private Class<TRight> rightClass;
 
   public static <T1, T2> OneToOneConnectable<T1, T2> OneToOneConnection(Class<T1> leftType, Class<T2> rightType)
   {
     return new OneToOneConnectable(leftType, rightType);
   }
 
   public OneToOneConnectable(Class<TLeft> leftClass, Class<TRight> rightClass) {
     this.leftClass = leftClass;
     this.rightClass = rightClass;
   }
 
   public boolean isConnectable(IOutputRaw output, Collection<IInputRaw> input)
   {
     if (input.size() != 1) {
       return false;
     }
     return isConnectable(output, (IInputRaw)input.iterator().next());
   }
 
   public boolean isConnectable(Collection<IOutputRaw> output, IInputRaw input)
   {
     if (output.size() != 1) {
       return false;
     }
     return isConnectable((IOutputRaw)output.iterator().next(), input);
   }
 
   private boolean isConnectable(IOutputRaw output, IInputRaw input) {
     if ((this.leftClass.isInstance(output)) && (this.rightClass.isInstance(input))) {
       return additionalCheck(output, input);
     }
     return false;
   }
 
   protected boolean additionalCheck(IOutputRaw output, IInputRaw input) {
     return true;
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.pipeline.OneToOneConnectable
 * JD-Core Version:    0.6.1
 */