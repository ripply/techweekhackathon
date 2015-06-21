var server = require('nodebootstrap-server'),
    mongoose = require('mongoose'),
    models = require('models.js');

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

  runningApp.post('/user', function(req, res) { //create new user
    var username = req.body.user;
    var password = req.body.password;
    var number = req.body.number;

    console.log("POST /user " + username + ":" + password);

    var login = mongoose.model('Login', models.Login); //create Login object

    var user = new login ({                     // Json for new user
      user:     username,
      password: password,
      number:   number
    });

    user.save(function(err, user) {           //save new user
      if (err || user == null) {return res.status(403).json({status: "ERROR"});}
      res.status(200).json({status: 'ok'});
    });
  });

  runningApp.post('/login', function(req, res) { //Query database
    var username = req.body.user;
    var password = req.body.password;

    console.log("POST /login " + username + ":" + password);

    var User = mongoose.model('Login', models.Login);

    User.findOne({user: username, password: password}, function (err, user){
      if (err || user === null) { return res.status(401).json({status: 'FORBIDDEN'});}
      res.status(200).json({id: user.id});
    });

  });

  runningApp.get('/pick_winner', function(req, res){
    var Entry = mongoose.model('Entry', models.Entry);

    Entry.find(function (err, contestants){      
      if (err) return console.error(err);
      console.log(contestants);
      var rand = contestants[Math.floor(Math.random() * contestants.length)];
      //console.log(rand);
    });
  });

  runningApp.post('/delete_entry', function(req, res){
    var Entry = mongoose.model('Entry', models.Entry);

    Entry.remove({}, function(err) {
      console.location('collection removed');
    });
  })

  runningApp.post('/entry', function(req, res) { //create new url/location entry
    var userid = req.body.id;
    var url = req.body.url;
    var location = req.body.location;

    console.log("POST /entry id:" + userid + ", url:" + url + ", location:" + location);
    res.status(200).json({status: 'ok'});

    
    var Entry = mongoose.model('Entry', models.Entry);   //create entry object

    var entry = new Entry ({                      //create entry JSON
      id: userid,
      url: url
    })

    entry.save(function(err, entry){
      if (err) return console.error(err);
      console.dir(entry);
    });
  });
  
  // If you need websockets:
  // var socketio = require('socket.io')(runningApp.http);
  // require('fauxchatapp')(socketio);

  //connect to Mongo (mongoose):
  mongoose.connect('mongodb://localhost/test');
  //mongoose.connection.db.dropCollection();
  mongoose.connection.on('error', console.error.bind(console, 'connection error: '));
  mongoose.connection.on('open', function(callback) {
    console.log("Connected to Mongoose...");
  });
  
  //routes.initialize(app);
});
