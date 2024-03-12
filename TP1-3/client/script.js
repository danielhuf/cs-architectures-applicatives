let msgsUrl;
let usernames = [];
let datetimes = [];
const imgUrl = "https://source.boringavatars.com/beam/40/";

function fact(n) {
    if (n < 0) {
        return "The value must be positive!";
    }

    if (n == 0) {
        return 1;
    } else {
        return n * fact(n - 1);
    }
}

function applique(f, tab) {
    return tab.map(element => f(element));
}

// CONSOLE RESULTS
// > fact(6)
// < 720
// > applique(fact,[1,2,3,4,5,6]);
// < (6) [1, 2, 6, 24, 120, 720]
// > applique(function(n) { return (n+1); } , [1,2,3,4,5,6]);
// < (6) [2, 3, 4, 5, 6, 7]

// ======================================== APPLICATION ========================================

// Fonction pour formater un message en un objet JSON
function formatMsg(msgItem) {
    return { "username": msgItem[0], "msg": msgItem[1], "datetime": msgItem[2] };
}

function update(messages) {
    const ul = document.querySelector('section[aria-label="Posted messages"] ul');
    ul.innerHTML = "";
    messagesList = [];

    // Si des noms d'utilisateurs et des dates sont disponibles, on les utilise
    if (usernames.length > 0 && datetimes.length > 0) {
        for (let i = 0; i < messages.length; i++) {
            messagesList.push([usernames[i], messages[i], datetimes[i]]);
        }
    } else if (usernames.length > 0) {
        for (let i = 0; i < messages.length; i++) {
            messagesList.push([usernames[i], messages[i], new Date().toISOString()]);
        }
    } else {
        for (let i = 0; i < messages.length; i++) {
            messagesList.push(["Anonymous", messages[i], new Date().toISOString()]);
        }
    }

    // On inverse l'ordre des messages pour avoir les plus récents en haut de l'écran
    messagesList.reverse().map(formatMsg).forEach(function(message) {
        const li = document.createElement('li');
        li.className = "message";

        const msgText = document.createElement('p');
        msgText.className = "message-text";

        const username = document.createElement('div');
        username.className = "username";
        username.textContent = message.username;

        const userImg = document.createElement('img');
        userImg.src = imgUrl + message.username;

        const msgDatetime = document.createElement('div');
        msgDatetime.className = "message-datetime";
        msgDatetime.textContent = new Date(message.datetime).toLocaleString().slice(0, -3);

        msgText.textContent = message.msg;

        const userInfoDiv = document.createElement('div');
        userInfoDiv.className = "user-info";
        userInfoDiv.appendChild(username);
        userInfoDiv.appendChild(msgText);
        userInfoDiv.appendChild(msgDatetime);

        li.appendChild(userImg);
        li.appendChild(userInfoDiv);

        ul.appendChild(li);
    });

    // Animation pour afficher le message le plus récent
    if (ul.firstChild) {
        ul.firstChild.classList.add('message-animate');
    }
}

function postMessagetoServer(msg, username) {
    fetch(msgsUrl + "msg/post/" + encodeURIComponent(msg),
        {
            headers: {
                "Username": username,
                "Datetime": new Date().toISOString()
            }
        })
        .then(res => res.json())
        .then(data => {
            console.log("Message posted: ", data);
            document.querySelector('#new-message').value = '';
            getMessagesFromServer();
        })
        .catch(error => console.error("Error posting message: ", error));
}

function getMessagesFromServer(beginning = false) {
    fetch(msgsUrl + "msg/getAll")
        .then(function(response) {
            const usernamesHeader = response.headers.get('Username');
            const datetimeHeader = response.headers.get('Datetime');

            if (usernamesHeader) {
                usernames = JSON.parse(usernamesHeader);
            } else {
                usernames = [];
            }

            if (datetimeHeader) {
                datetimes = JSON.parse(datetimeHeader);
            } else {
                datetimes = [];
            }

            console.log('USERNAMES', usernames);
            console.log('DATETIMES', datetimes);

            return response.json();
        })
        .then(function(messages) {
            update(messages);
            if (beginning) {
                if (messages.length > 0) {
                    alert("First message: " + messages[0]);
                } else {
                    alert("No messages found");
                }
            }
        })
        .catch(function(error) {
            console.error("Error fetching messages:", error);
            alert("Error fetching messages");
        });
}

// On paramètre l'URL du microservice
function updateUrl() {
    msgsUrl = document.querySelector('#microservice-url').value;
    getMessagesFromServer(true);
}

document.addEventListener('DOMContentLoaded', function() {
    updateUrl();

    document.querySelector('#update-url').addEventListener('click', function() {
        updateUrl();
    });

    document.querySelector('#post-msg-btn').addEventListener('click', function() {
        const msg = document.querySelector('#new-message').value;
        const username = document.querySelector('#username').value;
        postMessagetoServer(msg, username);
    });

    // On récupère une blague sur les développeurs avec l'API JokeAPI
    document.querySelector('#joke-btn').addEventListener('click', function() {
        fetch('https://v2.jokeapi.dev/joke/Programming?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&format=txt&type=single')
            .then(response => response.text())
            .then(jokeText => { document.querySelector('#new-message').value = unescape(jokeText); })
            .catch(error => console.error("Error fetching joke:", error));
    });
});