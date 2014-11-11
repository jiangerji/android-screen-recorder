 package com.intel.inde.mp.domain;
 
 import java.util.Collection;
 import java.util.LinkedList;
 
 class ConnectedNode<T, T1>
 {
   T node;
   LinkedList<T1> connectedTo = new LinkedList();
 
   ConnectedNode(T node) {
     this.node = node;
   }
 
   public T value() {
     return this.node;
   }
 
   public boolean isConnected() {
     return !this.connectedTo.isEmpty();
   }
 
   public Collection<T1> getConnector() {
     return this.connectedTo;
   }
 
   public void connect(T1 connector) {
     this.connectedTo.add(connector);
   }
 
   public void disconnect(T1 connector) {
     this.connectedTo.remove(connector);
   }
 
   public boolean isConnectedTo(T1 value) {
     return getConnector().contains(value);
   }
 }

/* Location:           E:\SouceCode\recordGame\gdxDemo\libs\domain-1.2.2415.jar
 * Qualified Name:     com.intel.inde.mp.domain.ConnectedNode
 * JD-Core Version:    0.6.1
 */