#!/usr/bin/env bash

# Do not run this script directly on PDC.

# This stops the script when a command's exit code is non-zero (i.e., error).
set -e

# ALGO=ParallelStream
# THREADS=2
SIZE=10000000
WARMUP=10
MEASURE=100
SEED=42

javac MeasureMain.java
java MeasureMain Sequential 1 $SIZE $WARMUP $MEASURE $SEED
for ALGO in ExecutorService ForkJoinPool ParallelStream JavaSort; do
    for THREADS in 1 2 4 8 16 32 48 64 96; do
        java MeasureMain $ALGO $THREADS $SIZE $WARMUP $MEASURE $SEED
    done
done