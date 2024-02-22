function financial(x) {
  const balance = Number.parseFloat(x).toFixed(2);
  return balance.toLocaleString('de-DE');
}

$( document ).ready(function() {

    var searchParams = new URLSearchParams(window.location.search);
    $('#tabs li a').removeClass('tab-active');
    $('.transfer-container').hide();
    if (searchParams.has('out')) {
      $('#a-out').addClass('tab-active');
      $('#transfer-out').fadeIn('slow');
        
    } else {
      $('#a-in').addClass('tab-active');
      $('#transfer-in').fadeIn('slow');
    }
});


$('#tabs li a').click(function(){
  var id = $(this).attr('href');
  if(!$(this).hasClass('tab-active')){
    $('#tabs li a').removeClass('tab-active');
    $(this).addClass('tab-active');
    $('.transfer-container').hide();
    $('#transfer-' + id.substring(1, id.length)).fadeIn('slow');
 }
});