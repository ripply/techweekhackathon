var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	ObjectID = Schema.Types.ObjectID;


var Login = new Schema ({
	user: 		{type: String,
				 unique: true},
	password: 	{type: String},
	number:     {type: String}
	//pictures: 	{type: [Entry]}
});

var Entry = new Schema({
	id: 		{type: String},
	url:  		{type: String} 
});

module.exports = {
	Login: Login,
	Entry: Entry
};

