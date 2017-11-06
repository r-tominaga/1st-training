# coding: UTF-8
import MeCab
import sys
param = sys.argv
infile = param[1]
f = open(infile)
line = f.readline()
mt = MeCab.Tagger("-Owakati")
res = ""
while line:
    res = res + mt.parse(line)

    line = f.readline()
print(res)
