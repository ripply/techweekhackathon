var server = require('nodebootstrap-server'),
    mongoose = require('mongoose');

server.setup(function(runningApp) {

  // runningApp.use(require('express-session')({secret: CONF.app.cookie_secret, resave: false, saveUninitialized: false}));

  // Choose your favorite view engine(s)  
  runningApp.set('view engine', 'handlebars');
  runningApp.engine('handlebars', require('hbs').__express);

  //// you could use two view engines in parallel (if you are brave):  
  // runningApp.set('view engine', 'j2');
  // runningApp.engine('j2', require('swig').renderFile);


  //---- Mounting well-encapsulated application modules
  //---- See: http://vimeo.com/56166857

  runningApp.use('/hello', require('hello')); // attach to sub-route
  runningApp.use(require('routes')); // attach to root route

  runningApp.post('/user', function(req, res) {
    var username = req.body.user;
    var password = req.body.password;

    console.log("POST /user " + username + ":" + password);
    res.status(200).json({status: 'ok'});
  });

  runningApp.post('/login', function(req, res) {
    var username = req.body.user;
    var password = req.body.password;

    console.log("POST /login " + username + ":" + password);
    res.status(200).json({status: 'ok'});
  });

  runningApp.post('/entry', function(req, res) {
    var userid = req.body.id;
    var url = req.body.url;
    var location = req.body.location;

    console.log("POST /entry id:" + userid + ", url:" + url + ", location:" + location);
    res.status(200).json({status: 'ok'});
  });
  
  // If you need websockets:
  // var socketio = require('socket.io')(runningApp.http);
  // require('fauxchatapp')(socketio);

  //connect to Mongo (mongoose):
  mongoose.connect('mongodb://localhost/test');
  mongoose.connection.on('error', console.error.bind(console, 'connection error: '));
  mongoose.connection.on('open', function(callback) {
    console.log("Connected to Mongoose...");
  });
  
  //routes.initialize(app);
});
