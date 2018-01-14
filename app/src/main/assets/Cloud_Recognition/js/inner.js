$(document).ready(function(){
    /*$.getJSON('http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/geoLocator/poi_detail.php?id=42', function(result){
        console.log(result[1].multiple_image[0]);
    });*/

    $("#carousel-generic").carousel("pause");
    $("#myBtn").click(function(){
        $("#carousel-generic").carousel("prev");
    });
    // Go to the next item
    $("#myBtn2").click(function(){
        $("#carousel-generic").carousel("next");
    });
    $("#next-button").click(function(){
        $("#carousel-generic").carousel("next");
    });
});