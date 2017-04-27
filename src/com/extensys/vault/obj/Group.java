package com.extensys.vault.obj;

import com.extensys.vault.DataBank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by extensys on 15/03/2017.
 */
public class Group implements Serializable,HasId {
    //TODO: Make groups relevant
    private static final long serialVersionUID = 1L;
    public Group( String groupName){
        this.mGroupName=groupName;
        Map<UUID,Group> groups = new HashMap<>();
        for(Group x: DataBank.getInstance().getGroups()){
            groups.put(x.getId(),x);
        }
        UUID id;
        do{
            id=UUID.randomUUID();
        }while(groups.containsKey(id));
        this.mGroupId=id;
    }
    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public UUID getId() {
        return mGroupId;
    }

    public void setGroupId(UUID groupId) {
        mGroupId = groupId;
    }

    private String mGroupName;
    private UUID mGroupId;

    @Override
    public int hashCode() {
        return mGroupId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode()==((Group)this).hashCode();
    }
}
