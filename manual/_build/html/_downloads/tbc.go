package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	sc "github.com/hyperledger/fabric/protos/peer"
)

// データストア用構造体
type UserInfo struct {
	// ユーザー名
	Name string `json:"name"`
	// １次配布用コイン
	Raw int64 `json:"raw"`
	//　決済可能な有効コイン
	Coin int64 `json:"coin"`
	// 権限情報
	Auth uint64 `json:"auth"`
}

//最大発行額用構造体
type Max struct {
	Max uint64 `json:"max"`
}

//有効期限用構造体
type Limit struct {
	Limit time.Time `json:"limit"`
}

type SmartContract struct {
}

func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {
	return shim.Success(nil)
}

//呼ぶ関数を決定するために、ここで引数を検証する。基本機能のインデックスのようなもの。
func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {

	// スマートコントラクトの関数名と引数を受け取る
	function, args := APIstub.GetFunctionAndParameters()
	// Route to the appropriate handler function to interact with the ledger appropriately
	//以下に関数名ごとの処理先を明示
	if function == "initLedger" {
		return s.initLedger(APIstub)
	} else if function == "queryUser" {
		return s.queryUser(APIstub, args)
	} else if function == "queryAll" {
		return s.queryAll(APIstub)
	} else if function == "queryMax" {
		return s.queryMax(APIstub)
	} else if function == "queryLimit" {
		return s.queryLimit(APIstub)
	} else if function == "createUser" {
		return s.createUser(APIstub, args)
	} else if function == "addRaw" {
		return s.addRaw(APIstub, args)
	} else if function == "changeAuth" {
		return s.changeAuth(APIstub, args)
	} else if function == "changeMax" {
		return s.changeMax(APIstub, args)
	} else if function == "sendTbc" {
		return s.sendTbc(APIstub, args)
	} else if function == "initDist" {
		return s.initDist(APIstub, args)
	} else if function == "modifyLimit" {
		return s.modifyLimit(APIstub, args)
	} else if function == "initializer" {
		return s.initializer(APIstub)
	}
	return shim.Error("無効なスマートコントラクト名です")
}

//ネットワーク起動時に呼ばれる。ワールドステートの初期値を入れる。
func (s *SmartContract) initLedger(APIstub shim.ChaincodeStubInterface) sc.Response {
	s.initializer(APIstub)
	return shim.Success(nil)
}

func (s *SmartContract) initializer(APIstub shim.ChaincodeStubInterface) sc.Response {
	args := []string{"0", "1966-12-9"}
	s.initDist(APIstub, args)

	startKey := "USR"
	endKey := ""

	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		user := UserInfo{}
		json.Unmarshal(queryResponse.Value, &user)
		user.Raw = 0
		user.Coin = 0
		userAsBytes, err := json.Marshal(user)
		if err != nil {
			return shim.Error(err.Error())
		}

		APIstub.PutState(queryResponse.Key, userAsBytes)

	}

	//これなにやってるんだ？デバッグ？
	fmt.Printf("- initialize:\n")

	return shim.Success(nil)
}

