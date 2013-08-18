// ROUTE HANDLERS

// Home page route handler
exports.home = function(req, res) {
        res.render('home', { scripts: ['/javascripts/home/home.js'], 
                             title: "Distributed Algorithms Using Pi-Cloud", 
                             stylesheetRef: "/stylesheets/home.css"
                             } );
    };

exports.mergeSort_Home = function (req, res) {
        res.render('mergeHome', { title: "Merge Sort", 
                                  stylesheetRef: "/stylesheets/mergeSort.css",
                                  } );
    }; 

exports.mergeSort_Input = function (req, res) {
        res.render('mergeInput', { title: "Merge Sort - Input",
                                   stylesheetRef: "/stylesheets/mergeSort.css"
                                 });
    };

exports.mergeSort_Configuration = function (req, res) {
        res.render('mergeConfiguration', { title: "Merge Sort - Configuration",
                                           stylesheetRef: "/stylesheets/mergeSort.css"
                                         } );
    };

exports.mergeSort_Graphs = function (req, res) {
       res.render('mergeGraphs', { title: "Merge Sort - Graphs and Statistics",
                                   stylesheetRef: "/stylesheets/mergeSort.css"
                                 } );
    } ;

exports.mergeSort_Configuration_postHandler = function (req, res) {
        cSize = req.body.clusterSize || 0;
        lSize = req.body.listSize || 0;
        configured = true;
        console.log("csize = " + cSize + ". lSize = " + lSize + ".Configured = " + configured);
        res.redirect('/merge_sort');
    };

exports.mergeSort_Visualisation = function (req, res) {
        res.render('mergeVis', { title: "Merge Sort - Visualisation",
                                  stylesheetRef: "/stylesheets/mergeSort.css",
                                  scripts: ["/javascripts/libraries/d3.v3.min.js", "/javascripts/visualisation/visualisation.js"]
                                  } );
    }; 
