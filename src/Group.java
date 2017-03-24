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
}
