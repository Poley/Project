package pi_cloud.piManager;

/* This class is used strictly to contribute to the creation of the JSON string that identifies the cluster network.
   the GSON package used in PiServerSocket translates this object into a JSON string, with all attributes being included within it. Operations are excluded.
 */
public class JsonNode {

    private String nodeID = "";
    private JsonNode[] children = new JsonNode[0];

    private boolean hasChildren = false;
    private boolean hasParent = false;

    public JsonNode(String id) {
        nodeID = id;
    } 

    protected void addChild(JsonNode childID){
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
    
    protected boolean hasChildren() { return hasChildren; } 
    protected boolean hasParent() { return hasParent; }
    protected void giveParent() { hasParent = true; }
} 
