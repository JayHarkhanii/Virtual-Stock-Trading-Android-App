const express = require('express');
const cors = require('cors');
const api_key = 'c867ivqad3i9fvji2mk0'

const app = express();

const corsOptions={
    "origin": "*"
};

app.use(cors(corsOptions));

const api_endpoints = require('./routes/routes')

app.use('/', api_endpoints)

app.listen(process.env.PORT || 3000, (req,res) => {
	console.log("Running!!");
});



module.exports = app;