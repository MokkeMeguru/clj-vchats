let connection = new WebSocket('ws://localhost:3000/test/websocket');

connection.onopen = function () {
    connection.send(JSON.stringify({'message' : 'Hello, Websocket from javascript!'}));
};

connection.onerror = function (error) {
    console.log(error);
};

connection.onmessage = function (e) {
    var data = JSON.parse (e.data);
    console.log (data);
    
};

