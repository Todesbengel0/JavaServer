package smf.ves.rayall;

import java.io.Serializable;

public class RenderableObjList implements Serializable {
  private static final long serialVersionUID = 1L;
  public Renderable elem;
  public RenderableObjList next;

  public RenderableObjList() {
    elem = null;
    next = null;
  }

  public RenderableObjList(Renderable obj) {
    elem = obj;
    next = null;
  }

  public void addElement(Renderable obj) {
    RenderableObjList myList;
    if (elem == null) {
      elem = obj;
      // next=null; //is already initialized
    }
    else {
      myList = this;
      while (myList.next != null)
        myList = myList.next;
      myList.next = new RenderableObjList(obj);
    }
  }
}
