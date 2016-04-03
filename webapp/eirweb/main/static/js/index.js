$("document").ready(function() {
    sinchClient = new SinchClient({
        applicationKey: "c4f755a1-407f-45a6-baae-40ef3932fce4",
        capabilities: {calling: true, video: true},
        supportActiveConnection: true,
        onLogMessage: function(message) {
            console.log(message.message);
        },
    });

    var callClient;
    var call;
  
    

    $("#login").on("click", function (event) {
        event.preventDefault();
        
        var signUpObj = {};
        signUpObj.username = "USERA";
        signUpObj.password = "testtest";

        sinchClient.start(signUpObj, afterStartSinchClient());          
    });

    function afterStartSinchClient() {
        // hide auth form
        $("form#authForm").css("display", "none");
        // show logged-in view
        $("div#sinch").css("display", "inline");
        // start listening for incoming calls
        sinchClient.startActiveConnection();
        // define call client (to handle incoming/outgoing calls)
        callClient = sinchClient.getCallClient();
        //initialize media streams, asks for microphone & video permission
        callClient.initStream();
        //what to do when there is an incoming call
        callClient.addEventListener(incomingCallListener);
    }

    $("#call").on("click", function (event) {
        event.preventDefault();
    	if (!call) {
            usernameToCall = $("input#usernameToCall").val()
        	call = callClient.callUser(usernameToCall);
        	call.addEventListener(callListeners);
    	}   
    });

    $("#answer").click(function(event) {
        event.preventDefault();
        if (call) {
        	call.answer();
        }
    });

    $("#hangup").click(function(event) {
        event.preventDefault();
        if (call) {
        	call.hangup();
        	call = null
        }
    });

    var incomingCallListener = {
        onIncomingCall: function(incomingCall) {
            $("div#status").append("<div>Incoming Call</div>");
            call = incomingCall;
            call.addEventListener(callListeners);
        }
    }

    var callListeners = {
        onCallProgressing: function(call) {
        },
        onCallEstablished: function(call) {
            $("video#outgoing").attr("src", call.outgoingStreamURL);
            $("video#incoming").attr("src", call.incomingStreamURL);
        },
        onCallEnded: function(call) {
            $("video#outgoing").attr("src", "");
            $("video#incoming").attr("src", "");
            call = null;
        }
    }        
});