var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	ObjectID = Schema.ObjectID;


var loginSchema = new Schema({
	user: 		{type: String},
	password: 	{type: String},
	id: 		{type: int}  
});

var entrySchema = new Schema({
	id: 		{type: int},
	url:  		{type: String} 
})