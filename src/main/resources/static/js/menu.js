$(function() {
    var url = location.protocol + '//' + location.host + location.pathname;
    $('.side-tab a').filter(function() {
        return this.href == url;
    }).children(".menu-el").addClass('active').parent().attr("disabled", "disabled").on("click", function() {
        return false;
    });;
});
