var endpoint = "https://testnet.blockexplorer.com";
var address = "ご自身のアドレス";
$.ajax({
    type: "get",
    url: endpoint + "/api/addr/" + address ,
    dataType: 'json',
})
.done(function(response){
    $("#contents").text(JSON.stringify(response,null,"\t"));
})
.fail(function(errordata){
    console.log(data);
});