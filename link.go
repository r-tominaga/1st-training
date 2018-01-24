package main

type Node struct {
  Next *Node
  Data int64
}

type List struct {
  Head Node
  Current Node
}

//追加
func (l *List) Push(add Node, obj List) {
  tmp := obj.Current
  tmp.Next = add
  obj.Current = add
}
//削除
func (l *List) Pop(rmv Node) {

}
// 先頭取り出し
func (l *List) Shift(obj List) {
    return obj.Head
}
// 先頭追加
func (l *List) Unshift(add Node, obj List) {
  // 先頭ノードがまだ無ければ
  if obj.Head == nil {
    obj.Head = add
    obj.Current = add
  } else {
    tmp := obj.Head
    obj.Head = add
    obj.Head.Next = tmp
  }
  return obj
}

func main() {
  list := List{}
  genesis := Node{Date: 1}
  list.Unshift(genesis, list)
}