//有効期限変更
func (s *SmartContract) modifyLimit(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 1 {
		return shim.Error("引数の数は1つです")
	}

	limit := Limit{}
	var input []string = strings.Split(args[0], "-")
	year64, err := strconv.ParseInt(input[0], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	year := int(year64)
	month64, err := strconv.ParseInt(input[1], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	month := time.Month(month64)
	day64, err := strconv.ParseInt(input[2], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	day := int(day64)
	date := time.Date(year, month, day, 23, 59, 0, 0, time.Local)
	limit = Limit{Limit: date}
	limitAsBytes, err := json.Marshal(limit)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState("LIMIT", limitAsBytes)

	return shim.Success(nil)
}

//配布準備時
func (s *SmartContract) initDist(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	if len(args) != 2 {
		return shim.Error("引数の数は2つです")
	}
	//増額は正の整数、減額は負の整数
	max := Max{}
	init, err := strconv.ParseUint(args[0], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	max.Max = init

	maxAsBytes, err := json.Marshal(max)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState("MAX", maxAsBytes)

	//有効期限の初期化
	input := []string{args[1]}
	s.modifyLimit(APIstub, input)

	//中央銀行に発行額を送る
	addraw := []string{"USR-1", args[0]}
	s.addRaw(APIstub, addraw)

	return shim.Success(nil)
}

//ユーザー情報を参照
func (s *SmartContract) queryUser(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 1 {
		return shim.Error("引数の数は1つです")
	} else if args[0] == "" {
		return shim.Error("無効なKey")
	}

	userAsBytes, err := APIstub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(userAsBytes)
}

//全台帳参照
func (s *SmartContract) queryAll(APIstub shim.ChaincodeStubInterface) sc.Response {
	startKey := ""
	endKey := ""

	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// bufferは参照結果を含むJSON配列
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// 配列内を","で区切っている
		//Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		//レコードはJSONオブジェクト。なのでそのまま書く。
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	//これなにやってるんだ？デバッグ？
	fmt.Printf("- queryAll:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

//最大発行額を参照
func (s *SmartContract) queryMax(APIstub shim.ChaincodeStubInterface) sc.Response {

	maxAsBytes, err := APIstub.GetState("MAX")
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(maxAsBytes)
}

//有効期限を参照
func (s *SmartContract) queryLimit(APIstub shim.ChaincodeStubInterface) sc.Response {

	limitAsBytes, err := APIstub.GetState("LIMIT")
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(limitAsBytes)
}

//ユーザー新規作成
func (s *SmartContract) createUser(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	//引数チェック
	if len(args) != 3 {
		return shim.Error("正しい引数は3個です" + strconv.Itoa(len(args)))
	} else if args[0] == "" {
		return shim.Error("無効なKey")
	} else if args[1] == "" {
		return shim.Error("ユーザー名が空欄です")
	}
	auth, err := strconv.ParseUint(args[2], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	var user = UserInfo{Name: args[1], Raw: 0, Coin: 0, Auth: auth}
	userAsBytes, err := json.Marshal(user)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState(args[0], userAsBytes)

	return shim.Success(nil)
}

//追加発行時にUSR-1の口座を増やしている。addMaxはMAXだけ増やしていて整合性取れないから。ひとつにまとめたほうが良い。
func (s *SmartContract) addRaw(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 2 {
		return shim.Error("正しい引数は2個です")
	} else if args[0] == "" {
		return shim.Error("無効なKey")
	}

	userAsBytes, err := APIstub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	user := UserInfo{}

	json.Unmarshal(userAsBytes, &user)
	//増額は正の整数
	Raw, err := strconv.ParseInt(args[1], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	user.Raw = user.Raw + Raw

	userAsBytes, err = json.Marshal(user)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState(args[0], userAsBytes)

	return shim.Success(nil)
}

// // 送金取り消しにも使う
// // 引数を増やしてRaw to RawかCoin to Rawか判別
// func (s *SmartContract) sendTbc(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
// 	//from,to,total, type
// 	if len(args) != 4 {
// 		return shim.Error("正しい引数は4個です")
// 	} else if args[0] == "" {
// 		return shim.Error("無効なユーザー名")
// 	} else if args[1] == "" {
// 		return shim.Error("無効なユーザー名")
// 	} else if args[0] == args[1] {
// 		return shim.Error("同一のユーザーに送金はできません")
// 	}
// 	//共通部分
// 	user1AsBytes, err := APIstub.GetState(args[0])
// 	if err != nil {
// 		return shim.Error(err.Error())
// 	}
// 	user2AsBytes, err := APIstub.GetState(args[1])
// 	if err != nil {
// 		return shim.Error(err.Error())
// 	}
// 	user1 := UserInfo{}
// 	user2 := UserInfo{}
//
// 	json.Unmarshal(user1AsBytes, &user1)
// 	json.Unmarshal(user2AsBytes, &user2)
//
// 	//共通部分
// 	user1AsBytes, err = json.Marshal(user1)
// 	if err != nil {
// 		return shim.Error(err.Error())
// 	}
// 	user2AsBytes, err = json.Marshal(user2)
// 	if err != nil {
// 		return shim.Error(err.Error())
// 	}
// 	APIstub.PutState(args[0], user1AsBytes)
// 	APIstub.PutState(args[1], user2AsBytes)
//
// 	return shim.Success(nil)
// }

//新sendTbc
func (s *SmartContract) sendTbc(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {
	//from,to,total, type
	if len(args) != 5 {
		return shim.Error("正しい引数は5個です")
	} else if args[0] == "" {
		return shim.Error("無効なユーザー名")
	} else if args[1] == "" {
		return shim.Error("無効なユーザー名")
	} else if args[0] == args[1] {
		return shim.Error("同一のユーザーに送金はできません")
	} else if args[2] == "0" && args[3] == "0" {
		return shim.Error("送金額エラー")
	} else if args[4] != "0" && args[4] != "1" {
		return shim.Error("typeエラー")
	}
	//共通部分
	user1AsBytes, err := APIstub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	user2AsBytes, err := APIstub.GetState(args[1])
	if err != nil {
		return shim.Error(err.Error())
	}
	user1 := UserInfo{}
	user2 := UserInfo{}

	json.Unmarshal(user1AsBytes, &user1)
	json.Unmarshal(user2AsBytes, &user2)

	var Coin int64
	var Raw int64

	//to Coin
	if args[4] == "1" {
		if args[2] != "0" && args[3] != "0" {
			arg2, err := strconv.ParseInt(args[2], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}
			arg3, err := strconv.ParseInt(args[3], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}

			if arg2 > user1.Raw {
				return shim.Error("P残高が足りません")
			} else if arg3 > user1.Coin {
				return shim.Error("tBC残高が足りません")
			}

			Coin = arg2 + arg3
			user1.Raw = user1.Raw - arg2
			user1.Coin = user1.Coin - arg3
			user2.Coin = user2.Coin + Coin
		} else if args[3] != "0" {
			//coin to coin
			Coin, err = strconv.ParseInt(args[3], 10, 0)

			if err != nil {
				return shim.Error(err.Error())
			}

			if Coin > user1.Coin {
				return shim.Error("tBC残高が足りません")
			}
			user1.Coin = user1.Coin - Coin
			user2.Coin = user2.Coin + Coin

		} else {
			// raw to coin
			Coin, err = strconv.ParseInt(args[2], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}

			if Coin > user1.Raw {
				return shim.Error("P残高が足りません")
			}

			user1.Raw = user1.Raw - Coin
			user2.Coin = user2.Coin + Coin
		}

		if Coin < 0 {
			return shim.Error("送金額エラー")
		}

	} else if args[4] == "0" {
		// to Raw
		if args[2] != "0" && args[3] != "0" {
			arg2, err := strconv.ParseInt(args[2], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}
			arg3, err := strconv.ParseInt(args[3], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}

			if arg2 > user1.Raw {
				return shim.Error("P残高が足りません")
			} else if arg3 > user1.Coin {
				return shim.Error("tBC残高が足りません")
			}

			Raw = arg2 + arg3
			user1.Raw = user1.Raw - arg2
			user1.Coin = user1.Coin - arg3
			user2.Raw = user2.Raw + Raw
		} else if args[3] != "0" {
			//coin to raw
			Raw, err = strconv.ParseInt(args[3], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}

			if Raw > user1.Coin {
				return shim.Error("tBC残高が足りません")
			}

			user1.Coin = user1.Coin - Raw
			user2.Raw = user2.Raw + Raw
		} else {
			// raw to raw
			Raw, err = strconv.ParseInt(args[2], 10, 0)
			if err != nil {
				return shim.Error(err.Error())
			}

			if Raw > user1.Raw {
				return shim.Error("P残高が足りません")
			}

			user1.Raw = user1.Raw - Raw
			user2.Raw = user2.Raw + Raw
		}

		if Raw < 0 {
			return shim.Error("送金額エラー")
		}

	}

	//共通部分
	user1AsBytes, err = json.Marshal(user1)
	if err != nil {
		return shim.Error(err.Error())
	}
	user2AsBytes, err = json.Marshal(user2)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState(args[0], user1AsBytes)
	APIstub.PutState(args[1], user2AsBytes)

	return shim.Success(nil)
}

func (s *SmartContract) changeAuth(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 2 {
		return shim.Error("正しい引数は2個です")
	} else if args[0] == "" {
		return shim.Error("無効なユーザー名")
	}

	userAsBytes, err := APIstub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	user := UserInfo{}

	json.Unmarshal(userAsBytes, &user)

	//args[1]に変更後の権限番号が入っている
	auth, err := strconv.ParseUint(args[1], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	user.Auth = auth

	userAsBytes, err = json.Marshal(user)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState(args[0], userAsBytes)

	return shim.Success(nil)
}

//最大発行額の変更
//USR-1のRawも変更するようにした
//逆に現在changeMax時にaddRawを呼んでいるのでWeb側の処理から外す必要が出てくる
func (s *SmartContract) changeMax(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {

	if len(args) != 1 {
		return shim.Error("正しい引数は1個です")
	}
	//keyはMAX
	maxAsBytes, err := APIstub.GetState("MAX")
	if err != nil {
		return shim.Error(err.Error())
	}
	max := Max{}

	json.Unmarshal(maxAsBytes, &max)
	//増額は正の整数、減額は負の整数
	diff, err := strconv.ParseInt(args[0], 10, 0)
	if err != nil {
		return shim.Error(err.Error())
	}
	imax := int64(max.Max)
	imax = imax + diff
	max.Max = uint64(imax)

	maxAsBytes, err = json.Marshal(max)
	if err != nil {
		return shim.Error(err.Error())
	}
	APIstub.PutState("MAX", maxAsBytes)

	args1 := []string{"USR-1", args[0]}
	s.addRaw(APIstub, args1)

	return shim.Success(nil)
}

//エントリーポイントはここ
func main() {

	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Printf("Error creating new Smart Contract: %s", err)
	}
}
