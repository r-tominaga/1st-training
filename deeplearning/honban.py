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

with tf.Session() as sess:

    sess.run(tf.global_variables_initializer())
    # saver = tf.train.Saver()
    cwd = os.getcwd()
    last_model = ckpt.model_checkpoint_path
    # saver.restore(sess, last_model)
    # 判別させたい文章を.txtに書き込んで
    lines = [ l.split("\t") for l in list(open('./test.txt').readlines()) ]
    t = MeCab.Tagger('-Owakati')

    contents = [ split_word(t, l[1]) for l in lines if len(l) is 2 ]
    contents = padding(contents, max_length)
    # labels   = [ one_hot_vec(int(l[0]) - 1) for l in lines ]
    print(contents)
    dictionaries_inv = { c: i for i, c in enumerate(dictionaries) }
    datum = [[ dictionaries_inv[word] for word in content ] for content in contents ]
    # print(datum)
    datum= numpy.array(datum)
    print(datum)
    #datumにはベクトル化された文章
    # input_x = tf.placeholder(tf.int32,   [ None, x_dim       ])
    # input_y = tf.placeholder(tf.float32, [ None, NUM_CLASSES ])
    # predict_y = tf.nn.softmax(tf.matmul(h0, w) + b)
    # predict  = tf.equal(tf.argmax(predict_y, 1), tf.argmax(, 1))
    # print(predict)
    x, y, d = data_helper.load_data_and_labels_and_dictionaries()

    # Split original data into two groups for training and testing.
    test_x,  test_y  = x[-NUM_TESTS:], y[-NUM_TESTS:]

    # Property for dropout. This is probability of keeping cell.
    keep = tf.placeholder(tf.float32)

    # ----------------------------------------------------------
    # Build Convolutional Neural Network for text classification
    # ----------------------------------------------------------
    # Define input layer.
    input_x = tf.placeholder(tf.int32,   [ None, max_length       ])
    input_y = tf.placeholder(tf.float32, [ None, NUM_CLASSES ])

    # Define 2nd layer (Word embedding layer).
    with tf.name_scope('embedding'):
        w  = tf.Variable(tf.random_uniform([ len(d), EMBEDDING_SIZE ], -1.0, 1.0), name='weight')
        e  = tf.nn.embedding_lookup(w, input_x)
        ex = tf.expand_dims(e, -1)

    # Define 3rd and 4th layer (Temporal 1-D convolutional and max-pooling layer).
    p_array = []
    for filter_size in FILTER_SIZES:
        with tf.name_scope('conv-%d' % filter_size):
            w  = tf.Variable(tf.truncated_normal([ filter_size, EMBEDDING_SIZE, 1, NUM_FILTERS ], stddev=0.02), name='weight')
            b  = tf.Variable(tf.constant(0.1, shape=[ NUM_FILTERS ]), name='bias')
            c0 = tf.nn.conv2d(ex, w, [ 1, 1, 1, 1 ], 'VALID')
            c1 = tf.nn.relu(tf.nn.bias_add(c0, b))
            c2 = tf.nn.max_pool(c1, [ 1, max_length - filter_size + 1, 1, 1 ], [ 1, 1, 1, 1 ], 'VALID')
            p_array.append(c2)

    p = tf.concat(p_array, 3)

    # Define output layer (Fully-connected layer).
    with tf.name_scope('fc'):
        total_filters = NUM_FILTERS * len(FILTER_SIZES)
        w = tf.Variable(tf.truncated_normal([ total_filters, NUM_CLASSES ], stddev=0.02), name='weight')
        b = tf.Variable(tf.constant(0.1, shape=[ NUM_CLASSES ]), name='bias')
        h0 = tf.nn.dropout(tf.reshape(p, [ -1, total_filters ]), keep)
        predict_y = tf.nn.softmax(tf.matmul(h0, w) + b)
        print(predict_y)
        predict  = tf.equal(tf.argmax(predict_y, 1), tf.argmax(input_y, 1))
        print(predict)
        print(tf.cast(predict, tf.float32))
        mini_batch_y = []
        random_indice = np.random.permutation(100)
        mini_batch_y.append(test_y[random_indice[NUM_MINI_BATCH]])

        xentropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits = predict_y, labels = input_y))
        aaa = tf.nn.softmax_cross_entropy_with_logits(logits = predict_y, labels = input_y)
        print(aaa)
        print(xentropy)

        # Add L2 regularization term in order to avoid overfitting.
        loss = xentropy + L2_LAMBDA * tf.nn.l2_loss(w)

        # _, temp_train_preds = sess.run([loss, predict], feed_dict={input_x: datum, input_y: mini_batch_y} )
        # predictions = np.argmax(temp_train_preds, axis=1)
        # print(predictions)

        #pred = np.argmax(predict_y.eval(feed_dict={ input_x: datum, input_y: [None,[ 0.,  1.,  0.,  0.]] ,keep: 1.0 }))
        #print(pred)
