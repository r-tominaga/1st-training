# coding: utf-8

import os
import data_helper
import numpy as np
import tensorflow as tf
from data_helper import *
from constants import *
import MeCab


if not os.path.exists(CHECKPOINTS_DIR):
    os.makedirs(CHECKPOINTS_DIR)

sess = tf.Session()
x, y, d = data_helper.load_data_and_labels_and_dictionaries()
arrays = x.tolist()
# print(arrays[0])
max_length = len(arrays[0])
dictionaries = d.tolist()

ckpt = tf.train.get_checkpoint_state('./checkpoints')
graph = tf.Graph()

lines = [ l.split("\t") for l in list(open('./test.txt').readlines()) ]
t = MeCab.Tagger('-Owakati')

contents = [ split_word(t, l[1]) for l in lines if len(l) is 2 ]
correct = lines[0]
contents = padding(contents, max_length)
dictionaries_inv = { c: i for i, c in enumerate(dictionaries) }
datum = [[ dictionaries_inv[word] for word in content ] for content in contents ]
datum= numpy.array(datum)
print("datum")
print(datum)
print("correct")
print(correct)
# Property for dropout. This is probability of keeping cell.
keep = tf.placeholder(tf.float32)
# ----------------------------------------------------------
# Build Convolutional Neural Network for text classification
# ----------------------------------------------------------
# Define input layer.
input_x = tf.placeholder(tf.int32,   [ None, max_length     ])
input_y = tf.placeholder(tf.float32, [ None, NUM_CLASSES ])

last_model = ckpt.model_checkpoint_path
saver = tf.train.import_meta_graph("{}.meta".format(last_model))
saver.restore(sess, last_model)


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

# ----------------------------------------------------------
# Create optimizer.
# ----------------------------------------------------------
# Use cross entropy for softmax as a cost function.
xentropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits = predict_y, labels = input_y))

# Add L2 regularization term in order to avoid overfitting.
loss = xentropy + L2_LAMBDA * tf.nn.l2_loss(w)

# Create optimizer for my cost function.
global_step = tf.Variable(0, name="global_step", trainable=False)
train = tf.train.AdamOptimizer(0.0001).minimize(loss, global_step=global_step)

# ----------------------------------------------------------
# Measurement of accuracy and summary for TensorBoard.
# ----------------------------------------------------------
sess.run(tf.initialize_all_variables())
prediction = tf.argmax(predict_y, 1)
real = tf.argmax(input_y, 1)
#tf.Variable(correct[0], shape=(None,4))
# sample = tf.constant(correct[0], shape=[ None, 4])
# TRAINING.
pred = sess.run(
    [ prediction ],
    feed_dict={ input_x: datum, keep: 0.5 }
)
print("予想は")
print(pred[0])
print("正解は:")
print(correct[0])
