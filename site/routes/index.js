// ROUTE HANDLERS

// Home page route handler
exports.home = function(req, res) {
        res.render('home', { title: "Distributed Algorithms Using Pi-Cloud",
				stylesheetRef: "/stylesheets/home.css"
                             } );
    };

exports.mergeSort_Home = function (req, res) {
        res.render('mergeHome', { title: "Merge Sort", 
                                  stylesheetRef: "/stylesheets/mergeSort.css" 
                                  } );
    }; 

// INPUT page
exports.mergeSort_Input = function (req, res) {
        res.render('mergeInput', { title: "Merge Sort - Input", 
                                  stylesheetRef: "/stylesheets/mergeSort.css", 
                                   gList: globalList
                                 });
    };

exports.mergeSort_Input_postHandler = function (req, res) {
        var splitList = (req.body.updatedList.trim()).split(" ");
        var checkedList = new Array();
        var checkListIndex = 0
        var error=false;
        eventString = "";
        eventsOrdered = [];

	if (splitList.length < 2) checkedList = globalList;
	else {
		for (i = 0; i < splitList.length; i++) {
		     if ( !isNaN(splitList[i]) ) checkedList[checkListIndex++] = splitList[i]; // isNaN returns false on whitespace
		     else error=true
		} 
	}
        globalList = checkedList; // updating the global variable to the list of that defined in the post request
        console.log(checkedList);
        
        visReady = false; // Indicates that an execution of the merge sort is in progress
        // Pi Manager will execute synchronously, so the cluster
        
	ws.send("getClusterNetwork|1|"); 
        ws.send("mergesort|1|2|" + checkedList);
        
        // Pi Manager has now been sent requests for tasks, a message handler in app.js will open up the visualisation page once all results have been retrieved.
        res.redirect('/merge_sort/visualisation');
    }; 

// VISUALISATION page
exports.mergeSort_Visualisation = function (req, res) {
        res.render('mergeVis', { title: "Merge Sort - Visualisation",
                                  stylesheetRef: "/stylesheets/mergeSort.css",
                                  scripts: ["/javascripts/libraries/d3.v3.min.js", "/javascripts/visualisation/visualisation.js", "/javascripts/visualisation/visPage.js"],
                                  gList: globalList,
                                  rList: resultList,
                                  events: eventString,
                                  eventsOrdered: eventsOrdered
                                  } );
    }; 
