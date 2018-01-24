import os
import data_helper
import numpy as np
import tensorflow as tf
from data_helper import *
from constants import *
import MeCab

# ベクトル化された文章の配列はdata
data         = np.load(DATA_FILE)
labels       = np.load(LABEL_FILE)
dictionaries = np.load(DICTIONARY_FILE)
# print(data)
arrays = data.tolist()
# print(arrays[0])
max_length = len(arrays[0])
dictionaries = dictionaries.tolist()

ckpt = tf.train.get_checkpoint_state('./checkpoints')
graph = tf.Graph()

with tf.Session() as sess:

    sess.run(tf.global_variables_initializer())
    cwd = os.getcwd()
    input_x = tf.placeholder(tf.int32,   [ None, max_length       ])
    input_y = tf.placeholder(tf.float32, [ None, NUM_CLASSES ])
    keep = tf.placeholder(tf.float32)

    last_model = ckpt.model_checkpoint_path
    saver = tf.train.import_meta_graph("{}.meta".format(last_model))
    saver.restore(sess, last_model)
    prediction=tf.argmax(data[1],1)
    best = sess.run([prediction],feed_dict={})
    print(best)
    lines = [ l.split("\t") for l in list(open('./test.txt').readlines()) ]
    t = MeCab.Tagger('-Owakati')

    contents = [ split_word(t, l[1]) for l in lines if len(l) is 2 ]
    contents = padding(contents, max_length)
    # labels   = [ one_hot_vec(int(l[0]) - 1) for l in lines ]
    print(contents)
    dictionaries_inv = { c: i for i, c in enumerate(dictionaries) }
    datum = [[ dictionaries_inv[word] for word in content ] for content in contents ]
    datum= numpy.array(datum)
    print(datum)
    x, y, d = data_helper.load_data_and_labels_and_dictionaries()
    # Split original data into two groups for training and testing.
    test_x,  test_y  = x[-NUM_TESTS:], y[-NUM_TESTS:]
    random_indice = np.random.permutation(train_x_length)
