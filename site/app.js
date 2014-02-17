/** * Module dependencies.  */

var express = require('express');
var index = require('./routes/index');
var http = require('http');
var path = require('path');
var fs = require('fs');
var util = require('util');

var app = express();


// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(express.cookieParser());
app.use(express.session({ secret: 'your secret here' }));
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// js scripts that are required in all pages.
app.locals.globalScripts = ['/javascripts/libraries/jquery-1.10.2.js']; 

// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

// Global list manipulated by web app.
globalList = [1,2,3,4,5,4,3,2,1];
resultList = [0];
recentTasks = []; //id of recent tasks
eventDictionary = {}; // dictionary containing all events occured during an algorithm. Key:value = timestamp:eventObj .
eventString = "";
eventsOrdered = []; // ordered list of events occuring within one task execution, used for web app to iterate across events in order.
visReady = false; // used to indicate when the visualisation has all information it requires e.g. result & event data.

// Establishing connection to back-end Pi Manager.
webSocket = require('faye-websocket');
ws = new webSocket.Client('ws://localhost:4444' );

ws.on('open', function(event){
            console.log('Connection to PiManager has been made.\n');
            ws.send("recentTasks|1"); //Updates list of recent tasks
            ws.send("getClusterNetwork|1|"); //Gets cluster network	
        });

ws.on('message', function(event) {
            splitMessage = event.data.split("|");
            console.log('Message Recieved: ' + splitMessage[0] + "," + splitMessage[1] );


            if (splitMessage[0]=="mergesort") { // "mergesort|optcode|distributed/single|tte|resultList"
                if (splitMessage[1]=="2") { // merge sort response
                    // skip checking single / distributed
                    var tte = splitMessage[3]
                    resultList = splitMessage[4];
                    ws.send("eventData|1"); // Requests the Pi Server sends information on events during the merge sort execution
                } 
            } else if (splitMessage[0]=="getClusterNetwork" && splitMessage[1]=="2"){
                console.log("Writing cluster tree json");
                var treeJsonText = splitMessage[2];
                fs.writeFileSync("./public/javascripts/visualisation/tree.json", splitMessage[2]);
            } else if (splitMessage[0]=="eventData" && splitMessage[1]=="2") {
                for (i=2; i<splitMessage.length; i++) {
                    eventString += splitMessage[i] + "|";
                } 
		recentTasks.push(splitMessage[3]);
		var i = recentTasks.shift();
                console.log( "\n" + eventString);
                //console.log("Event data received and dictionary created.");
                //console.log( eventDictionary);
            } else if (splitMessage[0]=="recentTasks" && splitMessage[1]=="2") {
                for (i=2; i<splitMessage.length; i++) {
                    recentTasks.push(splitMessage[i]);
                } 
                console.log( "\n" + recentTasks);
            } 
        });

ws.on('close', function(event) {
            console.log('close', ", code: " + event.code, ", reason:" + event.reason);
            ws = null;
        }); 


// Creating routes for each page.
app.get('/', index.home);
app.get('/merge_sort', index.mergeSort_Home);

app.get('/merge_sort/input', index.mergeSort_Input);
app.post('/merge_sort/input', index.mergeSort_Input_postHandler);

app.get('/merge_sort/visualisation', index.mergeSort_Visualisation);

app.get('/merge_sort/history', index.mergeSort_History);

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
    });


function eventObj(taskid, taskStatus, inp,out, timestamp, ipAddr, pm) {
    eventO = {};
    eventO.task_id = taskid;
    eventO.tStatus = taskStatus;
    eventO.input = inp;
    eventO.output = out;
    eventO.tStamp = timestamp;
    eventO.ip = ipAddr;
    eventO.pmem = pm;
    return eventO;
} 
