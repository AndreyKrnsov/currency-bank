

function financial(x) {
    const balance = Number.parseFloat(x).toFixed(2);
    return balance.toLocaleString('de-DE');
   }

$(document).ready(function() {

    var searchParams = new URLSearchParams(window.location.search);
    $('#tabs li a').removeClass('tab-active');
    $('.transfer-container').hide();
    if (searchParams.has('sell')) {
        $('#a-sell').addClass('tab-active');
        $('#transfer-container-sell').fadeIn('slow');
    } else {
        $('#a-buy').addClass('tab-active');
        $('#transfer-container-buy').fadeIn('slow');
    }


    updateSelectedInfo();
    
    $("#select-in-buy").change(function() {
        updateSelectedInfo();
    });

    $("#sum-input-buy").on("input", function() {
        updateTransferDraft();
    });


    function updateSelectedInfo() {
        var selectedOption = $("#select-in-buy").find("option:selected");
        var selectedInfo = selectedOption.data("info").split("|");
        $("#selected-account-buy").text(selectedInfo[1] + ' ' + selectedInfo[2]);

        updateTransferDraft();

        // $(".sum-input").attr('max', parseFloat(selectedInfo[1]));
    }

    function updateTransferDraft() {
        var selectedOption = $("#select-in-buy").find("option:selected");
        var selectedInfo = selectedOption.data("info").split("|");
        var rate = 1.0/parseFloat(selectedInfo[3]);
        var currency = selectedInfo[2];
        var sum = parseFloat($("#sum-input-buy").val());
        
        if (!isNaN(sum)) {
            $("#transfer-draft-buy").text(financial(sum * rate) + " " + currency);
            $("#transfer-draft-rate-buy").text("по курсу " + financial(rate));
        } else {
            $("#transfer-draft-buy").empty();
            $("#transfer-draft-rate-buy").empty();
        }
    }


    updateSelectedInfoSell();
    
    $("#select-in-sell").change(function() {
        updateSelectedInfoSell();
    });

    $("#sum-input-sell").on("input", function() {
        updateTransferDraftSell();
    });


    function updateSelectedInfoSell() {
        var selectedOption = $("#select-in-sell").find("option:selected");
        var selectedInfo = selectedOption.data("info").split("|");
        $("#selected-account-sell").text(selectedInfo[1] + ' ' + selectedInfo[2]);

        updateTransferDraftSell();

        $("#sum-input-sell").attr('placeholder', 'Сумма (' + selectedInfo[2] + ')');
        $("#sum-input-sell").attr('max', parseFloat(selectedInfo[1].replace(',', '.').replace(' ', '')));
    }

    function updateTransferDraftSell() {
        var selectedOption = $("#select-in-sell").find("option:selected");
        var selectedInfo = selectedOption.data("info").split("|");
        var rate = parseFloat(selectedInfo[3]);
        var currency = selectedInfo[2];
        var sum = parseFloat($("#sum-input-sell").val());
        
        if (!isNaN(sum)) {
            $("#transfer-draft-sell").text(financial(sum * rate) + " ₽");
            $("#transfer-draft-rate-sell").text("по курсу " + financial(rate));
        } else {
            $("#transfer-draft-sell").empty();
            $("#transfer-draft-rate-sell").empty();
        }
    }
});

$('#tabs li a').click(function(){
    var id = $(this).attr('href');
    if(!$(this).hasClass('tab-active')){
      $('#tabs li a').removeClass('tab-active');
      $(this).addClass('tab-active');
      $('.transfer-container').hide();
      $('#transfer-container-' + id.substring(1, id.length)).fadeIn('slow');
   }
  });

// function formatSearch(item) {
//     var selectionText = item.text.split("|");
//     var $returnString = $('<span>' + selectionText[0] + '</br>' + selectionText[1] + '</span>');
//     return $returnString;
// };
// function formatSelected(item) {
//     var selectionText = item.text.split("|");
//     var $returnString = $('<span>' + selectionText[0] + '</br>' + selectionText[1] + '</span>');
//     return $returnString;
// };
// $('#select-in').select2({
//     templateResult: formatSearch,
//     templateSelection: formatSelected
// });