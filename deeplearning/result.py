#!/usr/bin/env python
# -*- coding: utf-8 -*-
from gensim.models import word2vec
import logging
import sys

# 学習済みモデルのロード
model = word2vec.Word2Vec.load("sample.model")

result = model.most_similar(positive ="CNN")
print(result)
