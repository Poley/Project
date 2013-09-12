
var margin = {top: 0, right: 0, bottom: 0, left: 0},
  width = $('#center').width() - margin.left - margin.right,
  height = $('#center').height() - margin.top - margin.bottom;

var canvas = d3.select("#center").append("svg")
                .attr("width", width) 
                .attr("height", height)
                .style("border", "1px solid black")
                .append("g")
                    .attr("id", "canvas")
                    .attr("transform", "translate(0,50)");

var tree = d3.layout.tree()
            .size( [ width, height] );

var i = 0,
    duration = 750,
    root;

var diagonal = d3.svg.diagonal();

d3.json("/javascripts/visualisation/tree.json", function (error, data) {
    canvas.append("defs").append("marker")
            .attr("viewbox", "0 0 10 10")
            .attr("id", "marker")
            .attr("refX", "7.5")
            .attr("refY", "4")
            .attr("markerUnits", "strokewidth")
            .attr("markerWidth", "50")
            .attr("markerHeight", "50")
            .attr("stroke-width", "50")
            .attr("orient", "auto")
            .append("path")
                .attr("class", "highlightLink")
                .attr("d", "M 0,0 V8 L3.5,4 Z");
    
    root=data;
    root.x0 = height/2;
    root.y0 = 0;

    function collapse (d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(collapse);
            d.children = null;
        } 
    } 

    //root.children.forEach(collapse);
    update(root);
})

function elbow (d, i) {
    return "M" + d.source.x + "," + d.source.y + 
           "H" + d.source.x + "V" + d.source.y*1.5 + 
           "H" + d.target.x + "V" + d.source.y*1.5 + 
           "H" + d.target.x + "V" + d.target.y ;
}

function update (source) {

    // Compute new tree layout
    var nodes = tree.nodes(root).reverse(), //.reverse(),
        links = tree.links(nodes);
    
    // Normalize for fixed-depth
    nodes.forEach( function (d) { d.y = d.depth * 180; });

    // Update the nodes...
    var node = canvas.selectAll("g.node")
        .data(nodes, function (d) { return d.id || (d.id = ++i); });

    /* *** Creating New Nodes *** */
    var nodeEnter = node.enter().append("g")
        .attr("class", "node")
        .attr("text-anchor", "start")
        .attr("transform", function (d) { return "translate(" + source.x0 + "," + source.y0 + ")"; })
        .on("click", click);

    nodeEnter.append("circle").attr("class", "circle") 
        .attr("r", 1e-6)
        .style("fill", function(d) { return d.children ? "lightsteelblue" : "#fff"; });
    
    // Creating node text
    nodeEnter.append("text").attr("class", "name")
        .attr("id", "name")
        .attr("x", 10) .attr("y", -4)
        .text( function (d) { return d.piName; })
        .style("fill-opacity", 1e-6);
    
    nodeEnter.append("text").attr("class", "stats")
        .attr("id", "ID")
        .attr("x", 15)
        .attr("y", "1em")
        .text( function (d) { return "Hostname: " + d.nodeID; });
    
    nodeEnter.append("text").attr("class", "stats")
        .attr("id", "input")
        .attr("x", 17) .attr("y", "2em")
        .text( function (d) { return "Input: " + d.input; });
    
    nodeEnter.append("text").attr("class", "stats")
        .attr("id", "memory")
        .attr("x", 17) .attr("y", "3.5em")
        .text( function (d) { return "% Memory Used: " + d.pMem + "%"; });


    /* *** Node Transitions (Opening) *** */
    var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function (d) { return "translate(" + d.x + "," + d.y + ")"; });

    nodeUpdate.selectAll("circle").attr("class", "circle")
        .attr("r", 6)
        .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

    // Display node's text
    nodeUpdate.selectAll("text")
        .style("fill-opacity", 1);

    /* *** Node Transitions (Closing) *** */
    var nodeExit = node.exit().transition()
        .duration(duration)
        .attr("transform", function (d) { return "translate(" + d.parent.x + "," + d.parent.y + ")"; });

    nodeExit.select("circle") .attr("r", 0);
    nodeExit.selectAll("text").style("fill-opacity", 0);


    /* *** Updating links during transition *** */
    var link = canvas.selectAll("path.link")
        .data(links, function (d) { return d.target.id;  });

    // Enter any new links at the parent's previous position.
    link.enter().insert("path", "g")
        .attr("class", "link")
        .attr("d", function (d) {
            var o = {x: source.x0, y:source.y0 };
            return diagonal( {source: o, target: o});
        });
    
    // Transition links to their new position
    link.transition()
        .duration( duration)
        .attr("d", diagonal);
    
    // Transition
    link.exit().transition()
        .duration( duration)
        .attr( "d", function (d) {
            var o = {x: source.x, y:source.y };
            return diagonal( {source: o, target: o});
        } )
        .remove();
    
    // Stash the old positions for transition.
    nodes.forEach( function (d) {
            d.x0 = d.x;
            d.y0 = d.y;
        } );

}

// Toggle children on click.
function click(d) {
    if (d.children) {
        d._children = d.children;
        d.children = null;
    } else {
        d.children = d._children;
        d._children = null;
    }
    update(d);
}
