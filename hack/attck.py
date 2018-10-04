import itertools
def analyze_term(term):
    ans = []
    if not term:
        return ans
    for symbol in '!"@#$%&\'()=~-^\\|`{}[]:*;+<>,./?\_':
        if symbol in term:
            for t in term.split(symbol):
                ans.append(t)
                ans += analyse_term(t)
    return ans

def read_dictionaly(dictionaly_file):
    with open(dictionaly_file) as f:
        words = set(map(lambda x: x.replace('\n','').replace('\r',''), f.readlines()))
        f.close()
    return words

def create_indirect_word(dictionaly,string,additional_num):
    li = list()
    for term in dictionaly:
        for s in itertools.combinations_with_replacement(string,additional_num):
            s = ''.join(s)
            li.append(s+term)
            li.append(term+s)
    return li

def dictionaly_attack(string, dictionaly_file, max_additional_num):
    dictionaly = set(read_dictionaly(dictionaly_file))
    ret = [term for term in dictionaly]
    li = [i for i in range(1,max_additional_num+1)]
    for i in li:
        ret2 = create_indirect_word(dictionaly,string,i)
    ret = ret + ret2
    return ret