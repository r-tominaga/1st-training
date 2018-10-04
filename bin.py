# -*- coding: utf-8 -*-
a = 100
n = 0
def dec_to_bin(tmp):
    arr = []
    while tmp != 1:
        if tmp % 2 == 0:
            arr.insert(0, 0)
            tmp = tmp / 2
        else:
            arr.insert(0, 1)
            tmp = (tmp - 1) / 2       
    arr.insert(0, 1)
    return arr

def cal(num, dec):
    arr = []
    while num > dec:
        if num % dec == 0:
            arr.insert(0, 0)
            num = int(num / dec)
        else:
            arr.insert(0, num % dec)
            num = int(num / dec) 
    if num % dec != 0:
        arr.insert(0, num % dec)
    return arr

if __name__ == '__main__':
    print(dec_to_bin(a))
    print(cal(100,2))
    print(cal(100,3))
    print(cal(490,8))

