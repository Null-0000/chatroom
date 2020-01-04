package client.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class GrpDialog extends Dialog implements Serializable {
    private transient ArrayList<String> members;

    public GrpDialog(ArrayList<String> members, String userA){
        super(userA);
        this.members = members;
    }

    public void setChatView() throws IOException{

    }

}
