package masterformat.tree;

public class TreeNode {
    private final String type;
    private final String tag;
    private final String description;
    
    public TreeNode(String ty, String t, String d){
	type = ty;
	tag = t;
	description = d;
    }
    
    public String getType(){
	return type;
    }
    
    public String getTag(){
	return tag;
    }
    
    public String getDescription(){
	return description;
    }
    
    @Override
    public String toString(){
	return description;
    }
}
