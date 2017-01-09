function message(user, action, argument) {
    this.user=user;
    this.action=action;
    this.argument=argument;
    this.toString=function () {
        return JSON.stringify(this)
    }
}

function addNewUser(){
    var userName="";
    while(userName==""){ userName = prompt("Hello there! Enter Your name: ");}
    webSocket.send(new message(userName,"login","").toString());
}

function getMessage(msg){
    var message=JSON.parse(msg.data);
    //alert(message.action);
    if(message.action=="alert"){
        handleAlert(message.argument)
    }
}

function handleAlert(alrt){
    if(alrt=="usernameTaken"){
        alert("This username is already taken!");
        addNewUser();
    }
}
//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onopen=function () {
    addNewUser();
}
webSocket.onmessage = function (msg) {
    getMessage(msg);
};
webSocket.onclose = function () {
    id("active").style="display: none;";
    id("inactive").style="display: block;";
};



//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});


//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        //webSocket.send(message);
        id("message").value = "";
        id('chatControls').style.visibility='hidden'
    }
}

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

