//object sent to server
function message(action, argument) {
    this.action = action;
    this.argument = argument;
    this.toString = function () {
        return JSON.stringify(this)
    }
}

//adding new user
function addNewUser() {
    var userName = "";
    while (userName == "") {
        userName = prompt("Hello there! Enter Your name: ");
    }
    webSocket.send(new message("login", userName).toString());
}

function addNewChannel(channelName) {
    if(channelName!==""){
        id("newChatName").value=""
        webSocket.send(new message("newchannel",channelName).toString())
    }
}
//deciding what to do based on messages from server
function getMsgFromServer(msg) {
    var msg = JSON.parse(msg.data);
    var act=msg.action;
    if (act == "alert") {
        handleAlert(msg.argument)
    } else if(act=="login"){
        var lists=JSON.parse(msg.argument)
        var userslist=lists.users
        var channelslist=lists.channels
        login(msg.user, userslist,channelslist)
    } else if(act=="logout") {
        logout(msg.user, JSON.parse(msg.argument))
    }else if (act=="say"){
        say(msg.user, msg.argument);
    }else if(act=="newchannel"){
        newchannel(msg.user, msg.argument);
    }else if(act=="joinchannel"){
        console.log(msg.user, "joined channel "+msg.argument)
        say(msg.user, "joined channel "+msg.argument)
    }
}

//handling alerts from server
function handleAlert(alrt) {
    if (alrt == "usernameTaken") {
        alert("This username is already taken!");
        addNewUser();
    } else if(alrt=="channelExists"){
        alert("This channel already exists!")
    } else if(alrt=="noChannel"){
        alert("You must choose or create a channel first!")
    }
    else {
        alert(alrt);
    }
}

//handling logins from server
function login(username, userslist, channelslist) {
    id("userlist").innerHTML = "";
    userslist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
    });
    id("channellist").innerHTML="";
    channelslist.forEach(function (channel) {
        id("channellist").appendChild(channelButton(channel))
    });
}

//handling logouts from server
function logout(username, userslist) {
    id("userlist").innerHTML = "";
    userslist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
    })
}

//handling new messages in chat from server
function say(username, text){
    insert("msgslist","<li>"+username+": "+text+"</li>")
}

function newchannel(username, channelname) {
    id("channellist").appendChild(channelButton(channelname))
}

//Send a message if it's not empty, then clear the input field
function sendMessage(msg) {
    if (msg !== "") {
        webSocket.send(new message("say",msg).toString());
        id("message").value = "";
    }
}

//Join other channel
function joinChannel(channelname) {
    webSocket.send(new message("joinchannel",channelname))

}

//Button enabling joining a channel
function channelButton(channelname) {
    var elmnt=document.createElement("li");
    var button=document.createElement("button");
    var text=document.createTextNode(channelname);
    button.appendChild(text);
    button.addEventListener("click", function () {
        joinChannel(channelname)
    });
    elmnt.appendChild(button)
    return elmnt
}

//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onopen = function () {
    addNewUser();
}
webSocket.onmessage = function (msg) {
    getMsgFromServer(msg);
};
webSocket.onclose = function () {
    id("active").style = "display: none;";
    id("inactive").style = "display: block;";
};


//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        sendMessage(e.target.value);
    }
});

//Create new channel if "New channel" is clicked"
id("newChat").addEventListener("click", function () {
    addNewChannel(id("newChatName").value);
});

//Create new channel if enter is pressed in the input field
id("newChatName").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        addNewChannel(e.target.value);
    }
});


//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
    });
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

