/** * Module dependencies.  */

var express = require('express');
var index = require('./routes/index');
var http = require('http');
var path = require('path');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(require('stylus').middleware(__dirname + '/public'));
app.use(express.static(path.join(__dirname, 'public')));

// js scripts that are required in all pages.
app.locals.globalScripts = ['/javascripts/libraries/jquery-1.10.2.js']; 

// development only
if ('development' == app.get('env')) {
    app.use(express.errorHandler());
}

// Global list manipulated by web app.
globalList = [1,2,3];
resultList = [0];

// Establishing connection to back-end Pi Manager.
webSocket = require('faye-websocket');
ws = new webSocket.Client('ws://localhost:4444' );

ws.on('open', function(event){
            console.log('Connection to PiManager has been made.\n');
        });

ws.on('message', function(event) {
            console.log('Message Recieved: ', event.data);
            splitMessage = event.data.split("|");

            if (splitMessage[0]=="mergesort") { // "mergesort|optcode|distributed/single|tte|resultList"
                if (splitMessage[1]=="2") { // merge sort response
                    // skip checking single / distributed
                    var tte = splitMessage[3]
                    resultList = splitMessage[4];
                } 
            } 
        });

ws.on('close', function(event) {
            console.log('close', ", code: " + event.code, ", reason:" + event.reason);
            ws = null;
        }); 


// Creating routes for each page.
app.get('/', index.home);
app.get('/merge_sort', index.mergeSort_Home);

app.get('/merge_sort/config', index.mergeSort_Configuration);
app.post('/merge_sort/config', index.mergeSort_Configuration_postHandler);

app.get('/merge_sort/input', index.mergeSort_Input);
app.post('/merge_sort/input', index.mergeSort_Input_postHandler);

app.get('/merge_sort/visualisation', index.mergeSort_Visualisation);
app.get('/merge_sort/graphs', index.mergeSort_Graphs);

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
    });


