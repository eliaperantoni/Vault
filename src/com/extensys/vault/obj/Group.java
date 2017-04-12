package com.extensys.vault.obj;

import com.extensys.vault.Server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by extensys on 15/03/2017.
 */
public class Group {
    public Group(int groupId,String groupName){
        this.mGroupName=groupName;
        this.mGroupId=groupId;
    }
    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int groupId) {
        mGroupId = groupId;
    }

    private String mGroupName;
    private int mGroupId;

    void removeSelf(Server server){
        try {
            Connection con = server.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(String.format("DELETE FROM groups WHERE groupId=\"%s\"", this.mGroupId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void updateSelf(Server server){
        try {
            Connection con = server.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM groups WHERE groupId=\"%s\"", this.mGroupId));
            rs.first();
            setGroupName(rs.getString("groupName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
