var mongoose = require('mongoose'),
	Schema = mongoose.Schema,
	ObjectID = Schema.Types.ObjectID;


var Login = new Schema ({
	user: 		{type: String},
	password: 	{type: String}
	//pictures: 	{type: [String]}
});

var Entry = new Schema({
	id: 		{type: String},
	url:  		{type: String} 
});
	


module.exports = {
	Login: Login,
	Entry: Entry
}

