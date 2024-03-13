var express = require('express');    //import de la bibliothèque Express
var app = express();                 //instanciation d'une application Express
var counter = 0;
var now = new Date().toISOString();
var allMsgs = ["Hello World", "foobar", "CentraleSupelec Forever"];
var allUsernames = ["Jimmy", "Dani", "Gymnast24"]
var allDatetimes = [now, now, now];

app.use((req, res, next) => {
	res.setHeader('Content-Type', 'text/html; charset=utf-8');
	next();
});

// Pour s'assurer que l'on peut faire des appels AJAX au serveur
app.use(function(req, res, next) {
	res.header("Access-Control-Allow-Origin", "*");
	res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Username, Datetime");
	res.header("Access-Control-Expose-Headers", "Username, Datetime");

	next();
});

// Ici faut faire faire quelque chose à notre app...
// On va mettre les "routes"  == les requêtes HTTP acceptéés par notre application.

app.get("/", function(req, res) {
	res.send("Hello")
})

app.get("/test/*", function(req, res) {
	// res.send("Path after /test/ : " + req.params[0]);
	// res.send({"a": 1, "b" : 2});
	// res.send(["Hello", "World"]);
	// res.send(42);
	res.send({"msg": req.url.substr(6)});
});

app.get('/blah*', function(req, res) {
	console.log(req.query)
});

app.get('/cpt/query', function(req, res) {
	res.json({ "counter": counter });
});

app.get('/cpt/inc', function(req, res) {
    const queryParams = Object.keys(req.query);

    if (queryParams.length === 0) {
        counter++;
        res.json({ "code": 0 });
    } else if (queryParams.length === 1 && queryParams.includes('v')) {
        if (req.query.v.match(/^-?\d+$/)) {
            counter += parseInt(req.query.v, 10);
            res.json({ "code": 0 });
        } else {
            res.json({ "code": -1 });
        }
    } else {
        res.json({ "code": -1 });
    }
});

// ======================================== APPLICATION ========================================

// Fonction pour vérifier si un id est valide
function checkId(msgId) {
	const id = parseInt(msgId, 10);

	// L'id doit être un nombre et doit être dans l'intervalle [0, allMsgs.length[
	if (isNaN(id) || id < 0 || id >= allMsgs.length) {
		return -1;
	}

	return id;
}

app.get('/msg/get/*', function(req, res) {
	const msgIdStr = req.url.substr(9);
	const msgId = checkId(msgIdStr);

	if (msgId === -1) {
		res.json({ "code": 0 })
	}

	res.setHeader('Username', JSON.stringify(allUsernames[msgId]));
	res.setHeader('Datetime', JSON.stringify(allDatetimes[msgId]));
	res.json({ "code": 1, "msg": allMsgs[msgId] });
});

app.get('/msg/nber', function(req, res) {
	res.json(allMsgs.length)
});

app.get('/msg/getAll', function(req, res) {
	res.setHeader('Username', JSON.stringify(allUsernames));
	res.setHeader('Datetime', JSON.stringify(allDatetimes));
	res.json(allMsgs);
});

app.get('/msg/post/*', function(req, res) {
	const msg = unescape(req.url.substr(10));

	if (req.headers.username) {
		const username = req.headers.username;
		allUsernames.push(username);
	} else {
		allUsernames.push("Anonymous")
	}

	if (req.headers.datetime) {
		const datetime = req.headers.datetime;
		allDatetimes.push(datetime);
	} else {
		allDatetimes.push(new Date().toISOString())
	}

	allMsgs.push(msg);
	res.json({ "newMessageId": allMsgs.length - 1 })
});

app.get('/msg/del/*', function(req, res) {
	const msgIdStr = req.url.substr(9);
	const msgId = checkId(msgIdStr);

	if (msgId === -1) {
		res.json({ "code": 0 })
	} else {
		allMsgs.splice(msgId, 1);
		allUsernames.splice(msgId, 1);
		allDatetimes.splice(msgId, 1);
		res.json({ "code": 1 });
	}
});

app.listen(8080); //commence à accepter les requêtes
console.log("App listening on port 8080...");