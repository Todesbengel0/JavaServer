package smf.ves.rayall;

import java.io.Serializable;

public class LightObjList implements Serializable {
  private static final long serialVersionUID = 1L;
  public Light elem;
  public LightObjList next;

  public LightObjList() {
    elem = null;
    next = null;
  }

  public LightObjList(Light obj) {
    elem = obj;
    next = null;
  }

  public void addElement(Light obj) {
    LightObjList myList;
    if (elem == null) {
      elem = obj;
      // next=null; //is already initialized
    }
    else {
      myList = this;
      while (myList.next != null)
        myList = myList.next;
      myList.next = new LightObjList(obj);
    }
  }
}
