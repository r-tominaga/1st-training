import numpy
import itertools
import os.path
import sys
from collections import Counter
from constants import *
from datetime import datetime

def split_word(tagger, content):
    word = tagger.parse(content).split(' ')
    word = [ w.strip() for w in word ]
    return word

def one_hot_vec(index):
    v = numpy.zeros(NUM_CLASSES)
    v[index] = 1
    return v

def padding(contents, max_word_count):
    for content in contents :
        content += ['<PAD/>']  * (max_word_count - len(content))

    return contents

def load_data_and_labels_and_dictionaries():
    if os.path.exists(DATA_FILE) and os.path.exists(LABEL_FILE) and os.path.exists(DICTIONARY_FILE):
        data         = numpy.load(DATA_FILE)
        labels       = numpy.load(LABEL_FILE)
        dictionaries = numpy.load(DICTIONARY_FILE)

    else:
        import MeCab

        lines = [ l.split("\t") for l in list(open(RAW_FILE).readlines()) ]
        t = MeCab.Tagger('-Owakati')

        contents = [ split_word(t, l[1]) for l in lines if len(l) is 2 ]
        contents = padding(contents, max([ len(c) for c in contents ]))
        labels   = [ one_hot_vec(int(l[0]) - 1) for l in lines ]
        print(contents)

        ctr = Counter(itertools.chain(*contents))
        dictionaries     = [ c[0] for c in ctr.most_common() ]
        dictionaries_inv = { c: i for i, c in enumerate(dictionaries) }

        data = [ [ dictionaries_inv[word] for word in content ] for content in contents ]

        data         = numpy.array(data)
        labels       = numpy.array(labels)
        dictionaries = numpy.array(dictionaries)

        numpy.save(DATA_FILE,       data)
        numpy.save(LABEL_FILE,      labels)
        numpy.save(DICTIONARY_FILE, dictionaries)

    return data, labels, dictionaries

def log(content):
    time = datetime.now().strftime("%Y/%m/%d %H:%M:%S")
    print(time + ': ' + content)
    sys.stdout.flush()
