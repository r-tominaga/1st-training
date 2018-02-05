//ライブラリを読み込み
var bitcore = require('bitcore-lib');

//秘密鍵の新規作成
var privateKey = new bitcore.PrivateKey('testnet');

//公開鍵を秘密鍵から生成
var publicKey = privateKey.toPublicKey();

//アドレスを秘密鍵から生成
var address = privateKey.toAddress('testnet');

$("#contents").append("<div>秘密鍵：" + privateKey + "</div>");

$("#contents").append("<div>公開鍵：" + publicKey + "</div>");

$("#contents").append("<div>アドレス：" + address + "</div>");
