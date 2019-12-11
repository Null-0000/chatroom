package client.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

public class MyList<Item> extends ArrayList{
    transient private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public MyList(Collection c) {
        super(c);
        pcs = new PropertyChangeSupport(this);
//        if(pcs == null) ShowDialog.showMessage("pcs 为空 01");
    }

    @Override
    public boolean add(Object element) {
        super.add(element);
        pcs.firePropertyChange("MyList", null, this);
        return false;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener){
        if(pcs == null){
//            ShowDialog.showMessage("pcs 为空 02");
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(listener);
    }
}