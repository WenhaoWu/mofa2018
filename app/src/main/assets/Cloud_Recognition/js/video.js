var Recognition = {

	createModel: function createModelFn() {

        this.location = new AR.RelativeLocation(null, -5, -5, 0);


        this.playButtonImg = new AR.ImageResource("assets/next.png");
        this.playButton = new AR.ImageDrawable(Recognition.playButtonImg, 3, {
            enabled: false,
            clicked: false,
            onClick: function playButtonClicked() {
                Recognition.video.play(1);
                Recognition.video.playing = true;
                Recognition.playButton.clicked = true;
            },
            offsetY: -0.3
        });

        this.video = new AR.VideoDrawable("assets/FunnyCatsCompilation.mp4", 4, {
            offsetY: Recognition.playButton.offsetY,
            onLoaded: function videoLoaded() {
                Recognition.playButton.enabled = true;
            },
            onPlaybackStarted: function videoPlaying() {
                Recognition.playButton.enabled = false;
                Recognition.video.enabled = true;
            },
            onFinishedPlaying: function videoFinished() {
                Recognition.playButton.enabled = true;
                Recognition.video.playing = false;
                Recognition.video.enabled = false;
            },
            onClick: function videoClicked() {
                if (Recognition.playButton.clicked) {
                    Recognition.playButton.clicked = false;
                } else if (Recognition.video.playing) {
                    Recognition.video.pause();
                    Recognition.video.playing = false;
                } else {
                    Recognition.video.resume();
                    Recognition.video.playing = true;
                }
            }
        });

        this.indicatorImage = new AR.ImageResource("assets/indi.png");
        this.indicatorDrawable = new AR.ImageDrawable(this.indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });
        this.obj = new AR.GeoObject(Recognition.location, {
            drawables: {
               cam: [Recognition.video],
               indicator: [Recognition.indicatorDrawable]
            },
            onEnterFieldOfVision: function onEnterFieldOfVisionFn() {
                Recognition.video.play(-1);
            }
        });
    }

};
$(document).ready(function(){
    Recognition.createModel();
});
