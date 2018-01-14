var Recognition = {

    userLocation: null,

    createModel: function createModelFn(name, lat, lng) {
    //console.log("lat " + lat + "lng " + lng + "   " + name)
        this.location = new AR.RelativeLocation(null, -5, -5, 0);
        //this.location2 = new AR.GeoLocation(60.162880, 24.883690, -100.);
        this.location2 = new AR.GeoLocation(lat, lng);
        //this.modelLautasari = new AR.Model("assets/lautasari.wt3", {
        this.modelLautasari = new AR.Model("/sdcard/3dModels/" + name + ".wt3", {
            onLoaded: Recognition.loadingStep,
            onClick: Recognition.toggleAnimateModel,
            scale: {
                x: 0.5,
                y: 0.5,
                z: 0.5
            },
            translate: {
                x: 0.0,
                y: 0.0,
                z: 0.0
            },
            rotate: {
                heading: 20
                //tilt: 90,
                //roll: 90
            }
        });


        this.indicatorImage = new AR.ImageResource("assets/indi.png");
        this.indicatorDrawable = new AR.ImageDrawable(this.indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });
        Recognition.rotationAnimation = new AR.PropertyAnimation(Recognition.modelLautasari, "rotate.heading", -25, 335, 10000);
        this.obj = new AR.GeoObject(Recognition.location2, {
            drawables: {
               cam: [Recognition.modelLautasari],
               indicator: [Recognition.indicatorDrawable]
            }
        });
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
    },

    // udpates values show in "range panel"
    updateRangeValues: function updateRangeValuesFn() {
        // max range relative to the maximum distance of all visible places
        var maxRangeMeters = 100;
        // update culling distance, so only palces within given range are rendered
        AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);
    },

    locationChanged: function locationChangedFn(lat, lon, alt, acc) {
        Recognition.userLocation = {
            'latitude': lat,
            'longitude': lon,
            'altitude': alt,
            'accuracy': acc
        };
    }

}
$(document).ready(function(){
    Recognition.updateRangeValues();
    console.log("well, hello there");
});

AR.context.onLocationChanged = Recognition.locationChanged;