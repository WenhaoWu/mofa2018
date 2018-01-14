var Recognition = {
	tracker: null,

	//Initialization
	init: function initFn() {
		this.createTracker();
		this.createOverlays();
	},

	//Crate tracker: 1st parameter cloud token, 2nd target archive token
	createTracker: function createTrackerFn() {
		Recognition.tracker = new AR.CloudTracker("ca88a339361b0f378c450f072dcdc9eb", "56602b62aba250e80d17f555", {
			onLoaded: this.trackerLoaded,
			onError: this.trackerError
		});
	},

	//Error tracker
	trackerError: function trackerErrorFn(errorMessage) {
		alert(errorMessage);
	},

	//Overlay image, size and position
	createOverlays: function createOverlaysFn() {
        this.imgButton = new AR.ImageResource("assets/status.png");


        this.overlayPage = new AR.HtmlDrawable({
            uri: "assets/inner.html"
        }, 1, {
            viewportWidth: 1000,
            viewportHeight: 1000,
            offsetX: 0,
            offsetY: 0,
            clickThroughEnabled: true,
            //allowDocumentLocationChanges: false,
            onDocumentLocationChanged: function onDocumentLocationChangedFn(uri) {
                AR.context.openInBrowser(uri);
            }
        });


	},

	//Recognition of image on click
    onRecognition: function onRecognitionFn(recognized, response) {
        if (recognized) {
            console.log(response.metadata.id);
            var patt = /library_/;
            var pattKalion = /kalion_/;
            if(pattKalion.test(response.targetInfo.name)) {
                Recognition.kirkko = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name , {
                    drawables: {
                        cam: [Recognition.overlayPage]
                    }

                });
            } else {
                Recognition.pageOneButton = Recognition.createWwwButton(response.metadata.id, 0.3, {
                    offsetX: 0,
                    offsetY: 0
                });
                Recognition.wineLabelAugmentation = new AR.Trackable2DObject(Recognition.tracker, response.targetInfo.name , {
                    drawables: {
                        cam: [Recognition.pageOneButton]
                    }

                });
            }
        } else {
            $('#errorMessage').html("<div class='errorMessage'>Recognition failed! Try to stand in front of building</div>");
            setTimeout(function() {
                $('#errorMessage').empty();
            }, 5000);
        }
    },

	//Recognition error
	onRecognitionError: function onRecognitionError(errorCode, errorMessage) {
		alert("error code: " + errorCode + " error message: " + JSON.stringify(errorMessage));
	},

	//Scan button is pressed
	scan: function scanFn() {
		Recognition.tracker.recognize(this.onRecognition, this.onRecognitionError);
	},

	//Shows pop-up at the begining with instructions
	trackerLoaded: function trackerLoadedFn() {
		Recognition.showUserInstructions();
	},

	createWwwButton: function createWwwButtonFn(url, size, options) {
                            options.onClick = function() {
                                document.location = "architectsdk://snapShotButton?name=" + url;
                            };
                            return new AR.ImageDrawable(this.imgButton, size, options);
                        },

	showUserInstructions: function showUserInstructionsFn() {
        $('#messageBox').text("Point camera to building of interest");
		setTimeout(function() {
			$("#messageBox").remove();
		}, 5000);
	},

	toggleAnimateModel: function toggleAnimateModelFn() {
    		if (!Recognition.rotationAnimation.isRunning()) {
    			if (!Recognition.rotating) {
    				Recognition.rotationAnimation.start(-1);
    				Recognition.rotating = true;
    			} else {
    				Recognition.rotationAnimation.resume();
    			}
    		} else {
    			Recognition.rotationAnimation.pause();
    		}

    		return false;
    	}
};
$(document).ready(function(){
    Recognition.init();
});