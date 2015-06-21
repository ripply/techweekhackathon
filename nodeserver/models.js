var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	ObjectID = Schema.Types.ObjectID;


var Login = new Schema ({
	user: 		{type: String,
				 unique: true},
	password: 	{type: String}
	//pictures: 	{type: [String]}
});

var Entry = new Schema({
	id: 		{type: String,
				 unique: true},
	url:  		{type: String} 
});
	
var Admin = new Schema({
	entries: [Entry]
	
});

module.exports = {
	Login: Login,
	Entry: Entry
};

