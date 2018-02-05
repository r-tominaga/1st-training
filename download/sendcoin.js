//var endpoint = "https://test-insight.bitpay.com";
var endpoint = "https://testnet.blockexplorer.com";

var fromAddress = "送金元アドレス";
var privateKey = "送金元秘密鍵";
var toAddress = "送金先アドレス";
var feeCoin = 10000;
var sendCoin = 100000;

$.ajax({
    type: "get",
    url: endpoint + "/api/addrs/" + fromAddress + "/utxo" ,
    contentType: 'application/json',
    dataType: 'json'
}).done(function(response){
    createTransaction(response);
}).fail(function(errordata){
    console.log(data);
});

function createTransaction(utxo){
    //ライブラリを読み込み
    var bitcore = require('bitcore-lib');
    //トランザクション作成
    var transaction = new bitcore.Transaction()
    .fee(feeCoin)
    .from(utxo)
    .to(toAddress, sendCoin)
    .change(fromAddress)
    .sign(privateKey);

    //ブロードキャスト
    broadcast(transaction);
}

function broadcast(transaction){
    $.ajax({
        type: "post",
        url: endpoint + "/api/tx/send" ,
        contentType: 'application/json',
        dataType: 'json',
        data:JSON.stringify({rawtx:transaction.toString()})
    }).done(function(response){
        $("#contents").text(JSON.stringify(response,null,"\t"));
    }).fail(function(errordata){
        console.log(errordata);
    });
}