package pi_cloud.piManager;

public class JsonNode {

    private String nodeID = "";
    private String input = "None";
    private JsonNode[] children = new JsonNode[0];

    private boolean hasChildren = false;
    private boolean hasParent = false;

    public JsonNode(String id) {
        nodeID = id;
    } 

    public void addChild(JsonNode childID){
        if (children.length == 0) {
            children = new JsonNode[1];
            children[0] = childID; 
        } else if ( children.length == 1) {
            JsonNode oldchild = children[0];
            children = new JsonNode[2];
            children[0] = oldchild;
            children[1] = childID;
        }
        hasChildren = true;
    } 
    
    private boolean hasChildren() { return hasChildren; } 
    public boolean hasParent() { return hasParent; }
    public void giveParent() { hasParent = true; }
    public short childrenSize() { return (short) children.length; } 
} 
