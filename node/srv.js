var io = require('socket.io').listen(8080),
    express = require('express'),
    app = express();

app.use(express.bodyParser());

app.post('/push', function(req, res) {
    io.sockets.send(req.body.msg);
    res.send();
});

app.listen(3001);