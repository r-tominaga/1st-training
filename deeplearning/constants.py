NUM_TESTS         = 640
NUM_CLASSES       = 4
NUM_EPOCHS        = 100
NUM_MINI_BATCH    = 32
EMBEDDING_SIZE    = 128
NUM_FILTERS       = 128
FILTER_SIZES      = [ 3, 4, 5 ]
L2_LAMBDA         = 0.0001
EVALUATE_EVERY    = 5
CHECKPOINTS_EVERY = 20

SUMMARY_LOG_DIR = 'summary_log'
CHECKPOINTS_DIR = 'checkpoints'

RAW_FILE        = 'data/raw.txt'
DATA_FILE       = 'data/data.npy'
LABEL_FILE      = 'data/labels.npy'
DICTIONARY_FILE = 'data/dictionaries.npy'
